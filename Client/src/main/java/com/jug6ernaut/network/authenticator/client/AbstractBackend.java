package com.jug6ernaut.network.authenticator.client;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.android.utilites.ReflectionUtils;
import com.jug6ernaut.network.authenticator.client.auth.AccountInformation;
import com.jug6ernaut.network.authenticator.client.security.PRNGFixes;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by williamwebb on 3/2/14.
 */
public abstract class AbstractBackend {

    protected static OkHttpClient client = new OkHttpClient();
    public Application app;
    public String serverUrl;
    public AccountInformation accountInformation;

    protected static final Logger logger = Logger.getLogger(AbstractBackend.class);

    static { PRNGFixes.apply(); }

    public AbstractBackend(final Application application, final String serverUrl) {
        this.app = application;
        this.serverUrl = serverUrl;
        this.accountInformation = new AccountInformation(application);
//        checkManifest(application);
        checkSetup(application);
    }

    private static void checkSetup(Context context){
        try{
            int accountTypeId = ReflectionUtils.getFinalStatic(Class.forName(context.getPackageName() + ".R$string"), "accountType", Integer.class);
            String accountType = context.getResources().getString(accountTypeId);
            if(!context.getPackageName().equals(accountType)) throw new ConfigurationException("Missing or invalid R.string.accountType. Should match packageName");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

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
