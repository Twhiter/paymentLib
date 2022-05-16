package com.github.Twhiter.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {


    public static byte[] encrypt(byte[] textBytes, String base64EncodedPublicKey) {

        try {
            PublicKey publicKey = getPublicKeyFromBase64String(base64EncodedPublicKey);
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            return encryptCipher.doFinal(textBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] sign(byte[] textBytes, String base64EncodedPrivateKey) {

        try {
            PrivateKey privateKey = getPrivateKeyFromBase64String(base64EncodedPrivateKey);
            Signature privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(privateKey);
            privateSignature.update(textBytes);

            return privateSignature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static PrivateKey getPrivateKeyFromBase64String(String base64EncodedPrivateKey) {

        try {

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] privateKeysBytes = Base64.getDecoder().decode(base64EncodedPrivateKey);

            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeysBytes);
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static PublicKey getPublicKeyFromBase64String(String base64EncodedPublicKey) {

        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(base64EncodedPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }


}