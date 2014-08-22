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

package io.divide.client.android.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.inject.Inject;
import io.divide.client.BackendUser;
import io.divide.client.android.AndroidConfig;
import io.divide.client.auth.AuthManager;
import io.divide.client.auth.LoginListener;
import io.divide.client.web.AbstractWebManager;
import io.divide.shared.event.EventManager;
import io.divide.shared.logging.Logger;
import io.divide.shared.transitory.EncryptedEntity;
import retrofit.RetrofitError;

import java.util.Set;

public class PushManager extends AbstractWebManager<PushWebService> {
    private static Logger logger = Logger.getLogger(PushManager.class);
    private static PushManager pushManager;
    private static EventManager eventManager = EventManager.get();
    private String senderId;
    @Inject private AndroidConfig config;
    @Inject private AuthManager authManager;


    @Inject
    public PushManager(AndroidConfig config) {
        super(config);
        pushManager = this;
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
        public void onNext(BackendUser user) {
            logger.debug("onLogin: " + user);

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
