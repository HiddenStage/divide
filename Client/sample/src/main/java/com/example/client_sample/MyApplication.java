package com.example.client_sample;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;
import com.jug6ernaut.android.logging.ALogger;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.android.utilites.DeviceWake;
import com.jug6ernaut.network.authenticator.client.Backend;
import com.jug6ernaut.network.authenticator.client.BackendServices;
import com.jug6ernaut.network.authenticator.client.push.PushEvent;
import com.jug6ernaut.network.authenticator.client.push.PushListener;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/30/13
 * Time: 9:43 PM
 */
public class MyApplication extends Application {

    private static Logger logger;
    private SharedPreferences prefs;
    private Handler handler = new Handler();
    DeviceWake dw;

    @Override
    public void onCreate(){
        prefs = this.getSharedPreferences(Settings.PREFERENCE_NAME, Context.MODE_PRIVATE);
        ALogger.init(this, "Backend", true);
        logger = Logger.getLogger(MyApplication.class);

        Backend b = Backend.init(this,getUrl(prefs));
//        b.registerPush("");
        BackendServices.addPushListener(listener);

        dw = new DeviceWake(this);
        dw.attain().setTimeout(1000*60);
    }

    @Override
    public void onTerminate() {
        dw.release();
    }

    public void reinitialize(){
        Backend.init(this,getUrl(prefs));
    }

    public SharedPreferences getSharedPreferences(){
        return prefs;
    }

    public String getUrl(SharedPreferences preferences){
        boolean isDebug = preferences.getBoolean(getString(R.string.isDebugKey), false);
        if(isDebug){
            return preferences.getString(getString(R.string.debugUrlKey),getString(R.string.debugUrl));
        } else {
            return getString(R.string.remoteUrl);
        }
    }

    private PushListener listener = new PushListener() {

        @Override
        public void onEvent(final PushEvent event) {
            logger.debug("Push Message: " + event);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyApplication.this, "Push Message: " + event, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
}
