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

package io.divide.server;

import io.divide.dao.ServerDAO;
import io.divide.shared.transitory.Credentials;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.test.spi.TestContainer;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;

import javax.ws.rs.core.Response;
import java.net.URI;

public class AuthServerHelper {

    TestContainer container;
    final Credentials admin = TestUtils.getTestUser();
    final String token = "CUSTOM " + admin.getAuthToken();

    public void init(String url) throws Exception {
        URI uri = URI.create(url);
        ApplicationHandler h = new ApplicationHandler(TestApplication.class);
        WebContainerFactory factory = new WebContainerFactory();
        factory.enableEncrementPort(false);
        container = factory.create(uri,h);
        container.start();
    }

    public void destroy(){
        if(container != null) {
            container.stop();
        }
    }

    public void setUp(String url) throws ServerDAO.DAOException {
        TestService service = buildTestService(url);
        service.setup();
    }

    public void tearDown(String url) throws Exception {
        TestService service = buildTestService(url);
        service.tearDown();
    }

    private TestService testService;
    private TestService buildTestService(String url){
        if(testService==null){
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setClient( new OkClient() )
                    .setEndpoint(url)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new RestAdapter.Log() {
                        @Override
                        public void log(String s) {
                            System.out.println(s);
                        }
                    }).build();

            testService = restAdapter.create(TestService.class);
        }
        return testService;
    }

    private static interface TestService {
        @GET("/test/setup")
        public Credentials setup();
        @GET("/test/teardown")
        public Response tearDown();
    }

}
