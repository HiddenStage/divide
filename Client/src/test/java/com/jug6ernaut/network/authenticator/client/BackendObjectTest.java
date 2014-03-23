//package com.jug6ernaut.network.authenticator.client;
//
//import com.jug6ernaut.network.shared.web.transitory.FilePermissions;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;
//
///**
//* Created by williamwebb on 11/23/13.
//*/
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
