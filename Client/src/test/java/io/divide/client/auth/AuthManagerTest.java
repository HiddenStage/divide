package io.divide.client.auth;

import android.accounts.Account;
import io.divide.client.BackendUser;
import io.divide.client.ClientTest;
import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by williamwebb on 2/27/14.
 */
public class AuthManagerTest extends ClientTest {

    @Test
    public void testGetStoredAccount() throws Exception {

    }

    @Test
    public void testGetRemoteUserFromToken() throws Exception {
        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});

        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        BackendUser user = response.get();
        assertEquals(user.getUsername(),"name");

        ValidCredentials credentials = authManager.getRemoteUserFromToken(user.getAuthToken()).toBlockingObservable().first();
        assertEquals(credentials.getUsername(),"name");
    }

    @Test
    public void testRecoverFromOneTimeToken() throws Exception {
        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});

        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        BackendUser user = response.get();
        assertEquals(user.getUsername(),"name");

        RecoveryResponse credentials = authManager.recoverFromOneTimeToken(user.getRecoveryToken());
        assertTrue(credentials.getError(),credentials.getStatus().isSuccess());
        assertFalse(credentials.getStatus().isServerError());
        assertEquals(credentials.get().getUsername(),"name");
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
        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});

        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        assertEquals(response.get().getUsername(),"name");
    }

    @Test
    public void testSignUpASync() throws Exception {
        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});

        BackendUser user = authManager.signUpASync(new SignUpCredentials("name", "email", "")).toBlockingObservable().first();
        assertEquals(user.getUsername(),"name");
    }

    @Test
    public void testLogin() throws Exception {
        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});
        SignUpCredentials signUpCredentials = new SignUpCredentials("name", "email", "");
        String unEncryptedPW = signUpCredentials.getPassword();
        BackendUser signInUser = authManager.signUp(signUpCredentials).get();

        BackendUser user = authManager.login(new LoginCredentials(signInUser.getEmailAddress(),unEncryptedPW)).get();

        assertEquals(signInUser.getUsername(), user.getUsername());
    }

    @Test
    public void testLoginASync() throws Exception {
        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});
        SignUpCredentials signUpCredentials = new SignUpCredentials("name", "email", "");
        String unEncryptedPW = signUpCredentials.getPassword();
        BackendUser signInUser = authManager.signUp(signUpCredentials).get();

        BackendUser user = authManager.loginASync(new LoginCredentials(signInUser.getEmailAddress(),unEncryptedPW)).toBlockingObservable().first();

        assertEquals(signInUser.getUsername(), user.getUsername());

    }

    @Test
    public void testSendGetUserData() throws Exception {
        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});

        BackendUser user = authManager.signUp(new SignUpCredentials("name", "email", "11")).get();
        user.put("key", "value");
        System.out.println("BEFORE_USER: " + authManager.getUser());
        System.out.println("BEFORE_ID: " + backend.getConfig().id);
//        user.save();
        authManager.sendUserData(user).toBlockingObservable().first();
//        user.logout();

        user = authManager.login(new LoginCredentials("email", "11")).get();

        assertNotNull(user);
        assertEquals("value",user.get(String.class,"key"));
    }

    @Test
    public void testAddLoginListener() throws Exception {

    }
}
