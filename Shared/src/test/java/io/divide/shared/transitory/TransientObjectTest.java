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

package io.divide.shared.transitory;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

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

    private static class AObject extends TransientObject { }
}
