/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
