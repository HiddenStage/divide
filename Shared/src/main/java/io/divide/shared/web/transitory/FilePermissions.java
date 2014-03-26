package io.divide.shared.web.transitory;

import com.google.gson.Gson;
import io.divide.shared.util.BitMask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by williamwebb on 11/20/13.
 */
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

    //                                         User
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
            permissions.set(l.offset,readable);
        }
    }

    public void setWritable(boolean writable, Level... levels){
        for(Level l : levels){
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
