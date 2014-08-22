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
