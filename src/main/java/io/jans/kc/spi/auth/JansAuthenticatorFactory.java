package io.jans.kc.spi.auth;

import java.util.Arrays;
import java.util.List; 

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;

import org.keycloak.Config;

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;


public class JansAuthenticatorFactory implements AuthenticatorFactory {
    
    private static final String PROVIDER_ID = "jans-cloak-authenticator";
    private static final String DISPLAY_TYPE = "Janssen Authentication";
    private static final String REFERENCE_CATEGORY = "Janssen Authentication";
    private static final String HELP_TEXT= "A third-party authenticator for Janssen Auth";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.REQUIRED,
        AuthenticationExecutionModel.Requirement.ALTERNATIVE,
        AuthenticationExecutionModel.Requirement.DISABLED
    };

    private static final ProviderConfigProperty JANS_CLIENT_ID_PROP = new ProviderConfigProperty(
            "jans.auth.client.id",
            "Janssen Client ID",
            "Client ID of the OpenID Client created in Janssen-Auth",
            ProviderConfigProperty.STRING_TYPE,
            null, 
            false);
    
    private static final List<ProviderConfigProperty> CONFIG_PROPS = ProviderConfigurationBuilder
            .create()
            .property(JANS_CLIENT_ID_PROP)
            .build();
    
    private static final JansAuthenticator INSTANCE = new JansAuthenticator();

    @Override
    public String getId() {

        return PROVIDER_ID;
    }

    @Override
    public Authenticator create(KeycloakSession session) {

        return INSTANCE;
    }

    @Override
    public void init(Config.Scope config) {

        return;
    }

    @Override
    public void close() {

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

        return true;
    }

    @Override
    public String getHelpText() {

        return HELP_TEXT;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        return CONFIG_PROPS;
    }
}
