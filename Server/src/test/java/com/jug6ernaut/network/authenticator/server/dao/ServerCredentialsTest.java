package com.jug6ernaut.network.authenticator.server.dao;

import com.jug6ernaut.network.shared.web.transitory.Credentials;
import org.junit.Test;

/**
 * Created by williamwebb on 11/16/13.
 */
public class ServerCredentialsTest {

    @Test
    public void constructor() throws Exception{
        new ServerCredentials(new Credentials("","",""));
    }
}
