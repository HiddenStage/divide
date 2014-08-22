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

import io.divide.client.auth.credentials.LocalCredentials;

import java.util.List;

public interface AccountStorage {

    public void addAcccount(LocalCredentials credentials);
    public void removeAccount(String accountName);
    public LocalCredentials getAccount(String accountName);
    public boolean isAuthenticated(String accountName);
    public void setAuthToken(String accountName, String token);
    public void setRecoveryToken(String accountName, String token);
    public List<LocalCredentials> getAccounts();
    public boolean exists(String name);

}
