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

    private static class AObject extends TransientObject{

        protected <T extends TransientObject> AObject() {
            super(AObject.class);
        }
    }
}
