/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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