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

package io.divide.dao;

import io.divide.shared.transitory.FilePermissions;
import io.divide.shared.transitory.TransientObject;

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
