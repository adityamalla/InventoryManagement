package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.NestedScrollView;

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
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.util.Hex;
import com.google.gson.Gson;
import com.zebra.rfid.api3.TagData;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class RFIDScannerActivity extends AppCompatActivity implements RFIDHandler.ResponseHandlerInterface {
    public TextView statusTextViewRFID = null;
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;
    private ListView textrfid;
    private TextView testStatus;
    private TextView scannedProgressCount;
    private TextView scannedProgressPercentage;
    private ProgressBar progressVal;
    public TextView scanCount;
    ArrayList<String> newList = new ArrayList<String>();
    RFIDHandler rfidHandler;
    final static String TAG = "RFID_SAMPLE";
    ArrayAdapter adapter = null;
    ConstraintLayout header;
    String loggedinUsername = "";
    String loggedinUserSiteId = "";
    String md5Pwd = "";
    String selectedUserId = "";
    String selectedSearchValue = "";
    String sso = "";
    String site_name = "";
    String token="";
    String selectedFacilName = "";
    String selectedFacil = "";
    String selectedRoomName = "";
    String selectedRoom = "";
    String empName="";
    String total_inventory = "120";
    TableLayout tableInv;
    NestedScrollView invScrollview;
    RadioButton all;
    RadioButton found;
    RadioButton notfound;
    Button postScanData;
    Button saveScanData;
    ProgressBar spinner;
    TextView badge_notification;
    final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDScannerActivity.this);
    final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfidreader);
        SQLiteDatabase.loadLibs(this);
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
        tv.setText("Scan Activity");
        tv.setTextSize(20);
        tv.setVisibility(View.VISIBLE);
        // UI
        statusTextViewRFID = findViewById(R.id.rfidStatusText);
        scanCount = findViewById(R.id.scanCount);
        //textrfid = findViewById(R.id.tags_list);
        scannedProgressCount = findViewById(R.id.scannedProgressCount);
        scannedProgressPercentage = findViewById(R.id.scannedProgressPercentage);
        progressVal = findViewById(R.id.scanProgress);
        badge_notification = findViewById(R.id.badge_notification);
        all = findViewById(R.id.showall);
        found = findViewById(R.id.found);
        notfound = findViewById(R.id.notfound);
        postScanData = findViewById(R.id.postScan);
        saveScanData = findViewById(R.id.completeScan);
        scannedProgressPercentage.setText("0 %");
        Intent intent = getIntent();
        sso = intent.getStringExtra("sso");
        if (intent.getStringExtra("token") != null) {
            token = intent.getStringExtra("token");
        }
        if (intent.getStringExtra("total_inventory") != null) {
            total_inventory = intent.getStringExtra("total_inventory");
        }
        scannedProgressCount.setText("0/"+total_inventory);
        progressVal.setProgress(0);
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
        /*if (intent.getStringExtra("total_inventory") != null) {
            total_inventory = intent.getStringExtra("total_inventory");
        }*/
        // RFID Handler
        rfidHandler = new RFIDHandler();
        rfidHandler.onCreate(this);
        all.setChecked(true);
        ArrayList<InventoryObject> invList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
        tableInv = (TableLayout) findViewById(R.id.tableInv);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        invScrollview = (NestedScrollView)findViewById(R.id.invList);
        final TextView invNameHeader = new TextView(this);
        invNameHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                80,5));
        invNameHeader.setGravity(Gravity.CENTER);
        invNameHeader.setMaxWidth(300);
        invNameHeader.setPadding(15, 30, 0, 0);
        invNameHeader.setBackgroundResource(R.drawable.table_header_border);
        invNameHeader.setText("Product Name");
        invNameHeader.setTextSize(16);
        invNameHeader.setTextColor(Color.parseColor("#FFFFFF"));
        final TextView invRFIDCodeHeader = new TextView(this);
        invRFIDCodeHeader.setLayoutParams(new TableRow.LayoutParams(200,
                80,5));
        invRFIDCodeHeader.setGravity(Gravity.CENTER);
        invRFIDCodeHeader.setPadding(5, 30,20, 0);
        invRFIDCodeHeader.setBackgroundResource(R.drawable.table_header_border);
        invRFIDCodeHeader.setText("RFID Code");
        invRFIDCodeHeader.setTextSize(16);
        invRFIDCodeHeader.setTextColor(Color.parseColor("#FFFFFF"));
        final TextView invRFIDCodeHeader1 = new TextView(this);
        invRFIDCodeHeader1.setLayoutParams(new TableRow.LayoutParams(200,
                80,5));
        invRFIDCodeHeader1.setGravity(Gravity.CENTER);
        invRFIDCodeHeader1.setPadding(5, 30,20, 0);
        invRFIDCodeHeader1.setBackgroundResource(R.drawable.table_header_border);
        invRFIDCodeHeader1.setText("Code");
        invRFIDCodeHeader1.setTextSize(16);
        invRFIDCodeHeader1.setTextColor(Color.parseColor("#FFFFFF"));
        final TableRow trInvHeader = new TableRow(this);
        TableLayout.LayoutParams trParamsHeader = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        trInvHeader.setBackgroundResource(R.drawable.table_tr_border);
        //trParams.setMargins(10, 10, 10, 10);
        trInvHeader.setLayoutParams(trParamsHeader);
        trInvHeader.addView(invNameHeader);
        trInvHeader.addView(invRFIDCodeHeader);
        trInvHeader.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        trInvHeader.addView(invRFIDCodeHeader1);
        tableInv.addView(trInvHeader, trParamsHeader);
        int scannedJsonData = databaseHandler.getSavedDataCount(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
        if(scannedJsonData > 0){
            badge_notification.setVisibility(View.VISIBLE);
            badge_notification.setText(String.valueOf(scannedJsonData));
        }
        for (int i = 0; i < invList.size(); i++) {
            final TextView invName = new TextView(this);
            invName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    80,5));
            invName.setGravity(Gravity.CENTER);
            invName.setMaxWidth(300);
            invName.setPadding(10, 40, 0, 0);
            invName.setText(invList.get(i).getProductName());
            invName.setBackgroundResource(R.drawable.tab_border);
            invName.setId(i);
            invName.setTextSize(16);
            invName.setMinLines(2);
            invName.setSingleLine(true);
            invName.setEllipsize(TextUtils.TruncateAt.END);
            invName.setTextColor(Color.parseColor("#000000"));
            final TextView invRFIDCode = new TextView(this);
            invRFIDCode.setLayoutParams(new TableRow.LayoutParams(200,
                    80,5));
            invRFIDCode.setGravity(Gravity.CENTER);
            invRFIDCode.setPadding(5, 40, 20, 0);
            invRFIDCode.setText(invList.get(i).getRfidCode());
            invRFIDCode.setId(i+1);
            invRFIDCode.setBackgroundResource(R.drawable.tab_border);
            invRFIDCode.setTextSize(16);
            invRFIDCode.setTextColor(Color.parseColor("#000000"));
            final TextView invCode = new TextView(this);
            invCode.setLayoutParams(new TableRow.LayoutParams(200,
                    80,5));
            invCode.setGravity(Gravity.CENTER);
            invCode.setPadding(5, 40, 20, 0);
            invCode.setText(invList.get(i).getCode());
            invCode.setId(i+1);
            invCode.setBackgroundResource(R.drawable.tab_border);
            invCode.setTextSize(16);
            invCode.setTextColor(Color.parseColor("#000000"));
            final TableRow trInv = new TableRow(this);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trInv.setBackgroundResource(R.drawable.table_tr_border);
            trInv.setId(Integer.parseInt(invList.get(i).getInv_id()));
            //trParams.setMargins(10, 10, 10, 10);
            trInv.setLayoutParams(trParams);
            trInv.addView(invName);
            trInv.addView(invRFIDCode);
            trInv.addView(invCode);
            tableInv.addView(trInv, trParams);
        }
        found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(found.isChecked()){
                    ArrayList<InventoryObject> invList = databaseHandler.getFoundInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
                    tableInv.removeViews(1, Math.max(0, tableInv.getChildCount() - 1));
                    for (int i = 0; i < invList.size(); i++) {
                        final TextView invName = new TextView(RFIDScannerActivity.this);
                        invName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                80,5));
                        invName.setGravity(Gravity.CENTER);
                        invName.setMaxWidth(300);
                        invName.setPadding(10, 40, 0, 0);
                        invName.setText(invList.get(i).getProductName());
                        invName.setBackgroundResource(R.drawable.inv_scan_success);
                        invName.setId(i);
                        invName.setTextSize(16);
                        invName.setMinLines(2);
                        invName.setSingleLine(true);
                        invName.setEllipsize(TextUtils.TruncateAt.END);
                        invName.setTextColor(Color.parseColor("#000000"));
                        final TextView invRFIDCode = new TextView(RFIDScannerActivity.this);
                        invRFIDCode.setLayoutParams(new TableRow.LayoutParams(200,
                                80,5));
                        invRFIDCode.setGravity(Gravity.CENTER);
                        invRFIDCode.setPadding(5, 40, 20, 0);
                        invRFIDCode.setText(invList.get(i).getRfidCode());
                        invRFIDCode.setId(i+1);
                        invRFIDCode.setBackgroundResource(R.drawable.inv_scan_success);
                        invRFIDCode.setTextSize(16);
                        invRFIDCode.setTextColor(Color.parseColor("#000000"));
                        final TextView invCode = new TextView(RFIDScannerActivity.this);
                        invCode.setLayoutParams(new TableRow.LayoutParams(200,
                                80,5));
                        invCode.setGravity(Gravity.CENTER);
                        invCode.setPadding(5, 40, 20, 0);
                        invCode.setText(invList.get(i).getCode());
                        invCode.setId(i+1);
                        invCode.setBackgroundResource(R.drawable.inv_scan_success);
                        invCode.setTextSize(16);
                        invCode.setTextColor(Color.parseColor("#000000"));
                        final TableRow trInv = new TableRow(RFIDScannerActivity.this);
                        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT);
                        trInv.setBackgroundResource(R.drawable.table_tr_border);
                        trInv.setId(Integer.parseInt(invList.get(i).getInv_id()));
                        //trParams.setMargins(10, 10, 10, 10);
                        trInv.setLayoutParams(trParams);
                        trInv.addView(invName);
                        trInv.addView(invRFIDCode);
                        trInv.addView(invCode);
                        tableInv.addView(trInv, trParams);
                    }
                }
            }
        });
        notfound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notfound.isChecked()){
                    ArrayList<InventoryObject> invList = databaseHandler.getNotFoundInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
                    tableInv.removeViews(1, Math.max(0, tableInv.getChildCount() - 1));
                    for (int i = 0; i < invList.size(); i++) {
                        final TextView invName = new TextView(RFIDScannerActivity.this);
                        invName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                80,5));
                        invName.setGravity(Gravity.CENTER);
                        invName.setMaxWidth(300);
                        invName.setPadding(10, 40, 0, 0);
                        invName.setText(invList.get(i).getProductName());
                        invName.setBackgroundResource(R.drawable.tab_border);
                        invName.setId(i);
                        invName.setTextSize(16);
                        invName.setMinLines(2);
                        invName.setSingleLine(true);
                        invName.setEllipsize(TextUtils.TruncateAt.END);
                        invName.setTextColor(Color.parseColor("#000000"));
                        final TextView invRFIDCode = new TextView(RFIDScannerActivity.this);
                        invRFIDCode.setLayoutParams(new TableRow.LayoutParams(200,
                                80,5));
                        invRFIDCode.setGravity(Gravity.CENTER);
                        invRFIDCode.setPadding(5, 40, 20, 0);
                        invRFIDCode.setText(invList.get(i).getRfidCode());
                        invRFIDCode.setId(i+1);
                        invRFIDCode.setBackgroundResource(R.drawable.tab_border);
                        invRFIDCode.setTextSize(16);
                        invRFIDCode.setTextColor(Color.parseColor("#000000"));
                        final TextView invCode = new TextView(RFIDScannerActivity.this);
                        invCode.setLayoutParams(new TableRow.LayoutParams(200,
                                80,5));
                        invCode.setGravity(Gravity.CENTER);
                        invCode.setPadding(5, 40, 20, 0);
                        invCode.setText(invList.get(i).getCode());
                        invCode.setId(i+1);
                        invCode.setBackgroundResource(R.drawable.tab_border);
                        invCode.setTextSize(16);
                        invCode.setTextColor(Color.parseColor("#000000"));
                        final TableRow trInv = new TableRow(RFIDScannerActivity.this);
                        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT);
                        trInv.setBackgroundResource(R.drawable.table_tr_border);
                        trInv.setId(Integer.parseInt(invList.get(i).getInv_id()));
                        //trParams.setMargins(10, 10, 10, 10);
                        trInv.setLayoutParams(trParams);
                        trInv.addView(invName);
                        trInv.addView(invRFIDCode);
                        trInv.addView(invCode);
                        tableInv.addView(trInv, trParams);
                    }
                }
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(all.isChecked()){
                    ArrayList<InventoryObject> invList = databaseHandler.getALLInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
                    tableInv.removeViews(1, Math.max(0, tableInv.getChildCount() - 1));
                    for (int i = 0; i < invList.size(); i++) {
                        final TextView invName = new TextView(RFIDScannerActivity.this);
                        invName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                80,5));
                        invName.setGravity(Gravity.CENTER);
                        invName.setMaxWidth(300);
                        invName.setPadding(10, 40, 0, 0);
                        invName.setText(invList.get(i).getProductName());
                        if(invList.get(i).getScanned().equals("1")){
                            invName.setBackgroundResource(R.drawable.inv_scan_success);
                        }else{
                            invName.setBackgroundResource(R.drawable.tab_border);
                        }
                        invName.setId(i);
                        invName.setTextSize(16);
                        invName.setMinLines(2);
                        invName.setSingleLine(true);
                        invName.setEllipsize(TextUtils.TruncateAt.END);
                        invName.setTextColor(Color.parseColor("#000000"));
                        final TextView invRFIDCode = new TextView(RFIDScannerActivity.this);
                        invRFIDCode.setLayoutParams(new TableRow.LayoutParams(200,
                                80,5));
                        invRFIDCode.setGravity(Gravity.CENTER);
                        invRFIDCode.setPadding(5, 40, 20, 0);
                        invRFIDCode.setText(invList.get(i).getRfidCode());
                        invRFIDCode.setId(i+1);
                        if(invList.get(i).getScanned().equals("1")){
                            invRFIDCode.setBackgroundResource(R.drawable.inv_scan_success);
                        }else{
                            invRFIDCode.setBackgroundResource(R.drawable.tab_border);
                        }
                        invRFIDCode.setTextSize(16);
                        invRFIDCode.setTextColor(Color.parseColor("#000000"));
                        final TextView invCode = new TextView(RFIDScannerActivity.this);
                        invCode.setLayoutParams(new TableRow.LayoutParams(200,
                                80,5));
                        invCode.setGravity(Gravity.CENTER);
                        invCode.setPadding(5, 40, 20, 0);
                        invCode.setText(invList.get(i).getCode());
                        invCode.setId(i+1);
                        if(invList.get(i).getScanned().equals("1")){
                            invCode.setBackgroundResource(R.drawable.inv_scan_success);
                        }else{
                            invCode.setBackgroundResource(R.drawable.tab_border);
                        }
                        invCode.setTextSize(16);
                        invCode.setTextColor(Color.parseColor("#000000"));
                        final TableRow trInv = new TableRow(RFIDScannerActivity.this);
                        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT);
                        trInv.setBackgroundResource(R.drawable.table_tr_border);
                        trInv.setId(Integer.parseInt(invList.get(i).getInv_id()));
                        //trParams.setMargins(10, 10, 10, 10);
                        trInv.setLayoutParams(trParams);
                        trInv.addView(invName);
                        trInv.addView(invRFIDCode);
                        trInv.addView(invCode);
                        tableInv.addView(trInv, trParams);
                    }
                }
            }
        });
        postScanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Gson gson = new Gson();
                    String scannedObj = "";
                    ArrayList<RFIDScanDataObj> rfidScanDataObjs = databaseHandler.getALLInventoryScannedList(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                    scannedObj = gson.toJson(rfidScanDataObjs);
                    RFIDPostScanObj postScanObj = new RFIDPostScanObj(selectedUserId,
                            token,loggedinUserSiteId,scannedObj
                    );
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = "";
                    try {
                        jsonString = mapper.writeValueAsString(postScanObj);
                        String URL = ApiConstants.syncpostscanneddata;
                        RequestQueue requestQueue = Volley.newRequestQueue(RFIDScannerActivity.this);
                        String finalJsonString = jsonString;
                        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(jsonString),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Process os success response
                                        String res = response.toString();
                                        databaseHandler.delAllSavedScanData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedRoom);
                                        final Intent myIntent = new Intent(RFIDScannerActivity.this,
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
                                        startActivity(myIntent);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDScannerActivity.this);
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

                                error.printStackTrace();
                            }

                        });
                        int socketTimeout = 60000;//30 seconds - change to what you want
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
                        request_json.setRetryPolicy(policy);
    // add the request object to the queue to be executed
                        requestQueue.add(request_json);
                    } catch (JsonProcessingException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDScannerActivity.this);
                    dlgAlert.setTitle("Safety Stratus");
                    dlgAlert.setMessage("Slow or no Internet Connection. Your data will be saved offline. " +
                            "Please sync the data when the network is online");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final Intent myIntent = new Intent(RFIDScannerActivity.this,
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
                                    startActivity(myIntent);
                                }
                            });
                    dlgAlert.create().show();
                }
            }
        });
        saveScanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Gson gson = new Gson();
                    String scannedObj = "";
                    ArrayList<RFIDScanDataObj> rfidScanDataObjs = databaseHandler.getALLInventoryScannedList(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                    scannedObj = gson.toJson(rfidScanDataObjs);
                    RFIDPostScanObj postScanObj = new RFIDPostScanObj(selectedUserId,
                            token,loggedinUserSiteId,scannedObj
                    );
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = "";
                    try {
                        jsonString = mapper.writeValueAsString(postScanObj);
                        ContentValues cv = new ContentValues();
                        cv.put("json_data", jsonString);
                        cv.put("location_id", selectedFacil);
                        cv.put("room_id", selectedRoom);
                        databaseHandler.insertScannedInvJSONData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDScannerActivity.this);
                        dlgAlert.setTitle("Safety Stratus");
                        dlgAlert.setMessage("Data Saved Successfully!");
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Intent myIntent = new Intent(RFIDScannerActivity.this,
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
                                        startActivity(myIntent);
                                    }
                                });
                        dlgAlert.create().show();

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        rfidHandler.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        String status = rfidHandler.onResume();
        statusTextViewRFID.setText(status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rfidHandler.onDestroy();
    }

    @Override
    public void handleTagdata(TagData[] tagData) {
        final StringBuilder sb = new StringBuilder();
        final int[] scanCounts = {0};
        for (int index = 0; index < tagData.length; index++) {
            byte[] bytes = Hex.stringToBytes(String.valueOf(tagData[index].getTagID().toCharArray()));
            try {
                sb.append(new String(bytes, "UTF-8") + "&&&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                String[] tagList = sb.toString().split("&&&");
                ArrayList<String> list = new ArrayList<String>();
                for (int u=0;u<tagList.length;u++){
                    list.add(tagList[u]);
                }
                // Create a new ArrayList

                // Traverse through the first list
                for (String element : list) {

                    // If this element is not present in newList
                    // then add it
                    if (!newList.contains(element)) {

                        newList.add(element);
                    }
                }
                newList.replaceAll(String::trim);
            }
        });
    }

    @Override
    public void handleTriggerPress(boolean pressed) {
        if (pressed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invScrollview.setVisibility(View.GONE);
                    ConstraintLayout constraintLayout = findViewById(R.id.rfidLayout);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.completeScan,ConstraintSet.START,R.id.progressBar1,ConstraintSet.START,0);
                    constraintSet.connect(R.id.completeScan,ConstraintSet.END,R.id.progressBar1,ConstraintSet.END,0);
                    constraintSet.connect(R.id.completeScan,ConstraintSet.TOP,R.id.progressBar1,ConstraintSet.BOTTOM,0);
                    constraintSet.applyTo(constraintLayout);
                    ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) saveScanData.getLayoutParams();
                    newLayoutParams.topMargin = 20;
                    newLayoutParams.leftMargin = 10;
                    newLayoutParams.rightMargin = 10;
                    newLayoutParams.bottomMargin = 0;
                    saveScanData.setLayoutParams(newLayoutParams);
                    spinner.setVisibility(View.VISIBLE);
                }
            });
            rfidHandler.performInventory();
        } else {
            triggerReleaseEventRecieved();
        }
    }
    public void triggerReleaseEventRecieved() {
        rfidHandler.stopInventory();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newList = new ArrayList<String>(new HashSet<String>(newList));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("TESTTTTTTTTTTT",newList.size()+"*****");
                tableInv.removeAllViews();
                final TextView invNameHeader = new TextView(RFIDScannerActivity.this);
                invNameHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        80, 5));
                invNameHeader.setGravity(Gravity.CENTER);
                invNameHeader.setMaxWidth(300);
                invNameHeader.setPadding(15, 30, 0, 0);
                invNameHeader.setBackgroundResource(R.drawable.table_header_border);
                invNameHeader.setText("Product Name");
                invNameHeader.setTextSize(16);
                invNameHeader.setTextColor(Color.parseColor("#FFFFFF"));
                final TextView invRFIDCodeHeader = new TextView(RFIDScannerActivity.this);
                invRFIDCodeHeader.setLayoutParams(new TableRow.LayoutParams(200,
                        80, 5));
                invRFIDCodeHeader.setGravity(Gravity.CENTER);
                invRFIDCodeHeader.setPadding(5, 30, 20, 0);
                invRFIDCodeHeader.setBackgroundResource(R.drawable.table_header_border);
                invRFIDCodeHeader.setText("RFID Code");
                invRFIDCodeHeader.setTextSize(16);
                invRFIDCodeHeader.setTextColor(Color.parseColor("#FFFFFF"));
                final TextView invRFIDCodeHeader1 = new TextView(RFIDScannerActivity.this);
                invRFIDCodeHeader1.setLayoutParams(new TableRow.LayoutParams(200,
                        80, 5));
                invRFIDCodeHeader1.setGravity(Gravity.CENTER);
                invRFIDCodeHeader1.setPadding(5, 30, 20, 0);
                invRFIDCodeHeader1.setBackgroundResource(R.drawable.table_header_border);
                invRFIDCodeHeader1.setText("Code");
                invRFIDCodeHeader1.setTextSize(16);
                invRFIDCodeHeader1.setTextColor(Color.parseColor("#FFFFFF"));
                final TableRow trInvHeader = new TableRow(RFIDScannerActivity.this);
                TableLayout.LayoutParams trParamsHeader = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trInvHeader.setBackgroundResource(R.drawable.table_tr_border);
                //trParams.setMargins(10, 10, 10, 10);
                trInvHeader.setLayoutParams(trParamsHeader);
                trInvHeader.addView(invNameHeader);
                trInvHeader.addView(invRFIDCodeHeader);
                trInvHeader.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                trInvHeader.addView(invRFIDCodeHeader1);
                tableInv.addView(trInvHeader, trParamsHeader);
                ArrayList<InventoryObject> invList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE), selectedRoom);
                for (int i = 0; i < invList.size(); i++) {
                    final TextView invName = new TextView(RFIDScannerActivity.this);
                    invName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            80, 5));
                    invName.setGravity(Gravity.CENTER);
                    invName.setMaxWidth(300);
                    invName.setPadding(10, 40, 0, 0);
                    invName.setText(invList.get(i).getProductName());
                    //invName.setBackgroundResource(R.drawable.tab_border);
                    invName.setId(i);
                    invName.setTextSize(16);
                    invName.setMinLines(2);
                    invName.setSingleLine(true);
                    invName.setEllipsize(TextUtils.TruncateAt.END);
                    invName.setTextColor(Color.parseColor("#000000"));
                    final TextView invRFIDCode = new TextView(RFIDScannerActivity.this);
                    invRFIDCode.setLayoutParams(new TableRow.LayoutParams(200,
                            80, 5));
                    invRFIDCode.setGravity(Gravity.CENTER);
                    invRFIDCode.setPadding(5, 40, 20, 0);
                    invRFIDCode.setText(invList.get(i).getRfidCode());
                    invRFIDCode.setId(i + 1);
                    //invRFIDCode.setBackgroundResource(R.drawable.tab_border);
                    invRFIDCode.setTextSize(16);
                    invRFIDCode.setTextColor(Color.parseColor("#000000"));
                    final TextView invCode = new TextView(RFIDScannerActivity.this);
                    invCode.setLayoutParams(new TableRow.LayoutParams(200,
                            80, 5));
                    invCode.setGravity(Gravity.CENTER);
                    invCode.setPadding(5, 40, 20, 0);
                    invCode.setText(invList.get(i).getCode());
                    invCode.setId(i + 1);
                    //invCode.setBackgroundResource(R.drawable.tab_border);
                    invCode.setTextSize(16);
                    invCode.setTextColor(Color.parseColor("#000000"));
                    if (newList.contains(invList.get(i).getRfidCode())) {
                        invName.setBackgroundResource(R.drawable.inv_scan_success);
                        invRFIDCode.setBackgroundResource(R.drawable.inv_scan_success);
                        invCode.setBackgroundResource(R.drawable.inv_scan_success);
                        ContentValues cv = new ContentValues();
                        cv.put("location_id", selectedFacil);
                        cv.put("room_id", selectedRoom);
                        cv.put("inventory_id", Integer.parseInt(invList.get(i).getInv_id()));
                        cv.put("scanned_by", selectedUserId);
                        cv.put("scanned", 1);
                        databaseHandler.insertScannedInvData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                    } else {
                        invName.setBackgroundResource(R.drawable.tab_border);
                        invRFIDCode.setBackgroundResource(R.drawable.tab_border);
                        invCode.setBackgroundResource(R.drawable.tab_border);

                    }
                    final TableRow trInv = new TableRow(RFIDScannerActivity.this);
                    TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    trInv.setId(Integer.parseInt(invList.get(i).getInv_id()));
                    //trParams.setMargins(10, 10, 10, 10);
                    trInv.setLayoutParams(trParams);
                    trInv.addView(invName);
                    trInv.addView(invRFIDCode);
                        /*if (newList.contains(invList.get(i).getRfidCode())) {
                            Log.e("RFIDCONTAINS EXISTS>>",invList.get(i).getRfidCode()+"**");
                            trInv.setBackgroundResource(R.drawable.inv_scan_success);
                        } else {
                            Log.e("RFIDCONTAINS DONT EXISTS>>",invList.get(i).getRfidCode()+"**");
                            trInv.setBackgroundResource(R.drawable.table_tr_border);
                        }*/
                    trInv.setBackgroundResource(R.drawable.table_tr_border);
                    trInv.addView(invCode);
                    tableInv.addView(trInv, trParams);
                }
                spinner.setVisibility(View.GONE);
                invScrollview.setVisibility(View.VISIBLE);
                ConstraintLayout constraintLayout = findViewById(R.id.rfidLayout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.completeScan,ConstraintSet.START,R.id.invList,ConstraintSet.START,0);
                constraintSet.connect(R.id.completeScan,ConstraintSet.END,R.id.invList,ConstraintSet.END,0);
                constraintSet.connect(R.id.completeScan,ConstraintSet.TOP,R.id.invList,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) saveScanData.getLayoutParams();
                newLayoutParams.topMargin = 20;
                newLayoutParams.leftMargin = 10;
                newLayoutParams.rightMargin = 10;
                newLayoutParams.bottomMargin = 0;
                saveScanData.setLayoutParams(newLayoutParams);
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.postScan,ConstraintSet.BOTTOM,R.id.rfidLayout,ConstraintSet.BOTTOM,0);
                constraintSet.connect(R.id.postScan,ConstraintSet.END,R.id.completeScan,ConstraintSet.END,0);
                constraintSet.connect(R.id.postScan,ConstraintSet.START,R.id.completeScan,ConstraintSet.START,0);
                constraintSet.connect(R.id.postScan,ConstraintSet.TOP,R.id.completeScan,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
                ConstraintLayout.LayoutParams newLayoutParams1 = (ConstraintLayout.LayoutParams) postScanData.getLayoutParams();
                newLayoutParams1.topMargin = 20;
                newLayoutParams1.leftMargin = 0;
                newLayoutParams1.rightMargin = 0;
                newLayoutParams1.bottomMargin = 20;
                postScanData.setLayoutParams(newLayoutParams1);
                int scannedCount = databaseHandler.checkScannedDataCount(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedFacil,selectedRoom);
                setscancount(String.valueOf(scannedCount), String.valueOf(newList.size()));
                //SyncInvTable sdb = new SyncInvTable();
                //sdb.execute();
            }
        });
    }

    public void setscancount(String count, String total_scan_count){
        scanCount.setText(total_scan_count);
        Log.e("TEST000000",count+"**");
        Log.e("TEST0000001",total_inventory+"**");
        if(Integer.parseInt(total_inventory)>0) {
            if(Integer.parseInt(count)<=Integer.parseInt(total_inventory)) {
                int percent = (Integer.parseInt(count) * 100) / Integer.parseInt(total_inventory);
                scannedProgressPercentage.setText(percent + " %");
                scannedProgressCount.setText(count + "/" + total_inventory);
                progressVal.setProgress(percent);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            final Intent myIntent = new Intent(RFIDScannerActivity.this,
                    RFIDActivity.class);
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
            myIntent.putExtra("empName", empName);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
    }
    class SyncInvTable extends AsyncTask<String, String, String> {

        private ProgressDialog progressSync = new ProgressDialog(RFIDScannerActivity.this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDScannerActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // disable dismiss by tapping outside of the dialog
            progressSync.setTitle("");
            progressSync.setMessage("Uploading..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(400, 200);
            super.onPreExecute();
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tableInv.removeAllViews();
                    final TextView invNameHeader = new TextView(RFIDScannerActivity.this);
                    invNameHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            80, 5));
                    invNameHeader.setGravity(Gravity.CENTER);
                    invNameHeader.setMaxWidth(300);
                    invNameHeader.setPadding(15, 30, 0, 0);
                    invNameHeader.setBackgroundResource(R.drawable.table_header_border);
                    invNameHeader.setText("Product Name");
                    invNameHeader.setTextSize(16);
                    invNameHeader.setTextColor(Color.parseColor("#FFFFFF"));
                    final TextView invRFIDCodeHeader = new TextView(RFIDScannerActivity.this);
                    invRFIDCodeHeader.setLayoutParams(new TableRow.LayoutParams(200,
                            80, 5));
                    invRFIDCodeHeader.setGravity(Gravity.CENTER);
                    invRFIDCodeHeader.setPadding(5, 30, 20, 0);
                    invRFIDCodeHeader.setBackgroundResource(R.drawable.table_header_border);
                    invRFIDCodeHeader.setText("RFID Code");
                    invRFIDCodeHeader.setTextSize(16);
                    invRFIDCodeHeader.setTextColor(Color.parseColor("#FFFFFF"));
                    final TextView invRFIDCodeHeader1 = new TextView(RFIDScannerActivity.this);
                    invRFIDCodeHeader1.setLayoutParams(new TableRow.LayoutParams(200,
                            80, 5));
                    invRFIDCodeHeader1.setGravity(Gravity.CENTER);
                    invRFIDCodeHeader1.setPadding(5, 30, 20, 0);
                    invRFIDCodeHeader1.setBackgroundResource(R.drawable.table_header_border);
                    invRFIDCodeHeader1.setText("Code");
                    invRFIDCodeHeader1.setTextSize(16);
                    invRFIDCodeHeader1.setTextColor(Color.parseColor("#FFFFFF"));
                    final TableRow trInvHeader = new TableRow(RFIDScannerActivity.this);
                    TableLayout.LayoutParams trParamsHeader = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    trInvHeader.setBackgroundResource(R.drawable.table_tr_border);
                    //trParams.setMargins(10, 10, 10, 10);
                    trInvHeader.setLayoutParams(trParamsHeader);
                    trInvHeader.addView(invNameHeader);
                    trInvHeader.addView(invRFIDCodeHeader);
                    trInvHeader.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    trInvHeader.addView(invRFIDCodeHeader1);
                    tableInv.addView(trInvHeader, trParamsHeader);
                    ArrayList<InventoryObject> invList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE), selectedRoom);
                    for (int i = 0; i < invList.size(); i++) {
                        final TextView invName = new TextView(RFIDScannerActivity.this);
                        invName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                80, 5));
                        invName.setGravity(Gravity.CENTER);
                        invName.setMaxWidth(300);
                        invName.setPadding(10, 40, 0, 0);
                        invName.setText(invList.get(i).getProductName());
                        //invName.setBackgroundResource(R.drawable.tab_border);
                        invName.setId(i);
                        invName.setTextSize(16);
                        invName.setMinLines(2);
                        invName.setSingleLine(true);
                        invName.setEllipsize(TextUtils.TruncateAt.END);
                        invName.setTextColor(Color.parseColor("#000000"));
                        final TextView invRFIDCode = new TextView(RFIDScannerActivity.this);
                        invRFIDCode.setLayoutParams(new TableRow.LayoutParams(200,
                                80, 5));
                        invRFIDCode.setGravity(Gravity.CENTER);
                        invRFIDCode.setPadding(5, 40, 20, 0);
                        invRFIDCode.setText(invList.get(i).getRfidCode());
                        invRFIDCode.setId(i + 1);
                        //invRFIDCode.setBackgroundResource(R.drawable.tab_border);
                        invRFIDCode.setTextSize(16);
                        invRFIDCode.setTextColor(Color.parseColor("#000000"));
                        final TextView invCode = new TextView(RFIDScannerActivity.this);
                        invCode.setLayoutParams(new TableRow.LayoutParams(200,
                                80, 5));
                        invCode.setGravity(Gravity.CENTER);
                        invCode.setPadding(5, 40, 20, 0);
                        invCode.setText(invList.get(i).getCode());
                        invCode.setId(i + 1);
                        //invCode.setBackgroundResource(R.drawable.tab_border);
                        invCode.setTextSize(16);
                        invCode.setTextColor(Color.parseColor("#000000"));
                        if (newList.contains(invList.get(i).getRfidCode())) {
                            invName.setBackgroundResource(R.drawable.inv_scan_success);
                            invRFIDCode.setBackgroundResource(R.drawable.inv_scan_success);
                            invCode.setBackgroundResource(R.drawable.inv_scan_success);
                            ContentValues cv = new ContentValues();
                            cv.put("location_id", selectedFacil);
                            cv.put("room_id", selectedRoom);
                            cv.put("inventory_id", Integer.parseInt(invList.get(i).getInv_id()));
                            cv.put("scanned_by", selectedUserId);
                            cv.put("scanned", 1);
                            databaseHandler.insertScannedInvData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                        } else {
                            invName.setBackgroundResource(R.drawable.tab_border);
                            invRFIDCode.setBackgroundResource(R.drawable.tab_border);
                            invCode.setBackgroundResource(R.drawable.tab_border);

                        }
                        final TableRow trInv = new TableRow(RFIDScannerActivity.this);
                        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT);
                        trInv.setId(Integer.parseInt(invList.get(i).getInv_id()));
                        //trParams.setMargins(10, 10, 10, 10);
                        trInv.setLayoutParams(trParams);
                        trInv.addView(invName);
                        trInv.addView(invRFIDCode);
                        /*if (newList.contains(invList.get(i).getRfidCode())) {
                            Log.e("RFIDCONTAINS EXISTS>>",invList.get(i).getRfidCode()+"**");
                            trInv.setBackgroundResource(R.drawable.inv_scan_success);
                        } else {
                            Log.e("RFIDCONTAINS DONT EXISTS>>",invList.get(i).getRfidCode()+"**");
                            trInv.setBackgroundResource(R.drawable.table_tr_border);
                        }*/
                        trInv.setBackgroundResource(R.drawable.table_tr_border);
                        trInv.addView(invCode);
                        tableInv.addView(trInv, trParams);
                    }
                }
            });
            return "completed";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progressSync.dismiss();
        }

    }
}