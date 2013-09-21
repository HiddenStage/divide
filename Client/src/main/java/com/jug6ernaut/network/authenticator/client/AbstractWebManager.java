package com.jug6ernaut.network.authenticator.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.jug6ernaut.android.logging.Logger;
import retrofit.RestAdapter;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/21/13
 * Time: 10:17 AM
 */
public abstract class AbstractWebManager<T> {

    private static Logger logger = Logger.getLogger(AbstractWebManager.class);
    private static Logger retrologger = Logger.getLogger("Retrofit");
    private Backend backend;
    T t;

    protected AbstractWebManager(Backend backend){
        this.backend = backend;
        initAdapter();
        backend.app.registerReceiver(CONNECTION_RECIEVER, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void initAdapter(){
        RestAdapter.Builder builder = new RestAdapter.Builder();
        Class<T> type = getType();
        if(type==null)throw new IllegalStateException("getType can not be null");

        builder.setClient(backend.client)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String s) {
                        retrologger.debug(s);
                    }
                })
                .setServer(backend.serverUrl);
        setup(builder);

        t = builder.build().create(type);
    }

    public T getWebService(){
        return t;
    }


    protected void setup(RestAdapter.Builder builder){

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

    private static final ArrayList<ConnectionListner> listeners = new ArrayList<ConnectionListner>();
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
                logger.debug("Network " + activeNetInfo.getTypeName() + " connected");
                fireConnectionChange(true);
            }

            if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                logger.debug("There's no network connectivity");
                fireConnectionChange(false);
            }
        }
    };

}
