package com.jug6ernaut.network.authenticator.server.endpoints;

import com.jug6ernaut.network.authenticator.server.AbstractTest;
import com.jug6ernaut.network.authenticator.server.TestUtils;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import com.jug6ernaut.network.shared.web.transitory.EncryptedEntity;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.QueryBuilder;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.Test;

import java.security.PublicKey;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by williamwebb on 11/16/13.
 */
public class PushEndpointTest extends AbstractTest {

    @Test
    public void testRegister() throws Exception {
        Credentials user = AuthenticationEndpointTest.signUpUser(this);
        PublicKey key = AuthenticationEndpointTest.getPublicKey(this);

        EncryptedEntity.Writter entity = new EncryptedEntity.Writter(key);
        entity.put("token", "whatwhat");

        int statusCode = target("/push")
                .request()
                .header(ContainerRequest.AUTHORIZATION, "CUSTOM " + user.getAuthToken())
                .buildPost(TestUtils.toEntity(entity)).invoke().getStatus();
        assertEquals(200,statusCode);

        Collection<TransientObject> list = container.dao.query(new QueryBuilder().select().from(Credentials.class).build());
        TransientObject o = ObjectUtils.get1stOrNull(list);
        user = TestUtils.convert(o,Credentials.class);
        assertEquals("whatwhat", user.getPushMessagingKey());
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
