package com.jug6ernaut.network.shared.util;

import static org.junit.Assert.assertEquals;

/**
 * Created by williamwebb on 11/20/13.
 */
public class BitMaskTest {
    @org.junit.Test
    public void testAll() throws Exception {
        BitMask bm = new BitMask();

        bm.set(1,true);
        assertEquals("1=true",true,bm.get(1));
        bm.set(3,true);
        assertEquals("3=true",true,bm.get(3));
        bm.set(5,true);
        assertEquals("5=true",true,bm.get(5));

        assertEquals("2=false",false,bm.get(2));
        assertEquals("4=false",false,bm.get(4));
        assertEquals("6=false",false,bm.get(6));


        bm.set(1,false);
        assertEquals(false,bm.get(1));
        bm.set(3,false);
        assertEquals(false,bm.get(3));
        bm.set(5,false);
        assertEquals(false,bm.get(5));
    }

}
