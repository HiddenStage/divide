package com.jug6ernaut.network.authenticator.server.auth;

/**
 * Created by williamwebb on 2/13/14.
 */
import com.jug6ernaut.network.shared.web.transitory.Credentials;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

public class ResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {

        Credentials user = ((UserContext)requestContext.getSecurityContext()).getUser();
        if(user!=null && user.getAuthToken() != null)
            responseContext.getHeaders().add("Authorization", user.getAuthToken());
    }
}
