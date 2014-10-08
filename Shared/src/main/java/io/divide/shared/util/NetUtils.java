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

package io.divide.shared.util;

import java.net.InetAddress;

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
