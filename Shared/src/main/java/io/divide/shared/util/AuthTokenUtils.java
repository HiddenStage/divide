package io.divide.shared.util;

import io.divide.shared.transitory.Credentials;
import org.apache.commons.codec.binary.Base64;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import javax.security.sasl.AuthenticationException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by williamwebb on 11/18/13.
 */
public class AuthTokenUtils {
    static Logger logger = Logger.getLogger(AuthTokenUtils.class.getName());
    static long expirateIn = (1000 * 60 * 60 * 24);

    public static String getNewToken(String key, Credentials credentials){
        String token = UUID.randomUUID().toString() +
//                "|" + someImportantProjectToken +
                "|" + credentials.getOwnerId() +
                "|" + (System.currentTimeMillis() + expirateIn ); // TODO grab this from credentials?
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
        return encode(encrypted);
    }

    private static String decrypt(String string, String key){
        StandardPBEStringEncryptor encryptor = getEncryptor(key);
        String decoded = decode(string);
        return encryptor.decrypt(decoded);
    }

    private static String encode(String string){
        return Base64.encodeBase64URLSafeString(string.getBytes());
    }

    private static String decode(String string){
        return new String(Base64.decodeBase64(string));
    }

}
