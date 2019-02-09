package com.yehyaumar.mappy.data;

import android.provider.BaseColumns;

/**
 *
 * Created by YehyaUmar on 8/6/2017.
 */

class DbContract {
    final static class DbEntry implements BaseColumns{
        static final String TABLE_NAME = "pass_table";
        static final String COL_CIPHER_TEXT = "cipher_text"; //unamepass
        static final String COL_WEBAPP_NAME = "web_app";
//        static final String COL_UNAME = "unames";
//        static final String COL_PASSES = "passes";
        static final String COL_SALT = "salt";
        static final String COL_IV = "iv";
    }
}
