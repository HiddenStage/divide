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

package io.divide.server.endpoints;

import io.divide.server.ServerTest;
import io.divide.server.TestUtils;
import io.divide.shared.util.ObjectUtils;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.EncryptedEntity;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.Query;
import io.divide.shared.transitory.query.QueryBuilder;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.security.PublicKey;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PushEndpointTest extends ServerTest {

    private Response registerToken(Credentials user, PublicKey key, JerseyTest test){
        EncryptedEntity.Writter entity = new EncryptedEntity.Writter(key);
        entity.put("token", "whatwhat");

        Response response = target("/push")
                .request()
                .header(ContainerRequest.AUTHORIZATION, "CUSTOM " + user.getAuthToken())
                .buildPost(TestUtils.toEntity(entity)).invoke();
        int statusCode = response.getStatus();
        assertEquals(200,statusCode);
        return response;
    }

    @Test
    public void testRegister() throws Exception {
        Credentials user = AuthenticationEndpointTest.signUpUser(this);
        PublicKey key = AuthenticationEndpointTest.getPublicKey(this);

        registerToken(user,key,this);

        Collection<TransientObject> list = container.serverDao.query(new QueryBuilder().select().from(Credentials.class).build());
        TransientObject o = ObjectUtils.get1stOrNull(list);
        user = TestUtils.convert(o,Credentials.class);
        assertNotNull(user);
        assertEquals("whatwhat", user.getPushMessagingKey()); // check the token was actually saved
    }
//
    @Test
    public void testUnregister() throws Exception {
        Credentials user = AuthenticationEndpointTest.signUpUser(this);
        PublicKey key = AuthenticationEndpointTest.getPublicKey(this);

        Response tokenResponse =  registerToken(user, key, this);

        String newAuthToken = tokenResponse.getHeaderString("Authorization");

        Response response = target("/push")
                .request()
                .header(ContainerRequest.AUTHORIZATION, "CUSTOM " + newAuthToken)
                .delete();
        int statusCode = response.getStatus();
        assertEquals(200,statusCode);
        Collection<TransientObject> list = container.serverDao.get(Query.safeTable(Credentials.class),user.getObjectKey());
        TransientObject o = ObjectUtils.get1stOrNull(list);
        user = TestUtils.convert(o,Credentials.class);
        assertNotNull(user);
        assertEquals("", user.getPushMessagingKey()); // check the token was actually saved
    }
//
//    @Test
//    public void testPushToDevice() throws Exception {
//         no idea how to test this...
//    }
}
