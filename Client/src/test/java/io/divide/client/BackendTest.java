package io.divide.client;

import org.junit.Test;
import org.robolectric.Robolectric;

/**
 * Created by williamwebb on 2/27/14.
 */
public class BackendTest extends ClientTest{

    @Test
    public void testInit() throws Exception {
        Backend.init(Robolectric.application,url());
    }
}
