package com.jug6ernaut.network.authenticator.server.endpoints;

import com.jug6ernaut.network.authenticator.server.ServerTest;
import com.jug6ernaut.network.authenticator.server.TestUtils;
import com.jug6ernaut.network.shared.util.Crypto;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import java.net.URLEncoder;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;

/**
 * Created by williamwebb on 11/16/13.
 */
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

        int statusCode = target("/auth/user/data")
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
