package io.divide.client.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.inject.Inject;
import com.jug6ernaut.android.logging.Logger;
import io.divide.client.AbstractWebManager;
import io.divide.client.BackendConfig;
import io.divide.client.auth.AuthManager;
import io.divide.client.auth.LoginEvent;
import io.divide.client.auth.LoginListener;
import io.divide.shared.event.EventManager;
import io.divide.shared.web.transitory.EncryptedEntity;
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
    private static PushManager pushManager;
    private static EventManager eventManager = EventManager.get();
    private String senderId;
    @Inject private AuthManager authManager;

    @Inject
    public PushManager(BackendConfig config) {
        super(config);
        this.pushManager = this;
    }

    @Override
    protected Class<PushWebService> getType() {
        return PushWebService.class;
    }

    public void setEnablePush(boolean enable, String senderId){
        if(enable){
            this.senderId = senderId;
            authManager.addLoginListener(loginListener);
        } else {
            if(loginListener != null) loginListener.unsubscribe();
            this.senderId = null;
            if(isRegistered(config.app)){
                unregister();
                GCMRegistrar.unregister(config.app);
            }
        }
        this.senderId = senderId;
    }

    public boolean isRegistered(){
        return isRegistered(config.app);
    }

    private LoginListener loginListener = new LoginListener() {

        @Override
        public void onNext(LoginEvent loginEvent) {
            logger.debug("onLogin: " + loginEvent.user);

            register4Push();
        }
    };

    boolean register(String token){
        try {
            EncryptedEntity.Writter entity = new EncryptedEntity.Writter(authManager.getServerKey());
            entity.put("token",token);
//            entity.setCipherText(token,config.getAuthManager().getServerKey() );

            getWebService().register(entity);
            return true;
        } catch (Exception e) {
            logger.error("register failed",e);
            return false;
        }
    }

    boolean unregister(){
        try{
            getWebService().unregister();
            return true;
        }catch (RetrofitError error){
            logger.error("Failed to unregister",error);
            return false;
        }
    }

    private void register4Push(){
        Context context = config.app;
        GCMRegistrar.checkDevice(context);
        GCMRegistrar.checkManifest(context);
        final String regId = GCMRegistrar.getRegistrationId(context);
        if (regId.equals("")) {
            logger.info("Registering");
            GCMRegistrar.setRegisteredOnServer(context, true);
            GCMRegistrar.register(context, senderId);
        } else {
            logger.info("Push already registered: " + regId);
        }
    }

    private boolean isRegistered(Context context){
        final String regId = GCMRegistrar.getRegistrationId(context);
        return !regId.equals("");
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
            String data = extras.getString("body");

            if(data!=null && data.length()>0 && pushManager!=null){
                logger.info("Firing PushEvent");
                eventManager.fire(new PushEvent(data));
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
                pushManager.unregister();
        }
    }

    public void addPushListener(PushListener listener){
        EventManager.get().register(listener);
    }

    public void removePushListener(PushListener listener){
        EventManager.get().unregister(listener);
    }

}
