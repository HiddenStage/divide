package io.divide.shared.util;

/**
 * Created by williamwebb on 11/20/13.
 */

import java.io.Serializable;

public class BitMask implements Serializable {

    private int flags = 0;

//  private final void setclear(int mask, boolean set) { if (set) set(mask); else clear(mask); }

    public BitMask(Integer... set){
        for(Integer i : set){
            set(i);
        }
    }

    public void set(int mask, boolean set){
        if(set)set(mask);
        else clear(mask);
    }

    private final void set(int mask) { flags |= 1 << mask; }
    public final void clear(int mask) { flags &= ~(1 << mask); }
    public final boolean get(int mask) { return (flags & 1 << mask) != 0; }

    public int getFlags(){
        return flags;
    }

    /*

    Set union
        A | B
    Set intersection
        A & B
    Set subtraction
        A & ~B
    Set negation
        ALL_BITS ^ A
    Set bit
        A |= 1 << bit
    Clear bit
        A &= ~(1 << bit)
    Test bit
        (A & 1 << bit) != 0

     */
}




