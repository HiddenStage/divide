package io.divide.client;

import com.jug6ernaut.android.logging.ALogger;
import com.jug6ernaut.android.logging.Logger;
import io.divide.server.AuthServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

/**
 * Created by williamwebb on 3/14/14.
 */
@RunWith(RobolectricTestRunner.class)
public abstract class ClientTest {

    private static final String baseUrl = "http://localhost:9999/api";
    static protected final AuthServerHelper helper = new AuthServerHelper();

    /*
        As to avoid conflicts with the Roboletric custom classloader server
        setup needs to be done b4 test loading.
     */

    @BeforeClass
    public static void setUpClass() throws Exception {
        helper.init(baseUrl);
    }

    @AfterClass
    public static void tearDownClass(){
        helper.destroy();
    }

    @Before
    public void setUp() throws Exception {
        helper.setUp(baseUrl);
        ShadowLog.stream = System.out;
        Logger.FORCE_LOGGING = true;
        ALogger.init(Robolectric.application, "dummy", true);
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown(baseUrl);
    }

    public String url(){
        return baseUrl;
    }
}
