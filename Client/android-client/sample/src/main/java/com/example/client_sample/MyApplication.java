package com.example.client_sample;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;
import com.jug6ernaut.android.logging.ALogger;
import com.jug6ernaut.android.logging.Logger;
import io.divide.client.Backend;
import io.divide.client.android.AndroidBackend;
import io.divide.client.android.AndroidConfig;
import io.divide.client.android.push.PushEvent;
import io.divide.client.android.push.PushListener;

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

    @Override
    public void onCreate(){
        prefs = this.getSharedPreferences(Settings.PREFERENCE_NAME, Context.MODE_PRIVATE);
        ALogger.init(this, "Backend", true);
        logger = Logger.getLogger(MyApplication.class);

        AndroidBackend b = Backend.init(new AndroidConfig(this,getUrl(prefs)));
//        BackendServices.addPushListener(listener);
    }

    @Override
    public void onTerminate() {

    }

    public void reinitialize(){
        Backend.init(new AndroidConfig(this,getUrl(prefs)));
    }

    public SharedPreferences getSharedPreferences(){
        return prefs;
    }

    public String getUrl(SharedPreferences preferences){
        boolean isDebug = preferences.getBoolean(getString(R.string.isDebugKey), true);
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
