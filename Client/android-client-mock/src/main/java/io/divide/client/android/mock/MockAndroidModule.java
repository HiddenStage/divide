/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
