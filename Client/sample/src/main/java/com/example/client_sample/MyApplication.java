package com.example.client_sample;

import android.app.Application;
import android.content.SharedPreferences;
import com.jug6ernaut.android.logging.ALogger;
import com.jug6ernaut.network.authenticator.client.Backend;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/30/13
 * Time: 9:43 PM
 */
public class MyApplication extends Application {

    private static final String remoteUrl = "http://authenticator-test.appspot.com/api";
    private static final String localUrl = "http://williamwebb-mbp:8888/api";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static String url = (DEBUG ? localUrl : remoteUrl);
    private SharedPreferences prefs;
    private static String URL_KEY = "url_key";

    @Override
    public void onCreate(){
        prefs = this.getSharedPreferences("prefs",0);
        ALogger.init(this, "Backend", true);
        if(prefs.contains(URL_KEY)){
            url = prefs.getString(URL_KEY,url);
        }
        initBackend(url);
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//
//                if (NetUtils.ping(url,1000))
//                    Backend.init(MyApplication.this, url);
//                else
//                    Backend.init(MyApplication.this, remoteUrl);
//            }
//        });
    }

    @Override
    public void onTerminate() {

    }

    public void initBackend(String url){
        prefs.edit().putString(URL_KEY,url).commit();
        Backend.init(this,url,true);
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
}
