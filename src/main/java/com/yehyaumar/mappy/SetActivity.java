package com.yehyaumar.mappy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author YehyaUmar
 * Activity started when opening the app for the first time.
 * Set MasterPassword for the app here.
 * */
public class SetActivity extends AppCompatActivity {

    PrefManager prefManager;
    EditText setPassField;
    Button setBtn;
    String newPass;
    boolean confirm;
    boolean changePass;
    String oldPass;
    ProgressDialog progressDialog;

    PasswordHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        changePass = getIntent().getBooleanExtra("CHANGEPASS", false);
        oldPass = getIntent().getStringExtra("OLDPASS");
        helper = new PasswordHelper(SetActivity.this);


        if (!prefManager.isFirstTimeLaunch() && !changePass) {
            launchLoginActivty();
            finish();
        }
        else {

            confirm = false;

            setContentView(R.layout.activity_set);

            setBtn = (Button) findViewById(R.id.set_btn);
            setPassField = (EditText) findViewById(R.id.setPassField);


            setBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!confirm) {
                        newPass = setPassField.getText().toString();

                        if (newPass.isEmpty()) {
                            Snackbar.make(v, "Password can't be empty", Snackbar.LENGTH_SHORT)
                                    .setAction("action", null).show();
                        } else {
                            setPassField.setText("");
                            setBtn.setText(R.string.confirm);
                            setPassField.setHint(R.string.confirm_pass);
                            confirm = true;
                        }
                    } else if (confirm) {

                        String confirmPass = setPassField.getText().toString();

                        if (confirmPass.isEmpty()) {
                            Snackbar.make(v, "Password can't be empty", Snackbar.LENGTH_SHORT)
                                    .setAction("action", null).show();
                        } else {
                            if (newPass.equals(confirmPass)) {
                                setPassField.setText("");

                                if(changePass){
                                    DecryptEncryptTask task = new DecryptEncryptTask();
                                    task.execute(newPass, oldPass);

                                }else{
                                    helper.setMasterPassword(newPass, changePass);
                                    startMainActivity();

                                }

                            } else {
                                Snackbar.make(v, "Password do not match", Snackbar.LENGTH_SHORT)
                                        .setAction("action", null).show();
                                confirm = false;
                                setPassField.setText("");
                                setPassField.setHint(R.string.set_pass);
                                setBtn.setText(R.string.set);
                            }

                        }
                    }

                }
            });
        }

    }

    private void startMainActivity(){

        startActivity(new Intent(SetActivity.this, MainActivity.class)
                .putExtra("NEWPASS", newPass));

        prefManager.setFirstTimeLaunch(false);
        finish();
    }

    private void launchLoginActivty() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(SetActivity.this, LoginActivity.class));
    }

    private class DecryptEncryptTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(SetActivity.this);
            progressDialog.setMessage("Encrypting with new password...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params){
            helper.decryptEncrypt(params[0], params[1]);

            return null;
        }

        @Override
        protected void onPostExecute(String string){
            progressDialog.dismiss();
            startMainActivity();
        }

    }


//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
}
