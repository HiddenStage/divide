package io.divide.client.data;

import io.divide.client.BackendObject;
import io.divide.client.BackendServices;
import io.divide.client.ClientTest;
import io.divide.client.auth.SignUpResponse;
import io.divide.client.auth.credentials.SignUpCredentials;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by williamwebb on 3/19/14.
 */
public class DataManagerTest extends ClientTest {

    @Test
    public void testSend() throws Exception {
        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        assertEquals(response.get().getUsername(),"name");

        BackendServices.remote().save(new BackendObject()).toBlockingObservable();
    }
}
