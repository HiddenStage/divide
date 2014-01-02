package com.jug6ernaut.network.authenticator.server.appengine;

import com.jug6ernaut.network.shared.web.transitory.TransientObject;

/**
 * Created by williamwebb on 11/16/13.
 */
public class TestObject1 extends TransientObject {
    protected <T extends TransientObject> TestObject1() {
        super(TestObject1.class);
    }
}
