package com.jug6ernaut.network.authenticator.client;

import android.content.Context;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.data.DataWebService;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import org.apache.http.HttpStatus;
import retrofit.*;
import retrofit.Callback;
import retrofit.client.OkClient;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/28/13
 * Time: 9:59 PM
 */
public class DataManager {

    private static DataManager dataManager;
    private static DataWebService dataWebService;
    private static Logger logger = Logger.getLogger(DataManager.class);

    private DataManager(final Context context, String url, OkClient client) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String s) {
                        logger.debug(s);
                    }
                })
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        switch (retrofitError.getResponse().getStatus()) {
                            case HttpStatus.SC_UNAUTHORIZED: {
                                //AccountManager.get(context).invalidateAuthToken(AuthManager.ACCOUNT_TYPE,"");
                            }
                        }
                        return null;
                    }
                })
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        if (DataServices.get().getUser() != null) {
                            requestFacade.addHeader("Authorization", "CUSTOM " + DataServices.get().getUser().getAuthToken());
                        } else {
                            logger.error("no auth key: " + DataServices.get().getUser());
                        }
                    }
                })
                .setServer(url)
                .build();

        dataWebService = restAdapter.create(DataWebService.class);
    }

    static DataManager get(){
        if(dataManager == null){
            throw new RuntimeException("dataManager must be initialized first!");
        }
        else return dataManager;
    }

    public static DataManager init(Context context, String url, OkClient client) {
        if (dataManager == null){
            dataManager = new DataManager(context,url,client);
        }
        else {
            logger.error("dataManager already initialized");
        }
        return dataManager;
    }

    public void send(Collection<BackendObject> objects){
        getDataWebService().save(objects);
    }

    public Collection<BackendObject> get(Collection<BackendObject> objects){
        return getDataWebService().get(objects);
    }

    public Collection<BackendObject> query(Query query){
        return getDataWebService().query(query);
    }

    public void query(Query query, Callback<Collection<BackendObject>> callback){
        getDataWebService().query(query,callback);
    }

    private synchronized DataWebService getDataWebService(){
        return dataWebService;
    }

}
