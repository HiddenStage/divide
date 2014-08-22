/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.shared.util;

import static org.junit.Assert.assertEquals;

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
