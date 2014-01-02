package com.jug6ernaut.network.authenticator.client;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import retrofit.client.Response;

import java.io.IOException;

/**
 * Created by williamwebb on 12/21/13.
 */
public class GenericResponse<Type> {

    private static Gson gson = new Gson();
    Class<Type> typeClass;
    Response response;

    public GenericResponse(Class<Type> type, Response response) {
        this.typeClass = type;
        this.response = response;
    }

    public Type getBody(){
        String body = null;
        try {
            body = IOUtils.toString(response.getBody().in());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Type t = gson.fromJson(body,typeClass);
        return t;
    }

}
