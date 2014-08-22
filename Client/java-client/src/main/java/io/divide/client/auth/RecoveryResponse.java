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

import io.divide.client.BackendUser;
import io.divide.client.data.ServerResponse;
import io.divide.client.http.Status;

public class RecoveryResponse extends ServerResponse<BackendUser> {
    protected RecoveryResponse(BackendUser backendUser, Status status, String error) {
        super(backendUser, status, error);
    }
}
