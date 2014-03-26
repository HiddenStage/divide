package io.divide.server.endpoints;

import io.divide.server.ServerTest;
import io.divide.server.TestUtils;
import io.divide.shared.util.ObjectUtils;
import io.divide.shared.web.transitory.Credentials;
import io.divide.shared.web.transitory.EncryptedEntity;
import io.divide.shared.web.transitory.TransientObject;
import io.divide.shared.web.transitory.query.Query;
import io.divide.shared.web.transitory.query.QueryBuilder;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.security.PublicKey;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by williamwebb on 11/16/13.
 */
public class PushEndpointTest extends ServerTest {

    private Response registerToken(Credentials user, PublicKey key, JerseyTest test){
        EncryptedEntity.Writter entity = new EncryptedEntity.Writter(key);
        entity.put("token", "whatwhat");

        Response response = target("/push")
                .request()
                .header(ContainerRequest.AUTHORIZATION, "CUSTOM " + user.getAuthToken())
                .buildPost(TestUtils.toEntity(entity)).invoke();
        int statusCode = response.getStatus();
        assertEquals(200,statusCode);
        return response;
    }

    @Test
    public void testRegister() throws Exception {
        Credentials user = AuthenticationEndpointTest.signUpUser(this);
        PublicKey key = AuthenticationEndpointTest.getPublicKey(this);

        registerToken(user,key,this);

        Collection<TransientObject> list = container.dao.query(new QueryBuilder().select().from(Credentials.class).build());
        TransientObject o = ObjectUtils.get1stOrNull(list);
        user = TestUtils.convert(o,Credentials.class);
        assertNotNull(user);
        assertEquals("whatwhat", user.getPushMessagingKey()); // check the token was actually saved
    }
//
    @Test
    public void testUnregister() throws Exception {
        Credentials user = AuthenticationEndpointTest.signUpUser(this);
        PublicKey key = AuthenticationEndpointTest.getPublicKey(this);

        Response tokenResponse =  registerToken(user, key, this);

        String newAuthToken = tokenResponse.getHeaderString("Authorization");

        Response response = target("/push")
                .request()
                .header(ContainerRequest.AUTHORIZATION, "CUSTOM " + newAuthToken)
                .delete();
        int statusCode = response.getStatus();
        assertEquals(200,statusCode);
        Collection<TransientObject> list = container.dao.get(Query.safeTable(Credentials.class),user.getObjectKey());
        TransientObject o = ObjectUtils.get1stOrNull(list);
        user = TestUtils.convert(o,Credentials.class);
        assertNotNull(user);
        assertEquals("", user.getPushMessagingKey()); // check the token was actually saved
    }
//
//    @Test
//    public void testPushToDevice() throws Exception {
//         no idea how to test this...
//    }
}
