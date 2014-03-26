package io.divide.dao;

import io.divide.shared.web.transitory.FilePermissions;
import io.divide.shared.web.transitory.TransientObject;

/**
 * Created by williamwebb on 11/16/13.
 */
public class TestObject2 extends TransientObject {

    public TestObject2(){
        super(TestObject1.class);
        FilePermissions fp = this.getFilePermissions();
        fp.setReadable(true, FilePermissions.Level.WORLD);
        fp.setWritable(true, FilePermissions.Level.WORLD);
        this.setFilePermissions(fp);
    }

    public TestObject2(String... vals) {
        super(TestObject2.class);
        if(vals.length%2!=0) throw new  IllegalArgumentException("Must have valid pairs");
        for(int x=0; x<vals.length;x+=2){
            put(vals[x],vals[x+1]);
        }
        FilePermissions fp = this.getFilePermissions();
        fp.setReadable(true, FilePermissions.Level.WORLD);
        fp.setWritable(true, FilePermissions.Level.WORLD);
        this.setFilePermissions(fp);
    }

}
