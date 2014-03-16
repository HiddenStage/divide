package com.jug6ernaut.network.authenticator.server;

import com.jug6ernaut.network.authenticator.server.auth.ResponseFilter;
import com.jug6ernaut.network.authenticator.server.auth.SecurityFilter;
import com.jug6ernaut.network.authenticator.server.dao.CredentialBodyHandler;
import com.jug6ernaut.network.authenticator.server.dao.GsonMessageBodyHandler;
import com.jug6ernaut.network.authenticator.server.endpoints.AuthenticationEndpoint;
import com.jug6ernaut.network.authenticator.server.endpoints.DataEndpoint;
import com.jug6ernaut.network.authenticator.server.endpoints.MetaEndpoint;
import com.jug6ernaut.network.authenticator.server.endpoints.PushEndpoint;
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