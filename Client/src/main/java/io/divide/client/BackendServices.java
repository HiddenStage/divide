package io.divide.client;

import io.divide.client.auth.AuthManager;
import io.divide.client.auth.LoginListener;
import io.divide.client.cache.LocalStorage;
import io.divide.client.data.DataManager;
import io.divide.client.data.ObjectManager;
import io.divide.client.push.PushListener;
import io.divide.client.push.PushManager;

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
        init();
        return dataService.objectManager.remote();
    }

    public static LocalStorage local(){
        init();
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
