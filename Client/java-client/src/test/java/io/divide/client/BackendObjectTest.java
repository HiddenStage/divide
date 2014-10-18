package io.divide.client;

import io.divide.shared.transitory.query.Query;
import junit.framework.TestCase;
import org.junit.Test;

public class BackendObjectTest extends TestCase {

    @Test
    public void testExtend(){
        assertEquals(Query.safeTable(A.class),new A().getObjectType());
    }

    private static class A extends BackendObject { }
}