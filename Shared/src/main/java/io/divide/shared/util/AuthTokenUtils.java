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

import io.divide.shared.transitory.Credentials;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class AuthTokenUtils {
    static Logger logger = Logger.getLogger(AuthTokenUtils.class.getName());
    static long expirateIn = (1000 * 60 * 60 * 24);

    public static String getNewToken(String key, Credentials credentials){

        String uuid = UUID.randomUUID().toString();
        Integer ownerId = credentials.getOwnerId();
        Long expireIn = (System.currentTimeMillis() + expirateIn );

        if(ownerId == null) throw new InternalError("ownerId returned null for receating auth token");

        String token = uuid +
//                "|" + someImportantProjectToken +
                "|" + ownerId +
                "|" + expireIn; // TODO grab this from credentials?
        return encrypt(token,key);
    }

    public static class AuthToken {
        public String userId;
        public Long expirationDate;
        public AuthToken(String key,String token) throws AuthenticationException {
            try {
                logger.info("En: " + token);
                token = decrypt(token, key);
                logger.info("De: " + token);
                String[] parts = token.split("(\\|)");
                for (String s : parts){
                    logger.info("Part: " + s);
                }
                userId = parts[1];
                expirationDate = Long.parseLong(parts[2]);
            } catch (EncryptionOperationNotPossibleException e){
                e.printStackTrace();
                throw new AuthenticationException("Failed to create AuthToken",e);
            }
        }

        public boolean isExpired(){
            return expirationDate < System.currentTimeMillis();
        }
    }

    private static Map<String,StandardPBEStringEncryptor> encryptors = new HashMap<String,StandardPBEStringEncryptor>();
    private static StandardPBEStringEncryptor getEncryptor(String key){
        StandardPBEStringEncryptor encryptor;
        if(encryptors.containsKey(key)) encryptor = encryptors.get(key);
        else {
            encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword(key);
        }
        return encryptor;
    }

    private static String encrypt(String string, String key){
        StandardPBEStringEncryptor encryptor = getEncryptor(key);
        String encrypted = encryptor.encrypt(string);
        return Base64.encode(encrypted);
    }

    private static String decrypt(String string, String key){
        StandardPBEStringEncryptor encryptor = getEncryptor(key);
        String decoded = Base64.decode(string);
        return encryptor.decrypt(decoded);
    }

    public static class AuthenticationException extends Exception{
        public AuthenticationException(String message, Exception e){
            super(message,e);
        }
    }

    private static boolean isNorE(String s){
        if(s == null) return false;
        if(s.length() == 0) return false;
        return true;
    }
}
