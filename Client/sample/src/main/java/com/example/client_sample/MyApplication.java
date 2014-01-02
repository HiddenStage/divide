package com.example.client_sample;

import android.app.Application;
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
    private static final String remoteUrl = "http://authenticator-test.appspot.com/api";
    private static final String localUrl = "http://williams-mbp:8888/api";
//    private static final String localUrl = "http://192.168.1.12:8888/api";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static String url = (DEBUG ? localUrl : remoteUrl);
    private SharedPreferences prefs;
    private static String URL_KEY = "url_key";
    private Handler handler = new Handler();
    DeviceWake dw;

    @Override
    public void onCreate(){
        prefs = this.getSharedPreferences("prefs",0);
        ALogger.init(this, "Backend", true);
        logger = Logger.getLogger(MyApplication.class);

        if(prefs.contains(URL_KEY)){
            url = prefs.getString(URL_KEY,url);
        }
        initBackend(url);

        BackendServices.addPushListener(listener);

        dw = new DeviceWake(this);
        dw.attain().setTimeout(1000*60);
    }

    @Override
    public void onTerminate() {
        dw.release();
    }

    public void initBackend(String url){
        prefs.edit().putString(URL_KEY,url).commit();
        Backend.init(this,url);
        //backend.registerPush("171321841613");
    }

    public String switchUrl(){
        if(url.equals(localUrl)){
            url = remoteUrl;
        } else {
            url = localUrl;
        }
        prefs.edit().putString(URL_KEY,url).commit();

        return getUrl();
    }

    public String getUrl(){
        return url;
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
