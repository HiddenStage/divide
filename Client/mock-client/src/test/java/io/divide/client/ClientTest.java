package io.divide.client;

import io.divide.shared.logging.Logger;
import io.divide.client.auth.AuthManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockitoAnnotations;

/**
 * Created by williamwebb on 3/14/14.
 */
public abstract class ClientTest {

    @MockitoAnnotations.Mock
    protected Backend backend;
    protected AuthManager authManager;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
//        helper.setUp(baseUrl);
        MockitoAnnotations.initMocks(this);
        Logger.FORCE_LOGGING = true;

        BackendConfig config = new BackendConfig(folder.getRoot().getPath(),url(), MockBackendModule.class);

        backend = Backend.init(config);
        authManager = backend.getAuthManager();

//        ReflectionUtils.setObjectField(utils, "mAccountManager", accountManager);
//        ReflectionUtils.setObjectField(authManager,"authUtils",utils);

//        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});

        authManager.logout();
    }

    @After
    public void tearDown() throws Exception {
        authManager.logout();
//        accountManager = null;
        backend = null;
        authManager = null;
//        folder.delete();
//        FileUtils.deleteDirectory(folder.getRoot());
//        FileUtils.forceDelete(folder.getRoot());
    }

    public String url(){
        return "io/divide/client/mock";
    }
}
