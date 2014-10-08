/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
