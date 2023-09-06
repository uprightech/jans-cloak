package io.jans.kc.spi.auth;

import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class JansAuthenticator implements Authenticator {
    
    private static final Logger log = Logger.getLogger(JansAuthenticator.class);

    private static final String JANS_AUTH_FORM_TEMPLATE  = "jans-auth-redirect.ftl";
    private static final String JANS_AUTH_ERROR_TEMPLATE = "jans-auth-error.ftl";
    
    private static final String OPENID_CODE_RESPONSE = "code";
    private static final String OPENID_SCOPE = "openid";
    private static final String JANS_LOGIN_URL_ATTRIBUTE = "jansLoginUrl";
    
    
    private OIDCService oidcService;

    public JansAuthenticator(OIDCService oidcService) {

        this.oidcService = oidcService;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {

        Configuration config = pluginConfigurationFromContext(context);
        ValidationResult validationresult = config.validate();
        if(validationresult.hasErrors()) {
            
            for(String error: validationresult.getErrors()) {
                log.errorv("Invalid plugin configuration {0}",error);
            }
            Response response = context.form().createForm(JANS_AUTH_ERROR_TEMPLATE);
            context.failure(AuthenticationFlowError.INTERNAL_ERROR,response);
            return;
        }

        try {
            URI redirecturi = createRedirectUri(context);
            String state = getOIDCState();
            String nonce = getOIDCNonce();
            OIDCAuthRequest oidcauthrequest = createAuthnRequest(config, state, nonce,redirecturi.toString());
            URI jansloginurl = oidcService.createAuthorizationUrl(config.normalizedIssuerUrl(), oidcauthrequest);
            Response response = context
                .form()
                .setAttribute(JANS_LOGIN_URL_ATTRIBUTE,jansloginurl.toString())
                .setActionUri(redirecturi)
                .createForm(JANS_AUTH_FORM_TEMPLATE);
            context.challenge(response);
        }catch(OIDCMetaError e) {
            log.errorv(e,"OIDC Error obtaining the authorization url");
            Response response = context.form().createForm(JANS_AUTH_ERROR_TEMPLATE);
            context.failure(AuthenticationFlowError.INTERNAL_ERROR,response);
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {

        return;
    }

    @Override
    public boolean requiresUser() {

        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {

        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel model, UserModel user) {

        return;
    }

    @Override
    public List<RequiredActionFactory> getRequiredActions(KeycloakSession session) {

        return null;
    }

    @Override
    public void close() {

        return;
    }

    private URI createRedirectUri(AuthenticationFlowContext context) {

        String accesscode = context.generateAccessCode();
        URI actionuri = context.getActionUrl(accesscode);

        return actionuri;
    }
    private Configuration pluginConfigurationFromContext(AuthenticationFlowContext context) {
        
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        if(config == null || config.getConfig() == null) {
            return null;
        }

        String server_url = config.getConfig().get(JansAuthenticatorConfigProp.SERVER_URL.getName());
        String client_id  = config.getConfig().get(JansAuthenticatorConfigProp.CLIENT_ID.getName());
        String issuer = config.getConfig().get(JansAuthenticatorConfigProp.ISSUER.getName());
        String extra_scopes = config.getConfig().get(JansAuthenticatorConfigProp.EXTRA_SCOPES.getName());
        List<String> parsed_extra_scopes = new ArrayList<>();
        if(extra_scopes != null) {
            String [] tokens = extra_scopes.split("\\s*,\\s*");
            for(String token : tokens) {
                parsed_extra_scopes.add(token);
            }
        }

        return new Configuration(server_url,client_id, issuer,parsed_extra_scopes);
    }

    private String getOIDCState() {

        return generateRandomString(10);
    }

    private String getOIDCNonce() {

        return generateRandomString(10);
    }

    private String generateRandomString(int length) {
        int leftlimit = 48; 
        int rightlimit = 122;
    
        return new Random().ints(leftlimit,rightlimit+1)
               .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
               .limit(length)
               .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
               .toString();
    }

    private OIDCAuthRequest createAuthnRequest(Configuration config, String state, String nonce,String redirecturi) {
        //
        OIDCAuthRequest request = new OIDCAuthRequest();
        request.setClientId(config.clientId);
        request.addScope(OPENID_SCOPE);
        for(String extrascope : config.scopes) {
            request.addScope(extrascope);
        }
        request.addResponseType(OPENID_CODE_RESPONSE);
        request.setNonce(nonce);
        request.setState(state);
        request.setRedirectUri(redirecturi);
        return request;
    }

    public static class ValidationResult {

        private List<String> errors;

        public void addError(String error) {

            if(errors == null) {
                this.errors = new ArrayList<String>();
            }
            this.errors.add(error);
        }

        public boolean hasErrors() {

            return this.errors != null;
        }

        public List<String> getErrors() {

            return this.errors;
        }
    }

    private class Configuration  {

        private String serverUrl;
        private String clientId;
        private String issuerUrl;
        private List<String> scopes;

        public Configuration(String serverUrl,String clientId, String issuerUrl, List<String> scopes) {

            this.serverUrl = serverUrl;
            this.clientId  = clientId;
            this.issuerUrl = issuerUrl;
            this.scopes = scopes;
        }

        
        public ValidationResult validate() {

            ValidationResult result = new ValidationResult();

            if(serverUrl == null || serverUrl.isEmpty()) {
                result.addError("Missing or empty Server Url");
            }

            if(clientId == null || clientId.isEmpty()) {
                result.addError("Missing or empty Client ID");
            }
            return result;
        }

        public String normalizedIssuerUrl() {

            String effective_url = issuerUrl;
            if(effective_url == null) {
                effective_url = serverUrl;
            }
            if(effective_url == null) {
                return null;
            }
            
            if(effective_url.charAt(effective_url.length() -1) == '/') {
                return effective_url.substring(0, effective_url.length() -1);
            }
            return effective_url;
        } 
        
    }
}