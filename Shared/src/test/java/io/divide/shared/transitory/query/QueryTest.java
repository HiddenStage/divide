package io.divide.shared.transitory.query;

import io.divide.shared.transitory.TransientObject;
import junit.framework.TestCase;

public class QueryTest extends TestCase {

    public void testSafeTable() throws Exception {
        assertEquals(A.class.getName().replace('.','_'),Query.safeTable(A.class));
    }

    public void testSafeTable1() throws Exception {
        assertEquals(A.class.getName().replace('.','_'),Query.safeTable(A.class.getName()));
    }

    public void testReverseTable() throws Exception {
        assertEquals(A.class.getName(),Query.reverseTable(Query.safeTable(A.class)));
    }

    private static class A extends TransientObject{};
}