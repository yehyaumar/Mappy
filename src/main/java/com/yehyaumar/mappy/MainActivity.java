package com.yehyaumar.mappy;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yehyaumar.mappy.data.DbAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YehyaUmar
 * Activity started when passing the LoginActivity.
 * Shows user stored details as a list.
 * */

public class MainActivity extends AppCompatActivity {
    TextView nothingYetTextView;
    DbAdapter db;
    ListView listView;
    ListAdapter listAdapter;
    String newPass;
    String oldPass;
    boolean changePass;
//    long itemId;
    PasswordHelper helper;
    PrefManager manager;
    boolean WITHIN_APP = false;
    int listItemSelectedCount;

    List<Long> positionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = new PrefManager(this);

        newPass = getIntent().getStringExtra("NEWPASS");
//        oldPass = getIntent().getStringExtra("OLDPASS");
//        changePass = getIntent().getBooleanExtra("CHANGEPASS", false);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WITHIN_APP = true;
                startActivity(new Intent( MainActivity.this, SaveActivity.class)
                        .putExtra("NEWPASS", newPass)
                        .putExtra("WITHIN_APP", WITHIN_APP));
            }
        });

        nothingYetTextView = (TextView) findViewById(R.id.no_notes_textview);
//        db = new DbAdapter(this);
//        db.open();
        db = DbAdapter.getDBInstance(MainActivity.this);
        buildList();

//        db.close();
    }




    void buildList(){
        Cursor cursor = db.getAll();

        if(cursor != null){
            listAdapter = new ListAdapter(getApplicationContext(), cursor, 0);

            if(!(listAdapter.isEmpty()))
                nothingYetTextView.setVisibility(View.GONE);

            listView = (ListView) findViewById(R.id.list);

            listView.setAdapter(listAdapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

            addActionListener();
        }

    }

    void addActionListener(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final long itemId = listAdapter.getItemId(position);
                if (manager.isVerificationEnabled()){
                    handleDialog(itemId);
                }else{
                    startActivity(new Intent(MainActivity.this, ViewActivity.class)
                            .putExtra("ItemId", itemId)
                            .putExtra("NEWPASS", newPass));
                }
            }
        });

        positionsList = new ArrayList<>();
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {

                listItemSelectedCount = listView.getCheckedItemCount();
                actionMode.setTitle(listItemSelectedCount + " Selected");

                if(checked)
                    positionsList.add(id);
                else
                    positionsList.remove(id);

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.listview_contextmenu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.delete:

                        final ConfirmDialog confirmDialog = new ConfirmDialog(MainActivity.this);
                        confirmDialog.show();
                        Button yesBtn = confirmDialog.findViewById(R.id.yes_btn);
                        Button noBtn = confirmDialog.findViewById(R.id.no_btn);
                        yesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                for (long id : positionsList) {
                                    db.deleteRow(id);
                                    buildList();
                                    if(listAdapter.isEmpty())
                                        nothingYetTextView.setVisibility(View.VISIBLE);
                                }
                                confirmDialog.dismiss();
                            }
                        });

                        noBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                confirmDialog.dismiss();
                            }
                        });
//                        Log.i("Confirmation", ""+confirmDialog.isYesClicked());
//                        if (confirmDialog.isYesClicked()) {
//
//                        }

                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                listItemSelectedCount = 0;

            }
        });


    }

//  try encapsulating this
    private void handleDialog(final long itemId){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_verify);
        dialog.show();

        Button button = dialog.findViewById(R.id.verify_btn);
        final EditText verifyEdText = (EditText) dialog.findViewById(R.id.verify_pass);
        helper = new PasswordHelper(MainActivity.this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = verifyEdText.getText().toString();

                boolean check = helper.check(pass);
                if(check){
                    dialog.dismiss();
                    startActivity(new Intent(MainActivity.this, ViewActivity.class)
                            .putExtra("ItemId", itemId)
                            .putExtra("NEWPASS", pass));

                }else {
                    Toast.makeText(MainActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
                verifyEdText.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                WITHIN_APP = true;
                startActivity(new Intent(MainActivity.this, Settings.class)
                        .putExtra("WITHIN_APP", WITHIN_APP));
                return true;
            case R.id.about:
                WITHIN_APP = true;
                startActivity(new Intent(MainActivity.this, AboutActivity.class)
                        .putExtra("WITHIN_APP", WITHIN_APP));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
//        db.open();

        buildList();

//        db.close();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(!WITHIN_APP){
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            finish();
//        }
//    }

    //
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        db.close();
//    }
}
