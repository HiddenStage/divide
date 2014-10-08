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
