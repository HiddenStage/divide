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

import io.divide.client.BackendObject;
import io.divide.shared.transitory.query.Query;
import retrofit.client.Response;
import retrofit.http.*;
import rx.Observable;

import java.util.Collection;

public interface DataWebService {

    @POST("/data/get/{objectType}")
    public Response get(@Header("Authorization") String authToken, @EncodedPath("objectType") String objectType,@Body Collection<String> keys);

    @POST("/data/query")
    public Response query(@Header("Authorization") String authToken, @Body Query query);

    @POST("/data/save")
    public <B extends BackendObject> Observable<Void> save(@Header("Authorization") String authToken, @Body Collection<B> objects);

    @GET("/data/count/{objectType}")
    public Observable<Integer> count(@Header("Authorization") String authToken, @EncodedPath("objectType") String objectType);

}
