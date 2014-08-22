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

import com.google.gson.Gson;
import io.divide.shared.util.BitMask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FilePermissions {

    public static enum Level {
        OWNER(4),
        GROUP(2),
        WORLD(0);

        private int offset;
        private Level(int offset){
            this.offset = offset;
        }
    }

    //                                         Owner
    //                                         | Group
    //                                         | | World
    //                                         | | |
    //                                         RWRWRW
    //                                         543210
    private BitMask permissions = new BitMask(5,4);
    private Set<String> groups = new HashSet<String>();

    public boolean isReadable(Level level){
        return permissions.get(level.offset);
    }

    public boolean isWritable(Level level){
        return permissions.get(level.offset+1);
    }

    public void setReadable(boolean readable, Level... levels){
        for(Level l : levels){
            if (l.equals(Level.OWNER)) continue;
            permissions.set(l.offset,readable);
        }
    }

    public void setWritable(boolean writable, Level... levels){
        for(Level l : levels){
            if (l.equals(Level.OWNER)) continue;
            permissions.set(l.offset+1,writable);
        }
    }

    private void setGroups(String... groups){
        this.groups.clear();
        this.groups.addAll(Arrays.asList(groups));
    }

    public Set<String> getGroups(){
        return groups;
    }

//    private static BitSet fromString(final String s) {
//        return BitSet.valueOf(new long[] { Long.parseLong(s, 2) });
//    }
//
//    private static String toString(BitSet bs) {
//        return Long.toString(bs.toLongArray()[0], 2);
//    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
