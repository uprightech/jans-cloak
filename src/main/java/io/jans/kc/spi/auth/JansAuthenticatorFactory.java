package io.jans.kc.spi.auth;

import java.util.List; 

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;

import org.keycloak.Config;

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import org.keycloak.provider.ProviderConfigProperty;


public class JansAuthenticatorFactory implements AuthenticatorFactory {
    
    private static final String PROVIDER_ID = "jans-cloak-authenticator";

    @Override
    public String getId() {

        return null;
    }

    @Override
    public Authenticator create(KeycloakSession session) {

        return null;
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

        return null;
    }

    @Override
    public String getReferenceCategory() {

        return null;
    }

    @Override
    public boolean isConfigurable() {

        return false;
    }

    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {

        return null;
    }

    @Override
    public boolean isUserSetupAllowed() {

        return false;
    }

    @Override
    public String getHelpText() {

        return null;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        return null;
    }
}
