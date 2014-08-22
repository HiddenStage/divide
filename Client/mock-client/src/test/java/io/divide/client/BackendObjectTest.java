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

//package io.divide.authenticator.client;
//
//import io.divide.shared.web.transitory.FilePermissions;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;
//
//public class BackendObjectTest {
//
//    @Before
//    public void setUp() throws Exception {
////        super.setUp();
//    }
//
//    @Test
//    public void testIsReadable() throws Exception {
//        FilePermissions fp = new FilePermissions();
//        BackendObject to = new BackendObject();
//
//        to.setFilePermissions(fp);
//        assertEquals(true, to.isReadable());
//
//        fp.setReadable(false, FilePermissions.Level.OWNER);
//        to.setFilePermissions(fp);
//        assertEquals(false,to.isReadable());
//
//    }
//
//    @Test
//    public void testIsWritable() throws Exception {
//        FilePermissions fp = new FilePermissions();
//        BackendObject to = new BackendObject();
//
//        to.setFilePermissions(fp);
//        assertEquals(true, to.isWritable());
//
//        fp.setWritable(false, FilePermissions.Level.OWNER);
//        to.setFilePermissions(fp);
//        assertEquals(false,to.isWritable());
//    }
//
//    @Test
//    public void testGetGroups() throws Exception {
//        FilePermissions fp = new FilePermissions();
//        BackendObject to = new BackendObject();
//        String group = "some_group";
//        fp.setWritable(false, FilePermissions.Level.OWNER, FilePermissions.Level.WORLD);
//        fp.setReadable(false, FilePermissions.Level.OWNER, FilePermissions.Level.WORLD);
////        to.setOwnerId(group);
//
//        assertEquals(true,to.isWritable());
//    }
//
//}
