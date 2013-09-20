package com.jug6ernaut.network.authenticator.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.push.PushWebService;
import com.jug6ernaut.network.shared.web.transitory.EncryptedEntity;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/12/13
 * Time: 10:04 PM
 */
public class PushManager {
    private static PushManager pushManager;
    private static PushWebService pushWebService;
    private static Logger logger = Logger.getLogger(PushManager.class);
    private Context context;

    private PushManager(final Context context, String url, OkClient client) {
        this.context = context;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String s) {
                        logger.debug(s);
                    }
                })
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        if (DataServices.get().getUser() != null)
                            requestFacade.addHeader(
                                    "Authorization",
                                    "CUSTOM " + DataServices.get().getUser().getAuthToken());
                    }
                })
                .setServer(url)
                .build();

        pushWebService = restAdapter.create(PushWebService.class);
    }

    static PushManager get(){
        if(pushManager == null){
            throw new RuntimeException("PushManager must be initialized first!");
        }
        else return pushManager;
    }

    public static PushManager init(Context context, String url, OkClient client) {
        if (pushManager == null){
            pushManager = new PushManager(context,url,client);
        }
        else {
            logger.error("PushManager already initialized");
        }
        return pushManager;
    }

    public void setLoginListener(AuthManager authManager){
        authManager.addLoginListener(loginListener);
    }

    private AuthManager.LoginListener loginListener = new AuthManager.LoginListener() {
        @Override
        public void onLogin(BackendUser user) {
            register4Push();
        }
    };

    public boolean register(String token){
        try {
            EncryptedEntity entity = new EncryptedEntity();
            entity.setCipherText(token,Backend.get().getAuthManager().getServerKey() );

            pushWebService.register(entity);
        } catch (Exception e) {
            logger.error("register failed",e);
        }
        return false;
    }

    public boolean unregister(String token){
        try{
            pushWebService.unregister();
            return true;
        }catch (RetrofitError error){
            logger.error("Failed to unregister",error);
            return false;
        }
    }

    private void register4Push(){
        GCMRegistrar.checkDevice(context);
        GCMRegistrar.checkManifest(context);
        final String regId = GCMRegistrar.getRegistrationId(context);
        if (regId.equals("")) {
            logger.info("Registering");
            GCMRegistrar.setRegisteredOnServer(context, true);
            GCMRegistrar.register(context, Backend.SENDER_ID);
        } else {
            logger.info("Push already registered: " + regId);
        }
    }

    public static class PushReceiver extends GCMBaseIntentService {
        Logger logger = Logger.getLogger(PushReceiver.class);

        public PushReceiver() {
            super(PushReceiver.class.getSimpleName());
        }

        @Override
        protected void onMessage(Context context, Intent intent) {
            logger.debug("onMessage(): " + intent);
            Bundle extras = intent.getExtras();
            Set<String> keys = extras.keySet();
            for (String key : keys){
                logger.info(key + ": " + extras.get(key));
            }

        }

        @Override
        protected void onError(Context context, String s) {
            logger.debug("onError(): " + s);
        }

        @Override
        protected void onRegistered(Context context, String s) {
            logger.debug("onRegistered(): " + s);
            pushManager.register(s);
        }

        @Override
        protected void onUnregistered(Context context, String s) {
            logger.debug("onUnregistered(): " + s);
            pushManager.unregister(s);
        }
    }

}
