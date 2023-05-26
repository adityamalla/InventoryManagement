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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONObject;

import java.util.ArrayList;

public class BulkContainerUpdate extends AppCompatActivity {
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
    Button uploadData;
    Button saveData;
    String empName = "";
    String note = "";
    String comment="";
    String selectedStatusName = "";
    String selectedStatus = "";
    String selectedRoomName = "";
    String selectedRoom = "";
    String selectedOwnerName = "";
    String selectedOwner = "";
    String selectedPrimaryUserName = "";
    String selectedPrimaryUserId = "";
    SharedPreferences pref;
    EditText owner;
    EditText location;
    EditText status;
    String host="";
    EditText notes;
    EditText comments;
    EditText primaryUser;
    TextView badge_notification_bulk;
    public static final String PREFS_NAME = "MyPrefsFile";
    ArrayList<String> codelistfromIntent;
    ConstraintLayout header;
    final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(BulkContainerUpdate.this);
    final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_container_update);
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
        tv.setText("Edit Container");
        tv.setTextSize(18);
        tv.setVisibility(View.VISIBLE);
        pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
        host = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).getString("site_api_host", "services.labcliq.com");
        Log.e("Host-->",host);
        Intent intent = getIntent();
        sso = intent.getStringExtra("sso");
        if (intent.getStringExtra("token") != null) {
            token = intent.getStringExtra("token");
        }
        if(intent.getStringExtra("empName")!=null) {
            empName = intent.getStringExtra("empName");
        }

        if(intent.getStringExtra("note")!=null) {
            note = intent.getStringExtra("note");
        }
        if(intent.getStringExtra("comment")!=null) {
            comment = intent.getStringExtra("comment");
        }
        if (intent.getStringExtra("selectedStatus") != null) {
            selectedStatus = intent.getStringExtra("selectedStatus");
        }
        if (intent.getStringExtra("selectedStatusName") != null) {
            selectedStatusName = intent.getStringExtra("selectedStatusName");
        }
        if (intent.getStringExtra("selectedRoom") != null) {
            selectedRoom = intent.getStringExtra("selectedRoom");
        }
        if (intent.getStringExtra("selectedRoomName") != null) {
            selectedRoomName = intent.getStringExtra("selectedRoomName");
        }
        if (intent.getStringExtra("selectedPrimaryUserId") != null) {
            selectedPrimaryUserId = intent.getStringExtra("selectedPrimaryUserId");
        }
        if (intent.getStringExtra("selectedPrimaryUserName") != null) {
            selectedPrimaryUserName = intent.getStringExtra("selectedPrimaryUserName");
        }

        if (intent.getStringExtra("selectedOwner") != null) {
            selectedOwner = intent.getStringExtra("selectedOwner");
        }
        if (intent.getStringExtra("selectedOwnerName") != null) {
            selectedOwnerName = intent.getStringExtra("selectedOwnerName");
        }
        codelistfromIntent = new ArrayList<String>();
        if(intent.getSerializableExtra("codelistfromIntent")!=null)
            codelistfromIntent = (ArrayList<String>) intent.getSerializableExtra("codelistfromIntent");
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("loggedinUsername");
        selectedUserId = intent.getStringExtra("user_id");
        loggedinUserSiteId = intent.getStringExtra("site_id");
        md5Pwd = intent.getStringExtra("md5pwd");
        if (intent.getStringExtra("selectedSearchValue") != null) {
            selectedSearchValue = intent.getStringExtra("selectedSearchValue");
        }
        uploadData = (Button) findViewById(R.id.uploadToWeb);
        saveData = (Button) findViewById(R.id.saveData);
        owner = (EditText)findViewById(R.id.owner);
        notes = (EditText)findViewById(R.id.locationNotes);
        location = (EditText)findViewById(R.id.location);
        comments = (EditText)findViewById(R.id.comment);
        status = (EditText)findViewById(R.id.status);
        primaryUser = (EditText)findViewById(R.id.primaryUser);
        badge_notification_bulk = (TextView) findViewById(R.id.badge_notification_bulk);
        notes.setText(note);
        comments.setText(comment);
        if(selectedOwnerName.trim().length()>0)
            owner.setText(selectedOwnerName);
        else
            owner.setText("None");
        if(selectedRoomName.trim().length()>0)
            location.setText(selectedRoomName);
        else
            location.setText("None");
        if(selectedStatusName.trim().length()>0)
            status.setText(selectedStatusName);
        else
            status.setText("None");
        if(selectedPrimaryUserName.trim().length()>0)
            primaryUser.setText(selectedPrimaryUserName);
        else
            primaryUser.setText("None");
        int scannedJsonData = databaseHandler.getSavedBulkDataUpdateCount(databaseHandler.getWritableDatabase(PASS_PHRASE));
        if(scannedJsonData>0){
            badge_notification_bulk.setVisibility(View.VISIBLE);
            badge_notification_bulk.setText(String.valueOf(scannedJsonData));
        }else{
            badge_notification_bulk.setVisibility(View.GONE);
            badge_notification_bulk.setText("");
        }
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    note = notes.getText().toString();
                    comment = comments.getText().toString();
                    ArrayList<MyObject> roomlist = databaseHandler.getRoomList(db,"");
                    final Intent myIntent = new Intent(BulkContainerUpdate.this,
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
                    myIntent.putExtra("selectedRoomName", selectedRoomName);
                    myIntent.putExtra("selectedRoom", selectedRoom+"");
                    myIntent.putExtra("selectedStatusName", selectedStatusName);
                    myIntent.putExtra("selectedStatus", selectedStatus+"");
                    myIntent.putExtra("selectedPrimaryUserName", selectedPrimaryUserName);
                    myIntent.putExtra("selectedPrimaryUserId", selectedPrimaryUserId+"");
                    myIntent.putExtra("selectedOwnerName", selectedOwnerName);
                    myIntent.putExtra("codelistfromIntent", codelistfromIntent);
                    myIntent.putExtra("selectedOwner", selectedOwner+"");
                    myIntent.putExtra("note", note+"");
                    myIntent.putExtra("comment", comment+"");
                    myIntent.putExtra("empName", empName);
                    myIntent.putExtra("fromBulkUpdate", "yes");
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
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    note = notes.getText().toString();
                    comment = comments.getText().toString();
                    String user_role_id = pref.getString("logged_in_user_role_id", null);
                    ArrayList<MyObject> statusList = databaseHandler.getStatusList(db,user_role_id);
                    final Intent myIntent = new Intent(BulkContainerUpdate.this,
                            StatusList.class);
                    myIntent.putExtra("user_id", selectedUserId);
                    myIntent.putExtra("site_id", loggedinUserSiteId);
                    myIntent.putExtra("token", token);
                    myIntent.putExtra("sso", sso);
                    myIntent.putExtra("md5pwd", md5Pwd);
                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                    myIntent.putExtra("site_name", site_name);
                    myIntent.putExtra("statusList",statusList);
                    myIntent.putExtra("selectedRoomName", selectedRoomName);
                    myIntent.putExtra("selectedRoom", selectedRoom+"");
                    myIntent.putExtra("selectedStatusName", selectedStatusName);
                    myIntent.putExtra("selectedStatus", selectedStatus+"");
                    myIntent.putExtra("selectedOwnerName", selectedOwnerName);
                    myIntent.putExtra("codelistfromIntent", codelistfromIntent);
                    myIntent.putExtra("selectedOwner", selectedOwner+"");
                    myIntent.putExtra("note", note+"");
                    myIntent.putExtra("comment", comment+"");
                    myIntent.putExtra("empName", empName);
                    myIntent.putExtra("fromBulkUpdate", "yes");
                    myIntent.putExtra("selectedPrimaryUserName", selectedPrimaryUserName);
                    myIntent.putExtra("selectedPrimaryUserId", selectedPrimaryUserId+"");
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
        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    note = notes.getText().toString();
                    comment = comments.getText().toString();
                    ArrayList<MyObject> ownerList = databaseHandler.getOwnerList(db);
                    final Intent myIntent = new Intent(BulkContainerUpdate.this,
                            OwnerList.class);
                    myIntent.putExtra("user_id", selectedUserId);
                    myIntent.putExtra("site_id", loggedinUserSiteId);
                    myIntent.putExtra("token", token);
                    myIntent.putExtra("sso", sso);
                    myIntent.putExtra("md5pwd", md5Pwd);
                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                    myIntent.putExtra("codelistfromIntent", codelistfromIntent);
                    myIntent.putExtra("site_name", site_name);
                    myIntent.putExtra("ownerList",ownerList);
                    myIntent.putExtra("selectedRoomName", selectedRoomName);
                    myIntent.putExtra("selectedRoom", selectedRoom+"");
                    myIntent.putExtra("selectedStatusName", selectedStatusName);
                    myIntent.putExtra("selectedStatus", selectedStatus+"");
                    myIntent.putExtra("selectedOwnerName", selectedOwnerName);
                    myIntent.putExtra("selectedOwner", selectedOwner+"");
                    myIntent.putExtra("note", note+"");
                    myIntent.putExtra("comment", comment+"");
                    myIntent.putExtra("empName", empName);
                    myIntent.putExtra("fromBulkUpdate", "yes");
                    myIntent.putExtra("selectedPrimaryUserName", selectedPrimaryUserName);
                    myIntent.putExtra("selectedPrimaryUserId", selectedPrimaryUserId+"");
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
        primaryUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    note = notes.getText().toString();
                    comment = comments.getText().toString();
                    ArrayList<MyObject> primaryUsersList = databaseHandler.getPrimaryUsersList(db);
                    final Intent myIntent = new Intent(BulkContainerUpdate.this,
                            OwnerList.class);
                    myIntent.putExtra("user_id", selectedUserId);
                    myIntent.putExtra("site_id", loggedinUserSiteId);
                    myIntent.putExtra("token", token);
                    myIntent.putExtra("sso", sso);
                    myIntent.putExtra("md5pwd", md5Pwd);
                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                    myIntent.putExtra("codelistfromIntent", codelistfromIntent);
                    myIntent.putExtra("site_name", site_name);
                    myIntent.putExtra("pu", "1");
                    myIntent.putExtra("primaryUsersList",primaryUsersList);
                    myIntent.putExtra("selectedRoomName", selectedRoomName);
                    myIntent.putExtra("selectedRoom", selectedRoom+"");
                    myIntent.putExtra("selectedStatusName", selectedStatusName);
                    myIntent.putExtra("selectedStatus", selectedStatus+"");
                    myIntent.putExtra("selectedOwnerName", selectedOwnerName);
                    myIntent.putExtra("selectedOwner", selectedOwner+"");
                    myIntent.putExtra("note", note+"");
                    myIntent.putExtra("comment", comment+"");
                    myIntent.putExtra("empName", empName);
                    myIntent.putExtra("fromBulkUpdate", "yes");
                    myIntent.putExtra("selectedPrimaryUserName", selectedPrimaryUserName);
                    myIntent.putExtra("selectedPrimaryUserId", selectedPrimaryUserId+"");
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
        uploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                note = notes.getText().toString();
                comment = comments.getText().toString();
                String JSONObjectCodelist = "";
                for (int h=0;h<codelistfromIntent.size();h++){
                    JSONObjectCodelist = JSONObjectCodelist+codelistfromIntent.get(h)+",";
                    ContentValues cv = new ContentValues();
                    cv.put("code", codelistfromIntent.get(h));
                    if(selectedRoom.trim().length()==0||selectedRoom==null||selectedRoom=="null"){
                        selectedRoom = "-1";
                    }
                    if(selectedOwner.trim().length()==0||selectedOwner==null||selectedOwner=="null"){
                        selectedOwner = "-1";
                    }
                    if(selectedStatus.trim().length()==0||selectedStatus==null||selectedStatus=="null"){
                        selectedStatus = "-1";
                    }
                    if(selectedPrimaryUserId.trim().length()==0||selectedPrimaryUserId==null||selectedPrimaryUserId=="null"){
                        selectedPrimaryUserId = "-1";
                    }
                    cv.put("room_id", Integer.parseInt(selectedRoom));
                    cv.put("object_id", Integer.parseInt(selectedOwner));
                    cv.put("object_table", "site_users");
                    cv.put("owner", selectedOwnerName);
                    cv.put("room", selectedRoomName);
                    cv.put("status_id", Integer.parseInt(selectedStatus));
                    cv.put("status", selectedStatusName);
                    cv.put("notes", note);
                    cv.put("comment", comment);
                    cv.put("primary_user_id", selectedPrimaryUserId);
                    databaseHandler.updateInventoryDetails(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                }
                JSONObjectCodelist = JSONObjectCodelist.substring(0,JSONObjectCodelist.length()-1);
                BulkUpdateModel inv = new BulkUpdateModel(JSONObjectCodelist,selectedStatus,selectedRoom,selectedOwner,selectedPrimaryUserId,note,comment,"site_users",selectedUserId,loggedinUserSiteId,token);
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = "";
                try {
                    jsonString = mapper.writeValueAsString(inv);
                    ContentValues cv_save = new ContentValues();
                    cv_save.put("code", JSONObjectCodelist);
                    cv_save.put("user_id", Integer.parseInt(selectedUserId));
                    cv_save.put("location_id", -1);
                    cv_save.put("room_id", -1);
                    cv_save.put("scan_type", "bulkupdate");
                    cv_save.put("json_data", jsonString);
                    databaseHandler.insertScannedBarcodeInvJSONData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv_save);
                    if (connected) {
                        ArrayList<MyObject> jsonList = databaseHandler.getSavedJsonDataBulkUpdate(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                        //SyncInventory sdb = new SyncInventory();
                        //sdb.execute(jsonList);
                        uploadInventoryData(jsonList);
                        /*String URL = ApiConstants.syncbulkbarcodeScannedData;
                        RequestQueue requestQueue = Volley.newRequestQueue(BulkContainerUpdate.this);
                        String finalJSONObjectCodelist = JSONObjectCodelist;
                        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(jsonString),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Process os success response
                                        String res = response.toString();
                                        Log.e("res from bulk>>", res + "**");
                                        databaseHandler.deleteBarcodeInventoryDetails(db, finalJSONObjectCodelist);
                                        final Intent myIntent = new Intent(BulkContainerUpdate.this,
                                                PostSuccess.class);
                                        myIntent.putExtra("user_id", selectedUserId);
                                        myIntent.putExtra("site_id", loggedinUserSiteId);
                                        myIntent.putExtra("token", token);
                                        myIntent.putExtra("sso", sso);
                                        myIntent.putExtra("md5pwd", md5Pwd);
                                        myIntent.putExtra("loggedinUsername", loggedinUsername);
                                        myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                                        myIntent.putExtra("site_name", site_name);
                                        myIntent.putExtra("empName", empName);
                                        myIntent.putExtra("fromBulkUpdate", "yes");
                                        startActivity(myIntent);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(BulkContainerUpdate.this);
                                dlgAlert.setTitle("Safety Stratus");
                                dlgAlert.setMessage("Error response: Request timed out! Your data is saved offline");
                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                final Intent myIntent = new Intent(BulkContainerUpdate.this,
                                                        PostSuccess.class);
                                                myIntent.putExtra("user_id", selectedUserId);
                                                myIntent.putExtra("site_id", loggedinUserSiteId);
                                                myIntent.putExtra("token", token);
                                                myIntent.putExtra("sso", sso);
                                                myIntent.putExtra("md5pwd", md5Pwd);
                                                myIntent.putExtra("loggedinUsername", loggedinUsername);
                                                myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                                                myIntent.putExtra("site_name", site_name);
                                                myIntent.putExtra("empName", empName);
                                                myIntent.putExtra("fromBulkUpdate", "yes");
                                                startActivity(myIntent);
                                                return;
                                            }
                                        });
                                dlgAlert.create().show();
                            }
                        });
                        int socketTimeout = 60000;//30 seconds - change to what you want
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
                        request_json.setRetryPolicy(policy);
                        // add the request object to the queue to be executed
                        requestQueue.add(request_json);*/
                    }
                    else{
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(BulkContainerUpdate.this);
                        dlgAlert.setTitle("Safety Stratus");
                        dlgAlert.setMessage("No Internet!! Your data is saved offline");
                        String finalJsonString1 = jsonString;
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Intent myIntent = new Intent(BulkContainerUpdate.this,
                                                PostSuccess.class);
                                        myIntent.putExtra("user_id", selectedUserId);
                                        myIntent.putExtra("site_id", loggedinUserSiteId);
                                        myIntent.putExtra("token", token);
                                        myIntent.putExtra("sso", sso);
                                        myIntent.putExtra("md5pwd", md5Pwd);
                                        myIntent.putExtra("loggedinUsername", loggedinUsername);
                                        myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                                        myIntent.putExtra("site_name", site_name);
                                        myIntent.putExtra("empName", empName);
                                        myIntent.putExtra("fromBulkUpdate", "yes");
                                        startActivity(myIntent);
                                        return;
                                    }
                                });
                        dlgAlert.create().show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                note = notes.getText().toString();
                comment = comments.getText().toString();
                String JSONObjectCodelist = "";
                for (int h=0;h<codelistfromIntent.size();h++){
                    JSONObjectCodelist = JSONObjectCodelist+codelistfromIntent.get(h)+",";
                }
                JSONObjectCodelist = JSONObjectCodelist.substring(0,JSONObjectCodelist.length()-1);
                if(selectedRoom.trim().length()==0||selectedRoom==null||selectedRoom=="null"){
                    selectedRoom = "-1";
                }
                if(selectedOwner.trim().length()==0||selectedOwner==null||selectedOwner=="null"){
                    selectedOwner = "-1";
                }
                if(selectedStatus.trim().length()==0||selectedStatus==null||selectedStatus=="null"){
                    selectedStatus = "-1";
                }
                if(selectedPrimaryUserId.trim().length()==0||selectedPrimaryUserId==null||selectedPrimaryUserId=="null"){
                    selectedPrimaryUserId = "-1";
                }
                BulkUpdateModel inv = new BulkUpdateModel(JSONObjectCodelist,selectedStatus,selectedRoom,selectedOwner,selectedPrimaryUserId,note,comment,"site_users",selectedUserId,loggedinUserSiteId,token);
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = "";
                try {
                    jsonString = mapper.writeValueAsString(inv);
                    ContentValues cv_save = new ContentValues();
                    cv_save.put("code", JSONObjectCodelist);
                    cv_save.put("user_id", Integer.parseInt(selectedUserId));
                    cv_save.put("location_id", -1);
                    cv_save.put("room_id", -1);
                    cv_save.put("scan_type", "bulkupdate");
                    cv_save.put("json_data", jsonString);
                    databaseHandler.insertScannedBarcodeInvJSONData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv_save);
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(BulkContainerUpdate.this);
                    dlgAlert.setTitle("Safety Stratus");
                    dlgAlert.setMessage("Your data is saved successfully!");
                    String finalJsonString1 = jsonString;
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int scannedJsonData = databaseHandler.getSavedBulkDataUpdateCount(databaseHandler.getWritableDatabase(PASS_PHRASE));
                                    if(scannedJsonData>0){
                                        badge_notification_bulk.setVisibility(View.VISIBLE);
                                        badge_notification_bulk.setText(String.valueOf(scannedJsonData));
                                    }else{
                                        badge_notification_bulk.setVisibility(View.GONE);
                                        badge_notification_bulk.setText("");
                                    }
                                    return;
                                }
                            });
                    dlgAlert.create().show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public void uploadInventoryData(ArrayList<MyObject> jsonList){
        try {
            ProgressDialog progressSync = new ProgressDialog(BulkContainerUpdate.this);
            progressSync.setTitle("");
            progressSync.setMessage("Uploading..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(400, 200);
            RequestQueue requestQueue = Volley.newRequestQueue(BulkContainerUpdate.this);
            for (int k=0;k<jsonList.size();k++){
                int finalK = k;
                String URL = "https://"+host+ApiConstants.syncbulkbarcodeScannedData;
                JSONObject obj = new JSONObject(jsonList.get(k).getObjectName());
                String reconc_id = "-4";
                if (obj.has("reconc_id")) {
                    reconc_id = obj.getString("reconc_id");
                }
                String finalReconc_id = reconc_id;
                JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(jsonList.get(k).getObjectName()),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Process os success response
                                String res = response.toString();
                                databaseHandler.delSavedScanDatabyId(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), jsonList.get(finalK).getObjectId(),finalReconc_id);
                                ArrayList<MyObject> jsonListModified = databaseHandler.getSavedJsonDataBulkUpdate(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                                if (jsonListModified.size()==0){
                                    progressSync.dismiss();
                                    final Intent myIntent = new Intent(BulkContainerUpdate.this,
                                            PostSuccess.class);
                                    myIntent.putExtra("user_id", selectedUserId);
                                    myIntent.putExtra("site_id", loggedinUserSiteId);
                                    myIntent.putExtra("token", token);
                                    myIntent.putExtra("sso", sso);
                                    myIntent.putExtra("md5pwd", md5Pwd);
                                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                                    myIntent.putExtra("site_name", site_name);
                                    myIntent.putExtra("empName", empName);
                                    myIntent.putExtra("fromBulkUpdate", "yes");
                                    startActivity(myIntent);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(BulkContainerUpdate.this);
                        dlgAlert.setTitle("Safety Stratus");
                        dlgAlert.setMessage("Error response: Request timed out! Your data is saved offline");
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                    }
                                });
                    }
                });
                int socketTimeout = 60000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
                request_json.setRetryPolicy(policy);
                // add the request object to the queue to be executed
                requestQueue.add(request_json);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        //}
        //});
    }
    @Override
    public void onBackPressed() {
    }
    public static void hideKeyboard(BulkContainerUpdate activity) {
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
            final Intent myIntent = new Intent(BulkContainerUpdate.this,
                    BulkUpdateActivity.class);
            myIntent.putExtra("user_id", selectedUserId);
            myIntent.putExtra("site_id", loggedinUserSiteId);
            myIntent.putExtra("token", token);
            myIntent.putExtra("sso", sso);
            myIntent.putExtra("md5pwd", md5Pwd);
            myIntent.putExtra("loggedinUsername", loggedinUsername);
            myIntent.putExtra("site_name", site_name);
            myIntent.putExtra("pageLoadTemp", "-1");
            myIntent.putExtra("empName", empName);
            myIntent.putExtra("codelistfromIntent",codelistfromIntent);
            myIntent.putExtra("selectedRoomName", selectedRoomName);
            myIntent.putExtra("selectedRoom", selectedRoom+"");
            myIntent.putExtra("selectedStatusName", selectedStatusName);
            myIntent.putExtra("selectedStatus", selectedStatus+"");
            myIntent.putExtra("selectedPrimaryUserName", selectedPrimaryUserName);
            myIntent.putExtra("selectedPrimaryUserId", selectedPrimaryUserId+"");
            myIntent.putExtra("selectedOwnerName", selectedOwnerName);
            myIntent.putExtra("selectedOwner", selectedOwner+"");
            myIntent.putExtra("note", note+"");
            myIntent.putExtra("comment", comment+"");
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}