package com.yehyaumar.mappy;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by YehyaUmar on 8/28/2017.
 */

class ListAdapter extends CursorAdapter {

    private final int MAX = 20;

    ListAdapter(Context context, Cursor cursor, int flag){
        super(context, cursor, flag);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = view.findViewById(R.id.list_item);
        String webAppName = cursor.getString(cursor.getColumnIndex("web_app"));

        if(webAppName.length() > MAX){
            webAppName = webAppName.substring(0, MAX) + "...";
        }

        textView.setText(webAppName);
    }

}
