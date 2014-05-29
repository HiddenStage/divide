package io.divide.client;

import com.squareup.okhttp.OkHttpClient;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by williamwebb on 5/23/14.
 */
public abstract class Config<BackendType extends Backend> {

    public String fileSavePath;
    public final String serverUrl;
    public long id = System.currentTimeMillis();
    public OkHttpClient client;
    private Scheduler scheduleOn = Schedulers.io();
    private Scheduler observerOn = Schedulers.io();
    private BackendModule backendModule;
    public abstract Class<BackendType> getType();

    public Config(String fileSavePath, String url){
        this(fileSavePath, url, BackendModule.class);
    }

    protected <ModuleType extends BackendModule<BackendType>> Config(String fileSavePath, String url, Class<ModuleType> moduleClass){
        this.fileSavePath = fileSavePath;
        this.serverUrl = url;
        this.client = new OkHttpClient();

        try {
            this.backendModule = createInstance(moduleClass,this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    public void observerOn(Scheduler observerOn){
        this.observerOn = observerOn;
    }

    public void subscriberOn(Scheduler scheduleOn){
        this.scheduleOn = scheduleOn;
    }

    public Scheduler observerOn() {
        return observerOn;
    }

    public Scheduler scheduleOn() {
        return scheduleOn;
    }

    protected final BackendModule getModule(){
        return backendModule;
    }

    private static <B extends BackendModule, C extends Backend> B createInstance(Class<B> type, Config<C> config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        B b = type.newInstance();
        b.init(config);
        return b;
    }
}
