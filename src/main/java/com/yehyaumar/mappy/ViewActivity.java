package com.yehyaumar.mappy;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yehyaumar.mappy.data.DbAdapter;

import java.util.ArrayList;

/**
 * @author YehyaUmar
 * Activity used to display the user stored password and related details
 * after verifying the MasterPassword at the MainActivity.
 * */

public class ViewActivity extends AppCompatActivity {

    DbAdapter adapter;
    Encryption encryption;
    Cursor cursor;

    TextView webAppFld;
    TextView unameFld;
    TextView passFld;
    String newPass;
    long itemId;
    boolean webAppFlag = false;
    boolean WITHIN_APP = false;

    ClipboardManager clipboardManager;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
//        adapter = new DbAdapter(this);
//        adapter.open();
        adapter = DbAdapter.getDBInstance(this);

        encryption = new Encryption();

        itemId = getIntent().getLongExtra("ItemId", 0);
        newPass = getIntent().getStringExtra("NEWPASS");

        webAppFld = (TextView) findViewById(R.id.web_app);
        unameFld = (TextView) findViewById(R.id.uname);
        passFld = (TextView) findViewById(R.id.pass);

        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        inflateData();
//
//        InflationTask task = new InflationTask();
//        task.execute();

        Button editBtn = (Button) findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WITHIN_APP = true;
                startActivity(new Intent(ViewActivity.this, SaveActivity.class).putExtra("ItemId", itemId)
                    .putExtra("NEWPASS", newPass));
            }
        });

        unameFld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clipData = ClipData.newPlainText("Username", unameFld.getText());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(ViewActivity.this, "Username Copied", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        passFld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipData clipData = ClipData.newPlainText("UserName", passFld.getText());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(ViewActivity.this, "Password Copied", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private class InflationTask extends AsyncTask<String,String ,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ViewActivity.this);
            progressDialog.setMessage("Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String unamePassStr = "";
            String webAppName = "";
            cursor = adapter.get(itemId);
            if (cursor.moveToFirst()){
                 webAppName = cursor.getString(cursor.getColumnIndex("web_app"));
//
//                if(!webAppFlag)
//                    publishProgress(webAppName);

                String cipherText = cursor.getString(cursor.getColumnIndex("cipher_text"));
                String salt = cursor.getString(cursor.getColumnIndex("salt"));
                String iv = cursor.getString(cursor.getColumnIndex("iv"));

                ArrayList<String> arrayList = new ArrayList<>();

                arrayList.add(0, salt);
                arrayList.add(1, iv);
                arrayList.add(2, cipherText);
                arrayList.add(3, newPass);

                unamePassStr = encryption.decrypt(arrayList);

            }
            webAppFld.setText(webAppName);

            String[] text = unamePassStr.split(",");
            if(!(text[0].equals("")))
                unameFld.setText(text[0]);
            else unameFld.setVisibility(View.GONE);

            passFld.setText(text[1]);
            return webAppName + ","+ unamePassStr;
        }

//        @Override
//        protected void onProgressUpdate(String... values) {
//
//            webAppFld.setText(values[0]);
//            webAppFlag =true;
//        }

        @Override
        protected void onPostExecute(String webNamePassStr) {
            progressDialog.dismiss();

        }


    }

    void inflateData(){

        cursor = adapter.get(itemId);
        if (cursor.moveToFirst()){
            webAppFld.setText(cursor.getString(cursor.getColumnIndex("web_app")));
            String cipherText = cursor.getString(cursor.getColumnIndex("cipher_text"));
            String salt = cursor.getString(cursor.getColumnIndex("salt"));
            String iv = cursor.getString(cursor.getColumnIndex("iv"));

            ArrayList<String> arrayList = new ArrayList<>();

            arrayList.add(0, salt);
            arrayList.add(1, iv);
            arrayList.add(2, cipherText);
            arrayList.add(3, newPass);

            String unamePassStr = encryption.decrypt(arrayList);
            String[] text = unamePassStr.split(",");

            if(!(text[0].equals("")))
                unameFld.setText(text[0]);
            else unameFld.setVisibility(View.GONE);

            passFld.setText(text[1]);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
//        InflationTask task1 = new InflationTask();
//        task1.execute();
        inflateData();
    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(!WITHIN_APP){
//            startActivity(new Intent(ViewActivity.this, LoginActivity.class));
//            finish();
//        }
//    }

}
