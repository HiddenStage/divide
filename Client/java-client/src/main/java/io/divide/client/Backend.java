package io.divide.client;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.divide.client.auth.AuthManager;
import io.divide.client.data.DataManager;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/25/13
 * Time: 7:08 PM
 */
public class Backend {

    private static boolean initialized = false;
    @Inject static private Injector injector;
    @Inject private Config config;
    @Inject private AuthManager authManager;
    @Inject private DataManager dataManager;

    @Inject protected Backend() {}

    /**
     * Returns the AuthManager
     * @return
     */

    public AuthManager getAuthManager() { return authManager; }

    /**
     * Returns the DataManager
     * @return
     */
    public DataManager getDataManager() { return dataManager; }

    /**
     * Returns the config used by Divide.
     * @return
     */
    public Config getConfig(){
        return config;
    }

    /**
     * Used to inject objects managed by divide into an object.
     * @param o object to be injected.
     */
    public static void inject(Object o){
        if(!initialized) throw new RuntimeException("Backend must be initialized!");
        injector.injectMembers(o);
    }

    /**
     * Initialization point for divide. Returns an instance of the Divide object. Only one instance may exist at a time.
     * @param config Configuration information for Divide. @see Config
     * @param <BackendType> Specific type of Backend to return, for extentions on the Backend class.
     * @return
     */

    public static synchronized <BackendType extends Backend> BackendType init(Config<BackendType> config) {
        if(initialized) throw new RuntimeException("Backend already initialized!");

        Guice.createInjector(config.getModule());

        return injector.getInstance(config.getType());
    }

    protected Injector getInjector(BackendModule module){
        return Guice.createInjector(module);
    }

}
