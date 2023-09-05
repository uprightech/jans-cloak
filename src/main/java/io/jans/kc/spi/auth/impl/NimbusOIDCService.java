package io.jans.kc.spi.auth.impl;

import java.io.IOException;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.ResponseType.Value;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import io.jans.kc.spi.auth.OIDCAuthRequest;
import io.jans.kc.spi.auth.OIDCMetaCache;
import io.jans.kc.spi.auth.OIDCMetaCacheKeys;
import io.jans.kc.spi.auth.OIDCService;
import io.jans.kc.spi.auth.OIDCMetaError;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.jboss.logging.Logger;

public class NimbusOIDCService implements OIDCService {

    private OIDCMetaCache metaCache;
    private static final Logger log = Logger.getLogger(NimbusOIDCService.class);

    public NimbusOIDCService(OIDCMetaCache metaCache) {

        this.metaCache = metaCache;
    }
    
    @Override
    public URI getAuthorizationEndpoint(String issuerUrl) throws OIDCMetaError {

        URI ret = getAuthorizationEndpointFromCache(issuerUrl);
        if(ret == null) {
            return getAuthorizationEndpointFromServer(issuerUrl);
        }
        return ret;
    }

     @Override
    public URI createAuthorizationUrl(String issuerUrl, OIDCAuthRequest request) throws OIDCMetaError {

        try {
            
           return new AuthenticationRequest.Builder(
                extractResponseType(request.getResponseTypes()),
                extractScope(request.getScopes()),
                new ClientID(request.getClientId()),
                new URI(request.getRedirectUri())
           )
           .endpointURI(getAuthorizationEndpoint(issuerUrl))
           .state(new State(request.getState()))
           .nonce(new Nonce(request.getNonce()))
           .build().toURI();
        }catch(URISyntaxException e) {
            throw new OIDCMetaError("Error building the authentication url",e);
        }
    }

    private ResponseType extractResponseType(List<String> rtypes) {

        ResponseType rtype = new ResponseType();
        for(String val : rtypes) {
            rtype.add(new Value(val));
        }
        return rtype;
    }

    private Scope extractScope(List<String> scopes) {
        
        Scope scope = new Scope();
        for(String val :  scopes) {
            scope.add(val);
        }
        return scope;
    }

    private URI getAuthorizationEndpointFromCache(String issuerUrl) {

        return (URI) metaCache.get(issuerUrl, OIDCMetaCacheKeys.AUTHORIZATION_URL);
    }

    private URI  getAuthorizationEndpointFromServer(String issuerUrl) throws OIDCMetaError {

        OIDCProviderMetadata meta = obtainMetadataFromServer(issuerUrl);
        cacheMetadataFromServer(issuerUrl,meta);
        return getAuthorizationEndpointFromCache(issuerUrl);
    }

    private OIDCProviderMetadata obtainMetadataFromServer(String issuerUrl) throws OIDCMetaError {

        try {
            Issuer issuer = new Issuer(issuerUrl);
            return OIDCProviderMetadata.resolve(issuer);
        }catch(GeneralException e) {
            throw new OIDCMetaError("Could not obtain metadata from server",e);
        }catch(IOException e) {
            throw new OIDCMetaError("Could not obtain metadata from server",e);
        }
    }

    private void cacheMetadataFromServer(String issuerUrl,OIDCProviderMetadata metadata) {

        metaCache.put(issuerUrl,OIDCMetaCacheKeys.AUTHORIZATION_URL,metadata.getAuthorizationEndpointURI());
    }

}
