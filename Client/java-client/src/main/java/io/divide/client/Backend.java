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
    @Inject private BackendConfig config;
    @Inject private AuthManager authManager;
    @Inject private DataManager dataManager;

    @Inject
    protected Backend() {}

    public AuthManager getAuthManager() { return authManager; }

    public DataManager getDataManager() { return dataManager; }

    public BackendConfig getConfig(){
        return config;
    }

    public static void inject(Object o){
        if(!initialized) throw new RuntimeException("Backend must be initialized!");
        injector.injectMembers(o);
    }

    public static synchronized Backend  init(BackendConfig config) {
        if(initialized) throw new RuntimeException("Backend already initialized!");

        if(!config.isIsMockMode())
            Guice.createInjector(new BackendModule(config));
        else
            Guice.createInjector(new MockBackendModule(config));

        return injector.getInstance(Backend.class);
    }

    protected Injector getInjector(BackendModule module){
        return Guice.createInjector(module);
    }

}
