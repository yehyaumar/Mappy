package com.yehyaumar.mappy;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by YehyaUmar on 8/26/2017.
 * Encryption Code
 */

class Encryption {
    private final int ITERATION_COUNT = 8000;
    private final int KEY_LENGTH = 256;
    private final int SALT_LENGTH = KEY_LENGTH/8;

    private Cipher cipher;
    private IvParameterSpec ivParameterSpec;
    private byte[] saltBytes;
    private SecureRandom random;

    Encryption(){
        random = new SecureRandom();

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        }catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.v("ERROR", e.getMessage());
        }
    }

    private byte[] generateSalt(){
        saltBytes = new byte[SALT_LENGTH];
        random.nextBytes(saltBytes);
        return saltBytes;
    }

    private byte[] generateIV(){
        byte[] iv = new byte[cipher.getBlockSize()];
        random.nextBytes(iv);
        return iv;
    }

    ArrayList<String> genHashedPass(String password){
        byte[] saltBytes = generateSalt();
        SecretKey key = genKey(password, saltBytes);
        String salt = Base64.encodeToString(saltBytes, Base64.DEFAULT);
        String pass = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        ArrayList<String> list = new ArrayList<>();
        list.add(pass);
        list.add(salt);
        return list;
    }

    SecretKey genKey(String password, byte[] salt)  {

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                ITERATION_COUNT, KEY_LENGTH);
        try{
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");

        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            Log.v("ERROR", e.getMessage());
            return null;
        }
    }

    ArrayList<String> encrypt(String plainText, String password){
        byte[] saltBytes = generateSalt();
        String salt = Base64.encodeToString(saltBytes, Base64.NO_WRAP);

        byte[] ivBytes = generateIV();
        String iv = Base64.encodeToString(ivBytes, Base64.NO_WRAP);

        ArrayList<String> list = new ArrayList<>();

        ivParameterSpec = new IvParameterSpec(ivBytes);

        try {
            cipher.init(Cipher.ENCRYPT_MODE, genKey(password, saltBytes), ivParameterSpec);
            byte[] cipherBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

            String cipherText = Base64.encodeToString(cipherBytes, Base64.NO_WRAP);

            list.add(cipherText);
            list.add(salt);
            list.add(iv);

            return list;

        } catch (InvalidKeyException | InvalidAlgorithmParameterException |
                BadPaddingException | UnsupportedEncodingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }

    String decrypt(ArrayList<String> list){
        String salt = list.get(0);
        String iv = list.get(1);
        String cipherText = list.get(2);
        String password = list.get(3);

        byte[] cipherBytes = Base64.decode(cipherText, Base64.DEFAULT);

        byte[] saltBytes = Base64.decode(salt, Base64.DEFAULT);
        byte[] ivBytes = Base64.decode(iv, Base64.DEFAULT);
        ivParameterSpec = new IvParameterSpec(ivBytes);

        try {
            cipher.init(Cipher.DECRYPT_MODE, genKey(password, saltBytes), ivParameterSpec);
            byte[] plaintextBytes = cipher.doFinal(cipherBytes);
            return new String(plaintextBytes, "UTF-8");

        } catch (InvalidKeyException | InvalidAlgorithmParameterException |
                BadPaddingException | UnsupportedEncodingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();

            return "Invalid Password";
        }
    }
}
