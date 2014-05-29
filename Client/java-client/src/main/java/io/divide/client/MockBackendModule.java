package io.divide.client;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.divide.client.debug.MockLocalStorage;
import io.divide.client.debug.auth.MockAuthManager;
import io.divide.client.debug.auth.MockKeyManager;
import io.divide.client.debug.data.MockDataManager;
import io.divide.shared.server.DAO;
import io.divide.shared.server.KeyManager;
import io.divide.shared.web.transitory.TransientObject;

import java.security.NoSuchAlgorithmException;

/**
 * Created by williamwebb on 4/2/14.
 */
class MockBackendModule extends BackendModule {

    @Override
    public void init(Config config){
        super.init(config);
        setAuthManagerClass(MockAuthManager.class);
        setDataManagerClass(MockDataManager.class);
    }

    @Override
    public void additionalConfig(){
        // ORDER MATTER
        try {
            bind(KeyManager.class).toInstance(new MockKeyManager("someKey"));
        }
        catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }

        bind(new TypeLiteral<DAO<TransientObject, TransientObject>>(){})
                .to(new TypeLiteral<MockLocalStorage<TransientObject, TransientObject>>() { }).in(Singleton.class);
    }

}