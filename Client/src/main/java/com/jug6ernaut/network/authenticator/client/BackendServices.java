package com.jug6ernaut.network.authenticator.client;

import com.jug6ernaut.network.authenticator.client.auth.AuthManager;
import com.jug6ernaut.network.authenticator.client.auth.LoginListener;
import com.jug6ernaut.network.authenticator.client.cache.LocalStorage;
import com.jug6ernaut.network.authenticator.client.data.DataManager;
import com.jug6ernaut.network.authenticator.client.data.ObjectManager;
import com.jug6ernaut.network.authenticator.client.push.PushListener;
import com.jug6ernaut.network.authenticator.client.push.PushManager;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/29/13
 * Time: 9:42 PM
 */
public class BackendServices {

    private static BackendServices dataService;
    private DataManager dataManager;
    private AuthManager authManager;
    private PushManager pushManager;
    private ObjectManager objectManager;

    private BackendServices(){
        Backend b = Backend.get();
        dataManager = b.getDataManager();
        authManager = b.getAuthManager();
        pushManager = b.getPushManager();

        objectManager = new ObjectManager(b);
    }

    private static void init(){
        if(dataService == null)dataService = new BackendServices();
    }

    public static ObjectManager.RemoteStorage remote(){
        return dataService.objectManager.remote();
    }

    public static LocalStorage local(){
        return dataService.objectManager.local();
    }

    public static void addLoginListener(LoginListener loginListener){
        init();
        dataService.authManager.addLoginListener(loginListener);
    }

    public static void addPushListener(PushListener listener){
        init();
        dataService.pushManager.addPushListener(listener);
    }

    public static void removePushListener(PushListener listener){
        init();
        dataService.pushManager.removePushListener(listener);
    }
}
