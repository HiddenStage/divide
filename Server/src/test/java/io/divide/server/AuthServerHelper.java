package io.divide.server;

import io.divide.dao.ServerDAO;
import io.divide.shared.web.transitory.Credentials;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.test.spi.TestContainer;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;

import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Created by williamwebb on 2/27/14.
 */
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
