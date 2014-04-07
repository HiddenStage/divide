package io.divide.client;

import io.divide.client.auth.AuthManager;
import io.divide.client.auth.LoginListener;
import io.divide.client.cache.LocalStorage;
import io.divide.client.data.ObjectManager;
import io.divide.client.push.PushListener;
import io.divide.client.push.PushManager;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/29/13
 * Time: 9:42 PM
 */
public class BackendServices {

    @Inject private static AuthManager authManager;
    @Inject private static PushManager pushManager;
    @Inject private static ObjectManager objectManager;

    private BackendServices(){ }

    public static ObjectManager.RemoteStorage remote(){
        return objectManager.remote();
    }

    public static LocalStorage local(){
        return objectManager.local();
    }

    public static void addLoginListener(LoginListener loginListener){
        authManager.addLoginListener(loginListener);
    }

    public static void addPushListener(PushListener listener){
        pushManager.addPushListener(listener);
    }

    public static void removePushListener(PushListener listener){
        pushManager.removePushListener(listener);
    }
}
