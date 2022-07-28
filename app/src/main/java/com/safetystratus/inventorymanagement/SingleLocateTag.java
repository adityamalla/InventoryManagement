package com.safetystratus.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Comparator;

public class SingleLocateTag extends Fragment {
    String user_id;
    String site_id;
    String token;
    String sso;
    String md5Pwd;
    String firstname;
    String lastname;
    String site_name;
    String loggedinUsername;
    String selectedSearchValue;
    String lastsynctime;
    public SingleLocateTag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_single_locate_tag, container, false);
        return v;

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SQLiteDatabase.loadLibs(getActivity().getApplicationContext());
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(getActivity().getApplicationContext());
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE);
    }

}
