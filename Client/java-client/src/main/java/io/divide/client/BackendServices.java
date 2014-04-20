package io.divide.client;

import io.divide.client.auth.AuthManager;
import io.divide.client.auth.LoginListener;
import io.divide.client.data.ObjectManager;
import io.divide.shared.server.DAO;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/29/13
 * Time: 9:42 PM
 */
public class BackendServices {

    @Inject private static AuthManager authManager;
    @Inject private static ObjectManager objectManager;

    private BackendServices(){ }

    public static ObjectManager.RemoteStorage remote(){
        checkIsInitialized(objectManager);
        return objectManager.remote();
    }

    public static DAO<BackendObject,BackendObject> local(){
        checkIsInitialized(objectManager);
        return objectManager.local();
    }

    public static void addLoginListener(LoginListener loginListener){
        checkIsInitialized(authManager);
        authManager.addLoginListener(loginListener);
    }

    private static void checkIsInitialized(Object o){
        if( o == null )
        throw new RuntimeException("Backend not initialized!");
    }
}
