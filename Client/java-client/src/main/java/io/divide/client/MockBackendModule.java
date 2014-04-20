package io.divide.client;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.divide.client.auth.AccountStorage;
import io.divide.client.auth.AuthManager;
import io.divide.client.auth.credentials.XmlAccoutStorage;
import io.divide.client.data.DataManager;
import io.divide.client.data.ObjectManager;
import io.divide.client.debug.MockLocalStorage;
import io.divide.client.debug.auth.MockAuthManager;
import io.divide.client.debug.auth.MockKeyManager;
import io.divide.client.debug.data.MockDataManager;
import io.divide.shared.file.Storage;
import io.divide.shared.file.XmlStorage;
import io.divide.shared.server.DAO;
import io.divide.shared.server.KeyManager;
import io.divide.shared.web.transitory.TransientObject;

import java.io.File;
import java.security.NoSuchAlgorithmException;

/**
 * Created by williamwebb on 4/2/14.
 */
class MockBackendModule extends AbstractModule {
    private BackendConfig config;

    public MockBackendModule(BackendConfig config){
        this.config = config;
    }

    @Override
    protected void configure() {
        // ORDER MATTER
        bind(BackendConfig.class).toInstance(config);
        bind(Backend.class).in(Singleton.class);
        try {
            bind(KeyManager.class).toInstance(new MockKeyManager("someKey"));
        }
        catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }

        bind(Storage.class).toInstance(new XmlStorage(new File(config.fileSavePath + "storage.xml"),Storage.MODE_WORLD_WRITEABLE));
        bind(AccountStorage.class).toInstance(new XmlAccoutStorage(new File(config.fileSavePath + "accounts.xml")));

        bind(new TypeLiteral<DAO<BackendObject, BackendObject>>(){})
                .to(new TypeLiteral<MockLocalStorage<BackendObject, BackendObject>>() { }).in(Singleton.class);

        bind(new TypeLiteral<DAO<TransientObject, TransientObject>>(){})
                .to(new TypeLiteral<MockLocalStorage<TransientObject, TransientObject>>() { }).in(Singleton.class);
        bind(DataManager.class).to(MockDataManager.class).in(Singleton.class);

        bind(AuthManager.class).to(MockAuthManager.class).in(Singleton.class);
        bind(ObjectManager.class).in(Singleton.class);

        requestStaticInjection(Backend.class);
        requestStaticInjection(BackendUser.class);
        requestStaticInjection(BackendServices.class);
    }
}