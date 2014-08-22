/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.server.auth;

import io.divide.shared.transitory.Credentials;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.security.Principal;

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