package com.jug6ernaut.network.shared.util;

import com.jug6ernaut.network.shared.web.transitory.Credentials;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import javax.security.sasl.AuthenticationException;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by williamwebb on 11/18/13.
 */
public class AuthTokenUtils {
    static Logger logger = Logger.getLogger(AuthTokenUtils.class.getName());
    static StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
    static String pw="";

    static long expirateIn = (1000 * 60 * 60 * 24);

    public static String getNewToken(String key, Credentials credentials){
        if(!pw.equals(key)){
            pw = new String(key);
            jasypt.setPassword(pw);
        }
        String token = UUID.randomUUID().toString() +
//                "|" + someImportantProjectToken +
                "|" + credentials.getOwnerId() +
                "|" + (System.currentTimeMillis() + expirateIn ); // TODO grab this from credentials?
        return jasypt.encrypt(token);
    }

    public static class AuthToken {
        public String userId;
        public Long expirationDate;
        public AuthToken(String key,String token) throws AuthenticationException {
            try {
                if(!pw.equals(key)){
                    pw = new String(key);
                    jasypt.setPassword(pw);
                }
                logger.info("En: " + token);
                token = jasypt.decrypt(token);
                logger.info("De: " + token);
                String[] parts = token.split("(\\|)");
                for (String s : parts){
                    logger.info("Part: " + s);
                }
                userId = parts[1];
                expirationDate = Long.parseLong(parts[2]);
            } catch (EncryptionOperationNotPossibleException e){
                throw new AuthenticationException("Failed to create AuthToken",e);
            }
        }

        public boolean isExpired(){
            return expirationDate < System.currentTimeMillis();
        }
    }

}
