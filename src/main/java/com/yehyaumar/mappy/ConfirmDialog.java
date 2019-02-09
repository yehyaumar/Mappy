package com.yehyaumar.mappy;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ConfirmDialog extends Dialog  {

    private boolean yesClicked;

    public ConfirmDialog(@NonNull Context context) {
        super(context);
        yesClicked = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm);

//        Button yesBtn = findViewById(R.id.yes_btn);
//        Button noBtn = findViewById(R.id.no_btn);

//        yesBtn.setOnClickListener(this);
//        noBtn.setOnClickListener(this);
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.yes_btn:
//                yesClicked = true;
//                dismiss();
//                break;
//            case R.id.no_btn:
//                dismiss();
//                break;
//            default:
//                break;
//        }
//    }
//
//    public boolean isYesClicked() {
//        return yesClicked;
//    }
}
