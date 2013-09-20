package com.jug6ernaut.network.authenticator.client.push;

import com.jug6ernaut.network.shared.web.transitory.EncryptedEntity;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.POST;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/27/13
 * Time: 8:16 PM
 */
public interface PushWebService {

    @POST("/push")
    public Boolean register(@Body EncryptedEntity entity);
    @POST("/push")
    public void register(@Body EncryptedEntity entity, Callback<Boolean> callback);

    @DELETE("/push")
    public Boolean unregister();
    @DELETE("/push")
    public void unregister(Callback<Boolean> callback);

}
