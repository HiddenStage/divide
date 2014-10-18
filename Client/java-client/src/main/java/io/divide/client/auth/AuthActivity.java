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

//package io.divide.client.auth;
//
//import android.accounts.AccountAuthenticatorActivity;
//import android.os.Bundle;
//import com.jug6ernaut.android.logging.Logger;
//import io.divide.client.BackendServices;
//
///**
// * The AuthUtils activity.
// *
// * Called by the AuthUtils and in charge of identifing the user.
// *
// * It sends back to the AuthUtils the result.
// */
//public class AuthActivity extends AccountAuthenticatorActivity {
//    Logger logger = Logger.getLogger(AuthActivity.class);
//
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(new CredentialView(this,"client-example"));
//        BackendServices.addLoginListener(new LoginListener() {
//
//            @Override
//            public void onNext(LoginEvent loginEvent) {
//                switch(loginEvent.state){
//                    case LOGGED_IN:
//                        AuthActivity.this.finish();
//                        break;
//                }
//            }
//        });
//    }
//
//}
