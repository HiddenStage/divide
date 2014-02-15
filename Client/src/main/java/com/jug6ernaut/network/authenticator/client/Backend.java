package com.jug6ernaut.network.authenticator.client;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.android.utilites.ReflectionUtils;
import com.jug6ernaut.network.authenticator.client.auth.AccountInformation;
import com.jug6ernaut.network.authenticator.client.auth.AuthManager;
import com.jug6ernaut.network.authenticator.client.data.DataManager;
import com.jug6ernaut.network.authenticator.client.push.PushManager;
import com.jug6ernaut.network.authenticator.client.security.PRNGFixes;
import com.squareup.okhttp.OkHttpClient;

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
    protected static OkHttpClient client = new OkHttpClient();
    static {
//        client.set
    }
    public Application app;
    public String serverUrl;
    public AccountInformation accountInformation;

    static {
        PRNGFixes.apply();
    }

    private Backend(final Application application, final String serverUrl) {
        this.app = application;
        this.serverUrl = serverUrl;
        this.accountInformation = new AccountInformation(application);
//        checkManifest(application);
        checkSetup(application);

        authManager = new AuthManager(this);
        dataManager = new DataManager(this);
        pushManager = new PushManager(this);
    }

    public void registerPush(String senderId){
        pushManager.setEnablePush(true, senderId);
    }

    public static Backend init(final Application context, final String serverUrl) {
        logger.info("Connecting to: " + serverUrl);
        backend = new Backend(context,serverUrl);
        return backend;
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

    private static void checkSetup(Context context){
        try{
            int accountTypeId = ReflectionUtils.getFinalStatic(Class.forName(context.getPackageName() + ".R$string"),"accountType",Integer.class);
            String accountType = context.getResources().getString(accountTypeId);
            if(!context.getPackageName().equals(accountType)) throw new ConfigurationException("Missing or invalid R.string.accountType. Should match packageName");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    Class c = Class.forName("com.startpage.mobile.R$string");
    Field field = c.getDeclaredField("text_" + num);
    int resId = (int)field.get(null);
     */

    private static void checkManifest(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String permissionName = "com.jug6ernaut.network.authenticator.client.auth.AuthService";
        String metaDataName = "android.accounts.AccountAuthenticator";
        // check permission
        try {
            packageManager.getPermissionInfo(permissionName,
                    PackageManager.GET_SERVICES);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException(
                    "Application does not define permission " + permissionName);
        }
        // check receivers
        PackageInfo receiversInfo;
        try {
            receiversInfo = packageManager.getPackageInfo(
                    metaDataName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException(
                    "Could not get metadata for package " + packageName);
        }
    }

    public static final class ConfigurationException extends RuntimeException{

        public ConfigurationException(String s) {
            super(s);
        }
    }
}
