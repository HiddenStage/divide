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

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.divide.client.auth.MockAuthManager;
import io.divide.client.auth.MockKeyManager;
import io.divide.client.data.MockDataManager;
import io.divide.shared.server.DAO;
import io.divide.shared.server.KeyManager;
import io.divide.shared.transitory.TransientObject;

import java.security.NoSuchAlgorithmException;

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