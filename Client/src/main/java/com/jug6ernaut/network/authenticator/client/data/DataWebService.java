package com.jug6ernaut.network.authenticator.client.data;

import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

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

    @POST("/data/query")
    public Response query(@Body Query query);

    @POST("/data/save")
    public <B extends BackendObject> Observable<String> save(@Body Collection<B> objects);

    @GET("/data/count/{objectType}")
    public Observable<Integer> count(@EncodedPath("objectType") String objectType);

}
