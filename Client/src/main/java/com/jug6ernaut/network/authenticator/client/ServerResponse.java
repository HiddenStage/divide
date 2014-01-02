package com.jug6ernaut.network.authenticator.client;

import com.jug6ernaut.network.authenticator.client.http.Status;
import retrofit.client.Response;

/**
 * Created by williamwebb on 12/21/13.
 */
public class ServerResponse<T> {

    protected T t;
    protected String error;
    protected Status status;

    public static <T> ServerResponse<T> from(GenericResponse<T> response){
        ServerResponse<T> o = null;
        try {
            Response r = response.response;
            o = new ServerResponse(response.getBody(),Status.valueOf(r.getStatus()),r.getReason());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return o;
    }

    protected ServerResponse(T t, Status status, String error){
        this.t = t;
        this.status = status;
        this.error = error;
    }

    public Status getStatus(){
        return status;
    };

    public String getError(){
        return error;
    };

    public T get(){
        return t;
    };
}
