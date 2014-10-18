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

import io.divide.client.auth.AuthManager;
import io.divide.client.auth.LoginListener;
import io.divide.client.data.ObjectManager;
import io.divide.shared.server.DAO;

import javax.inject.Inject;

public class BackendServices {

    @Inject private static AuthManager authManager;
    @Inject private static ObjectManager objectManager;

    private BackendServices(){ }

    /**
     * @return @see RemoteStorage instance used to interface with the remote server.
     */
    public static ObjectManager.RemoteStorage remote(){
        checkIsInitialized(objectManager);
        return objectManager.remote();
    }

    /**
     * @return LocalStorage instanced used to store and manage @see BackendObject locally.
     */
    public static DAO<BackendObject,BackendObject> local(){
        checkIsInitialized(objectManager);
        return objectManager.local();
    }

    /**
     * Subscribe to login events.
     * @param loginListener
     */
    public static void addLoginListener(LoginListener loginListener){
        checkIsInitialized(authManager);
        authManager.addLoginListener(loginListener);
    }

    private static void checkIsInitialized(Object o){
        if( o == null )
        throw new RuntimeException("Backend not initialized!");
    }
}
