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
