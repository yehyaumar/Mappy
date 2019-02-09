package com.yehyaumar.mappy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {

//    boolean result = false;
    Dialog dialog;
    String pass;
    boolean verified;

    SwitchPreference switchPreference;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.settings);

//        final PrefManager manager = new PrefManager(getActivity());

        Preference button = findPreference(getString( R.string.change_password));

        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                handleDialog();

                boolean verified = verify();
                if(verified){
                    startActivity(new Intent(getActivity(), SetActivity.class)
                            .putExtra("CHANGEPASS", true).putExtra("OLDPASS", pass));
                }
//                if (result)


                return true;
            }
        });

//        switchPreference = (SwitchPreference) findPreference("verification");
//        switchPreference.
//        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o) {
//
//                if (switchPreference.isChecked()){
//
//                    handleDialog();
//                    boolean verified = verify(false);
//
//                    if(verified)
//                        switchPreference.setChecked(false);
//                    else
//                        switchPreference.setChecked(true);
//
//                }else {
//                    switchPreference.setChecked(false);
//                }
//
//                return true;
//            }
//        });


    }

    private void handleDialog(){
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_verify);
        dialog.show();

    }

    private boolean verify(){
        Button button = dialog.findViewById(R.id.verify_btn);
        final EditText verifyEdText = dialog.findViewById(R.id.verify_pass);
        verified = false;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = verifyEdText.getText().toString();

                PasswordHelper helper = new PasswordHelper(getActivity());
                boolean check = helper.check(pass);


                if(check){
                    verified = true;
                    dialog.dismiss();

                }else {
                    verified = false;
                    Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
                }
                verifyEdText.setText("");

            }
        });
        return verified;
    }
}
