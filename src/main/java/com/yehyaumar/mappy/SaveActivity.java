package com.yehyaumar.mappy;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yehyaumar.mappy.data.DbAdapter;

import java.util.ArrayList;

/**
 * @author YehyaUmar
 * Activity to Save the details such as Username and Password.
 * Started when pressing the FloatingActionButton on MainActivity
 * or pressing Edit button on ViewActivity.
 * */

public class SaveActivity extends AppCompatActivity {

    Encryption encryption;
    DbAdapter db;
    boolean WITHIN_APP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        final String textPassword = getIntent().getStringExtra("NEWPASS");
        final long itemId = getIntent().getLongExtra("ItemId", -1);

        encryption = new Encryption();
//        db = new DbAdapter(this);
//        db.open();
        db = DbAdapter.getDBInstance(this);

        final EditText webAppFld = (EditText) findViewById(R.id.web_app);
        final EditText unameFld = (EditText) findViewById(R.id.uname);
        final EditText passFld = (EditText) findViewById(R.id.pass);
        Button saveBtn = (Button) findViewById(R.id.save_btn);


        if (itemId != -1) {
            Cursor cursor = db.get(itemId);
            if (cursor.moveToFirst()) {
                webAppFld.setText(cursor.getString(cursor.getColumnIndex("web_app")));
                webAppFld.setSelection(webAppFld.getText().length());

                String cipherText = cursor.getString(cursor.getColumnIndex("cipher_text"));
                String salt = cursor.getString(cursor.getColumnIndex("salt"));
                String iv = cursor.getString(cursor.getColumnIndex("iv"));

                ArrayList<String> arrayList = new ArrayList<>();

                arrayList.add(0, salt);
                arrayList.add(1, iv);
                arrayList.add(2, cipherText);
                arrayList.add(3, textPassword);

                String unamePassStr = encryption.decrypt(arrayList);
                String[] text = unamePassStr.split(",");

                unameFld.setText(text[0]);
                passFld.setText(text[1]);

            }
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String webAppName = webAppFld.getText().toString().trim();
                String unameStr = unameFld.getText().toString(); // code can be reduced here....
                String passStr = passFld.getText().toString();

                if (webAppName.isEmpty()) {
                    Snackbar.make(v, "Website or App name is required", Snackbar.LENGTH_SHORT)
                            .setAction("action", null).show();
                } else if (passStr.isEmpty()) {
                    Snackbar.make(v, "Password is required", Snackbar.LENGTH_SHORT)
                            .setAction("action", null).show();
                } else {
                    ArrayList<String> encryptedList = encryption.encrypt
                            (unameStr + "," + passStr, textPassword);

                    if (itemId == -1) {
                        db.insert(webAppName, encryptedList.get(0), encryptedList.get(1),
                                encryptedList.get(2));
                    } else {
                        db.update(itemId, webAppName, encryptedList.get(0), encryptedList.get(1),
                                encryptedList.get(2));
                    }

                    finish();
                }
            }
        });
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(!WITHIN_APP){
//            startActivity(new Intent(SaveActivity.this, LoginActivity.class));
//            finish();
//        }
//    }

    //
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        db.close();
//    }
}
