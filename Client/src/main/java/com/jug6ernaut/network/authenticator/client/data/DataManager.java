package com.jug6ernaut.network.authenticator.client.data;

import com.google.gson.Gson;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.AbstractWebManager;
import com.jug6ernaut.network.authenticator.client.Backend;
import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import org.apache.commons.io.IOUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
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
    protected Class<DataWebService> getType() {
        return DataWebService.class;
    }

    public <B extends BackendObject> void send(Collection<B> objects){
        getWebService().save(objects);
    }

    public <B extends BackendObject> void send(Collection<B> objects, Callback<String> callback){
        getWebService().save(objects,callback);
    }

    public <B extends BackendObject> Collection<B> get(Class<B> type, Collection<String> objects){
        Response response = getWebService().get(objects);
        return processRequest(getArrayType(type),response);
    }

    public <B extends BackendObject> void get(Class<B> type, Collection<String> keys, Callback<Collection<B>> callback){
        getWebService().get(keys, new DataCallback<B>(getArrayType(type), callback));
    }

    public <B extends BackendObject> Collection<B> query(Class<B> type, Query query){
        Response response = getWebService().query(query);
        return processRequest(getArrayType(type),response);
    }

    public <B extends BackendObject> void query(Class<B> type, Query query, Callback<Collection<B>> callback){
        getWebService().query(query,new DataCallback<B>(getArrayType(type),callback));
    }

    private class DataCallback<B extends BackendObject> implements Callback<Response> {
        Callback<Collection<B>> callback;
        Class<B[]> mType;

        public DataCallback(Class<B[]> type, Callback<Collection<B>> callback){
            this.callback = callback;
            mType = type;
        }

        @Override
        public void success(Response response, Response response2) {
            callback.success(processRequest(mType,response),response);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            callback.failure(retrofitError);
        }
    }

    private static Gson gson = new Gson();

    private <B extends TransientObject> Collection<B> processRequest(Class<B[]> type, Response response){
        String body = null;
        try {
            body = IOUtils.toString(response.getBody().in());
        } catch (IOException e) {
            e.printStackTrace();
        }
        B[] t = gson.fromJson(body,type);
        return Arrays.asList(t);
    }

    private <T extends TransientObject> Class<T[]> getArrayType(Class<T> type){
        return (Class<T[]>) Array.newInstance(type, 0).getClass();
    }
}
