package io.divide.client.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import io.divide.client.Backend;
import io.divide.client.BackendUser;
import io.divide.client.ClientTest;
import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import io.divide.shared.util.ReflectionUtils;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;

/**
 * Created by williamwebb on 2/27/14.
 */
public class AuthManagerTest extends ClientTest {

    @Mock AccountManager accountManager;
    Backend backend;
    AuthManager authManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        MockitoAnnotations.initMocks(this);

        backend = Backend.init(Robolectric.application, url());
        authManager = backend.getAuthManager();

        AuthUtils utils = AuthUtils.get(Robolectric.application,backend.accountInformation.getAccountType());
        ReflectionUtils.setObjectField(utils, "mAccountManager", accountManager);
        ReflectionUtils.setObjectField(authManager,"authUtils",utils);

        when(accountManager.getAccountsByType(backend.accountInformation.getAccountType())).thenReturn(new Account[]{new Account("email", backend.accountInformation.getAccountType())});

        authManager.logout();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        authManager.logout();
        accountManager = null;
        backend = null;
        authManager = null;
    }

    @Test
    public void testGetStoredAccount() throws Exception {

    }

    @Test
    public void testGetRemoteUserFromToken() throws Exception {
        when(accountManager.getAccountsByType(backend.accountInformation.getAccountType())).thenReturn(new Account[]{new Account("email", backend.accountInformation.getAccountType())});

        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        BackendUser user = response.get();
        assertEquals(user.getUsername(),"name");

        ValidCredentials credentials = authManager.getRemoteUserFromToken(user.getAuthToken());
        assertEquals(credentials.getUsername(),"name");
    }

    @Test
    public void testRecoverFromOneTimeToken() throws Exception {
        when(accountManager.getAccountsByType(backend.accountInformation.getAccountType())).thenReturn(new Account[]{new Account("email", backend.accountInformation.getAccountType())});

        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        BackendUser user = response.get();
        assertEquals(user.getUsername(),"name");

        RecoveryResponse credentials = authManager.recoverFromOneTimeToken(user.getRecoveryToken());
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
        when(accountManager.getAccountsByType(backend.accountInformation.getAccountType())).thenReturn(new Account[]{new Account("email", backend.accountInformation.getAccountType())});

        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        assertEquals(response.get().getUsername(),"name");
    }

    @Test
    public void testSignUpASync() throws Exception {
        when(accountManager.getAccountsByType(backend.accountInformation.getAccountType())).thenReturn(new Account[]{new Account("email", backend.accountInformation.getAccountType())});

        BackendUser user = authManager.signUpASync(new SignUpCredentials("name", "email", "")).toBlockingObservable().single();
        assertEquals(user.getUsername(),"name");
    }

    @Test
    public void testLogin() throws Exception {
        when(accountManager.getAccountsByType(backend.accountInformation.getAccountType())).thenReturn(new Account[]{new Account("email", backend.accountInformation.getAccountType())});
        SignUpCredentials signUpCredentials = new SignUpCredentials("name", "email", "");
        String unEncryptedPW = signUpCredentials.getPassword();
        BackendUser signInUser = authManager.signUp(signUpCredentials).get();

        BackendUser user = authManager.login(new LoginCredentials(signInUser.getEmailAddress(),unEncryptedPW)).get();

        assertEquals(signInUser.getUsername(), user.getUsername());
    }

    @Test
    public void testLoginASync() throws Exception {
        when(accountManager.getAccountsByType(backend.accountInformation.getAccountType())).thenReturn(new Account[]{new Account("email", backend.accountInformation.getAccountType())});
        SignUpCredentials signUpCredentials = new SignUpCredentials("name", "email", "");
        String unEncryptedPW = signUpCredentials.getPassword();
        BackendUser signInUser = authManager.signUp(signUpCredentials).get();

        BackendUser user = authManager.loginASync(new LoginCredentials(signInUser.getEmailAddress(),unEncryptedPW)).toBlockingObservable().single();

        assertEquals(signInUser.getUsername(), user.getUsername());

    }

    @Test
    public void testSendGetUserData() throws Exception {
        when(accountManager.getAccountsByType(backend.accountInformation.getAccountType())).thenReturn(new Account[]{new Account("email", backend.accountInformation.getAccountType())});

        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        BackendUser user = response.get();
        user.put("key","value");
        authManager.sendUserData(user);
        user.logout();

        user = authManager.login(new LoginCredentials("email", "")).get();

        assertEquals("value",user.get(String.class,"key"));
    }

    @Test
    public void testAddLoginListener() throws Exception {

    }
}
