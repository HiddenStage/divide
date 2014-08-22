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

package io.divide.shared.file;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XmlStorageTest {

    @Test
    public void testStorage(){

        new File("storage").delete();

        // test string
        XmlStorage storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        storage.edit().clear().commit();

        assertTrue(storage.edit().putString("s1", "someString").commit());
        assertEquals("someString",storage.getString("s1",null));

        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        assertEquals("someString",storage.getString("s1",null));
        storage.edit().clear().commit();
        storage.edit().apply();

        // test int
//        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
//        assertTrue(storage.edit().putInt("i1", 1).commit());
//        assertEquals(1,storage.getInt("i1", 0));
//
//        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
//        assertEquals(1,storage.getInt("i1", 0));
//        storage.edit().clear().commit();
//        storage.edit().apply();

        // test float
        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        assertTrue(storage.edit().putFloat("f1", 1F).commit());
        assertEquals(1F,storage.getFloat("f1", 0F), 0);

        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        assertEquals(1F, storage.getFloat("f1", 0F), 0);
        storage.edit().clear().commit();
        storage.edit().apply();

        // test long
        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        assertTrue(storage.edit().putLong("l1", 1L).commit());
        assertEquals(1L,storage.getLong("l1", 0));

        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        assertEquals(1L,storage.getLong("l1", 0));
        storage.edit().clear().commit();
        storage.edit().apply();

        // test boolean
        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        assertTrue(storage.edit().putBoolean("b1", true).commit());
        assertEquals(true,storage.getBoolean("b1", false));

        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        assertEquals(true,storage.getBoolean("b1", false));
        storage.edit().clear().commit();
        storage.edit().apply();

        // test Set<String>
        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        assertTrue(storage.edit().putStringSet("b1", new HashSet<String>(Arrays.asList("a", "b"))).commit());
        assertEquals(new HashSet<String>(Arrays.asList("a", "b")),storage.getStringSet("b1", null));

        storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        assertEquals(new HashSet<String>(Arrays.asList("a", "b")),storage.getStringSet("b1", null));
        storage.edit().clear().commit();
        storage.edit().apply();
    }

}
