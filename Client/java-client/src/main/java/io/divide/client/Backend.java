/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.client;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.divide.client.auth.AuthManager;
import io.divide.client.data.DataManager;
import io.divide.shared.logging.Logger;

public class Backend {

    private static Logger logger = Logger.getLogger(Backend.class);
    private static boolean initialized = false;
    @Inject static private Injector injector;
    @Inject private Config config;
    @Inject private AuthManager authManager;
    @Inject private DataManager dataManager;

    @Inject protected Backend() {initialized = true;}

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
//        if(initialized) throw new RuntimeException("Backend already initialized!");
        logger.debug("Initializing... " + config);
        Guice.createInjector(config.getModule());

        return injector.getInstance(config.getModuleType());
    }

    protected Injector getInjector(BackendModule module){
        return Guice.createInjector(module);
    }

}
