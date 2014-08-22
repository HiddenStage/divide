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
