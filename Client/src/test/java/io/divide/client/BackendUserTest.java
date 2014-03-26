package io.divide.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import io.divide.client.auth.AuthManager;
import io.divide.client.auth.AuthUtils;
import io.divide.shared.util.ReflectionUtils;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.when;

/**
 * Created by williamwebb on 3/19/14.
 */
public class BackendUserTest extends ClientTest{

    @MockitoAnnotations.Mock AccountManager accountManager;
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
    public void testGetAnonymousUser() throws Exception {
        BackendUser.getAnonymousUser();
    }
}
