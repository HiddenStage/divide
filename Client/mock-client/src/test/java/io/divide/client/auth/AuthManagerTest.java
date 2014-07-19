package io.divide.client.auth;

import io.divide.client.BackendUser;
import io.divide.client.ClientTest;
import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by williamwebb on 2/27/14.
 */
public class AuthManagerTest extends ClientTest {

    @Test
    public void testGetStoredAccount() throws Exception {

    }

    @Test
    public void testGetRemoteUserFromToken() throws Exception {
        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        BackendUser user = response.get();
        assertEquals(user.getUsername(), "name");

        BackendUser credentials = authManager.getUserFromAuthToken(user.getAuthToken()).toBlockingObservable().first();
        assertEquals(credentials.getUsername(), "name");
    }

    @Test
    public void testRecoverFromOneTimeToken() throws Exception {

        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        BackendUser user = response.get();
        assertEquals(user.getUsername(), "name");

        BackendUser credentials = authManager.getUserFromRecoveryToken(user.getRecoveryToken()).toBlockingObservable().first();
        assertNotNull(credentials);
        assertEquals(credentials.getUsername(), "name");
    }

    @Test
    public void testGetUser() throws Exception {

    }

    @Test
    public void testLogout() throws Exception {

    }

    @Test
    public void testGetServerKey() throws Exception {
        authManager.getServerKey();
    }

    @Test
    public void testSignUp() throws Exception {
        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        assertEquals(response.get().getUsername(), "name");
    }

    @Test
    public void testSignUpASync() throws Exception {
        BackendUser user = authManager.signUpASync(new SignUpCredentials("name", "email", "")).toBlockingObservable().first();
        assertEquals(user.getUsername(), "name");
    }

    @Test
    public void testLogin() throws Exception {
        SignUpCredentials signUpCredentials = new SignUpCredentials("name", "email", "");
        String unEncryptedPW = signUpCredentials.getPassword();
        BackendUser signInUser = authManager.signUp(signUpCredentials).get();

        BackendUser user = authManager.login(new LoginCredentials(signInUser.getEmailAddress(),unEncryptedPW)).get();

        assertEquals(signInUser.getUsername(), user.getUsername());
    }

    @Test
    public void testLoginASync() throws Exception {
        SignUpCredentials signUpCredentials = new SignUpCredentials("name", "email", "");
        String unEncryptedPW = signUpCredentials.getPassword();
        BackendUser signInUser = authManager.signUp(signUpCredentials).get();

        BackendUser user = authManager.loginASync(new LoginCredentials(signInUser.getEmailAddress(),unEncryptedPW)).toBlockingObservable().first();

        assertEquals(signInUser.getUsername(), user.getUsername());

    }

    @Test
    public void testSendGetUserData() throws Exception {
        BackendUser user = authManager.signUp(new SignUpCredentials("name", "email", "11")).get();
        user.put("key", "value");
        System.out.println("BEFORE_USER: " + authManager.getUser());
        System.out.println("BEFORE_ID: " + backend.getConfig().id);
//        user.save();
        authManager.sendUserData(user).toBlockingObservable().first();
//        user.logout();

        user = authManager.login(new LoginCredentials("email", "11")).get();

        assertNotNull(user);
        assertEquals("value", user.get(String.class, "key"));
    }

    @Test
    public void testLoginListener() throws Exception {

    }
}
