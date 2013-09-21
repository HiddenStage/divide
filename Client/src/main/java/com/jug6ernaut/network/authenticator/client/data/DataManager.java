package com.jug6ernaut.network.authenticator.client.data;

import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.AbstractWebManager;
import com.jug6ernaut.network.authenticator.client.Backend;
import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.authenticator.client.DataServices;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import org.apache.http.HttpStatus;
import retrofit.*;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/28/13
 * Time: 9:59 PM
 */
public class DataManager extends AbstractWebManager<DataWebService> {

    private static Logger logger = Logger.getLogger(DataManager.class);

    public DataManager(Backend backend) {
        super(backend);
    }

    @Override
    public void setup(RestAdapter.Builder builder){
        builder.setErrorHandler(new ErrorHandler() {
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
        });
    }

    @Override
    protected Class<DataWebService> getType() {
        return DataWebService.class;
    }

    public void send(Collection<BackendObject> objects){
        getWebService().save(objects);
    }

    public Collection<BackendObject> get(Collection<BackendObject> objects){
        return getWebService().get(objects);
    }

    public Collection<BackendObject> query(Query query){
        return getWebService().query(query);
    }

    public void query(Query query, Callback<Collection<BackendObject>> callback){
        getWebService().query(query,callback);
    }

}
