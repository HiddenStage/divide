package com.jug6ernaut.network.authenticator.client.data;

import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import retrofit.Callback;
import retrofit.client.Response;
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
    public Response get(@Body Collection<String> keys);
    @POST("/data/get")
    public void get(@Body Collection<String> keys, Callback<Response> callback);

    @POST("/data/query")
    public Response query(@Body Query query);
    @POST("/data/query")
    public void query(@Body Query query, Callback<Response> callback);

    @POST("/data/save")
    public <B extends BackendObject> String save(@Body Collection<B> objects);
    @POST("/data/save")
    public <B extends BackendObject> void save(@Body Collection<B> objects, Callback<String> callback);

}
