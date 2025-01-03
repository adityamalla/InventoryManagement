package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PostSuccess extends AppCompatActivity {
    String loggedinUsername = "";
    String loggedinUserSiteId = "";
    String md5Pwd = "";
    String selectedUserId = "";
    String selectedSearchValue = "";
    String sso = "";
    String site_name = "";
    String token="";
    String empName="";
    String selectedRoom;
    String selectedFacil;
    ConstraintLayout header;
    String host="";
    Button gotohome;
    TextView badge_notification;
    Button postScanData;
    Button startAnotherInv;
    String fromBarcodeScan="";
    String fromBulkUpdate="";
    EditText defaultText;
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;
    final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(PostSuccess.this);
    final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_post_success);
        super.onCreate(savedInstanceState);
        SQLiteDatabase.loadLibs(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.header);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        header = (ConstraintLayout) findViewById(R.id.header);
        TextView tv = (TextView) findViewById(R.id.headerId);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.RED);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);
        tv.setText("");
        tv.setTextSize(20);
        tv.setVisibility(View.VISIBLE);
        gotohome = findViewById(R.id.gotohome);
        startAnotherInv = findViewById(R.id.startAnotherInvScan);
        host = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).getString("site_api_host", "services.labcliq.com");
        //Log.e("Host-->",host);
        // UI
        Intent intent = getIntent();
        sso = intent.getStringExtra("sso");
        if (intent.getStringExtra("token") != null) {
            token = intent.getStringExtra("token");
        }
        if (intent.getStringExtra("empName") != null) {
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
        if (intent.getStringExtra("selectedRoom") != null) {
            selectedRoom = intent.getStringExtra("selectedRoom");
        }
        if (intent.getStringExtra("selectedFacil") != null) {
            selectedFacil = intent.getStringExtra("selectedFacil");
        }
        if (intent.getStringExtra("fromBarcodeScan") != null) {
            fromBarcodeScan = intent.getStringExtra("fromBarcodeScan");
        }

        if (intent.getStringExtra("fromBulkUpdate") != null) {
            fromBulkUpdate = intent.getStringExtra("fromBulkUpdate");
        }
        badge_notification = findViewById(R.id.badge_notification);
        postScanData = findViewById(R.id.postScan);
        defaultText = findViewById(R.id.defaultText);

        /*if (fromBarcodeScan.trim().length()>0){
            int scannedJsonData = databaseHandler.getSavedBarcodeDataCount(databaseHandler.getWritableDatabase(PASS_PHRASE));
            postScanData.setVisibility(View.VISIBLE);
            startAnotherInv.setVisibility(View.GONE);
            if(scannedJsonData>0) {
                badge_notification.setVisibility(View.VISIBLE);
                badge_notification.setText(scannedJsonData);
            }
            defaultText.setText("Data Updated Successfully");
            ConstraintLayout constraintLayout = findViewById(R.id.successLayout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.gotohome,ConstraintSet.START,R.id.defaultText,ConstraintSet.START,0);
            constraintSet.connect(R.id.gotohome,ConstraintSet.END,R.id.defaultText,ConstraintSet.END,0);
            constraintSet.connect(R.id.gotohome,ConstraintSet.TOP,R.id.defaultText,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
            ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) gotohome.getLayoutParams();
            newLayoutParams.topMargin = 10;
            newLayoutParams.leftMargin = 20;
            newLayoutParams.rightMargin = 20;
            newLayoutParams.bottomMargin = 0;
            gotohome.setLayoutParams(newLayoutParams);
        }else{*/
            int scannedJsonData = databaseHandler.getSavedDataCount(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedUserId);
            if(scannedJsonData > 0){
                postScanData.setVisibility(View.VISIBLE);
                ConstraintLayout constraintLayout = findViewById(R.id.successLayout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.startAnotherInvScan,ConstraintSet.START,R.id.postScan,ConstraintSet.START,0);
                constraintSet.connect(R.id.startAnotherInvScan,ConstraintSet.END,R.id.postScan,ConstraintSet.END,0);
                constraintSet.connect(R.id.startAnotherInvScan,ConstraintSet.TOP,R.id.postScan,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) startAnotherInv.getLayoutParams();
                newLayoutParams.topMargin = 10;
                newLayoutParams.leftMargin = 20;
                newLayoutParams.rightMargin = 20;
                newLayoutParams.bottomMargin = 0;
                startAnotherInv.setLayoutParams(newLayoutParams);
                badge_notification.setVisibility(View.VISIBLE);
                badge_notification.setText(String.valueOf(scannedJsonData));
                if (fromBarcodeScan.trim().length()>0 || fromBulkUpdate.trim().length()>0){
                    startAnotherInv.setVisibility(View.GONE);
                    defaultText.setText("Data Updated Successfully");
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.gotohome,ConstraintSet.START,R.id.postScan,ConstraintSet.START,0);
                    constraintSet.connect(R.id.gotohome,ConstraintSet.END,R.id.postScan,ConstraintSet.END,0);
                    constraintSet.connect(R.id.gotohome,ConstraintSet.TOP,R.id.postScan,ConstraintSet.BOTTOM,0);
                    constraintSet.applyTo(constraintLayout);
                    ConstraintLayout.LayoutParams newLayoutParams1 = (ConstraintLayout.LayoutParams) gotohome.getLayoutParams();
                    newLayoutParams1.topMargin = 10;
                    newLayoutParams1.leftMargin = 20;
                    newLayoutParams1.rightMargin = 20;
                    newLayoutParams1.bottomMargin = 0;
                    gotohome.setLayoutParams(newLayoutParams1);
                }
            }else{
                postScanData.setVisibility(View.GONE);
                ConstraintLayout constraintLayout = findViewById(R.id.successLayout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.startAnotherInvScan,ConstraintSet.START,R.id.defaultText,ConstraintSet.START,0);
                constraintSet.connect(R.id.startAnotherInvScan,ConstraintSet.END,R.id.defaultText,ConstraintSet.END,0);
                constraintSet.connect(R.id.startAnotherInvScan,ConstraintSet.TOP,R.id.defaultText,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) startAnotherInv.getLayoutParams();
                newLayoutParams.topMargin = 10;
                newLayoutParams.leftMargin = 20;
                newLayoutParams.rightMargin = 20;
                newLayoutParams.bottomMargin = 0;
                startAnotherInv.setLayoutParams(newLayoutParams);
                badge_notification.setVisibility(View.GONE);
                badge_notification.setText("");
                if (fromBarcodeScan.trim().length()>0 || fromBulkUpdate.trim().length()>0){
                    startAnotherInv.setVisibility(View.GONE);
                    defaultText.setText("Data Updated Successfully");
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.gotohome,ConstraintSet.START,R.id.defaultText,ConstraintSet.START,0);
                    constraintSet.connect(R.id.gotohome,ConstraintSet.END,R.id.defaultText,ConstraintSet.END,0);
                    constraintSet.connect(R.id.gotohome,ConstraintSet.TOP,R.id.defaultText,ConstraintSet.BOTTOM,0);
                    constraintSet.applyTo(constraintLayout);
                    ConstraintLayout.LayoutParams newLayoutParams1 = (ConstraintLayout.LayoutParams) gotohome.getLayoutParams();
                    newLayoutParams1.topMargin = 10;
                    newLayoutParams1.leftMargin = 20;
                    newLayoutParams1.rightMargin = 20;
                    newLayoutParams1.bottomMargin = 0;
                    gotohome.setLayoutParams(newLayoutParams1);
                }
            }
        //}
        postScanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postScanData.setEnabled(false);
                boolean connected;
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
                if(connected){
                    int scannedJsonData = databaseHandler.getSavedDataCount(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedUserId);
                    if(scannedJsonData > 0) {
                        try {
                            ArrayList<MyObject> jsonList = databaseHandler.getSavedJsonData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE),selectedUserId);
                            //SyncInventory sdb = new SyncInventory();
                            //sdb.execute(jsonList);
                            uploadScannedInventoryData(jsonList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }else{
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PostSuccess.this);
                        dlgAlert.setTitle("Safety Stratus");
                        dlgAlert.setMessage("There is no saved data to upload to CMS!");
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        postScanData.setEnabled(true);
                                        return;
                                    }
                                });
                        dlgAlert.create().show();
                    }
                }else{
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PostSuccess.this);
                    dlgAlert.setTitle("Safety Stratus");
                    dlgAlert.setMessage("Slow or no Internet Connection. Your data will be saved offline. " +
                            "Please sync the data when the network is online");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    postScanData.setEnabled(true);
                                    return;
                                }
                            });
                    dlgAlert.create().show();
                }
            }
        });
        gotohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent myIntent = new Intent(PostSuccess.this,
                        HomeActivity.class);
                myIntent.putExtra("user_id", selectedUserId);
                myIntent.putExtra("site_id", loggedinUserSiteId);
                myIntent.putExtra("token", token);
                myIntent.putExtra("sso", sso);
                myIntent.putExtra("md5pwd", md5Pwd);
                myIntent.putExtra("loggedinUsername", loggedinUsername);
                myIntent.putExtra("site_name", site_name);
                myIntent.putExtra("empName", empName);
                myIntent.putExtra("pageLoadTemp", "-1");
                startActivity(myIntent);
            }
        });
        startAnotherInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent myIntent = new Intent(PostSuccess.this,
                        RFIDActivity.class);
                myIntent.putExtra("user_id", selectedUserId);
                myIntent.putExtra("site_id", loggedinUserSiteId);
                myIntent.putExtra("token", token);
                myIntent.putExtra("sso", sso);
                myIntent.putExtra("md5pwd", md5Pwd);
                myIntent.putExtra("loggedinUsername", loggedinUsername);
                myIntent.putExtra("site_name", site_name);
                myIntent.putExtra("empName", empName);
                startActivity(myIntent);
            }
        });
    }
    public void uploadScannedInventoryData(ArrayList<MyObject> jsonList){
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {*/
                try {
                    ProgressDialog progressSync = new ProgressDialog(PostSuccess.this);
                    progressSync.setTitle("");
                    progressSync.setMessage("Uploading..");
                    progressSync.setCancelable(false);
                    progressSync.show();
                    progressSync.getWindow().setLayout(400, 200);
                    RequestQueue requestQueue = Volley.newRequestQueue(PostSuccess.this);
                    for (int k=0;k<jsonList.size();k++){
                        int finalK = k;
                        String URL = "";
                        String scan_type = databaseHandler.getScanType(db,jsonList.get(k).getObjectId());
                        if(scan_type.trim().equalsIgnoreCase("barcode")){
                            URL = "https://"+host+ApiConstants.syncbarcodeScannedData;
                        }else if(scan_type.trim().equalsIgnoreCase("bulkupdate")){
                            URL = "https://"+host+ApiConstants.syncbulkbarcodeScannedData;
                        }else{
                            URL = "https://"+host+ApiConstants.syncpostscanneddata;
                        }
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
                                        ArrayList<MyObject> jsonListModified = databaseHandler.getSavedJsonData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE),selectedUserId);
                                        if (jsonListModified.size()==0){
                                            progressSync.dismiss();
                                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PostSuccess.this);
                                            dlgAlert.setTitle("Safety Stratus");
                                            dlgAlert.setMessage("Inventory data uploaded successfully!");
                                            dlgAlert.setPositiveButton("Ok",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            int scannedJsonData = databaseHandler.getSavedDataCount(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedUserId);
                                                            if(scannedJsonData > 0){
                                                                badge_notification.setVisibility(View.VISIBLE);
                                                                badge_notification.setText(String.valueOf(scannedJsonData));
                                                            }else{
                                                                badge_notification.setVisibility(View.GONE);
                                                                badge_notification.setText("");
                                                            }
                                                            postScanData.setEnabled(true);
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
                                progressSync.dismiss();
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PostSuccess.this);
                                dlgAlert.setTitle("Safety Stratus");
                                dlgAlert.setMessage("Error response: Request timed out! Your data is saved offline");
                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                postScanData.setEnabled(true);
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
    /*class SyncInventory extends AsyncTask<ArrayList<MyObject>, Void, Void> {

        private ProgressDialog progressSync = new ProgressDialog(PostSuccess.this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(PostSuccess.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // disable dismiss by tapping outside of the dialog
            progressSync.setTitle("");
            progressSync.setMessage("Uploading..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(900, 300);
            super.onPreExecute();
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(ArrayList<MyObject>... params) {
            uploadScannedInventoryData(params[0]);
            String URL = ApiConstants.syncpostscanneddata;
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(PostSuccess.this);
                for (int k = 0; k < params[0].size(); k++) {
                    int finalK = k;
                    JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params[0].get(k).getObjectName()),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //Process os success response
                                    String res = response.toString();
                                    Log.e("TESTYYYYYY", res);
                                    databaseHandler.delSavedScanData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), params[0].get(finalK).getObjectId());
                                    ArrayList<MyObject> jsonListModified = databaseHandler.getSavedJsonData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                                    Log.e("TESTZZZZZZ", jsonListModified.size() + "***");
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
            }
            return "completed";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(progressSync!=null && progressSync.isShowing()){
                progressSync.dismiss();
                progressSync = null;
            }
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PostSuccess.this);
            dlgAlert.setTitle("Safety Stratus");
            dlgAlert.setMessage("Inventory data got uploaded to CMS successfully!");
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int scannedJsonData = databaseHandler.getSavedDataCount(databaseHandler.getWritableDatabase(PASS_PHRASE));
                            if(scannedJsonData > 0){
                                badge_notification.setVisibility(View.VISIBLE);
                                badge_notification.setText(String.valueOf(scannedJsonData));
                            }else{
                                badge_notification.setVisibility(View.GONE);
                                badge_notification.setText("");
                            }
                            return;
                        }
                    });
            dlgAlert.create().show();
        }

    }*/
}