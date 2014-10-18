/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
