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

package io.divide.server;

import io.divide.server.auth.ResponseFilter;
import io.divide.server.auth.SecurityFilter;
import io.divide.server.dao.CredentialBodyHandler;
import io.divide.server.dao.GsonMessageBodyHandler;
import io.divide.server.endpoints.AuthenticationEndpoint;
import io.divide.server.endpoints.DataEndpoint;
import io.divide.server.endpoints.MetaEndpoint;
import io.divide.server.endpoints.PushEndpoint;
import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AuthApplicationTest extends ServerTest {

    // sanity test
    @Test
    public void sanity() {
        final String version = target("").request().get(String.class);
        assertEquals(MetaEndpoint.VERSION, version);
    }

    @Test
    public void classesRegistered(){
        Set<Class<?>> classes = container.app.getClasses();
        Set<Object> singletons = container.app.getSingletons();

        assertTrue(classes.contains(AuthenticationEndpoint.class));
        assertTrue(classes.contains(DataEndpoint.class));
        assertTrue(classes.contains(PushEndpoint.class));
        assertTrue(classes.contains(MetaEndpoint.class));
        assertTrue(classes.contains(CredentialBodyHandler.class));
        assertTrue(classes.contains(GsonMessageBodyHandler.class));
        assertTrue(classes.contains(SecurityFilter.class));
        assertTrue(classes.contains(ResponseFilter.class));

//        assertTrue(classes.contains(UserContext.class));
//
//        assertTrue(singletons.contains(DAOManager.class));
//        assertTrue(singletons.contains(KeyManager.class));
//        assertTrue(classes.contains(Session.class));
    }


}