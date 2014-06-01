package io.divide.dao;

import io.divide.shared.transitory.FilePermissions;
import io.divide.shared.transitory.TransientObject;

/**
 * Created by williamwebb on 11/16/13.
 */
public class TestObject1 extends TransientObject {

    public TestObject1(){
        super(TestObject1.class);
        FilePermissions fp = this.getFilePermissions();
        fp.setReadable(true, FilePermissions.Level.WORLD);
        fp.setWritable(true, FilePermissions.Level.WORLD);
        this.setFilePermissions(fp);
    }

    public TestObject1(String... vals) {
        super(TestObject1.class);
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
