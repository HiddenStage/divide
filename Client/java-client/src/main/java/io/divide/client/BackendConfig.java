package io.divide.client;

import com.squareup.okhttp.OkHttpClient;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by williamwebb on 4/4/14.
 */
public class BackendConfig {

    public String fileSavePath;
    public final String serverUrl;
    public long id;
    public OkHttpClient client;
    private boolean isMockMode = false;
    private Scheduler scheduleOn = Schedulers.io();
    private Scheduler observerOn = Schedulers.io();

    public BackendConfig(String fileSavePath, String url){
        this(fileSavePath,url,System.currentTimeMillis());
    }

    public BackendConfig(String fileSavePath, String url, long id){
        this.fileSavePath = fileSavePath;
        this.serverUrl = url;
        this.id = id;
        this.client = new OkHttpClient();
    };

    public boolean isIsMockMode() {
        return isMockMode;
    }

    public void setIsMockMode(boolean isMockMode) {
        this.isMockMode = isMockMode;
    }

    public void observerOn(Scheduler observerOn){
        this.observerOn = observerOn;
    }

    public void subscriberOn(Scheduler scheduleOn){
        this.observerOn = scheduleOn;
    }


    public Scheduler observerOn() {
        return observerOn;
    }
}
