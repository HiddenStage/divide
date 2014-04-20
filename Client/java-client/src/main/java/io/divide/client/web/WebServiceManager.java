//package io.divide.client.web;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import com.jug6ernaut.android.logging.Logger;
//import io.divide.client.BackendConfig;
//import retrofit.RequestInterceptor;
//import retrofit.RestAdapter;
//import rx.Observer;
//import rx.Subscription;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//import rx.subjects.PublishSubject;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CopyOnWriteArrayList;
//
///**
// * Created by williamwebb on 4/9/14.
// */
//public class WebServiceManager {
//
//    private static Logger logger = Logger.getLogger(AbstractWebManager.class);
//    private static Logger retrologger = Logger.getLogger("Retrofit");
//    private static Boolean connectionReceiverRegistered = false;
//    static final Map<Long,AbstractWebManager> webManagers = new HashMap<Long,AbstractWebManager>();
//    protected BackendConfig config;
//    private List<OnRequestInterceptor> requestInterceptors = new CopyOnWriteArrayList<OnRequestInterceptor>();
//    private RestAdapter restAdapter;
//    private Class t;
//
//    private PublishSubject<RequestObject> requestEventPublisher = PublishSubject.create();
//    private PublishSubject<Boolean> connectionEventPublisher = PublishSubject.create();
//
//    public static void add(AbstractWebManager manager){
//
//    }
//
//    private void onRequest(final RequestInterceptor.RequestFacade requestFacade){
//        for(OnRequestInterceptor ori : requestInterceptors) ori.onRequest(requestFacade);
//    }
//
//    public void addConnectionListener(ConnectionListener listener){
//        Subscription subscription = connectionEventPublisher
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(listener.getObserver());
//    }
//
//    public final void addResponseObserver(Observer<RequestObject> observer){
//        Subscription subscription = requestEventPublisher
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);
//    }
//
//    protected void addRequestInterceptor(OnRequestInterceptor requestInterceptor){
//        requestInterceptors.add(requestInterceptor);
//    }
//
//    private BroadcastReceiver CONNECTION_RECIEVER = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
//            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//
//            if (activeNetInfo != null && activeNetInfo.getState() == NetworkInfo.State.CONNECTED) {
//                retrologger.debug("Network " + activeNetInfo.getTypeName() + " connected");
//                connectionEventPublisher.onNext(true);
//            }
//
//            if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
//                retrologger.debug("There's no network connectivity");
//                connectionEventPublisher.onNext(false);
//            }
//        }
//    };
//}
