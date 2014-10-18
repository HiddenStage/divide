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

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.divide.client.auth.AccountStorage;
import io.divide.client.auth.AuthManager;
import io.divide.client.auth.credentials.XmlAccoutStorage;
import io.divide.client.cache.LocalStorageIBoxDb;
import io.divide.client.data.DataManager;
import io.divide.client.data.ObjectManager;
import io.divide.shared.file.Storage;
import io.divide.shared.file.XmlStorage;
import io.divide.shared.logging.Logger;
import io.divide.shared.server.DAO;

import java.io.File;
import java.util.Objects;

public class BackendModule<BackendType extends Backend,ConfigType extends Config<BackendType>> extends AbstractModule {

    private Logger logger = Logger.getLogger(getClass());
    private Class<?> authManagerClass = AuthManager.class;
    private Class<?> dataManagerClass = DataManager.class;
    private Class<?> objectManagerClass = ObjectManager.class;

    protected ConfigType config;
    protected Class<ConfigType> type;

    /**
     * No args constructor, all implementations/extentions must provide a no args constructor
     */
    protected BackendModule(){ }

    /**
     * Sets @see config, must be set before module can be loaded.
     * @param config
     */
    public void init(ConfigType config){ this.config = config; type = (Class<ConfigType>) config.getClass(); }

    @Override
    protected final void configure() {
        logConfiguration();
        if(config == null) throw new IllegalStateException("Config can not be null");
        // ORDER MATTER
        bind(type).toInstance(config);
        bind(Config.class).toInstance(config);
        bind(Backend.class).in(Singleton.class);

        bind(Storage.class).toInstance(new XmlStorage(new File(config.fileSavePath + "storage.xml"), Storage.MODE_WORLD_WRITEABLE));
        bind(AccountStorage.class).toInstance(new XmlAccoutStorage(new File(config.fileSavePath + "accounts.xml")));

        bind(new TypeLiteral<DAO<BackendObject, BackendObject>>() {})
            .to(new TypeLiteral<LocalStorageIBoxDb<BackendObject, BackendObject>>() { }).in(Singleton.class);

        // ugly but required, doesnt allow binding to itself
        if(AuthManager.class.equals(getAuthManagerClass())){
            bind(AuthManager.class).in(Singleton.class);
        } else {
            bind(AuthManager.class).to(getAuthManagerClass()).in(Singleton.class);
        }

        if(DataManager.class.equals(getDataManagerClass())){
            bind(DataManager.class).in(Singleton.class);
        } else {
            bind(DataManager.class).to(getDataManagerClass()).in(Singleton.class);
        }

        if(ObjectManager.class.equals(getObjectManagerClass())){
            bind(ObjectManager.class).in(Singleton.class);
        } else {
            bind(ObjectManager.class).to(getObjectManagerClass()).in(Singleton.class);
        }

        requestStaticInjection(Backend.class);
        requestStaticInjection(BackendUser.class);
        requestStaticInjection(BackendServices.class);

        additionalConfig(config);
    }

    /**
     * Override to provide additional module injection configuration. Runs after default configuration.
     * @param config
     */
    protected void additionalConfig(ConfigType config){}

    public Class<AuthManager> getAuthManagerClass() {
        return (Class<AuthManager>) authManagerClass;
    }

    public <A extends AuthManager> void setAuthManagerClass(Class<A> authManagerClass) {
        this.authManagerClass = authManagerClass;
    }

    public Class<DataManager> getDataManagerClass() {
        return (Class<DataManager>) dataManagerClass;
    }

    public <D extends DataManager> void setDataManagerClass(Class<D> dataManagerClass) {
        this.dataManagerClass = dataManagerClass;
    }

    public Class<ObjectManager> getObjectManagerClass() {
        return (Class<ObjectManager>) objectManagerClass;
    }

    public <O extends ObjectManager> void setObjectManagerClass(Class<O> objectManagerClass) {
        this.objectManagerClass = objectManagerClass;
    }

    private void Log(Objects o){
        System.out.println("Config: " + o);
    }

    private void logConfiguration(){
        // ORDER MATTER
        logger.debug("Module Configuration");
        logger.debug("Binding: " + type + " to " + config.getClass());
        logger.debug("Binding: " + Config.class + " to " + config.getClass());
        logger.debug("Binding: " + Backend.class + " in " + Singleton.class);
        logger.debug("Binding: " + Storage.class + " to " + XmlStorage.class + " by " + config.fileSavePath + "storage.xml");
        logger.debug("Binding: " + AccountStorage.class + " to " + XmlAccoutStorage.class + " by " + config.fileSavePath + "accounts.xml");

        logger.debug("Binding: " + AuthManager.class + " to " + XmlAccoutStorage.class + " by " + config.fileSavePath + "accounts.xml");
        logger.debug("Binding: " + DataManager.class + " to " + XmlAccoutStorage.class + " by " + config.fileSavePath + "accounts.xml");
        logger.debug("Binding: " + ObjectManager.class + " to " + XmlAccoutStorage.class + " by " + config.fileSavePath + "accounts.xml");

        logger.debug("Binding: " + AuthManager.class + " to " + getAuthManagerClass());
        logger.debug("Binding: " + DataManager.class + " to " + getDataManagerClass());
        logger.debug("Binding: " + ObjectManager.class + " to " + getObjectManagerClass());
    }

//    {
//        Injector injector = Guice.createInjector(myModuleInstance);
//        Map<Key<?>,Binding<?>> map = injector.getBindings();
//        List<Key<?>> keys = new ArrayList<Key<?>>(map.keySet());
//        for(Key<?> key : keys)
//            System.out.println(key.toString() + ": " + map.get(key));
//    }
}