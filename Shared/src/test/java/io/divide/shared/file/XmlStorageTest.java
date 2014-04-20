package io.divide.shared.file;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by williamwebb on 3/28/14.
 */
public class XmlStorageTest {

    @Test
    public void testStorage(){
        XmlStorage storage = new XmlStorage(new File("storage"), Storage.MODE_WORLD_READABLE);
        assertTrue(storage.edit().putString("key1", "someString").commit());
        assertEquals("someString",storage.getString("key1",null));
    }

}
