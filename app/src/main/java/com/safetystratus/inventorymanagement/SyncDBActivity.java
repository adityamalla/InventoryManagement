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
import net.sqlcipher.database.SQLiteStatement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SyncDBActivity extends AppCompatActivity {
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
    Button syncData;
    String selectedFacilName = "";
    String selectedFacil = "";
    String selectedRoomName = "";
    String selectedRoom = "";
    String empName = "";
    ConstraintLayout header;
    String host= "";
    ProgressDialog progressSynStart = null;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_dbactivity);
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
        tv.setText("Download Data");
        tv.setTextSize(20);
        tv.setVisibility(View.VISIBLE);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(SyncDBActivity.this);
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
        syncData = (Button)findViewById(R.id.syncData);
        employeeId.setText(loggedinUsername);
        employeeId.setEnabled(false);
        if(selectedFacilName.trim().length()>0){
            building.setText(selectedFacilName);
        }else{
            building.setText("None");
        }
        building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(SyncDBActivity.this);
                final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
                ArrayList<MyObject> facillist = databaseHandler.getBuildingList(databaseHandler.getWritableDatabase(PASS_PHRASE));
                    final Intent myIntent = new Intent(SyncDBActivity.this,
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
                    myIntent.putExtra("fromSync", "fromSync");
                    startActivity(myIntent);
            }
        });
        syncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int scannedJsonData = databaseHandler.getSavedDataCount(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedUserId);
                if(scannedJsonData > 0){
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SyncDBActivity.this);
                    dlgAlert.setTitle("SafetyStratus");
                    dlgAlert.setMessage("Reconciliations still pending. Upload to CMS or Cancel to return to the homepage.");
                    dlgAlert.setPositiveButton("Upload",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(connected){
                                        try {
                                            ArrayList<MyObject> jsonList = databaseHandler.getSavedJsonData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                                            //SyncInventory sdb = new SyncInventory();
                                            //sdb.execute(jsonList);
                                            uploadScannedInventoryData(jsonList);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {

                                        }
                                    }else{
                                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SyncDBActivity.this);
                                        dlgAlert.setTitle("Safety Stratus");
                                        dlgAlert.setMessage("Slow or no Internet Connection. Your data will be saved offline. " +
                                                "Please sync the data when the network is online");
                                        dlgAlert.setPositiveButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        return;
                                                    }
                                                });
                                        dlgAlert.create().show();
                                    }
                                    return;
                                }
                            });
                    dlgAlert.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final Intent myIntent = new Intent(SyncDBActivity.this,
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
                            });
                    dlgAlert.create().show();
                }
                else {
                    if (selectedFacilName.length() > 0) {
                        if (connected) {
                            progressSynStart = new ProgressDialog(SyncDBActivity.this);
                            progressSynStart.setTitle("");
                            progressSynStart.setMessage("Synchronizing..");
                            progressSynStart.setCancelable(false);
                            progressSynStart.show();
                            progressSynStart.getWindow().setLayout(450, 200);
                            insertDbData(loggedinUserSiteId, selectedUserId, token, selectedFacil);
                        } else {
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SyncDBActivity.this);
                            dlgAlert.setTitle("Not getting connection");
                            dlgAlert.setMessage("Please check your wifi or mobile data!!");
                            dlgAlert.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    });
                            dlgAlert.create().show();
                        }
                    } else {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SyncDBActivity.this);
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
            }});
    }
    @Override
    public void onBackPressed() {
    }
    public static void hideKeyboard(SyncDBActivity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }
    public void uploadScannedInventoryData(ArrayList<MyObject> jsonList){
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(SyncDBActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        try {
            ProgressDialog progressSync = new ProgressDialog(SyncDBActivity.this);
            progressSync.setTitle("");
            progressSync.setMessage("Uploading..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(400, 200);
            String URL = "https://"+host+ApiConstants.syncpostscanneddata;
            RequestQueue requestQueue = Volley.newRequestQueue(SyncDBActivity.this);
            for (int k=0;k<jsonList.size();k++){
                int finalK = k;
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
                                ArrayList<MyObject> jsonListModified = databaseHandler.getSavedJsonData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                                if (jsonListModified.size()==0){
                                    progressSync.dismiss();
                                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SyncDBActivity.this);
                                    dlgAlert.setTitle("Safety Stratus");
                                    dlgAlert.setMessage("Inventory data uploaded successfully!");
                                    dlgAlert.setPositiveButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    return;
                                                }
                                            });
                                    dlgAlert.create().show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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
            // progress.dismiss();
            db.close();
            if (databaseHandler != null) {
                databaseHandler.close();
            }
        }
        //}
        //});
    }
    public void insertDbData(String siteId, String userId, String token, String selectedFacil) {
        String url = "https://"+host+String.format(ApiConstants.downloadRoomInvDbUrl, siteId, userId, token,selectedFacil);
        RequestQueue requestQueue = Volley.newRequestQueue(SyncDBActivity.this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (progressSynStart != null && progressSynStart.isShowing()){
                    progressSynStart.dismiss();
                    progressSynStart = null;
                }
                SyncDbDialogs sdb = new SyncDbDialogs();
                sdb.execute(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SyncDBActivity.this);
                dlgAlert.setTitle("Safety Stratus");
                dlgAlert.setMessage("Error response: Request timed out please check your network and try again");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (progressSynStart != null && progressSynStart.isShowing()){
                                    progressSynStart.dismiss();
                                    progressSynStart = null;
                                }
                                return;
                            }
                        });
                dlgAlert.create().show();
            }
        });
        int socketTimeout = 60000;//3 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
        objectRequest.setRetryPolicy(policy);
        objectRequest.setShouldCache(false);
        requestQueue.add(objectRequest);
    }
    CustomProgressDialog progressDialog;
    class SyncDbDialogs extends AsyncTask<String, String, String> {

        //private ProgressDialog progressSync = new ProgressDialog(SyncDBActivity.this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(SyncDBActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // disable dismiss by tapping outside of the dialog
            /*progressSync.setTitle("");
            progressSync.setMessage("Uploading..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(450, 200);*/
            /*if (progressSync != null && progressSync.isShowing()){
                progressSync.dismiss();
                progressSync = null;
            }*/
            progressDialog = new CustomProgressDialog(SyncDBActivity.this, "Loading data...");
            progressDialog.getWindow().setLayout(450, 200);
            progressDialog.show();
            super.onPreExecute();
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            db.beginTransaction();
            try {
                insertDataIntoTables(params[0].toString(),databaseHandler,db);
            } finally {
                db.endTransaction();
                // progress.dismiss();
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
            progressDialog.dismiss();
            //HashMap<String, String> settings = databaseHandler.getPermissionDetails(databaseHandler.getWritableDatabase(PASS_PHRASE));
            /*if (progressSync != null && progressSync.isShowing()){
                progressSync.dismiss();
                progressSync = null;
            }*/
        }

    }
    public void insertDataIntoTables(String tableData, DatabaseHandler databaseHandler, SQLiteDatabase db){
        try {
            hideKeyboard(this);
            JSONObject obj = new JSONObject(tableData);
            ContentValues values = new ContentValues();
            JSONArray jsonArrayFiFacilRooms = obj.getJSONArray("fi_facil_rooms");
            JSONArray jsonArrayChemicalInventory = obj.getJSONArray("chemical_inventory");
            JSONArray jsonArrayPrimaryUsers = obj.getJSONArray("primary_users");
            db.delete(QueryConstants.TABLE_NAME_FI_FACIL_ROOMS, null, null);
            db.delete(QueryConstants.TABLE_NAME_CHEMICAL_INVENTORY, null, null);
            db.delete(QueryConstants.TABLE_NAME_PRIMARY_USERS, null, null);
            ArrayList<BatchInsertionObjectInventory> dataList = new ArrayList<BatchInsertionObjectInventory>();
            ArrayList<BatchInsertRooms> dataListRooms = new ArrayList<BatchInsertRooms>();
            ArrayList<BatchInsertPUs> dataListPUs = new ArrayList<BatchInsertPUs>();
            for (int i = 0, size = jsonArrayChemicalInventory.length(); i < size; i++) {
                JSONObject objectInArray = jsonArrayChemicalInventory.getJSONObject(i);
                String id = objectInArray.getString("id");
                if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_OT_ORGANIZATION, "id", id) == 0) {
                    dataList.add(new BatchInsertionObjectInventory(id,
                            objectInArray.getString("opened_date"),
                            objectInArray.getString("name"),
                            objectInArray.getString("room_id"),
                            objectInArray.getString("sec_code"),
                            objectInArray.getString("object_table"),
                            objectInArray.getString("modified_user_id"),
                            objectInArray.getString("modified_date"),
                            objectInArray.getString("last_test_date"),
                            objectInArray.getString("primary_user_id"),
                            objectInArray.getString("lot"),
                            objectInArray.getString("create_date"),
                            objectInArray.getString("code"),
                            objectInArray.getString("expiration_date"),
                            objectInArray.getString("create_user_id"),
                            objectInArray.getString("object_id"),
                            objectInArray.getString("facil_id"),
                            objectInArray.getString("room"),
                            objectInArray.getString("receipt_date"),
                            objectInArray.getString("notes"),
                            objectInArray.getString("comment"),
                            objectInArray.getString("quantity"),
                            objectInArray.getString("concentration"),
                            objectInArray.getString("quantity_unit_abbreviation"),
                            objectInArray.getString("quantity_unit_abbreviation_id"),
                            objectInArray.getString("concentration_unit_abbrevation"),
                            objectInArray.getString("concentration_unit_abbrevation_id"),
                            objectInArray.getString("cas_number"),
                            objectInArray.getString("status"),
                            objectInArray.getString("status_id"),
                            objectInArray.getString("loc"),
                            objectInArray.getString("loc_id"),
                            objectInArray.getString("test_frequency"),
                            objectInArray.getString("owner")
                                    ));
                }
            }
            for (int i = 0, size = jsonArrayFiFacilRooms.length(); i < size; i++) {
                JSONObject objectInArray = jsonArrayFiFacilRooms.getJSONObject(i);
                String id = objectInArray.getString("id");
                if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_FI_FACIL_ROOMS, "id", id) == 0) {
                    dataListRooms.add(new BatchInsertRooms(objectInArray.getString("room"),
                            objectInArray.getString("area"),
                            objectInArray.getString("img_src"),
                            objectInArray.getString("type_id"),
                            objectInArray.getString("id"),
                            objectInArray.getString("status"),
                            objectInArray.getString("notes"),
                            objectInArray.getString("facil_id")));
                }
            }
            for (int i = 0, size = jsonArrayPrimaryUsers.length(); i < size; i++) {
                JSONObject objectInArray = jsonArrayPrimaryUsers.getJSONObject(i);
                dataListPUs.add(new BatchInsertPUs(objectInArray.getString("primary_user"),
                        objectInArray.getString("primary_user_id")));
            }
            //databaseHandler.batchInsertChemInventory(dataList,databaseHandler.getWritableDatabase(PASS_PHRASE));
            int count = 0;
            String INSERT_QUERY_CHEM_INVENTORY = "INSERT INTO chemical_inventory" +
                    " (id,\n" +
                    "opened_date,\n" +
                    "name,\n" +
                    "room_id,\n" +
                    "sec_code,\n" +
                    "object_table,\n" +
                    "modified_user_id,\n" +
                    "modified_date,\n" +
                    "last_test_date,\n" +
                    "primary_user_id,\n" +
                    "lot,\n" +
                    "create_date,\n" +
                    "code,\n" +
                    "expiration_date,\n" +
                    "create_user_id,\n" +
                    "object_id,\n" +
                    "facil_id,\n" +
                    "room,\n" +
                    "receipt_date,\n" +
                    "notes,\n" +
                    "comment,\n" +
                    "quantity,\n" +
                    "concentration,\n" +
                    "quantity_unit_abbreviation,\n" +
                    "quantity_unit_abbreviation_id,\n" +
                    "concentration_unit_abbrevation,\n" +
                    "concentration_unit_abbrevation_id,\n" +
                    "cas_number,\n" +
                    "status,\n" +
                    "status_id,\n" +
                    "loc,\n" +
                    "loc_id,test_frequency,\n" +
                    "owner) VALUES (?, ?, ?,?,?,?,?,?, ?, ?,?,?,?,?,?, ?, ?,?,?,?,?,?, ?, ?,?,?,?,?,?, ?, ?,?,?,?)";
            SQLiteStatement insertStatement = db.compileStatement(INSERT_QUERY_CHEM_INVENTORY);
            try {
                db.beginTransaction();
                for (BatchInsertionObjectInventory data : dataList) {
                    insertStatement.bindString(1, data.getId());
                    insertStatement.bindString(2, data.getOpened_date());
                    insertStatement.bindString(3, data.getName());
                    insertStatement.bindString(4, data.getRoom_id());
                    insertStatement.bindString(5, data.getSec_code());
                    insertStatement.bindString(6, data.getObject_table());
                    insertStatement.bindString(7, data.getModified_user_id());
                    insertStatement.bindString(8, data.getModified_date());
                    insertStatement.bindString(9, data.getLast_test_date());
                    insertStatement.bindString(10, data.getPrimary_user_id());
                    insertStatement.bindString(11, data.getLot());
                    insertStatement.bindString(12, data.getCreate_date());
                    insertStatement.bindString(13, data.getCode());
                    insertStatement.bindString(14, data.getExpiration_date());
                    insertStatement.bindString(15, data.getCreate_user_id());
                    insertStatement.bindString(16, data.getObject_id());
                    insertStatement.bindString(17, data.getFacil_id());
                    insertStatement.bindString(18, data.getRoom());
                    insertStatement.bindString(19, data.getReceipt_date());
                    insertStatement.bindString(20, data.getNotes());
                    insertStatement.bindString(21, data.getComment());
                    insertStatement.bindString(22, data.getQuantity());
                    insertStatement.bindString(23, data.getConcentration());
                    insertStatement.bindString(24, data.getQuantity_unit_abbreviation());
                    insertStatement.bindString(25, data.getQuantity_unit_abbreviation_id());
                    insertStatement.bindString(26, data.getConcentration_unit_abbrevation());
                    insertStatement.bindString(27, data.getConcentration_unit_abbrevation_id());
                    insertStatement.bindString(28, data.getCas_number());
                    insertStatement.bindString(29, data.getStatus());
                    insertStatement.bindString(30, data.getStatus_id());
                    insertStatement.bindString(31, data.getLoc());
                    insertStatement.bindString(32, data.getLoc_id());
                    insertStatement.bindString(33, data.getTest_frequency());
                    insertStatement.bindString(34, data.getOwner());
                    insertStatement.executeInsert();
                    insertStatement.clearBindings();
                    count++;
                    int percent = (count * 100) / dataList.size();
                    int finalCount = count;
                    runOnUiThread(new Runnable() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void run() {
                            progressDialog.setPercentageAndProgress(percent,String.valueOf(finalCount),String.valueOf(dataList.size()),"Uploading Inventory data...");
                        }}
                        );
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                count = 0;
                runOnUiThread(new Runnable() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void run() {
                        progressDialog.setPercentageAndProgress(0,String.valueOf(0),null,"");
                    }}
                );
                insertStatement = null;
            }
            //databaseHandler.batchInsertRooms(dataListRooms,databaseHandler.getWritableDatabase(PASS_PHRASE));
            String INSERT_QUERY_ROOMS = "INSERT INTO fi_facil_rooms (" +
                    "room,\n" +
                    "area, \n" +
                    "img_src, \n" +
                    "type_id, \n" +
                    "id,\n" +
                    "status,\n" +
                    "notes,\n" +
                    "facil_id) VALUES (?, ?, ?,?,?,?,?,?)";
            insertStatement = db.compileStatement(INSERT_QUERY_ROOMS);
            try {
                db.beginTransaction();
                for (BatchInsertRooms data : dataListRooms) {
                    insertStatement.bindString(1, data.getRoom());
                    insertStatement.bindString(2, data.getArea());
                    insertStatement.bindString(3, data.getImg_src());
                    insertStatement.bindString(4, data.getType_id());
                    insertStatement.bindString(5, data.getId());
                    insertStatement.bindString(6, data.getStatus());
                    insertStatement.bindString(7, data.getNotes());
                    insertStatement.bindString(8, data.getFacil_id());
                    insertStatement.execute();
                    insertStatement.clearBindings();
                    count++;
                    int percent = (count * 100) / dataListRooms.size();
                    int finalCount = count;
                    runOnUiThread(new Runnable() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void run() {
                            progressDialog.setPercentageAndProgress(percent,String.valueOf(finalCount),String.valueOf(dataListRooms.size()),"Uploading Rooms data...");
                        }}
                    );
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                count = 0;
                runOnUiThread(new Runnable() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void run() {
                        progressDialog.setPercentageAndProgress(0,String.valueOf(0),null,"");
                    }}
                );
                insertStatement = null;
            }
            //databaseHandler.batchInsertPUs(dataListPUs,databaseHandler.getWritableDatabase(PASS_PHRASE));
            String INSERT_QUERY_PRIMARY_USERS = "INSERT INTO primary_users (primary_user, primary_user_id) VALUES (?, ?)";
            insertStatement = db.compileStatement(INSERT_QUERY_PRIMARY_USERS);
            try {
                db.beginTransaction();
                for (BatchInsertPUs data : dataListPUs) {
                    insertStatement.bindString(1, data.getPrimary_user());
                    insertStatement.bindString(2, data.getPrimary_user_id());
                    insertStatement.execute();
                    insertStatement.clearBindings();
                    count++;
                    int percent = (count * 100) / dataListPUs.size();
                    int finalCount = count;
                    runOnUiThread(new Runnable() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void run() {
                            progressDialog.setPercentageAndProgress(percent,String.valueOf(finalCount),String.valueOf(dataListPUs.size()),"Uploading Primary User data...");
                        }}
                    );
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                count = 0;
                runOnUiThread(new Runnable() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void run() {
                        progressDialog.setPercentageAndProgress(0,String.valueOf(0),null,"");
                    }}
                );
                insertStatement = null;
            }
            db.setTransactionSuccessful();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            final Intent myIntent = new Intent(SyncDBActivity.this,
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