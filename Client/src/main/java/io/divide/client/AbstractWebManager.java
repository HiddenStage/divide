package io.divide.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.gson.Gson;
import com.jug6ernaut.android.logging.Logger;
import retrofit.Profiler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.HashMap;
import java.util.Map;

import static retrofit.Profiler.RequestInformation;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/21/13
 * Time: 10:17 AM
 */
public abstract class AbstractWebManager<T> {

    private static Logger logger = Logger.getLogger(AbstractWebManager.class);
    private static Logger retrologger = Logger.getLogger("Retrofit");
    private Boolean connectionReceiverRegistered = false;
    private RestAdapter restAdapter;
    protected BackendConfig config;
    static final Map<Long,AbstractWebManager> webManagers = new HashMap<Long,AbstractWebManager>();
    T t;

    PublishSubject<Boolean> connectionEventPublisher = PublishSubject.create();
    PublishSubject<RequestObject> requestEventPublisher = PublishSubject.create();


    protected AbstractWebManager(BackendConfig config){
        this.config = config;
        initAdapter();
        synchronized (connectionReceiverRegistered){
            if(!connectionReceiverRegistered){
                config.app.registerReceiver(CONNECTION_RECIEVER, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                connectionReceiverRegistered = true;
            }
        }
    }

    public static class RequestObject{
        public final RequestInformation info;
        public final long l;
        public final int i;
        public final Object o;

        private RequestObject(RequestInformation info,long l, int i, Object o){
            this.info = info;
            this.l = l;
            this.i = i;
            this.o = o;
        }
    }

    private void initAdapter(){
        if(!webManagers.containsKey(config.id)){
            logger.debug("Creating new RestAdapter for: " + config.id);
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder.setClient( new OkClient( config.client ) )
                    .setEndpoint(config.serverUrl)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new RestAdapter.Log() {
                        @Override
                        public void log(String s) {
                            retrologger.debug(s);
                        }
                    })
                    .setConverter(new GsonConverter(new Gson()))
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade requestFacade) {
                            onRequest(requestFacade);
                        }
                    })
                    .setProfiler(new Profiler() {
                        @Override
                        public Object beforeCall() {
                            return null;
                        }

                        @Override
                        public void afterCall(RequestInformation requestInformation, long l, int i, Object o) {
                            retrologger.error("afterCall(" + requestInformation.getRelativePath() + ":" + requestInformation.getMethod() + ": " + i + " : " + o);
                            requestEventPublisher.onNext(new RequestObject(requestInformation,l,i,o));
                        }
                    });

            restAdapter = builder.build();
            webManagers.put(config.id,this);
        } else {
            restAdapter = webManagers.get(config.id).restAdapter;
        }

        Class<T> type = getType();
        if(type==null)throw new IllegalStateException("getType can not be null");

        t = restAdapter.create(type);
    }

    public T getWebService(){
        return t;
    }

    public final void addRequestInterceptor(Observer<RequestObject> observer){
        Subscription subscription = requestEventPublisher
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    protected RequestInterceptor.RequestFacade onRequest(RequestInterceptor.RequestFacade requestFacade){
        return requestFacade;
    }

    protected abstract Class<T> getType();

    public static abstract class ConnectionListener{

        public abstract void onEvent(boolean connected);

        protected Observer<Boolean> getObserver(){ return connectionObserver; }

        private Observer<Boolean> connectionObserver = new Observer<Boolean>() {

            @Override public void onCompleted() { }

            @Override public void onError(Throwable throwable) { }

            @Override
            public void onNext(Boolean aBoolean) {
                onEvent(aBoolean);
            }
        };
    }

    public void addConnectionListener(ConnectionListener listener){
        Subscription subscription = connectionEventPublisher
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener.getObserver());
    }

    private BroadcastReceiver CONNECTION_RECIEVER = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetInfo != null && activeNetInfo.getState() == NetworkInfo.State.CONNECTED) {
                retrologger.debug("Network " + activeNetInfo.getTypeName() + " connected");
                connectionEventPublisher.onNext(true);
            }

            if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                retrologger.debug("There's no network connectivity");
                connectionEventPublisher.onNext(false);
            }
        }
    };

}
