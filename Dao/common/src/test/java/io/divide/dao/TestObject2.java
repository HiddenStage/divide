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

package io.divide.dao;

import io.divide.shared.transitory.FilePermissions;
import io.divide.shared.transitory.TransientObject;

public class TestObject2 extends TransientObject {

    public TestObject2(){
        FilePermissions fp = this.getFilePermissions();
        fp.setReadable(true, FilePermissions.Level.WORLD);
        fp.setWritable(true, FilePermissions.Level.WORLD);
        this.setFilePermissions(fp);
    }

    public TestObject2(String... vals) {
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
