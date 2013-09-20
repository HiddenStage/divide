package com.jug6ernaut.network.authenticator.client.data;

import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/27/13
 * Time: 8:16 PM
 */
public interface DataWebService {

    @POST("/data/get")
    public Collection<BackendObject> get(@Body Collection<BackendObject> keys);
    @POST("/data/get")
    public void get(@Body Collection<TransientObject> keys, Callback<Collection<BackendObject>> callback);

    @POST("/data/query")
    public Collection<BackendObject> query(@Body Query query);
    @POST("/data/query")
    public void query(@Body Query query, Callback<Collection<BackendObject>> callback);

    @POST("/data/save")
    public String save(@Body Collection<BackendObject> objects);
    @POST("/data/save")
    public void save(@Body Collection<BackendObject> objects, Callback<String> callback);

}
