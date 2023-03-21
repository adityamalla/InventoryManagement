package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RFIDActivity extends AppCompatActivity {
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;
    boolean connected = false;
    String loggedinUsername = "";
    String loggedinUserSiteId = "";
    String md5Pwd = "";
    String selectedUserId = "";
    String selectedSearchValue = "";
    String sso = "";
    String site_name = "";
    String token="";
    EditText employeeId;
    EditText building;
    EditText room;
    Button scanRFID;
    String selectedFacilName = "";
    String selectedFacil = "";
    String selectedRoomName = "";
    String selectedRoom = "";
    String empName = "";
    ConstraintLayout header;
    ProgressDialog progressSynStart = null;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid);
        SQLiteDatabase.loadLibs(this);
        hideKeyboard(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.header);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        header = (ConstraintLayout) findViewById(R.id.header);
        TextView tv = (TextView) findViewById(R.id.headerId);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.RED);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);
        tv.setText("Reconciliation");
        tv.setTextSize(20);
        tv.setVisibility(View.VISIBLE);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo result = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(result!=null) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            } else
                connected = false;
        }else{
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                connected = true;
            }else{
                connected = false;
            }
        }
        Intent intent = getIntent();
        sso = intent.getStringExtra("sso");
        if (intent.getStringExtra("token") != null) {
            token = intent.getStringExtra("token");
        }
        if(intent.getStringExtra("empName")!=null) {
            empName = intent.getStringExtra("empName");
        }
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("loggedinUsername");
        selectedUserId = intent.getStringExtra("user_id");
        loggedinUserSiteId = intent.getStringExtra("site_id");
        md5Pwd = intent.getStringExtra("md5pwd");
        if (intent.getStringExtra("selectedSearchValue") != null) {
            selectedSearchValue = intent.getStringExtra("selectedSearchValue");
        }
        if (intent.getStringExtra("selectedFacilName") != null) {
            selectedFacilName = intent.getStringExtra("selectedFacilName");
        }
        if (intent.getStringExtra("selectedFacil") != null) {
            selectedFacil = intent.getStringExtra("selectedFacil");
        }
        if (intent.getStringExtra("selectedRoom") != null) {
            selectedRoom = intent.getStringExtra("selectedRoom");
        }
        if (intent.getStringExtra("selectedRoomName") != null) {
            selectedRoomName = intent.getStringExtra("selectedRoomName");
        }
        employeeId = (EditText)findViewById(R.id.employeeId);
        building = (EditText)findViewById(R.id.building);
        room = (EditText)findViewById(R.id.room);
        scanRFID = (Button)findViewById(R.id.scanRFID);
        employeeId.setText(loggedinUsername);
        employeeId.setEnabled(false);
        if(selectedFacilName.trim().length()>0){
            building.setText(selectedFacilName);
        }else{
            building.setText("None");
        }
        if(selectedRoomName.trim().length()>0){
            room.setText(selectedRoomName);
        }else if(selectedFacil.trim().length()>0 && intent.getStringExtra("fromFacil")!=null){
            ArrayList<MyObject> roomlist = databaseHandler.getRoomList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedFacil);
            if(roomlist.size()>0) {
                room.setText(roomlist.get(0).getObjectName());
                selectedRoomName = roomlist.get(0).getObjectName();
                selectedRoom = roomlist.get(0).getObjectId();
            }else{
                room.setText("None");
            }
        }else{
            room.setText("None");
        }
        building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
                final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
                try {
                    ArrayList<MyObject> facillist = databaseHandler.getBuildingList(databaseHandler.getWritableDatabase(PASS_PHRASE));
                    final Intent myIntent = new Intent(RFIDActivity.this,
                            BuildingList.class);
                    myIntent.putExtra("user_id", selectedUserId);
                    myIntent.putExtra("site_id", loggedinUserSiteId);
                    myIntent.putExtra("token", token);
                    myIntent.putExtra("sso", sso);
                    myIntent.putExtra("md5pwd", md5Pwd);
                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                    myIntent.putExtra("site_name", site_name);
                    myIntent.putExtra("facillist",facillist);
                    myIntent.putExtra("selectedFacilName", selectedFacilName);
                    myIntent.putExtra("selectedFacil", selectedFacil+"");
                    myIntent.putExtra("selectedRoomName", selectedRoomName);
                    myIntent.putExtra("selectedRoom", selectedRoom+"");
                    myIntent.putExtra("empName", empName);
                    startActivity(myIntent);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    // progress.dismiss();
                    db.close();
                    if (databaseHandler != null) {
                        databaseHandler.close();
                    }
                }
            }
        });
        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
                final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
                if(selectedFacil.length()>0){
                    try {
                        ArrayList<MyObject> roomlist = databaseHandler.getRoomList(db,selectedFacil);
                        final Intent myIntent = new Intent(RFIDActivity.this,
                                RoomList.class);
                        myIntent.putExtra("user_id", selectedUserId);
                        myIntent.putExtra("site_id", loggedinUserSiteId);
                        myIntent.putExtra("token", token);
                        myIntent.putExtra("sso", sso);
                        myIntent.putExtra("md5pwd", md5Pwd);
                        myIntent.putExtra("loggedinUsername", loggedinUsername);
                        myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                        myIntent.putExtra("site_name", site_name);
                        myIntent.putExtra("roomlist",roomlist);
                        myIntent.putExtra("selectedFacilName", selectedFacilName);
                        myIntent.putExtra("selectedFacil", selectedFacil+"");
                        myIntent.putExtra("selectedFacilName", selectedFacilName);
                        myIntent.putExtra("selectedFacil", selectedFacil+"");
                        myIntent.putExtra("selectedRoomName", selectedRoomName);
                        myIntent.putExtra("selectedRoom", selectedRoom+"");
                        myIntent.putExtra("empName", empName);
                        startActivity(myIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        // progress.dismiss();
                        db.close();
                        if (databaseHandler != null) {
                            databaseHandler.close();
                        }
                    }
                }else{
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDActivity.this);
                    dlgAlert.setTitle("SafetyStratus");
                    dlgAlert.setMessage("Please select a building");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dlgAlert.create().show();
                }

            }
        });
        scanRFID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedRoom.length()>0){
                    final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
                    final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
                    try {
                        ContentValues cv = new ContentValues();
                        cv.put("location_id", selectedFacil);
                        cv.put("user_id", selectedUserId);
                        cv.put("room_id", selectedRoom);
                        int reconc_id = databaseHandler.insertReconciliaionData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                        Log.e("fff",reconc_id+"**");
                        int inventoryCount = databaseHandler.checkCount(db,selectedRoom);
                        final Intent myIntent = new Intent(RFIDActivity.this,
                                RFIDScannerActivity.class);
                        myIntent.putExtra("user_id", selectedUserId);
                        myIntent.putExtra("site_id", loggedinUserSiteId);
                        myIntent.putExtra("token", token);
                        myIntent.putExtra("sso", sso);
                        myIntent.putExtra("md5pwd", md5Pwd);
                        myIntent.putExtra("loggedinUsername", loggedinUsername);
                        myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                        myIntent.putExtra("site_name", site_name);
                        myIntent.putExtra("selectedFacilName", selectedFacilName);
                        myIntent.putExtra("selectedFacil", selectedFacil+"");
                        myIntent.putExtra("selectedFacilName", selectedFacilName);
                        myIntent.putExtra("selectedFacil", selectedFacil+"");
                        myIntent.putExtra("selectedRoomName", selectedRoomName);
                        myIntent.putExtra("selectedRoom", selectedRoom+"");
                        myIntent.putExtra("reconc_id", reconc_id+"");
                        myIntent.putExtra("empName", empName);
                        myIntent.putExtra("total_inventory", inventoryCount+"");
                        startActivity(myIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        // progress.dismiss();
                        db.close();
                        if (databaseHandler != null) {
                            databaseHandler.close();
                        }
                    }
                }else{
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDActivity.this);
                    dlgAlert.setTitle("SafetyStratus");
                    dlgAlert.setMessage("Seems like you haven't downloaded rooms for the building '"+selectedFacilName+"'. Please go to home page and download the rooms or you haven't selected any room!");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dlgAlert.create().show();
                }

            }});
    }
    @Override
    public void onBackPressed() {
    }
    public static void hideKeyboard(RFIDActivity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            final Intent myIntent = new Intent(RFIDActivity.this,
                    HomeActivity.class);
            myIntent.putExtra("user_id", selectedUserId);
            myIntent.putExtra("site_id", loggedinUserSiteId);
            myIntent.putExtra("token", token);
            myIntent.putExtra("sso", sso);
            myIntent.putExtra("md5pwd", md5Pwd);
            myIntent.putExtra("loggedinUsername", loggedinUsername);
            myIntent.putExtra("site_name", site_name);
            myIntent.putExtra("pageLoadTemp", "-1");
            myIntent.putExtra("empName", empName);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }

}