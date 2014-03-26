package io.divide.dao.appengine;

import io.divide.dao.TestObject1;
import org.junit.Test;

import java.util.HashMap;

/**
 * Created by williamwebb on 11/16/13.
 */
public class BackendToOfyTest {
    @Test
    public void testGetOfy() throws Exception {
        TestObject1 object = new TestObject1();
        BackendToOfy.getOfy(object);
    }

    @Test
    public void testGetBack() throws Exception {
        OfyObject object = new OfyObject("somekey",new HashMap<String, Object>(),new HashMap<String, String>());
        BackendToOfy.getBack(object);
    }
}
