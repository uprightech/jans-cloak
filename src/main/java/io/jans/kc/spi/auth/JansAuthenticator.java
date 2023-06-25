package io.jans.kc.spi.auth;

import java.util.List; 

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.RequiredActionFactory;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RequiredActionProviderModel;
import org.keycloak.models.UserModel;

public class JansAuthenticator implements Authenticator {
    
    @Override
    public void authenticate(AuthenticationFlowContext context) {

        return;
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
}