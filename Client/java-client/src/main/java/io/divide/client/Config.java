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
    private Scheduler subscribeOn = Schedulers.io();
    private Scheduler observeOn = Schedulers.io();
    private BackendModule backendModule;

    /**
     * Returns a concrete class type used to initialize the specific Backend class.
     * @return
     */
    public abstract Class<BackendType> getType();

    /**
     * @param fileSavePath base file storage location to be used by Divide.
     * @param url url used to connect to remote Divide server.
     */
    public Config(String fileSavePath, String url){
        this(fileSavePath, url, BackendModule.class);
    }

    /**
     *
     * @param fileSavePath base file storage location to be used by Divide.
     * @param url url used to connect to remote Divide server.
     * @param moduleClass module class type to be used by Divide.
     */
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

    /**
     * Sets @see scheduler Divide callbacks will run on
     * @param observeOn scheduler to which Divide callbacks will run on
     */
    public Config observeOn(Scheduler observeOn){
        this.observeOn = observeOn;
        return this;
    }

    /**
     * Sets @see schedule Divide operations will run on
     * @param subscribeOn scheduler to which Divide operations will run on
     * @return
     */
    public Config subscribeOn(Scheduler subscribeOn){
        this.subscribeOn = subscribeOn;
        return this;
    }

    public Scheduler observeOn() {
        return observeOn;
    }

    public Scheduler subscribeOn() {
        return subscribeOn;
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
