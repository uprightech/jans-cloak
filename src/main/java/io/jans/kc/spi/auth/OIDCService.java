package io.jans.kc.spi.auth;

import java.net.URI;

public interface OIDCService {

    public URI getAuthorizationEndpoint(String issuerUrl) throws OIDCMetaError;
    public URI createAuthorizationUrl(String issuerUrl, OIDCAuthRequest request) throws OIDCMetaError;
}
