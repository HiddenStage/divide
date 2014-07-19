package io.divide.client.android.mock;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.divide.client.MockLocalStorage;
import io.divide.client.android.AndroidConfig;
import io.divide.client.android.AndroidModule;
import io.divide.client.auth.MockAuthManager;
import io.divide.client.auth.MockKeyManager;
import io.divide.client.data.MockDataManager;
import io.divide.shared.server.DAO;
import io.divide.shared.server.KeyManager;
import io.divide.shared.transitory.TransientObject;

import java.security.NoSuchAlgorithmException;

/**
 * Created by williamwebb on 7/17/14.
 */
public class MockAndroidModule extends AndroidModule {

    public MockAndroidModule(){
        setAuthManagerClass(MockAuthManager.class);
        setDataManagerClass(MockDataManager.class);
    }

    @Override
    protected void additionalConfig(AndroidConfig config){
        super.additionalConfig(config);
        // ORDER MATTER
        try {
            bind(KeyManager.class).toInstance(new MockKeyManager("someKey"));
        }
        catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }

        bind(new TypeLiteral<DAO<TransientObject, TransientObject>>(){})
                .to(new TypeLiteral<MockLocalStorage<TransientObject, TransientObject>>() {
                }).in(Singleton.class);
    }

}
