package com.example.smsread;

import android.util.Base64;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//sveto trojstvo, ovo va pogleda

public class GenerateRSAKeyPair {

    private static String privateKeyString = "";
    private static String publicKeyString = "";

    //text to encrypt
    private static String textToEncrypt = "KURCINA";

    public void Test() {

        try {
            //this does the server only ONCE
            // generates a keyPair (public and private key) that both server and client will use
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024); //key length
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            privateKeyString = Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT);
            publicKeyString = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT);

//            System.out.println("GENERATED KEYS\n");
//            System.out.println("PRIVATE: " + privateKeyString);
//            System.out.println("PUBLIC: " + publicKeyString + "\n");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static class ClientEncrypt {

        private static String publicKeyString = GenerateRSAKeyPair.publicKeyString;

        public static void Test() {
            try {
                //generate secret key using AES
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(128);
                SecretKey secretKey = keyGenerator.generateKey();


                //encrypt string using secret key
                byte[] raw = secretKey.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
                String cipherTextString = Base64.encodeToString(cipher.doFinal(textToEncrypt.getBytes(Charset.forName("UTF-8"))), Base64.DEFAULT);

                // 4. get public key
                X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(Base64.decode(publicKeyString, Base64.DEFAULT));
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(publicSpec);

                // 6. encrypt secret key using public key
                Cipher cipher2 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
                cipher2.init(Cipher.ENCRYPT_MODE, publicKey);
                String encryptedSecretKey = Base64.encodeToString(cipher2.doFinal(secretKey.getEncoded()), Base64.DEFAULT);

                // 7. pass cipherTextString (encypted sensitive data) and encryptedSecretKey to your server via your preferred way.
                // Tips:
                // You may use JSON to combine both the strings under 1 object.
                // You may use a volley call to send this data to your server.
            } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
                    | IllegalBlockSizeException | BadPaddingException
                    | InvalidKeySpecException | InvalidAlgorithmParameterException e) {

                e.printStackTrace();
            }
        }
    }

    public static class ServerDecrypt {
        static String privateKey = privateKeyString;
        static String encryptedTextString = textToEncrypt;
        static String encryptedSecretKeyString = "<your_received_encrypted_secret_key_here>";


        public static void Test() {

            try {

                // 1. Get private key
                PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT));
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = keyFactory.generatePrivate(privateSpec);

                // 2. Decrypt encrypted secret key using private key
                Cipher cipher1 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
                cipher1.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] secretKeyBytes = cipher1.doFinal(Base64.decode(encryptedSecretKeyString, Base64.DEFAULT));
                SecretKey secretKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "AES");

                // 3. Decrypt encrypted text using secret key
                byte[] raw = secretKey.getEncoded();
                SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
                byte[] original = cipher.doFinal(Base64.decode(encryptedTextString, Base64.DEFAULT));
                String text = new String(original, Charset.forName("UTF-8"));

                // 4. Print the original text sent by client
                System.out.println("text\n" + text + "\n\n");

            } catch (NoSuchAlgorithmException | InvalidKeySpecException
                    | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException
                    | BadPaddingException | InvalidAlgorithmParameterException e) {

                e.printStackTrace();
            }
        }
    }
}
