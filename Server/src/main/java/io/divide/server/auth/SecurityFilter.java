/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://jersey.dev.java.net/CDDL+GPL.html
 * or jersey/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at jersey/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package io.divide.server.auth;

import io.divide.dao.ServerDAO;
import io.divide.server.dao.DAOManager;
import io.divide.server.dao.ServerCredentials;
import io.divide.server.dao.Session;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.OPERAND;
import io.divide.shared.transitory.query.Query;
import io.divide.shared.transitory.query.QueryBuilder;
import io.divide.shared.util.AuthTokenUtils;
import io.divide.shared.util.ObjectUtils;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.logging.Logger;

import static io.divide.server.utils.ResponseUtils.notAuthReponse;

public class SecurityFilter implements ContainerRequestFilter {

    Logger log = Logger.getLogger(SecurityFilter.class.getName());

    @Context DAOManager dao;

    @Context
    SecManager securityManager;

    public SecurityFilter() {}

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        log.info("Filter(): " + request.getUriInfo().getPath());

        String path = request.getUriInfo().getPath();
        if(!path.startsWith("/auth/user/data") && !path.startsWith("/auth/user/data/"))
        if (
           path.startsWith("auth")
        || path.startsWith("/auth")
        || securityManager.getSafePaths().contains(path)
           ) {
            log.info("Auth Skipped : (" + path +")");
            return;
        }

        UserContext context = authenticate(request);
        if (context != null) {
            log.info("Authenticated: " + context.getUser().getEmailAddress());
        } else {
            log.info("Authentication Failed");
        }
        request.setProperty(Session.SESSION_KEY,context);
        request.setSecurityContext(context);
    }

    private UserContext authenticate(ContainerRequestContext request) {

        // Extract authentication credentials
        String authentication = request.getHeaderString(ContainerRequest.AUTHORIZATION);
        System.out.println("HeaderCount: " + request.getHeaders().keySet().size());
        System.out.println(request.getHeaders().keySet());
        System.out.println(request.getPropertyNames());
        System.out.println(request.getCookies().keySet());

        if (authentication == null) {
            return abort(request, "Authentication credentials are required");
        }
        if (!authentication.startsWith("CUSTOM ")) {
            return abort(request, "Only CUSTOM authentication is supported: " + authentication);
        }
        authentication = authentication.substring("CUSTOM ".length());
        String token = authentication;

        if (token == null) {
            return abort(request, "Missing token");
        }

        // TODO verify
        try {
            AuthTokenUtils.AuthToken authToken = new AuthTokenUtils.AuthToken(securityManager.getSymmetricKey(),token);
            if(authToken.isExpired()){
                return abort(request,"Auth Token Expired: " + System.currentTimeMillis() + " : " + authToken.expirationDate);
            }
        } catch (AuthTokenUtils.AuthenticationException e) {
            return abort(request,"Auth Token Expired: " + e.getMessage());
        }

        // Validate the extracted credentials
        synchronized (dao) {
            Query q = new QueryBuilder().select().from(Credentials.class).where(Credentials.AUTH_TOKEN_KEY, OPERAND.EQ, token).build();
            try {
                TransientObject temp = ObjectUtils.get1stOrNull(dao.query(q));
                if (temp != null) {
                    ServerCredentials creds = new ServerCredentials(temp);
//                    creds.setAuthToken(AuthTokenUtils.getNewToken(securityManager.getSymmetricKey(),creds)); // assign new token
                    return new UserContext(request.getUriInfo(), new ServerCredentials(creds));
                } else {
//                    System.err.println("IN DB: " + dao.query(new QueryBuilder().select().from(Credentials.class).build()));
                    request.abortWith(notAuthReponse("Invalid authentication token"));
                    return abort(request, "Invalid authentication token");
                }
            } catch (ServerDAO.DAOException e) {
                log.severe("Authentication Failed("+e.getStatusCode()+") " + e.getMessage());
                e.printStackTrace();
                return abort(request, "Invalid authentication token");
            }
        }
    }


    private UserContext abort(ContainerRequestContext request, String message) {
        log.warning("Auth Failed: " + message);
        request.abortWith(notAuthReponse(message));
        return null;
    }

}