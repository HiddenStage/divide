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
import io.divide.shared.server.DAO;

import java.io.File;

/**
 * Created by williamwebb on 4/2/14.
 */
class BackendModule<Type> extends AbstractModule {

    private Class<?> authManagerClass = AuthManager.class;
    private Class<?> dataManagerClass = DataManager.class;
    private Class<?> objectManagerClass = ObjectManager.class;

    protected Config config;

    protected BackendModule(){ }

    public void init(Config config){ this.config = config; }

    @Override
    protected final void configure() {
        // ORDER MATTER
        bind(Config.class).toInstance(config);
        bind(Backend.class).in(Singleton.class);

        bind(Storage.class).toInstance(new XmlStorage(new File(config.fileSavePath + "storage.xml"), Storage.MODE_WORLD_WRITEABLE));
        bind(AccountStorage.class).toInstance(new XmlAccoutStorage(new File(config.fileSavePath + "accounts.xml")));

        bind(new TypeLiteral<DAO<BackendObject, BackendObject>>() {})
            .to(new TypeLiteral<LocalStorageIBoxDb<BackendObject, BackendObject>>() { }).in(Singleton.class);

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

        additionalConfig();
    }

    public void additionalConfig(){}


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
}