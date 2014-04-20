package io.divide.client.data;

import io.divide.client.BackendObject;
import io.divide.shared.web.transitory.query.Query;
import retrofit.client.Response;
import retrofit.http.*;
import rx.Observable;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/27/13
 * Time: 8:16 PM
 */
public interface DataWebService {

    @POST("/data/get/{objectType}")
    public Response get(@Header("Authorization") String authToken, @EncodedPath("objectType") String objectType,@Body Collection<String> keys);

    @POST("/data/query")
    public Response query(@Header("Authorization") String authToken, @Body Query query);

    @POST("/data/save")
    public <B extends BackendObject> Observable<String> save(@Header("Authorization") String authToken, @Body Collection<B> objects);

    @GET("/data/count/{objectType}")
    public Observable<Integer> count(@Header("Authorization") String authToken, @EncodedPath("objectType") String objectType);

}
