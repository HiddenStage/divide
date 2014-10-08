/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.divide.client.data;

import com.google.gson.Gson;
import io.divide.client.http.Status;
import io.divide.shared.logging.Logger;
import io.divide.shared.util.IOUtils;
import retrofit.client.Response;

import java.io.IOException;

public class ServerResponse<T> {

    private static Gson gson = new Gson();
    private static Logger logger = Logger.getLogger(ServerResponse.class);
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
        logger.debug("from("+type.getName()+"): " + response.getStatus());
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
