package com.yehyaumar.mappy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * @author YehyaUmar
 * Activity started when opening the app.
 * Login using MasterPassword to enter the MainActivity.
 * */

public class LoginActivity  extends AppCompatActivity {

    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final EditText passwordTxtField = (EditText) findViewById(R.id.passField);

        Button loginBtn = (Button) findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = passwordTxtField.getText().toString();

                PasswordHelper helper = new PasswordHelper(LoginActivity.this);

                if (newPass.isEmpty()){
                    Snackbar.make(v, "Input password", Snackbar.LENGTH_SHORT)
                            .setAction("action", null).show();
                }else {

                    boolean check = helper.check(newPass);

                    if (check) {
                        passwordTxtField.setText("");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                .putExtra("NEWPASS", newPass));
                        finish();

                    } else {
                        passwordTxtField.setText("");
                        Snackbar.make(v, "Try again", Snackbar.LENGTH_SHORT)
                                .setAction("action", null).show();                    }
                }
            }
        });

    }
}
