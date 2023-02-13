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
        Log.e("selecteduserid4>>",selectedUserId+"**");

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
        Log.e("loggedinUsername>>",loggedinUsername+"**");
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
                Log.e("selecteduserid5>>",selectedUserId+"**");

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
                int scannedJsonData = databaseHandler.getSavedDataCount(databaseHandler.getWritableDatabase(PASS_PHRASE));
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
                                    Log.e("selecteduserid6>>",selectedUserId+"**");
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
                            Log.e("test>", loggedinUserSiteId + "*");
                            Log.e("test1>", selectedUserId + "*");
                            Log.e("test2>", token + "*");
                            Log.e("test3>", selectedFacil + "*");
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
            String URL = ApiConstants.syncpostscanneddata;
            RequestQueue requestQueue = Volley.newRequestQueue(SyncDBActivity.this);
            for (int k=0;k<jsonList.size();k++){
                int finalK = k;
                Log.e("JSON>>>",jsonList.get(k).getObjectName()+"**");
                JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(jsonList.get(k).getObjectName()),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Process os success response
                                String res = response.toString();
                                Log.e("res>>>>",res);
                                databaseHandler.delSavedScanDatabyId(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), jsonList.get(finalK).getObjectId());
                                ArrayList<MyObject> jsonListModified = databaseHandler.getSavedJsonData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                                Log.e("size******",jsonListModified.size()+"***");
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
        String url = String.format(ApiConstants.downloadRoomInvDbUrl, siteId, userId, token,selectedFacil);
        RequestQueue requestQueue = Volley.newRequestQueue(SyncDBActivity.this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("room inv response>>",response.toString()+"**");
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
        requestQueue.add(objectRequest);
    }

    class SyncDbDialogs extends AsyncTask<String, String, String> {

        private ProgressDialog progressSync = new ProgressDialog(SyncDBActivity.this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(SyncDBActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // disable dismiss by tapping outside of the dialog
            progressSync.setTitle("");
            progressSync.setMessage("Uploading..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(450, 200);
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
            //HashMap<String, String> settings = databaseHandler.getPermissionDetails(databaseHandler.getWritableDatabase(PASS_PHRASE));
            if (progressSync != null && progressSync.isShowing()){
                progressSync.dismiss();
                progressSync = null;
            }
        }

    }
    public void insertDataIntoTables(String tableData, DatabaseHandler databaseHandler, SQLiteDatabase db){
        try {
            hideKeyboard(this);
            JSONObject obj = new JSONObject(tableData);
            ContentValues values = new ContentValues();
            JSONArray jsonArrayFiFacilRooms = obj.getJSONArray("fi_facil_rooms");
            JSONArray jsonArrayChemicalInventory = obj.getJSONArray("chemical_inventory");
            db.delete(QueryConstants.TABLE_NAME_FI_FACIL_ROOMS, null, null);
            db.delete(QueryConstants.TABLE_NAME_CHEMICAL_INVENTORY, null, null);
            for (int i = 0, size = jsonArrayChemicalInventory.length(); i < size; i++) {
                JSONObject objectInArray = jsonArrayChemicalInventory.getJSONObject(i);
                String id = objectInArray.getString("id");
                if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_OT_ORGANIZATION, "id", id) == 0) {
                    values.put("id", id);
                    values.put("opened_date", objectInArray.getString("opened_date"));
                    values.put("name", objectInArray.getString("name"));
                    values.put("room_id", objectInArray.getString("room_id"));
                    values.put("sec_code", objectInArray.getString("sec_code"));
                    values.put("object_table", objectInArray.getString("object_table"));
                    values.put("modified_user_id", objectInArray.getString("modified_user_id"));
                    values.put("modified_date", objectInArray.getString("modified_date"));
                    values.put("last_test_date", objectInArray.getString("last_test_date"));
                    values.put("lot", objectInArray.getString("lot"));
                    values.put("create_date", objectInArray.getString("create_date"));
                    values.put("code", objectInArray.getString("code"));
                    values.put("expiration_date", objectInArray.getString("expiration_date"));
                    values.put("create_user_id", objectInArray.getString("create_user_id"));
                    values.put("object_id", objectInArray.getString("object_id"));
                    values.put("facil_id", objectInArray.getString("facil_id"));
                    values.put("room", objectInArray.getString("room"));
                    values.put("receipt_date", objectInArray.getString("receipt_date"));
                    values.put("notes", objectInArray.getString("notes"));
                    values.put("comment", objectInArray.getString("comment"));
                    values.put("quantity", objectInArray.getString("quantity"));
                    values.put("concentration", objectInArray.getString("concentration"));
                    values.put("quantity_unit_abbreviation", objectInArray.getString("quantity_unit_abbreviation"));
                    values.put("quantity_unit_abbreviation_id", objectInArray.getString("quantity_unit_abbreviation_id"));
                    values.put("concentration_unit_abbrevation", objectInArray.getString("concentration_unit_abbrevation"));
                    values.put("concentration_unit_abbrevation_id", objectInArray.getString("concentration_unit_abbrevation_id"));
                    values.put("cas_number", objectInArray.getString("cas_number"));
                    values.put("status", objectInArray.getString("status"));
                    values.put("status_id", objectInArray.getString("status_id"));
                    values.put("loc", objectInArray.getString("loc"));
                    values.put("loc_id", objectInArray.getString("loc_id"));
                    values.put("owner", objectInArray.getString("owner"));
                    db.insert(QueryConstants.TABLE_NAME_CHEMICAL_INVENTORY, null, values);
                    Log.e("checkValues0>>",values.toString()+"**");
                    values.clear();
                }
            }
            for (int i = 0, size = jsonArrayFiFacilRooms.length(); i < size; i++) {
                JSONObject objectInArray = jsonArrayFiFacilRooms.getJSONObject(i);
                String id = objectInArray.getString("id");
                if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_FI_FACIL_ROOMS, "id", id) == 0) {
                    values.put("room", objectInArray.getString("room"));
                    values.put("area", objectInArray.getString("area"));
                    values.put("img_src", objectInArray.getString("img_src"));
                    values.put("type_id", objectInArray.getString("type_id"));
                    values.put("id", id);
                    values.put("status", objectInArray.getString("status"));
                    values.put("notes", objectInArray.getString("notes"));
                    values.put("facil_id", objectInArray.getString("facil_id"));
                    db.insert(QueryConstants.TABLE_NAME_FI_FACIL_ROOMS, null, values);
                    values.clear();
                }
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
            Log.e("selecteduserid6>>",selectedUserId+"**");
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