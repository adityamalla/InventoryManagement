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
                building.setClickable(false);
                building.setEnabled(false);
                /*final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
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
                }*/
                LoadBuildingList cobj = new LoadBuildingList();
                cobj.execute();
            }
        });
        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                room.setClickable(false);
                room.setEnabled(false);
                final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
                final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
                if(selectedFacil.length()>0){
                    /*try {
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
                    }*/
                    LoadRoomList cobj = new LoadRoomList();
                    cobj.execute();
                }else{
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDActivity.this);
                    dlgAlert.setTitle("SafetyStratus");
                    dlgAlert.setMessage("Please select a building");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    room.setClickable(true);
                                    room.setEnabled(true);
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
                    scanRFID.setEnabled(false);
                    /*final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
                    final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
                    try {
                        int rec_id = databaseHandler.checkReconciliationStarted(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedFacil,selectedRoom,selectedUserId);
                        if (rec_id>0){
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDActivity.this);
                            dlgAlert.setTitle("SafetyStratus");
                            dlgAlert.setMessage("Reconciliation has already been initiated for this location '"+selectedRoomName+"'. Would you like to proceed with the reconciliation?");
                            dlgAlert.setPositiveButton("Continue",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            ScanInfo sc = databaseHandler.getPendingReconcScans(databaseHandler.getWritableDatabase(PASS_PHRASE),String.valueOf(rec_id));
                                            final Intent myIntent = new Intent(RFIDActivity.this,
                                                    RFIDScannerActivity.class);
                                            myIntent.putExtra("user_id", selectedUserId);
                                            myIntent.putExtra("site_id", loggedinUserSiteId);
                                            myIntent.putExtra("token", token);
                                            myIntent.putExtra("sso", sso);
                                            myIntent.putExtra("md5pwd", md5Pwd);
                                            myIntent.putExtra("loggedinUsername", loggedinUsername);
                                            myIntent.putExtra("site_name", site_name);
                                            myIntent.putExtra("selectedFacilName", selectedFacilName);
                                            myIntent.putExtra("selectedFacil", selectedFacil+"");
                                            myIntent.putExtra("selectedRoomName", selectedRoomName);
                                            myIntent.putExtra("selectedRoom", selectedRoom+"");
                                            myIntent.putExtra("fromContinueInsp","true");
                                            myIntent.putExtra("empName",empName);
                                            myIntent.putExtra("json_data_from_continue",sc.getJson_data());
                                            myIntent.putExtra("reconc_id", rec_id+"");
                                            int inventoryCount = databaseHandler.checkCount(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
                                            myIntent.putExtra("total_inventory", inventoryCount+"");
                                            startActivity(myIntent);
                                        }
                                    });
                            dlgAlert.setNegativeButton("New",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDActivity.this);
                                            dlgAlert.setTitle("SafetyStratus");
                                            dlgAlert.setMessage("Are you sure you want to proceed? This action will result in the complete erasure of the previously saved reconciliation.");
                                            dlgAlert.setPositiveButton("Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            ContentValues cv = new ContentValues();
                                                            cv.put("location_id", selectedFacil);
                                                            cv.put("user_id", selectedUserId);
                                                            cv.put("room_id", selectedRoom);
                                                            databaseHandler.deletePendingScanByReconc_id(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE),String.valueOf(rec_id));
                                                            int reconc_id = databaseHandler.insertReconciliaionData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                                                            int inventoryCount = databaseHandler.checkCount(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE),selectedRoom);
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
                                                        }
                                                    });
                                            dlgAlert.setNegativeButton("No",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            return;
                                                    }
                                            });
                                            dlgAlert.create().show();
                                        }
                                    });
                            dlgAlert.create().show();
                        }else{
                            ContentValues cv = new ContentValues();
                            cv.put("location_id", selectedFacil);
                            cv.put("user_id", selectedUserId);
                            cv.put("room_id", selectedRoom);
                            int reconc_id = databaseHandler.insertReconciliaionData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
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
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        // progress.dismiss();
                        db.close();
                        if (databaseHandler != null) {
                            databaseHandler.close();
                        }
                    }*/
                    StartReconciliation cobj = new StartReconciliation();
                    cobj.execute();
                }
                else{
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDActivity.this);
                    dlgAlert.setTitle("SafetyStratus");
                    dlgAlert.setMessage("Seems like you haven't downloaded rooms for the building '"+selectedFacilName+"'. Please go to home page and download the rooms or you haven't selected any room!");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    scanRFID.setEnabled(true);
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
    class LoadBuildingList extends AsyncTask<String, String, String> {
        private ProgressDialog progressSync = new ProgressDialog(RFIDActivity.this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        ArrayList<MyObject> facillist = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // disable dismiss by tapping outside of the dialog
            progressSync.setTitle("");
            progressSync.setMessage("Loading building data..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(450, 200);
            super.onPreExecute();
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            // db.beginTransaction();
            try {
                facillist = databaseHandler.getBuildingList(databaseHandler.getWritableDatabase(PASS_PHRASE));
            } finally {
                db.close();
                if (databaseHandler != null) {
                    databaseHandler.close();
                }
            }
            return "completed";
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //building.setClickable(true);
            //HashMap<String, String> settings = databaseHandler.getPermissionDetails(databaseHandler.getWritableDatabase(PASS_PHRASE));
            if (progressSync != null && progressSync.isShowing()){
                progressSync.dismiss();
                progressSync = null;
            }
            if (facillist.size()>0){
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
            } else {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDActivity.this);
                dlgAlert.setTitle("Safety Stratus");
                dlgAlert.setMessage("Building information is not available on this device! Please contact administrator");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                dlgAlert.create().show();
            }
        }
    }
    class LoadRoomList extends AsyncTask<String, String, String> {
        private ProgressDialog progressSync = new ProgressDialog(RFIDActivity.this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        ArrayList<MyObject> roomlist = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // disable dismiss by tapping outside of the dialog
            progressSync.setTitle("");
            progressSync.setMessage("Loading room list..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(450, 200);
            super.onPreExecute();
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            // db.beginTransaction();
            try {
                roomlist = databaseHandler.getRoomList(db,selectedFacil);
            } finally {
                db.close();
                if (databaseHandler != null) {
                    databaseHandler.close();
                }
            }
            return "completed";
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //room.setClickable(true);
            //HashMap<String, String> settings = databaseHandler.getPermissionDetails(databaseHandler.getWritableDatabase(PASS_PHRASE));
            if (progressSync != null && progressSync.isShowing()){
                progressSync.dismiss();
                progressSync = null;
            }
            if (roomlist.size()>0){
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
            } else {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDActivity.this);
                dlgAlert.setTitle("Safety Stratus");
                dlgAlert.setMessage("It appears that the building data you have chosen has not been downloaded. Kindly download the data and attempt the task again!");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                room.setClickable(true);
                                room.setEnabled(true);
                                return;
                            }
                        });
                dlgAlert.create().show();
            }
        }
    }
    public int inventoryCount = 0;
    class StartReconciliation extends AsyncTask<String, String, String> {
        private ProgressDialog progressSync = new ProgressDialog(RFIDActivity.this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        Boolean reconc_initiated = false;
        int rec_id = 0;
        int new_rec_id = 0;
        ScanInfo sc = null;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // disable dismiss by tapping outside of the dialog
            progressSync.setTitle("");
            progressSync.setMessage("Commencing the reconciliation process..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(450, 200);
            super.onPreExecute();
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            // db.beginTransaction();
            try {
                rec_id = databaseHandler.checkReconciliationStarted(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedFacil,selectedRoom,selectedUserId);
                inventoryCount = databaseHandler.checkCount(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
                if (rec_id > 0){
                    reconc_initiated = true;
                    sc = databaseHandler.getPendingReconcScans(databaseHandler.getWritableDatabase(PASS_PHRASE),String.valueOf(rec_id));
                }else{
                    reconc_initiated = false;
                    ContentValues cv = new ContentValues();
                    cv.put("location_id", selectedFacil);
                    cv.put("user_id", selectedUserId);
                    cv.put("room_id", selectedRoom);
                    new_rec_id = databaseHandler.insertReconciliaionData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                }
            } finally {
                db.close();
                if (databaseHandler != null) {
                    databaseHandler.close();
                }
            }
            return "completed";
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //HashMap<String, String> settings = databaseHandler.getPermissionDetails(databaseHandler.getWritableDatabase(PASS_PHRASE));
            if (reconc_initiated) {
                if (progressSync != null && progressSync.isShowing()){
                    progressSync.dismiss();
                    progressSync = null;
                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDActivity.this);
                dlgAlert.setTitle("SafetyStratus");
                dlgAlert.setMessage("Reconciliation has already been initiated for this location '" + selectedRoomName + "'. Would you like to proceed with the reconciliation?");
                dlgAlert.setPositiveButton("Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final Intent myIntent = new Intent(RFIDActivity.this,
                                        RFIDScannerActivity.class);
                                myIntent.putExtra("user_id", selectedUserId);
                                myIntent.putExtra("site_id", loggedinUserSiteId);
                                myIntent.putExtra("token", token);
                                myIntent.putExtra("sso", sso);
                                myIntent.putExtra("md5pwd", md5Pwd);
                                myIntent.putExtra("loggedinUsername", loggedinUsername);
                                myIntent.putExtra("site_name", site_name);
                                myIntent.putExtra("selectedFacilName", selectedFacilName);
                                myIntent.putExtra("selectedFacil", selectedFacil + "");
                                myIntent.putExtra("selectedRoomName", selectedRoomName);
                                myIntent.putExtra("selectedRoom", selectedRoom + "");
                                myIntent.putExtra("fromContinueInsp", "true");
                                myIntent.putExtra("empName", empName);
                                myIntent.putExtra("json_data_from_continue", sc.getJson_data());
                                myIntent.putExtra("reconc_id", rec_id + "");
                                myIntent.putExtra("total_inventory", inventoryCount + "");
                                startActivity(myIntent);
                            }
                        });
                dlgAlert.setNegativeButton("New",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDActivity.this);
                                dlgAlert.setTitle("SafetyStratus");
                                dlgAlert.setMessage("Are you sure you want to proceed? This action will result in the complete erasure of the previously saved reconciliation.");
                                dlgAlert.setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                ErasePreviousAndCreateNew obj = new ErasePreviousAndCreateNew();
                                                obj.execute(rec_id);
                                            }
                                        });
                                dlgAlert.setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                scanRFID.setEnabled(true);
                                                return;
                                            }
                                        });
                                dlgAlert.create().show();
                            }
                        });
                dlgAlert.create().show();
            }
            else {
                if (progressSync != null && progressSync.isShowing()){
                    progressSync.dismiss();
                    progressSync = null;
                }
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
                myIntent.putExtra("selectedFacil", selectedFacil + "");
                myIntent.putExtra("selectedFacilName", selectedFacilName);
                myIntent.putExtra("selectedFacil", selectedFacil + "");
                myIntent.putExtra("selectedRoomName", selectedRoomName);
                myIntent.putExtra("selectedRoom", selectedRoom + "");
                myIntent.putExtra("reconc_id", new_rec_id + "");
                myIntent.putExtra("empName", empName);
                myIntent.putExtra("total_inventory", inventoryCount + "");
                startActivity(myIntent);
            }
        }
    }
    class ErasePreviousAndCreateNew extends AsyncTask<Integer, String, String> {
        private ProgressDialog progressSync = new ProgressDialog(RFIDActivity.this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        int new_rec_id = 0;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // disable dismiss by tapping outside of the dialog
            progressSync.setTitle("");
            progressSync.setMessage("Initiating a fresh reconciliation procedure by eliminating the current one...");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(450, 200);
            super.onPreExecute();
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Integer... params) {
            // db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();
                cv.put("location_id", selectedFacil);
                cv.put("user_id", selectedUserId);
                cv.put("room_id", selectedRoom);
                databaseHandler.deletePendingScanByReconc_id(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), String.valueOf(params[0]));
                new_rec_id = databaseHandler.insertReconciliaionData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
            } finally {
                db.close();
                if (databaseHandler != null) {
                    databaseHandler.close();
                }
            }
            return "completed";
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //HashMap<String, String> settings = databaseHandler.getPermissionDetails(databaseHandler.getWritableDatabase(PASS_PHRASE));
            if (progressSync != null && progressSync.isShowing()){
                progressSync.dismiss();
                progressSync = null;
            }
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
            myIntent.putExtra("selectedFacil", selectedFacil + "");
            myIntent.putExtra("selectedFacilName", selectedFacilName);
            myIntent.putExtra("selectedFacil", selectedFacil + "");
            myIntent.putExtra("selectedRoomName", selectedRoomName);
            myIntent.putExtra("selectedRoom", selectedRoom + "");
            myIntent.putExtra("reconc_id", new_rec_id + "");
            myIntent.putExtra("empName", empName);
            myIntent.putExtra("total_inventory", inventoryCount + "");
            startActivity(myIntent);
        }
    }


}