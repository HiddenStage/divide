package io.divide.client;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.divide.client.auth.MockAuthManager;
import io.divide.client.auth.MockKeyManager;
import io.divide.client.data.MockDataManager;
import io.divide.shared.server.DAO;
import io.divide.shared.server.KeyManager;
import io.divide.shared.transitory.TransientObject;

import java.security.NoSuchAlgorithmException;

/**
 * Created by williamwebb on 4/2/14.
 */
class MockBackendModule extends BackendModule<Backend,Config<Backend>> {

    @Override
    public void init(Config<Backend> config){
        super.init(config);
        setAuthManagerClass(MockAuthManager.class);
        setDataManagerClass(MockDataManager.class);
    }

    @Override
    public void additionalConfig(Config<Backend> config){
        // ORDER MATTER
        try {
            bind(KeyManager.class).toInstance(new MockKeyManager("someKey"));
        }
        catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }

        bind(new TypeLiteral<DAO<TransientObject, TransientObject>>(){})
                .to(new TypeLiteral<MockLocalStorage<TransientObject, TransientObject>>() { }).in(Singleton.class);
    }

}