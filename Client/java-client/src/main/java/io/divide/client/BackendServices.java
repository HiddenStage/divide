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
