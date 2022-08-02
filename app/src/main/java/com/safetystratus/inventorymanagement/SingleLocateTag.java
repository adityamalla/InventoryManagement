package com.safetystratus.inventorymanagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.RFIDResults;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.TagData;
import com.safetystratus.inventorymanagement.Constants.*;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PrimitiveIterator;

import static android.content.ContentValues.TAG;
import static com.safetystratus.inventorymanagement.RFIDHandler.TagProximityPercent;

public class SingleLocateTag extends Fragment{
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
    TextView singleLocate;
    RFIDHandler rfidHandler;

    public SingleLocateTag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_single_locate_tag, container, false);
        singleLocate = (TextView) v.findViewById(R.id.singleLocate);
        return v;

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SQLiteDatabase.loadLibs(getActivity().getApplicationContext());
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(getActivity().getApplicationContext());
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE);
    }

}
