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
import io.divide.shared.util.Crypto;
import io.divide.shared.transitory.Credentials;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import java.net.URLEncoder;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;

public class AuthenticationEndpointTest extends ServerTest {

    @Test
    public void testGetPublicKey() throws Exception{
        getPublicKey(this);
    }

    public static synchronized PublicKey getPublicKey(JerseyTest test) throws Exception{
            String publicKeyBytes = test.target("/auth/key").request().get(String.class);
            byte[] bytes = TestUtils.getGson().fromJson(publicKeyBytes,byte[].class);
        return Crypto.pubKeyFromBytes(bytes);
    }

    @Test
    public void testUserSignUp() throws Exception {
        signUpUser(this);
    }

    public static synchronized Credentials signUpUser(JerseyTest test) throws Exception{
        PublicKey publicKey = getPublicKey(test);
        Credentials signInUser = TestUtils.getTestUser();
        signInUser.encryptPassword(publicKey);
        String user = test.target("/auth").request().post(TestUtils.toEntity(signInUser), String.class);
        Credentials returnedUser = TestUtils.getGson().fromJson(user,Credentials.class);

        assertEquals(signInUser.getUsername(), returnedUser.getUsername());
        return returnedUser;
    }

    @Test
    public void testUserSignIn() throws Exception {
        // create user
        Credentials user = signUpUser(this);

        // set password for login attempt
        System.out.println("User1:" + TestUtils.getTestUser());
        user.setPassword(TestUtils.getTestUser().getPassword());
        user.encryptPassword(getPublicKey(this));
        target("/auth").request().put(TestUtils.toEntity(user), String.class);
    }


//    @Test
//    public void testValidateAccount() throws Exception {
//
//    }
//
    @Test
    public void testGetUserFromToken() throws Exception {
        Credentials user = signUpUser(this);
        String token = user.getAuthToken();
        token = URLEncoder.encode(token, "UTF-8");
        int status = target("/auth/from/").path(token).request().buildGet().invoke().getStatus();
        assertEquals(200,status);
    }

    @Test
    public void testRecoverUserFromToken() throws Exception {
        Credentials user = signUpUser(this);
        String token = user.getRecoveryToken();

        token = URLEncoder.encode(token,"ISO-8859-1");
        int status = target("/auth/recover/").path(token).request().buildGet().invoke().getStatus();
        assertEquals(200,status);

    }

    @Test
    public void testGetUserData() throws Exception {
        Credentials user = signUpUser(this);

        int statusCode = target("/auth/user/data/"+user.getOwnerId())
                .request()
                .header(ContainerRequest.AUTHORIZATION, "CUSTOM " + user.getAuthToken())
                .put(TestUtils.toEntity(1)).getStatus();
        assertEquals(200, statusCode);
    }
//
//    @Test
//    public void testResetAccount() throws Exception {
//
//    }
//
//    @Test
//    public void testSendEmail() throws Exception {
//
//    }
//
//    @Test
//    public void testGetUserByEmail() throws Exception {
//
//    }
//
//    @Test
//    public void testGetUserById() throws Exception {
//
//    }
}
