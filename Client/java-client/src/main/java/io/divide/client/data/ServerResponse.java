package io.divide.client.data;

import com.google.gson.Gson;
import io.divide.client.http.Status;
import org.apache.commons.io.IOUtils;
import retrofit.client.Response;

import java.io.IOException;

/**
 * Created by williamwebb on 12/21/13.
 */
public class ServerResponse<T> {

    private static Gson gson = new Gson();
    protected T t;
    protected String error;
    protected Status status;

    /**
     *
     * @param type class type contained within response
     * @param response Retrofit response to be converted to type.
     * @return ServerResponse containing object contained within retrofit response
     */
    public static <T> ServerResponse<T> from(Class<T> type,Response response){
        ServerResponse<T> o = null;
        try {
            o = new ServerResponse<T>(convertBody(type,response),Status.valueOf(response.getStatus()),response.getReason());
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

    /**
     * @return HTML Status code
     */
    public Status getStatus(){
        return status;
    }

    /**
      * @return String representation of the error is exists for this response.
     */
    public String getError(){
        return error;
    }

    /**
     * @return Object contained within this response
     */
    public T get(){
        return t;
    }

    private static <T> T convertBody(Class<T> typeClass, Response response){
        String body = null;
        try {
            body = IOUtils.toString(response.getBody().in());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gson.fromJson(body,typeClass);
    }
}
