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
class BackendModule extends AbstractModule {
    private BackendConfig config;

    public BackendModule(BackendConfig config){
        this.config = config;
    }

    @Override
    protected void configure() {
        // ORDER MATTER
        bind(BackendConfig.class).toInstance(config);
        bind(Backend.class).in(Singleton.class);

        bind(Storage.class).toInstance(new XmlStorage(new File(config.fileSavePath + "storage.xml"), Storage.MODE_WORLD_WRITEABLE));
        bind(AccountStorage.class).toInstance(new XmlAccoutStorage(new File(config.fileSavePath + "accounts.xml")));

        bind(new TypeLiteral<DAO<BackendObject, BackendObject>>() {})
            .to(new TypeLiteral<LocalStorageIBoxDb<BackendObject, BackendObject>>() { }).in(Singleton.class);

        bind(AuthManager.class).in(Singleton.class);
        bind(DataManager.class).in(Singleton.class);
        bind(ObjectManager.class).in(Singleton.class);

        requestStaticInjection(Backend.class);
        requestStaticInjection(BackendUser.class);
        requestStaticInjection(BackendServices.class);
    }
}