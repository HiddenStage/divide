package io.divide.server;

import io.divide.server.auth.ResponseFilter;
import io.divide.server.auth.SecurityFilter;
import io.divide.server.dao.CredentialBodyHandler;
import io.divide.server.dao.GsonMessageBodyHandler;
import io.divide.server.endpoints.AuthenticationEndpoint;
import io.divide.server.endpoints.DataEndpoint;
import io.divide.server.endpoints.MetaEndpoint;
import io.divide.server.endpoints.PushEndpoint;
import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by williamwebb on 2/13/14.
 */
public class AuthApplicationTest extends ServerTest {

    // sanity test
    @Test
    public void sanity() {
        final String version = target("").request().get(String.class);
        assertEquals(MetaEndpoint.VERSION, version);
    }

    @Test
    public void classesRegistered(){
        Set<Class<?>> classes = container.app.getClasses();
        Set<Object> singletons = container.app.getSingletons();

        assertTrue(classes.contains(AuthenticationEndpoint.class));
        assertTrue(classes.contains(DataEndpoint.class));
        assertTrue(classes.contains(PushEndpoint.class));
        assertTrue(classes.contains(MetaEndpoint.class));
        assertTrue(classes.contains(CredentialBodyHandler.class));
        assertTrue(classes.contains(GsonMessageBodyHandler.class));
        assertTrue(classes.contains(SecurityFilter.class));
        assertTrue(classes.contains(ResponseFilter.class));

//        assertTrue(classes.contains(UserContext.class));
//
//        assertTrue(singletons.contains(DAOManager.class));
//        assertTrue(singletons.contains(KeyManager.class));
//        assertTrue(classes.contains(Session.class));
    }


}