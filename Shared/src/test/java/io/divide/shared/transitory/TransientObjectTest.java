package io.divide.shared.transitory;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * Created by williamwebb on 12/21/13.
 */
public class TransientObjectTest extends TestCase {

    public void testMeta_put() throws Exception {
        AObject o = new AObject();
        List l = Arrays.asList(1,2);
        o.put("key", l);
        assertEquals(l, o.get(l.getClass(),"key"));
    }

    public void testMeta_get() throws Exception {

    }

    public void testMeta_remove() throws Exception {

    }

    public void testPut() throws Exception {

    }

    public void testPutAll() throws Exception {

    }

    public void testGet() throws Exception {

    }

    public void testRemove() throws Exception {

    }

    public void testRemoveAll() throws Exception {

    }

    private static class AObject extends TransientObject{

        protected <T extends TransientObject> AObject() {
            super(AObject.class);
        }
    }
}
