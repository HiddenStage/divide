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

package io.divide.client.auth;

import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import retrofit.client.Response;
import retrofit.http.*;
import rx.Observable;

import java.util.Map;

/*
 * Interface defining relationship with server.
 */
public interface AuthWebService {

    @POST("/auth")
    public Response userSignUp(@Body SignUpCredentials credentials);

    @POST("/auth")
    public Observable<ValidCredentials> userSignUpA(@Body SignUpCredentials credentials);

    @PUT("/auth")
    public Response login(@Body LoginCredentials credentials);
    @PUT("/auth")
    public Observable<ValidCredentials> loginA(@Body LoginCredentials credentials);

    @GET("/auth/key")
    public Observable<byte[]> getPublicKeyA();

    @GET("/auth/key")
    public byte[] getPublicKey();

    @GET("/auth/from/{token}")
    public Observable<ValidCredentials> getUserFromAuthToken(@Path("token")String authToken);

    @GET("/auth/recover/{token}")
    public Observable<ValidCredentials> getUserFromRecoveryToken(@Path("token")String authToken);

    @POST("/auth/user/data/{userId}")
    public Observable<Void> sendUserData(@Header("Authorization") String authToken, @Path("userId") String userId, @Body Map<String,?> map);

    @GET("/auth/user/data/{userId}")
    public Observable<Map<String,Object>> getUserData(@Header("Authorization") String authToken, @Path("userId") String userId);

}
