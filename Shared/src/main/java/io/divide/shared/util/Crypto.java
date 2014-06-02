package io.divide.shared.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/29/13
 * Time: 9:10 PM
 */
public class Crypto {
    private KeyPair keyPair;

    public static KeyPair getNew() throws NoSuchAlgorithmException {
        return new Crypto().keyPair;
    }
    private Crypto() throws NoSuchAlgorithmException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048, new SecureRandom());
        keyPair = keyGen.generateKeyPair();
    }

    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey(){
        return keyPair.getPrivate();
    }

    public static byte[] sign(byte[] message, PrivateKey privateKey)
    {
        try
        {
            // Initialize a container for our signedMessage
            byte[] signedMessage = new byte[0];

            // Calculate the signature with an SHA1 hash function signed by the RSA private key
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(privateKey);
            sig.update(message);
            byte[] signature = sig.sign();

            // Add the length of the signature and the signature itself in front of the message
            signedMessage = concat(signedMessage,intToByteArray(signature.length));
            signedMessage = concat(signedMessage,signature);

            return concat(signedMessage,message);
        }
        catch (GeneralSecurityException exception)
        {
            exception.printStackTrace();
            return null;
        }
    }

    public static byte[] encrypt(byte[] message, PublicKey publicKey)
    {
        try
        {
            // Initialize the new message container
            byte[] encryptedMessage = new byte[0];

            // Generate a symmetric key with the AES algorithm
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128,new SecureRandom());
            SecretKey symmetricKey = keygen.generateKey();

            // Wrap the symmetric key with the public key and add its length and itself to the message
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.WRAP_MODE, publicKey);
            byte[] wrappedKey = cipher.wrap(symmetricKey);
            encryptedMessage = concat(encryptedMessage, intToByteArray(wrappedKey.length));
            encryptedMessage = concat(encryptedMessage,wrappedKey);

            // Encrypt the message with the symmetric key
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
            encryptedMessage = concat(encryptedMessage,cipher.doFinal(message));

            return encryptedMessage;
        }
        catch (GeneralSecurityException exception)
        {
            exception.printStackTrace();
            return null;
        }
    }

    public static byte[] unsign(byte[] signedMessage, PublicKey publicKey)
    {
        try
        {
            // Read the signature from the signedMessage (and its length)
            int length = byteArrayToInt(Arrays.copyOf(signedMessage, 4));
            byte[] sentSignature = Arrays.copyOfRange(signedMessage,4,4+length);

            // Determine the signed hash sum of the message
            byte[] message = Arrays.copyOfRange(signedMessage, 4+length, signedMessage.length);
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);
            sig.update(message);

            // Verify the signature
            if (!sig.verify(sentSignature))
                throw new SignatureException("Signature invalid");

            return message;
        }
        catch (GeneralSecurityException exception)
        {
            exception.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(byte[] encryptedMessage, PrivateKey privateKey)
    {
        try
        {
            // Read the symmetric key from the encrypted message (and its length)
            int length = byteArrayToInt(Arrays.copyOf(encryptedMessage,4));
            byte[] wrappedKey = Arrays.copyOfRange(encryptedMessage,4,4+length);

            // Decrypt the symmetric key
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.UNWRAP_MODE, privateKey);
            Key symmetricKey = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);

            // Decrypt the message and return it
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, symmetricKey);

            return cipher.doFinal(Arrays.copyOfRange(encryptedMessage,4+length,encryptedMessage.length));
        }
        catch (GeneralSecurityException exception)
        {
            exception.printStackTrace();
            return null;
        }
    }

    public static PublicKey pubKeyFromBytes(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
    }

    public static PrivateKey priKeyFromBytes(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    public static KeyPair createKeyPair(byte[] encodedPublicKey, byte[]  encodedPrivateKey) {
        try {
            KeyFactory generator = KeyFactory.getInstance("RSA");

            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            PrivateKey privateKey = generator.generatePrivate(privateKeySpec);

            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            PublicKey publicKey = generator.generatePublic(publicKeySpec);
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create KeyPair from provided encoded keys", e);
        }
    }

    /**
     * Converts a int value into a byte array.
     *
     * @param value  int value to be converted
     * @return  byte array containing the int value
     */
    private static byte[] intToByteArray(int value)
    {
        byte[] data = new byte[4];

        // int -> byte[]
        for (int i = 0; i < 4; ++i)
        {
            int shift = i << 3; // i * 8
            data[3 - i] = (byte) ((value & (0xff << shift)) >>> shift);
        }
        return data;
    }

    /**
     * Converts a byte array to an int value.
     *
     * @param data  byte array to be converted
     * @return  int value of the byte array
     */
    private static int byteArrayToInt(byte[] data)
    {
        // byte[] -> int
        int number = 0;
        for (int i = 0; i < 4; ++i)
        {
            number |= (data[3-i] & 0xff) << (i << 3);
        }
        return number;
    }

    /**
     * Concatenates two byte arrays and returns the resulting byte array.
     *
     * @param a  first byte array
     * @param b  second byte array
     * @return  byte array containing first and second byte array
     */
    private static byte[] concat(byte[] a, byte[] b)
    {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);

        return c;
    }

}
