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

package io.divide.client.android.push;

import io.divide.shared.transitory.EncryptedEntity;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.POST;

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
