package com.yehyaumar.mappy;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import com.yehyaumar.mappy.data.DbAdapter;

import java.util.ArrayList;

import javax.crypto.SecretKey;

public class PasswordHelper {

    private DbAdapter db;
    private Cursor cursor;
    private String pass;
    private String salt;
    private Encryption encryption;
    private byte[] saltBytes = new byte[256/8];
    private Context context;

    PasswordHelper(Context context){
        this.context = context;
        encryption = new Encryption();
//        db = new DbAdapter(context);
//        db.open();
        db = DbAdapter.getDBInstance(context);
    }

    public boolean check(String textPassword) {
        if (textPassword.equals(""))
            return false;

        cursor = db.get(1);
        if (cursor.moveToFirst()) {
            pass = cursor.getString(cursor.getColumnIndex("cipher_text"));
            salt = cursor.getString(cursor.getColumnIndex("salt"));
            saltBytes = Base64.decode(salt, Base64.DEFAULT);
        }else {
            Toast.makeText(context, "NO DATA FOUND", Toast.LENGTH_SHORT).show();
        }

        SecretKey hashedPass = encryption.genKey(textPassword, saltBytes);
        String hashedPassStr = Base64.encodeToString(hashedPass.getEncoded(), Base64.DEFAULT);
//        db.close();

        return pass.equals(hashedPassStr);

    }

    public void setMasterPassword(String pass, boolean changePass) {
        ArrayList<String> list = encryption.genHashedPass(pass);
        String keyStr = list.get(0);
        String salt = list.get(1);
        long number;
        if (!changePass){
            number = db.insert("MasterPassword", keyStr, salt, null);
//            db.close();
        }
        else {
            db.update(1, "MasterPassword", keyStr, salt, null);
//            decryptEncrypt(newPass, oldPass);
        }

//        return true;

    }

    public void decryptEncrypt(String newPass, String oldPass){
        //decrypt encrypt everythng

        db.beginTransaction();
        setMasterPassword(newPass, true);
        cursor = db.getAll();

        while(cursor.moveToNext()){
            //decrypt //redundant
            String webAppName = cursor.getString(cursor.getColumnIndex("web_app"));
            String cipherText = cursor.getString(cursor.getColumnIndex("cipher_text"));
            String salt = cursor.getString(cursor.getColumnIndex("salt"));
            String iv = cursor.getString(cursor.getColumnIndex("iv"));
            long itemId = cursor.getLong(cursor.getColumnIndex("_id"));

            ArrayList<String> arrayList = new ArrayList<>();

            arrayList.add(0, salt);
            arrayList.add(1, iv);
            arrayList.add(2, cipherText);
            arrayList.add(3, oldPass);

            String unamePassStr = encryption.decrypt(arrayList);

            //encrypt
            ArrayList<String> encryptedList = encryption.encrypt
                    (unamePassStr, newPass);

            db.update(itemId, webAppName, encryptedList.get(0), encryptedList.get(1),
                    encryptedList.get(2));
        }

        db.setTransactionSuccessful();
        db.endTransaction();
//        db.close();

    }
}

