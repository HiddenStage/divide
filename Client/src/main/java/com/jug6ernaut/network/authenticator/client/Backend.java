package com.jug6ernaut.network.authenticator.client;

import android.app.Application;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.security.PRNGFixes;
import retrofit.client.OkClient;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/25/13
 * Time: 7:08 PM
 */
public class Backend {

    private static final Logger logger = Logger.getLogger(Backend.class);
    private static Backend backend;
    private AuthManager authManager;
    private DataManager dataManager;
    private PushManager pushManager;
    private OkClient client = new OkClient();
    private Application app;

    public static final String SENDER_ID = "171321841613";

    static {
        PRNGFixes.apply();
    }

    private Backend(final Application context, final String serverUrl,boolean register4Push) {
        authManager = AuthManager.init(context, serverUrl, client);
        dataManager = DataManager.init(context, serverUrl, client);
        pushManager = PushManager.init(context, serverUrl, client);
        app = context;
        if(register4Push)pushManager.setLoginListener(authManager);
    }

    public static void init(final Application context, final String serverUrl,boolean register4Push) {
        logger.info("Connecting to: " + serverUrl);
        backend = new Backend(context,serverUrl,register4Push);
    }

    protected static Backend get() {
        isInit();
        return backend;
    }

    protected AuthManager getAuthManager() {
        return authManager;
    }

    protected DataManager getDataManager() {
        return dataManager;
    }

    protected PushManager getPushManager() {
        return pushManager;
    }

    private static void isInit(){
        if (backend == null) throw new RuntimeException(Backend.class.getSimpleName() + ".init() Must be called first!");
    }
}
