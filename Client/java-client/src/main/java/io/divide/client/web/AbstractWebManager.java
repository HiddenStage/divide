package io.divide.client.web;

import com.google.gson.Gson;
import com.google.inject.Inject;
import io.divide.client.Config;
import io.divide.shared.logging.Logger;
import retrofit.Profiler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static retrofit.RequestInterceptor.RequestFacade;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/21/13
 * Time: 10:17 AM
 */
public abstract class AbstractWebManager<T> {

    private static Logger logger = Logger.getLogger(AbstractWebManager.class);
    private static Logger retrologger = Logger.getLogger("Retrofit");
    private static Boolean connectionReceiverRegistered = false;
    static final Map<Long,AbstractWebManager> webManagers = new HashMap<Long,AbstractWebManager>();
    protected Config config;
    private List<OnRequestInterceptor> requestInterceptors = new CopyOnWriteArrayList<OnRequestInterceptor>();
    private RestAdapter restAdapter;
    private T t;

    private PublishSubject<RequestObject> requestEventPublisher = PublishSubject.create();
    private PublishSubject<Boolean> connectionEventPublisher = PublishSubject.create();

    protected abstract Class<T> getType();

    @Inject
    protected AbstractWebManager(Config config){
        this.config = config;
        initAdapter(config);

//        synchronized (connectionReceiverRegistered){
//            if(!connectionReceiverRegistered){
//                config.app.registerReceiver(CONNECTION_RECIEVER, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//                connectionReceiverRegistered = true;
//            }
//        }
    }

    protected void initAdapter(Config config){
        Class<T> type = getType();
        if(type==null)throw new IllegalStateException("getType can not be null");

        if(!webManagers.containsKey(config.id)){
            logger.debug("Creating new RestAdapter for: " + config.id);

            restAdapter = createRestAdapter(config);
            webManagers.put(config.id,this);
        } else {
            restAdapter = webManagers.get(config.id).restAdapter;
        }

        t = restAdapter.create(type);
    }

    public T getWebService(){
        return t;
    }

    private RestAdapter createRestAdapter(Config config){
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

        return builder.build();
    }

    private void onRequest(final RequestFacade requestFacade){
        for(OnRequestInterceptor ori : requestInterceptors) ori.onRequest(requestFacade);
    }

    public void addConnectionListener(ConnectionListener listener){
        Subscription subscription = connectionEventPublisher
                .subscribeOn(Schedulers.io())
                .subscribe(listener.getObserver());
    }

    public final void addResponseObserver(Observer<RequestObject> observer){
        Subscription subscription = requestEventPublisher
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    protected void addRequestInterceptor(OnRequestInterceptor requestInterceptor){
        requestInterceptors.add(requestInterceptor);
    }



}
