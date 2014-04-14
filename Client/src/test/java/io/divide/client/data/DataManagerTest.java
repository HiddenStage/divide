package io.divide.client.data;

import android.accounts.Account;
import io.divide.client.BackendObject;
import io.divide.client.BackendServices;
import io.divide.client.ClientTest;
import io.divide.client.auth.SignUpResponse;
import io.divide.client.auth.credentials.SignUpCredentials;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by williamwebb on 3/19/14.
 */
public class DataManagerTest extends ClientTest {

    @Test
    public void testSend() throws Exception {
        when(accountManager.getAccountsByType(accountInfo.getAccountType())).thenReturn(new Account[]{new Account("email", accountInfo.getAccountType())});

        SignUpResponse response = authManager.signUp(new SignUpCredentials("name", "email", ""));
        assertEquals(response.get().getUsername(),"name");

        BackendServices.remote().save(new BackendObject()).toBlockingObservable();
    }
}
