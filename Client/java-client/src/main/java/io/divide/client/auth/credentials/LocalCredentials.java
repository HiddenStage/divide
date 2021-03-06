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

package io.divide.client.auth.credentials;

public class LocalCredentials {
    private String emailAddress;
    private String authToken;
    private String recoveryToken;

    public String getName() {
        return emailAddress;
    }

    public void setName(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getRecoveryToken() {
        return recoveryToken;
    }

    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
    }

    @Override
    public String toString() {
        return "LocalCredentials{" +
                "emailAddress='" + emailAddress + '\'' +
                ", authToken='" + authToken + '\'' +
                ", recoveryToken='" + recoveryToken + '\'' +
                '}';
    }
}
