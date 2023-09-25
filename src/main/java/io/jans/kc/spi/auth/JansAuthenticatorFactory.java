package io.jans.kc.spi.auth;

import java.util.List;

import org.jboss.logging.Logger;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;

import org.keycloak.Config;

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import org.keycloak.provider.ProviderConfigProperty;

import io.jans.kc.spi.auth.oidc.OIDCMetaCache;
import io.jans.kc.spi.auth.oidc.OIDCService;
import io.jans.kc.spi.auth.oidc.impl.HashBasedOIDCMetaCache;
import io.jans.kc.spi.auth.oidc.impl.NimbusOIDCService;


public class JansAuthenticatorFactory implements AuthenticatorFactory {
    
    private static final String PROVIDER_ID = "jans-cloak-authenticator";
    private static final String DISPLAY_TYPE = "Janssen Authentication";
    private static final String REFERENCE_CATEGORY = "Janssen Authentication";
    private static final String HELP_TEXT= "A third-party authenticator for Janssen Auth";

    private static final Logger log = Logger.getLogger(JansAuthenticatorFactory.class);

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.REQUIRED,
        AuthenticationExecutionModel.Requirement.ALTERNATIVE,
        AuthenticationExecutionModel.Requirement.DISABLED
    };

    
    private static final OIDCMetaCache META_CACHE = new HashBasedOIDCMetaCache();
    private static final OIDCService OIDC_SERVICE = new NimbusOIDCService(META_CACHE);
    private static final JansAuthenticator INSTANCE = new JansAuthenticator(OIDC_SERVICE);

    @Override
    public String getId() {

        return PROVIDER_ID;
    }

    @Override
    public Authenticator create(KeycloakSession session) {

        log.info("Jans Authenticator created");
        return INSTANCE;
    }

    @Override
    public void init(Config.Scope config) {

        return;
    }

    @Override
    public void close() {

        log.info("Jans Authenticator destroyed");
        return;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

        return;
    }

    @Override
    public String getDisplayType() {

        return DISPLAY_TYPE;
    }

    @Override
    public String getReferenceCategory() {

        return REFERENCE_CATEGORY;
    }

    @Override
    public boolean isConfigurable() {

        return true;
    }

    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {

        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {

        return false;
    }

    @Override
    public String getHelpText() {

        return HELP_TEXT;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        return JansAuthenticatorConfigProp.asList();
    }
}
