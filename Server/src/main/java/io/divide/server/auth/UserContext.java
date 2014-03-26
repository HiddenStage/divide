package io.divide.server.auth;

import io.divide.shared.web.transitory.Credentials;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.security.Principal;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/4/13
 * Time: 9:20 PM
 */
public class UserContext implements SecurityContext {
    private Credentials creds;
    private UriInfo uriInfo;

    public UserContext(UriInfo uriInfo, final Credentials user) {
        this.uriInfo = uriInfo;
        this.creds = user;
        //creds.setPassword("");
        this.principal = new Principal() {
            public String getName() {
                return user.getEmailAddress();
            }
        };
    }

    private Principal principal;

    public Principal getUserPrincipal() {
        return this.principal;
    }


    public boolean isUserInRole(String role) {
        if ("admin".equals(role)) {
            return "admin".equals(this.principal.getName());
        } else
        if ("user".equals(role)) {
            if ("admin".equals(this.principal.getName())) {
                return true;
            }
            String pathParam = uriInfo.getPathParameters().getFirst("username");
            if ((pathParam != null) &&
                    this.principal.getName().endsWith(pathParam)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSecure() {
        return "https".equals(uriInfo.getRequestUri().getScheme());
    }

    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }

    public Credentials getUser(){
        return creds;
    }

    public UriInfo getUriInfo(){
        return uriInfo;
    }
}