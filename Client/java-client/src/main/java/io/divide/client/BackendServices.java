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
