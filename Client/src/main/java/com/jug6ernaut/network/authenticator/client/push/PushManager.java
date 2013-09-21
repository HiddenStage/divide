package com.jug6ernaut.network.authenticator.client.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.AbstractWebManager;
import com.jug6ernaut.network.authenticator.client.Backend;
import com.jug6ernaut.network.authenticator.client.BackendUser;
import com.jug6ernaut.network.authenticator.client.DataServices;
import com.jug6ernaut.network.authenticator.client.auth.AuthManager;
import com.jug6ernaut.network.shared.web.transitory.EncryptedEntity;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/12/13
 * Time: 10:04 PM
 */
public class PushManager extends AbstractWebManager<PushWebService> {
    private static Logger logger = Logger.getLogger(PushManager.class);
    private Backend backend;
    private static PushManager pushManager;

    public PushManager(Backend backend) {
        super(backend);
        this.backend = backend;
        this.pushManager = this;

        enableLoginListener(backend.register4Push);
    }

    @Override
    public void setup(RestAdapter.Builder builder){
        builder.setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade requestFacade) {
                if (DataServices.get().getUser() != null)
                    requestFacade.addHeader(
                            "Authorization",
                            "CUSTOM " + DataServices.get().getUser().getAuthToken());
            }
        });
    }

    @Override
    protected Class<PushWebService> getType() {
        return PushWebService.class;
    }

    public void enableLoginListener(boolean enable){
        if(enable)
            backend.getAuthManager().addLoginListener(loginListener);
        else
            backend.getAuthManager().removeLoginListener(loginListener);
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
            entity.setCipherText(token,backend.getAuthManager().getServerKey() );

            getWebService().register(entity);
        } catch (Exception e) {
            logger.error("register failed",e);
        }
        return false;
    }

    public boolean unregister(String token){
        try{
            getWebService().unregister();
            return true;
        }catch (RetrofitError error){
            logger.error("Failed to unregister",error);
            return false;
        }
    }

    private void register4Push(){
        Context context = backend.app;
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
            if(pushManager!=null)
                pushManager.register(s);
        }

        @Override
        protected void onUnregistered(Context context, String s) {
            logger.debug("onUnregistered(): " + s);
            if(pushManager!=null)
                pushManager.unregister(s);
        }
    }

}
