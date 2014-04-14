package io.divide.client;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.divide.client.auth.AuthManager;
import io.divide.client.cache.LocalStorageIBoxDb;
import io.divide.client.data.DataManager;
import io.divide.client.data.ObjectManager;
import io.divide.client.push.PushManager;
import io.divide.shared.server.DAO;

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

        bind(new TypeLiteral<DAO<BackendObject, BackendObject>>(){})
                .to(new TypeLiteral<LocalStorageIBoxDb<BackendObject, BackendObject>>() { }).in(Singleton.class);

        bind(AuthManager.class).in(Singleton.class);
        bind(DataManager.class).in(Singleton.class);
        bind(PushManager.class).in(Singleton.class);
        bind(ObjectManager.class).in(Singleton.class);

        requestStaticInjection(Backend.class);
        requestStaticInjection(BackendUser.class);
        requestStaticInjection(BackendServices.class);
    }
}