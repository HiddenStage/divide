package io.divide.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import com.jug6ernaut.android.logging.ALogger;
import com.jug6ernaut.android.logging.Logger;
import io.divide.client.auth.AccountInformation;
import io.divide.client.auth.AuthManager;
import io.divide.client.auth.AuthUtils;
import io.divide.shared.util.ReflectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import static org.mockito.Mockito.when;

/**
 * Created by williamwebb on 3/14/14.
 */
@RunWith(RobolectricTestRunner.class)
public abstract class ClientTest {

    @MockitoAnnotations.Mock
    protected AccountManager accountManager;
    protected Backend backend;
    protected AccountInformation accountInfo;
    protected AuthManager authManager;

    @Before
    public void setUp() throws Exception {
//        helper.setUp(baseUrl);
        MockitoAnnotations.initMocks(this);
        BackendConfig.setIsMockMode(true);

        ShadowLog.stream = System.out;
        Logger.FORCE_LOGGING = true;
        ALogger.init(Robolectric.application, "dummy", true);

        backend = Backend.init(Robolectric.application, url());
        authManager = backend.getAuthManager();
        accountInfo = backend.getConfig().accountInformation;

        AuthUtils utils = AuthUtils.get(Robolectric.application,accountInfo.getAccountType());
        ReflectionUtils.setObjectField(utils, "mAccountManager", accountManager);
        ReflectionUtils.setObjectField(authManager,"authUtils",utils);

        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});

        authManager.logout();
    }

    @After
    public void tearDown() throws Exception {
        authManager.logout();
        accountManager = null;
        backend = null;
        authManager = null;
    }

    public String url(){
        return "mock";
    }
}
