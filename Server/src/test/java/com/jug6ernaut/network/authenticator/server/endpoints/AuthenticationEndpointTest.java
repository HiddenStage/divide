package com.jug6ernaut.network.authenticator.server.endpoints;

import com.jug6ernaut.network.authenticator.server.AbstractTest;
import com.jug6ernaut.network.authenticator.server.TestUtils;
import com.jug6ernaut.network.shared.util.Crypto;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URLEncoder;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;

/**
 * Created by williamwebb on 11/16/13.
 */
public class AuthenticationEndpointTest extends AbstractTest {

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
        signUpUser();
    }

    public synchronized Credentials signUpUser() throws Exception{
        PublicKey publicKey = getPublicKey(this);
        Credentials signInUser = TestUtils.getTestUser();
        signInUser.encryptPassword(publicKey);
        String user = target("/auth").request().post(TestUtils.toEntity(signInUser), String.class);
        Credentials returnedUser = TestUtils.getGson().fromJson(user,Credentials.class);

        assertEquals(signInUser.getUsername(), returnedUser.getUsername());
        return returnedUser;
    }

    @Test
    public void testUserSignIn() throws Exception {
        // create user
        Credentials user = signUpUser();
        user.setPassword(BCrypt.hashpw("somePassword", BCrypt.gensalt(10)));
        container.dao.save(user);

        user.setPassword("somePassword");
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
        Credentials user = TestUtils.getTestUser();
        container.dao.save(user);
        String token = user.getAuthToken();
        token = URLEncoder.encode(token, "UTF-8");
        int status = target("/auth/from/").path(token).request().buildGet().invoke().getStatus();
        assertEquals(200,status);
    }

    @Test
    public void testRecoverUserFromToken() throws Exception {
        Credentials user = TestUtils.getTestUser();
        container.dao.save(user);
        String token = user.getRecoveryToken();

        token = URLEncoder.encode(token,"ISO-8859-1");
        int status = target("/auth/recover/").path(token).request().buildGet().invoke().getStatus();
        assertEquals(200,status);

    }

    @Test
    public void testGetUserData() throws Exception {
        Credentials user = TestUtils.getTestUser();
        container.dao.save(user);
//        System.out.println(container.dao.query(new QueryBuilder().select().from(Credentials.class).build()));


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
