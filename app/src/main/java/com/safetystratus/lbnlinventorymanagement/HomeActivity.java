package com.safetystratus.lbnlinventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import net.sqlcipher.database.SQLiteDatabase;

public class HomeActivity extends AppCompatActivity {
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SQLiteDatabase.loadLibs(this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(HomeActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
    }
}