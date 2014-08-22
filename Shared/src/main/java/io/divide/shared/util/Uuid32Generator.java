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

package io.divide.shared.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Uuid32Generator {

    public static String generateUUID() {
        return new Uuid32Generator().generate();
    }

    public static String generateUUIDstr() {
        return String.valueOf(generateUUIDint());
    }

    public static long generateUUIDint() {
        String uuid = new Uuid32Generator().generate();
        long num = toLong(uuid);
        return num;
    }

    private static final String ZEROS = "000000000000"; // 12

    public String generate() {
        StringBuilder strRetVal = new StringBuilder();
        String strTemp;
        try {
            // IPAddress segment
            InetAddress addr = InetAddress.getLocalHost();
            byte[] ipaddr = addr.getAddress();
            for (byte anIpaddr : ipaddr) {
                Byte b = new Byte(anIpaddr);
                strTemp = Integer.toHexString(b.intValue() & 0x000000ff);
                strRetVal.append(ZEROS.substring(0, 2 - strTemp.length()));
                strRetVal.append(strTemp);
            }
            strRetVal.append(':');

            // CurrentTimeMillis() segment
            strTemp = Long.toHexString(System.currentTimeMillis());
            strRetVal.append(ZEROS.substring(0, 12 - strTemp.length()));
            strRetVal.append(strTemp).append(':');

            // random segment
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            strTemp = Integer.toHexString(prng.nextInt());
            while (strTemp.length() < 8) {
                strTemp = '0' + strTemp;
            }
            strRetVal.append(strTemp.substring(4)).append(':');

            // IdentityHash() segment
            strTemp = Long.toHexString(System.identityHashCode(this));
            strRetVal.append(ZEROS.substring(0, 8 - strTemp.length()));
            strRetVal.append(strTemp);
        } catch (UnknownHostException uhex) {
            throw new RuntimeException("Unknown host.", uhex);
        } catch (NoSuchAlgorithmException nsaex) {
            throw new RuntimeException("Algorithm 'SHA1PRNG' is unavailiable.", nsaex);
        }
        return strRetVal.toString().toUpperCase();
    }

    public static long toLong(String string){
        String binStr = toBinaryString(string);
        return parseLong(binStr,2);
    }

    private static long parseLong(String s, int base) {
        return new BigInteger(s, base).longValue();
    }

    public static String toBinaryString(String s) {

        char[] cArray=s.toCharArray();

        StringBuilder sb=new StringBuilder();

        for(char c:cArray)
        {
            String cBinaryString=Integer.toBinaryString((int)c);
            sb.append(cBinaryString);
        }

        return sb.toString();
    }

}
