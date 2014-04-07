package io.divide.client;

import android.app.Application;
import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.jug6ernaut.android.logging.Logger;
import io.divide.client.auth.AuthManager;
import io.divide.client.data.DataManager;
import io.divide.client.push.PushManager;
import io.divide.client.security.PRNGFixes;
import io.divide.shared.util.ReflectionUtils;
import roboguice.RoboGuice;


/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/25/13
 * Time: 7:08 PM
 */
public class Backend {

    private static final Logger logger = Logger.getLogger(Backend.class);
    private static boolean initialized = false;
    private BackendConfig config;
    @Inject static public Application app;
    @Inject static private Injector injector;
    @Inject private AuthManager authManager;
    @Inject private DataManager dataManager;
    @Inject private PushManager pushManager;

    static { PRNGFixes.apply(); }

    @Inject
    protected Backend(BackendConfig config) {
        this.config = config;
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

    public BackendConfig getConfig(){
        return config;
    }

    public static void inject(Object o){
        injector.injectMembers(o);
    }

    public static synchronized Backend init(final Application context, final String serverUrl) {

        RoboGuice.setBaseApplicationInjector(
                context,
                RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(context),
                new BackendModule(new BackendConfig(context, serverUrl, System.currentTimeMillis())));

        return injector.getInstance(Backend.class);
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

    public static final class ConfigurationException extends RuntimeException{

        public ConfigurationException(String s) {
            super(s);
        }
    }
}
