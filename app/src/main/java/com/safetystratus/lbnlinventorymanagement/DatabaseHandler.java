package com.safetystratus.lbnlinventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.security.auth.login.LoginException;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler instance;
    public DatabaseHandler(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
    }
    static public synchronized DatabaseHandler getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHandler(context);
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_SITE_USERS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_OT_ORGANIZATION);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_OT_DEPARTMENTS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_FI_LOCATIONS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_FI_FACILITIES);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_FI_ROOM_TYPES);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_FI_FACIL_ROOMS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_FI_ROOM_DEPT);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}

