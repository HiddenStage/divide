package io.divide.shared.util;

/***************************************************************

 Copyright (c) 1998, 1999 Nate Sammons <nate@protomatter.com>
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Library General Public
 License as published by the Free Software Foundation; either
 version 2 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Library General Public License for more details.

 You should have received a copy of the GNU Library General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.

 Contact support@protomatter.com with your questions, comments,
 gripes, praise, etc...

 ***************************************************************/


/***************************************************************
 - moved to the net.matuschek.util tree by Daniel Matuschek
 - replaced deprecated getBytes() method in method decode
 - added String encode(String) method to encode a String to
 base64
 ***************************************************************/

import java.util.Arrays;

/**
 * Base64 encoder/decoder.  Does not stream, so be careful with
 * using large amounts of data
 *
 * @author Nate Sammons
 * @author Daniel Matuschek
 * @version $Id: Base64.java,v 1.4 2001/04/17 10:09:27 matuschd Exp $
 */
public class Base64
{

    private Base64()
    {
        super();
    }

    /**
     *  Encode some data and return a String.
     */
    private static byte[] encodeEnternal(byte[] d)
    {
        if (d == null) return null;
        byte data[] = new byte[d.length+2];
        System.arraycopy(d, 0, data, 0, d.length);
        byte dest[] = new byte[(data.length/3)*4];

        // 3-byte to 4-byte conversion
        for (int sidx = 0, didx=0; sidx < d.length; sidx += 3, didx += 4)
        {
            dest[didx]   = (byte) ((data[sidx] >>> 2) & 077);
            dest[didx+1] = (byte) ((data[sidx+1] >>> 4) & 017 |
                    (data[sidx] << 4) & 077);
            dest[didx+2] = (byte) ((data[sidx+2] >>> 6) & 003 |
                    (data[sidx+1] << 2) & 077);
            dest[didx+3] = (byte) (data[sidx+2] & 077);
        }

        // 0-63 to ascii printable conversion
        for (int idx = 0; idx <dest.length; idx++)
        {
            if (dest[idx] < 26)     dest[idx] = (byte)(dest[idx] + 'A');
            else if (dest[idx] < 52)  dest[idx] = (byte)(dest[idx] + 'a' - 26);
            else if (dest[idx] < 62)  dest[idx] = (byte)(dest[idx] + '0' - 52);
            else if (dest[idx] < 63)  dest[idx] = (byte)'+';
            else            dest[idx] = (byte)'/';
        }

        // add padding
        for (int idx = dest.length-1; idx > (d.length*4)/3; idx--)
        {
            dest[idx] = (byte)'=';
        }
        return dest;
    }

    /**
     *  Decode data and return bytes.  Assumes that the data passed
     *  in is ASCII text.
     */
    private static byte[] decodeInternal(byte[] data)
    {
        int tail = data.length;
        while (data[tail-1] == '=')  tail--;
        byte dest[] = new byte[tail - data.length/4];

        // ascii printable to 0-63 conversion
        for (int idx = 0; idx <data.length; idx++)
        {
            if (data[idx] == '=')    data[idx] = 0;
            else if (data[idx] == '/') data[idx] = 63;
            else if (data[idx] == '+') data[idx] = 62;
            else if (data[idx] >= '0'  &&  data[idx] <= '9')
                data[idx] = (byte)(data[idx] - ('0' - 52));
            else if (data[idx] >= 'a'  &&  data[idx] <= 'z')
                data[idx] = (byte)(data[idx] - ('a' - 26));
            else if (data[idx] >= 'A'  &&  data[idx] <= 'Z')
                data[idx] = (byte)(data[idx] - 'A');
        }

        // 4-byte to 3-byte conversion
        int sidx, didx;
        for (sidx = 0, didx=0; didx < dest.length-2; sidx += 4, didx += 3)
        {
            dest[didx]   = (byte) ( ((data[sidx] << 2) & 255) |
                    ((data[sidx+1] >>> 4) & 3) );
            dest[didx+1] = (byte) ( ((data[sidx+1] << 4) & 255) |
                    ((data[sidx+2] >>> 2) & 017) );
            dest[didx+2] = (byte) ( ((data[sidx+2] << 6) & 255) |
                    (data[sidx+3] & 077) );
        }
        if (didx < dest.length)
        {
            dest[didx]   = (byte) ( ((data[sidx] << 2) & 255) |
                    ((data[sidx+1] >>> 4) & 3) );
        }
        if (++didx < dest.length)
        {
            dest[didx]   = (byte) ( ((data[sidx+1] << 4) & 255) |
                    ((data[sidx+2] >>> 2) & 017) );
        }
        return dest;
    }

    private static byte[] encodeUrlSafe(byte[] data) {
        byte[] encode = Base64.encodeEnternal(data);
        for (int i = 0; i < encode.length; i++) {
            if (encode[i] == '+') {
                encode[i] = '-';
            } else if (encode[i] == '/') {
                encode[i] = '_';
            }
        }
        return encode;
    }

    private static byte[] decodeUrlSafe(byte[] data) {
        byte[] encode = Arrays.copyOf(data, data.length);
        for (int i = 0; i < encode.length; i++) {
            if (encode[i] == '-') {
                encode[i] = '+';
            } else if (encode[i] == '_') {
                encode[i] = '/';
            }
        }
        return Base64.decodeInternal(encode);
    }

    public static byte[] encode(byte[] data){
        return encodeUrlSafe(data);
    }

    public static byte[] decode(byte[] data){
        return decodeUrlSafe(data);
    }

    public static String encode(String s){
        return new String( encodeUrlSafe(s.getBytes()) );
    }

    public static String decode(String s){
        return new String( decodeUrlSafe(s.getBytes()) );
    }
    
}