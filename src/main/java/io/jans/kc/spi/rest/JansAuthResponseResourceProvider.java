package io.jans.kc.spi.rest;

import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.annotations.cache.NoCache;

public class JansAuthResponseResourceProvider implements RealmResourceProvider {
    


    public JansAuthResponseResourceProvider() {

    }

    @Override
    public Object getResource() {

        return this;
    }

    @Override
    public void close() {

    }

    @GET
    @NoCache
    @Produces(MediaType.TEXT_HTML)
    @Path("/auth-complete")
    public Response completeAuthentication() {

        ResponseBuilder builder = Response.ok("<html><head><title>Hello::World</title></head></html>");
        return builder.build();
    }
}
