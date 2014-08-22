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

package io.divide.client;

import iBoxDB.LocalServer.BoxSystem;
import io.divide.client.auth.AuthManager;
import io.divide.shared.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockitoAnnotations;

public abstract class ClientTest {

    @MockitoAnnotations.Mock
    protected Backend backend;
    protected AuthManager authManager;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
//        helper.setUp(baseUrl);
        MockitoAnnotations.initMocks(this);
        Logger.FORCE_LOGGING = true;

        BackendConfig config = new BackendConfig(folder.getRoot().getPath(),url(), MockBackendModule.class);

        backend = Backend.init(config);
        authManager = backend.getAuthManager();

//        ReflectionUtils.setObjectField(utils, "mAccountManager", accountManager);
//        ReflectionUtils.setObjectField(authManager,"authUtils",utils);

//        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});

        authManager.logout();
    }

    @After
    public void tearDown() throws Exception {
        authManager.logout();
//        accountManager = null;
        backend = null;
        authManager = null;

        if (!BoxSystem.DBDebug.DeleteDBFiles(1, 10, 20, -10)) {
            System.out.println("delete=false,system locks");
        }

        folder.delete();
    }

    public String url(){
        return "io/divide/client/mock";
    }
}
