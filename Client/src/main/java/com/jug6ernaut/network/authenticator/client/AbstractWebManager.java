package com.jug6ernaut.network.authenticator.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.jug6ernaut.android.logging.Logger;
import retrofit.Profiler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/21/13
 * Time: 10:17 AM
 */
public abstract class AbstractWebManager<T> {

//    private static Logger logger = Logger.getLogger(AbstractWebManager.class);
    private static Logger retrologger = Logger.getLogger("Retrofit");
    private static Boolean connectionReceiverRegistered = false;
    private static RestAdapter restAdapter;
    protected Backend backend;
    T t;

    protected AbstractWebManager(Backend backend){
        this.backend = backend;
        initAdapter();
        synchronized (connectionReceiverRegistered){
            if(!connectionReceiverRegistered){
                backend.app.registerReceiver(CONNECTION_RECIEVER, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                connectionReceiverRegistered = true;
            }
        }
    }

    private void initAdapter(){

        if(restAdapter == null){
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder.setClient( new OkClient( backend.client ) )
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new RestAdapter.Log() {
                        @Override
                        public void log(String s) {
                            retrologger.debug(s);
                        }
                    })
                    .setServer(backend.serverUrl)
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
                            retrologger.error("afterCall(" + requestInformation.getRelativePath() + ":"+requestInformation.getMethod() + ": " + i);
                            onError(i);
                        }
                    });
//                    .setErrorHandler(new ErrorHandler() {
//                        @Override
//                        public Throwable handleError(RetrofitError retrofitError) {
//                            return onError(retrofitError.getResponse().getStatus());
//                        }
//                    });
            restAdapter = builder.build();
        }

        Class<T> type = getType();
        if(type==null)throw new IllegalStateException("getType can not be null");

        t = restAdapter.create(type);
    }

    public T getWebService(){
        return t;
    }

    protected int onError(int statusCode){
        return statusCode;
    }

    protected RequestInterceptor.RequestFacade onRequest(RequestInterceptor.RequestFacade requestFacade){
        return requestFacade;
    }

    protected abstract Class<T> getType();

    public static interface ConnectionListner{
        public void onConnectionChange(boolean connected);
    }

    private void fireConnectionChange(final boolean connected){
        for(ConnectionListner listner : listeners){
            listner.onConnectionChange(connected);
        }
    }

    private static final List<ConnectionListner> listeners = new CopyOnWriteArrayList<ConnectionListner>();
    public void addConnectionListener(ConnectionListner listener){
        listeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListner listener){
        listeners.remove(listener);
    }

    private BroadcastReceiver CONNECTION_RECIEVER = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetInfo != null && activeNetInfo.getState() == NetworkInfo.State.CONNECTED) {
                retrologger.debug("Network " + activeNetInfo.getTypeName() + " connected");
                fireConnectionChange(true);
            }

            if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                retrologger.debug("There's no network connectivity");
                fireConnectionChange(false);
            }
        }
    };

}
