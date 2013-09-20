package com.jug6ernaut.network.shared.util;

import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/2/13
 * Time: 3:12 PM
 */
public class NetUtils {

    public static boolean ping(byte[] ip, int timeout){
        try {
            return InetAddress.getByAddress(ip).isReachable(timeout);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean ping(String host, int timeout){
        try {
            return InetAddress.getByName(host).isReachable(timeout);
        } catch (Exception e) {
            return false;
        }
    }
}
