package com.jug6ernaut.network.authenticator.client;

import android.app.Application;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.auth.AuthManager;
import com.jug6ernaut.network.authenticator.client.data.DataManager;
import com.jug6ernaut.network.authenticator.client.push.PushManager;
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
    protected OkClient client = new OkClient();
    public Application app;
    public String serverUrl;
    public boolean register4Push;

    public static final String SENDER_ID = "171321841613";

    static {
        PRNGFixes.apply();
    }

    private Backend(final Application application, final String serverUrl,boolean register4Push) {
        this.app = application;
        this.serverUrl = serverUrl;
        this.register4Push = register4Push;

        authManager = new AuthManager(this);
        dataManager = new DataManager(this);
        pushManager = new PushManager(this);
    }

    public static void init(final Application context, final String serverUrl,boolean register4Push) {
        logger.info("Connecting to: " + serverUrl);
        backend = new Backend(context,serverUrl,register4Push);
    }

    protected static Backend get() {
        isInit();
        return backend;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public PushManager getPushManager() {
        return pushManager;
    }

    private static void isInit(){
        if (backend == null) throw new RuntimeException(Backend.class.getSimpleName() + ".init() Must be called first!");
    }
}
