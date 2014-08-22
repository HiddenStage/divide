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

package io.divide.client.data;

import io.divide.client.BackendObject;
import io.divide.client.BackendServices;
import io.divide.client.ClientTest;
import io.divide.client.auth.SignUpResponse;
import io.divide.client.auth.credentials.SignUpCredentials;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataManagerTest extends ClientTest {

    @Test
    public void testSend() throws Exception {
        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        assertEquals(response.get().getUsername(), "name");

        BackendServices.remote().save(new BackendObject()).toBlockingObservable();
    }
}
