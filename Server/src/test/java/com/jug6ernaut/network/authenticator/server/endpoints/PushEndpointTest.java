package com.jug6ernaut.network.authenticator.server.endpoints;

import com.jug6ernaut.network.authenticator.server.AbstractTest;
import com.jug6ernaut.network.authenticator.server.TestUtils;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import com.jug6ernaut.network.shared.web.transitory.EncryptedEntity;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.Test;

import java.security.PublicKey;

import static org.junit.Assert.assertEquals;

/**
 * Created by williamwebb on 11/16/13.
 */
public class PushEndpointTest extends AbstractTest {

    @Test
    public void testRegister() throws Exception {
        Credentials user = TestUtils.getTestUser();
        ODatabaseRecordThreadLocal.INSTANCE.set(container.db);
        container.dao.save(user);
        PublicKey key = AuthenticationEndpointTest.getPublicKey(this);

        EncryptedEntity.Writter entity = new EncryptedEntity.Writter(key);
        entity.put("token", "whatwhat");

        int statusCode = target("/push")
                .request()
                .header(ContainerRequest.AUTHORIZATION, "CUSTOM " + user.getAuthToken())
                .buildPost(TestUtils.toEntity(entity)).invoke().getStatus();
        assertEquals(200,statusCode);
    }
//
//    @Test
//    public void testUnregister() throws Exception {
//
//    }
//
//    @Test
//    public void testPushToDevice() throws Exception {
//
//    }
}
