package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
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
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.TagData;
import net.sqlcipher.database.SQLiteDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public class RFIDScannerActivity extends AppCompatActivity implements RFIDHandler.ResponseHandlerInterface {
    public TextView statusTextViewRFID = null;
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;
    private ListView textrfid;
    private boolean backpressedonce = false;
    private TextView testStatus;
    private TextView scannedProgressCount;
    private TextView scannedProgressPercentage;
    private ProgressBar progressVal;
    public TextView scanCount;
    CopyOnWriteArrayList<String> newList = new CopyOnWriteArrayList<String>();
    CopyOnWriteArrayList<String> scannedTagList = new CopyOnWriteArrayList<String>();
    //ArrayList<String> newListFiltered = new ArrayList<String>();
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
    String reconc_id="";
    String json_data_from_continue="";
    String total_inventory = "120";
    ListView tagList;
    NestedScrollView invScrollview;
    ProgressBar progressbarrecon;
    RadioButton all;
    RadioButton found;
    RadioButton notfound;
    Button saveScanData;
    Button completeScan;
    Button scanBarcode;
    ProgressBar spinner;
    TextView scanText;
    Button backToHome;
    IntentModel model;
    String scannedTotalCount="0";
    ArrayList<InventoryObject> scannedInvList = null;
    ArrayList<String> scannedListfromContinue = new ArrayList<String>();
    ArrayList<String> scannedOutOflocationListfromContinue = new ArrayList<String>();
    ArrayList<String> rfidTagsExisting = new ArrayList<String>();
    DatabaseHandler databaseHandler =null;
    SQLiteDatabase db = null;
    ArrayList<InventoryObject> disposedinvList=null;
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
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        header = (ConstraintLayout) findViewById(R.id.header);
        TextView tv = (TextView) findViewById(R.id.headerId);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.RED);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);
        tv.setText("Scan Activity");
        tv.setTextSize(20);
        tv.setVisibility(View.VISIBLE);
        databaseHandler = DatabaseHandler.getInstance(RFIDScannerActivity.this);
        db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        // UI
        statusTextViewRFID = findViewById(R.id.rfidStatusText);
        scanCount = findViewById(R.id.scanCount);
        //textrfid = findViewById(R.id.tags_list);
        scannedProgressCount = findViewById(R.id.scannedProgressCount);
        scannedProgressPercentage = findViewById(R.id.scannedProgressPercentage);
        scanText = findViewById(R.id.progressbartext);
        progressVal = findViewById(R.id.scanProgress);
        progressbarrecon = findViewById(R.id.progressbarreconciliation);
        all = findViewById(R.id.showall);
        found = findViewById(R.id.found);
        notfound = findViewById(R.id.notfound);
        completeScan = findViewById(R.id.completeScan);
        saveScanData = findViewById(R.id.saveScan);
        backToHome = (Button) findViewById(R.id.backToHome);
        scanBarcode = (Button) findViewById(R.id.scanBarcodeReconc);
        backToHome.setVisibility(View.VISIBLE);
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
        if(intent.getStringExtra("reconc_id")!=null) {
            reconc_id = intent.getStringExtra("reconc_id");
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
        if (intent.getStringExtra("json_data_from_continue") != null) {
            json_data_from_continue = intent.getStringExtra("json_data_from_continue");
        }
        if (intent.getStringExtra("scannedTotalCount") != null) {
            scannedTotalCount = intent.getStringExtra("scannedTotalCount");
        }
        int scanned = databaseHandler.checkScannedDataCount(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedFacil, selectedRoom,selectedUserId, reconc_id);
        // RFID Handler
        scannedTagList = databaseHandler.getScannedRFIDCodes(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE),selectedRoom, reconc_id);
        setscancount(String.valueOf(scanned), String.valueOf(scannedTagList.size()));
        rfidHandler = new RFIDHandler();
        rfidHandler.onCreate(this);
        all.setChecked(true);
        scannedInvList = new ArrayList<InventoryObject>();
        if(intent.getSerializableExtra("scannedInvList")!=null) {
            scannedInvList = (ArrayList<InventoryObject>) intent.getSerializableExtra("scannedInvList");
        }else
            scannedInvList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
        if (scannedInvList.size()==0)
            scannedInvList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
        //ArrayList<InventoryObject> invList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
        disposedinvList = databaseHandler.getDisposedInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE), selectedRoom);
        for (int t=0;t<scannedInvList.size();t++){
            rfidTagsExisting.add(scannedInvList.get(t).getRfidCode());
        }
        tagList = (ListView)findViewById(R.id.invList);
        //spinner = (ProgressBar)findViewById(R.id.progressBar1);
        model = new IntentModel(loggedinUserSiteId,selectedUserId,token,md5Pwd,sso,empName,site_name,loggedinUsername,"2",null,selectedSearchValue,selectedFacilName,selectedFacil,selectedRoomName,selectedRoom,total_inventory,reconc_id);
        if (json_data_from_continue.trim().length()>0) {
            try {
                JSONObject obj = new JSONObject(json_data_from_continue.toString());
                JSONArray inventory_details = new JSONArray(obj.getString("inventory_details"));
                int n = inventory_details.length();
                for (int i = 0; i < n; ++i) {
                    JSONObject details = inventory_details.getJSONObject(i);
                    if(details.getInt("inventory_id")==-1){
                        if (details.getString("rfid_code").trim().length()>0)
                            scannedOutOflocationListfromContinue.add(String.valueOf(details.getString("rfid_code")));
                        else
                            scannedOutOflocationListfromContinue.add(String.valueOf(details.getString("code")));
                    }else{
                        scannedListfromContinue.add(String.valueOf(details.getInt("inventory_id")));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (scannedListfromContinue.size()>0){
            scannedProgressCount.setText(scannedListfromContinue.size()+"/"+total_inventory);
            int percent = (scannedListfromContinue.size() * 100) / Integer.parseInt(total_inventory);
            scannedProgressPercentage.setText(percent + " %");
            progressVal.setProgress(percent);
        }
        if (scannedOutOflocationListfromContinue.size()>0){
            for (int h=0;h<scannedOutOflocationListfromContinue.size();h++) {
                scannedInvList.add(new InventoryObject(scannedOutOflocationListfromContinue.get(h),"N/A","-1","N/A","1","N/A",true,"0"));
            }
        }
        if (scannedListfromContinue.size()>0){
            for (int h=0;h<scannedInvList.size();h++) {
                if(scannedListfromContinue.contains(scannedInvList.get(h).getInv_id())){
                    scannedInvList.get(h).setFlag(true);
                }
            }
        }
        CustomisedRFIDScannedList adapter = new CustomisedRFIDScannedList(scannedInvList,model, RFIDScannerActivity.this);
        tagList.setAdapter(adapter);
        found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(found.isChecked()){
                    CustomisedRFIDScannedList adapter = (CustomisedRFIDScannedList)tagList.getAdapter();
                    tagList.removeAllViewsInLayout();
                    adapter.notifyDataSetChanged();
                    ArrayList<InventoryObject> invList = databaseHandler.getFoundInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom,reconc_id,"rfid");
                    CustomisedRFIDScannedList adapter1 = new CustomisedRFIDScannedList(invList,model, RFIDScannerActivity.this);
                    tagList.setAdapter(adapter1);
                }
            }
        });
        notfound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notfound.isChecked()){
                    CustomisedRFIDScannedList adapter = (CustomisedRFIDScannedList)tagList.getAdapter();
                    tagList.removeAllViewsInLayout();
                    adapter.notifyDataSetChanged();
                    ArrayList<InventoryObject> invList = databaseHandler.getNotFoundInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom,reconc_id,"rfid");
                    CustomisedRFIDScannedList adapter1 = new CustomisedRFIDScannedList(invList,model, RFIDScannerActivity.this);
                    tagList.setAdapter(adapter1);
                }
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(all.isChecked()){
                    CustomisedRFIDScannedList adapter = (CustomisedRFIDScannedList)tagList.getAdapter();
                    tagList.removeAllViewsInLayout();
                    adapter.notifyDataSetChanged();
                    ArrayList<InventoryObject> invList = new ArrayList<InventoryObject>();
                    invList = databaseHandler.getALLInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom,reconc_id,"rfid");
                    if(invList.size()==0){
                        invList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
                    }
                    CustomisedRFIDScannedList adapter1 = new CustomisedRFIDScannedList(invList,model, RFIDScannerActivity.this);
                    tagList.setAdapter(adapter1);
                }
            }
        });

        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    rfidHandler.reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.BARCODE_MODE, true);
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                }
                scannedTagList = databaseHandler.getScannedRFIDCodes(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE),selectedRoom, reconc_id);
                final Intent myIntent = new Intent(RFIDScannerActivity.this,
                        ScanBarcodeReconciliation.class);
                myIntent.putExtra("user_id", selectedUserId);
                myIntent.putExtra("site_id", loggedinUserSiteId);
                myIntent.putExtra("token", token);
                myIntent.putExtra("sso", sso);
                myIntent.putExtra("md5pwd", md5Pwd);
                myIntent.putExtra("reconc_id", reconc_id+"");
                myIntent.putExtra("loggedinUsername", loggedinUsername);
                myIntent.putExtra("site_name", site_name);
                myIntent.putExtra("empName",empName);
                myIntent.putExtra("selectedFacilName", selectedFacilName);
                myIntent.putExtra("selectedFacil", selectedFacil+"");
                myIntent.putExtra("selectedRoomName", selectedRoomName);
                myIntent.putExtra("selectedRoom", selectedRoom+"");
                myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                myIntent.putExtra("scannedInvList", scannedInvList);
                myIntent.putExtra("total_inventory", total_inventory+"");
                myIntent.putExtra("scannedTotalCount", scannedTagList.size()+"");
                myIntent.putExtra("flag","2");
                startActivity(myIntent);
            }
        });
        saveScanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                ArrayList<RFIDScanDataObj> rfidScanDataObjs = databaseHandler.getALLInventoryScannedList(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE),reconc_id);
                String rfidJson = gson.toJson(rfidScanDataObjs);
                RFIDPostScanObj postScanObj = new RFIDPostScanObj(selectedUserId,
                        token,loggedinUserSiteId,selectedRoom,rfidJson,reconc_id
                );
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = "";
                try {
                    jsonString = mapper.writeValueAsString(postScanObj);
                    ContentValues cv = new ContentValues();
                    cv.put("json_data", jsonString);
                    cv.put("location_id", selectedFacil);
                    cv.put("user_id", selectedUserId);
                    cv.put("room_id", selectedRoom);
                    cv.put("reconc_id", reconc_id);
                    cv.put("scan_type", "rfid");
                    databaseHandler.insertScannedInvJSONData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDScannerActivity.this);
                    dlgAlert.setTitle("Safety Stratus");
                    dlgAlert.setMessage("Data Saved Successfully!");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    dlgAlert.create().show();

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        completeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                ArrayList<RFIDScanDataObj> rfidScanDataObjs = databaseHandler.getALLInventoryScannedList(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE),reconc_id);
                RFIDPostScanObj postScanObj = new RFIDPostScanObj(selectedUserId,
                        token,loggedinUserSiteId,selectedRoom,gson.toJson(rfidScanDataObjs),reconc_id
                );
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = "";
                try {
                    jsonString = mapper.writeValueAsString(postScanObj);
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
                    ContentValues cv = new ContentValues();
                    cv.put("json_data", jsonString);
                    cv.put("location_id", selectedFacil);
                    cv.put("room_id", selectedRoom);
                    cv.put("user_id", selectedUserId);
                    cv.put("scan_type", "rfid");
                    cv.put("reconc_id", reconc_id);
                    databaseHandler.insertScannedInvJSONData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                    if(connected){
                        String URL = ApiConstants.syncpostscanneddata;
                        String finalJsonString = jsonString;
                        Log.e("TestJson>>>",jsonString);
                        RequestQueue requestQueue = Volley.newRequestQueue(RFIDScannerActivity.this);
                        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(jsonString),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Process os success response
                                        String res = response.toString();
                                        databaseHandler.delSavedScanData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedUserId,selectedRoom,reconc_id);
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
                                        myIntent.putExtra("selectedRoom", selectedRoom+"");
                                        myIntent.putExtra("selectedFacil", selectedFacil+"");
                                        startActivity(myIntent);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDScannerActivity.this);
                                dlgAlert.setTitle("Safety Stratus");
                                dlgAlert.setMessage("Error response: Request timed out! Your data is saved offline");
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
                                                myIntent.putExtra("selectedRoom", selectedRoom+"");
                                                myIntent.putExtra("selectedFacil", selectedFacil+"");
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
                        requestQueue.add(request_json);
                    }else{
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDScannerActivity.this);
                        dlgAlert.setTitle("Safety Stratus");
                        dlgAlert.setMessage("Your internet connection is not active! Your data is saved offline");
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
                                        myIntent.putExtra("selectedRoom", selectedRoom+"");
                                        myIntent.putExtra("selectedFacil", selectedFacil+"");
                                        startActivity(myIntent);
                                        return;
                                    }
                                });
                        dlgAlert.create().show();
                    }
                } catch (JsonProcessingException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!backpressedonce) {
                    backpressedonce = true;
                    backToHome();
                }
            }
        });
        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InventoryObject selectedItem = (InventoryObject) parent.getItemAtPosition(position);
                final Intent myIntent = new Intent(RFIDScannerActivity.this,
                        Container_Info.class);
                myIntent.putExtra("user_id", selectedUserId);
                myIntent.putExtra("site_id", loggedinUserSiteId);
                myIntent.putExtra("token", token);
                myIntent.putExtra("sso", sso);
                myIntent.putExtra("md5pwd", md5Pwd);
                myIntent.putExtra("reconc_id", reconc_id+"");
                myIntent.putExtra("loggedinUsername", loggedinUsername);
                myIntent.putExtra("site_name", site_name);
                myIntent.putExtra("empName",empName);
                myIntent.putExtra("selectedFacilName", selectedFacilName);
                myIntent.putExtra("selectedFacil", selectedFacil+"");
                myIntent.putExtra("selectedRoomName", selectedRoomName);
                myIntent.putExtra("selectedRoom", selectedRoom+"");
                myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                myIntent.putExtra("scannedInvList", scannedInvList);
                myIntent.putExtra("total_inventory", total_inventory+"");
                myIntent.putExtra("scannedTotalCount", scannedTotalCount+"");
                myIntent.putExtra("flag","2");
                myIntent.putExtra("scannedCode", selectedItem.getInv_id()+"");
                startActivity(myIntent);
            }
        });
    }
    public void backToHome() {

            if(rfidHandler!=null) {
                try {
                    rfidHandler.reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.BARCODE_MODE, true);
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                }
            }
            String scannedCount = databaseHandler.checkScannedDataFullCount(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedFacil,selectedRoom,selectedUserId,reconc_id);
            if (Integer.parseInt(scannedCount)>0){
                String savedscannedCount = databaseHandler.checkSavedScannedDataFullCount(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), reconc_id);
                if (Integer.parseInt(savedscannedCount)==0){
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RFIDScannerActivity.this);
                    dlgAlert.setTitle("Safety Stratus");
                    dlgAlert.setMessage("Would you like to save the scanned data before navigating to the home page? If not, it will be erased.");
                    dlgAlert.setPositiveButton("Save and Continue",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Gson gson = new Gson();
                                    ArrayList<RFIDScanDataObj> rfidScanDataObjs = databaseHandler.getALLInventoryScannedList(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE),reconc_id);
                                    String rfidJson = gson.toJson(rfidScanDataObjs);
                                    RFIDPostScanObj postScanObj = new RFIDPostScanObj(selectedUserId,
                                            token,loggedinUserSiteId,selectedRoom,rfidJson,reconc_id
                                    );
                                    ObjectMapper mapper = new ObjectMapper();
                                    String jsonString = "";
                                    try {
                                        jsonString = mapper.writeValueAsString(postScanObj);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    ContentValues cv = new ContentValues();
                                    cv.put("json_data", jsonString);
                                    cv.put("location_id", selectedFacil);
                                    cv.put("user_id", selectedUserId);
                                    cv.put("room_id", selectedRoom);
                                    cv.put("reconc_id", Integer.parseInt(reconc_id));
                                    cv.put("scan_type", "rfid");
                                    databaseHandler.insertScannedInvJSONData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                                    //databaseHandler.delSavedScanDataOnly(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedUserId,selectedRoom,reconc_id);
                                    final Intent myIntent = new Intent(RFIDScannerActivity.this,
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
                                    //finish();
                                }
                            });
                    dlgAlert.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseHandler.delSavedScanDataOnly(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedUserId,selectedRoom,reconc_id);
                                    final Intent myIntent = new Intent(RFIDScannerActivity.this,
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
                                    //finish();
                                }
                            });
                    dlgAlert.create().show();
                }
                else{
                    Gson gson = new Gson();
                    ArrayList<RFIDScanDataObj> rfidScanDataObjs = databaseHandler.getALLInventoryScannedList(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE),reconc_id);
                    String rfidJson = gson.toJson(rfidScanDataObjs);
                    RFIDPostScanObj postScanObj = new RFIDPostScanObj(selectedUserId,
                            token,loggedinUserSiteId,selectedRoom,rfidJson,reconc_id
                    );
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = "";
                    try {
                        jsonString = mapper.writeValueAsString(postScanObj);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    ContentValues cv = new ContentValues();
                    cv.put("json_data", jsonString);
                    cv.put("location_id", selectedFacil);
                    cv.put("user_id", selectedUserId);
                    cv.put("room_id", selectedRoom);
                    cv.put("reconc_id", reconc_id);
                    cv.put("scan_type", "rfid");
                    databaseHandler.insertScannedInvJSONData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                    //databaseHandler.delSavedScanDataOnly(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedUserId,selectedRoom,reconc_id);
                    final Intent myIntent = new Intent(RFIDScannerActivity.this,
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
                    //finish();
                }
            }
            else {
                final Intent myIntent = new Intent(RFIDScannerActivity.this,
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
                //finish();
            }
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
            if (isHex(tagData[index].getTagID())) {
                if (tagData[index].getTagID().startsWith("0000000000000000")) {
                    String tagId = tagData[index].getTagID().substring(16, tagData[index].getTagID().length());
                    String firstLetter = tagId.substring(0, 1);
                    if(firstLetter.equalsIgnoreCase("C")) {
                        String outputString = tagId.replaceAll("\u0000", "");
                        sb.append(outputString + "&&&");
                    }
                } else {
                    byte[] bytes = Hex.stringToBytes(String.valueOf(tagData[index].getTagID().toCharArray()));
                    if (!containsNonAscii(new String(bytes, StandardCharsets.UTF_8))) {
                        String tag_Id = new String(bytes, StandardCharsets.UTF_8);
                        String firstLetter_tag_id = tag_Id.substring(0, 1);
                        if (firstLetter_tag_id.equalsIgnoreCase("C")) {
                            String outputString = tag_Id.replaceAll("\u0000", "");
                            sb.append(outputString + "&&&");
                        }
                    }
                }
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
                        /*if (!flag.contains(element.trim())) {
                            if (rfidTagsExisting.contains(element.trim())) {
                                rfidHandler.startbeepingTimer();
                                flag = flag + element.trim() + ",";
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }*/
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
                    progressbarrecon.setVisibility(View.VISIBLE);
                    scanText.setVisibility(View.VISIBLE);
                    CustomisedRFIDScannedList adapter = (CustomisedRFIDScannedList)tagList.getAdapter();
                    tagList.removeAllViewsInLayout();
                    adapter.notifyDataSetChanged();
                    tagList.setVisibility(View.GONE);
                }
            });
            rfidHandler.performInventory();
        } else {
            triggerReleaseEventRecieved();
        }
    }
    public void triggerReleaseEventRecieved() {
        rfidHandler.stopInventory();
            HashSet<String> setWithoutDuplicates = new HashSet<String>(newList);
            newList.clear();
            newList.addAll(setWithoutDuplicates);
            ArrayList<BatchInsertionObject> batchInsertData = new ArrayList<BatchInsertionObject>();
            ArrayList<String> rfids = new ArrayList<String>();
            for (int i = 0; i < scannedInvList.size(); i++) {
            if(scannedInvList.get(i).getRfidCode()!=null) {
                if (scannedInvList.get(i).getRfidCode().trim().length() > 0) {
                    if (newList.contains(scannedInvList.get(i).getRfidCode())) {
                        if (!scannedInvList.get(i).isFlag()) {
                            scannedInvList.get(i).setFlag(true);
                            batchInsertData.add(new BatchInsertionObject(selectedFacil,selectedRoom,scannedInvList.get(i).getInv_id(),selectedUserId, "1",reconc_id,""));
                        }
                    }
                }
            }
            rfids.add(scannedInvList.get(i).getRfidCode());
        }
        for (int i = 0; i < disposedinvList.size(); i++) {
            if(disposedinvList.get(i).getRfidCode()!=null) {
                if (disposedinvList.get(i).getRfidCode().trim().length() > 0) {
                    if (newList.contains(disposedinvList.get(i).getRfidCode())) {
                        if (!disposedinvList.get(i).isFlag()) {
                            disposedinvList.get(i).setFlag(true);
                            batchInsertData.add(new BatchInsertionObject(selectedFacil,selectedRoom,disposedinvList.get(i).getInv_id(),selectedUserId, "1",reconc_id,""));
                        }
                    }
                }
            }
            rfids.add(disposedinvList.get(i).getRfidCode());
        }

        for (int k=0;k < scannedOutOflocationListfromContinue.size();k++){
            rfids.add(scannedOutOflocationListfromContinue.get(k));
        }

        for (int k=0;k < newList.size();k++){
            if (!rfids.contains(newList.get(k))){
                batchInsertData.add(new BatchInsertionObject(selectedFacil,selectedRoom,"-1",selectedUserId, "1",reconc_id,newList.get(k)));
                scannedInvList.add(new InventoryObject(newList.get(k),"N/A","-1","N/A","1","N/A",true,"0"));
            }
        }
        if(batchInsertData!=null)
        databaseHandler.batchInsert(batchInsertData,databaseHandler.getWritableDatabase(PASS_PHRASE));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //ArrayList<InventoryObject> invList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE), selectedRoom);
                if(found.isChecked()){
                    CustomisedRFIDScannedList adapter = (CustomisedRFIDScannedList)tagList.getAdapter();
                    tagList.removeAllViewsInLayout();
                    adapter.notifyDataSetChanged();
                    ArrayList<InventoryObject> invList = databaseHandler.getFoundInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom,reconc_id,"rfid");
                    CustomisedRFIDScannedList adapter1 = new CustomisedRFIDScannedList(invList,model, RFIDScannerActivity.this);
                    tagList.setAdapter(adapter1);
                }else if (notfound.isChecked()){
                    CustomisedRFIDScannedList adapter = (CustomisedRFIDScannedList)tagList.getAdapter();
                    tagList.removeAllViewsInLayout();
                    adapter.notifyDataSetChanged();
                    ArrayList<InventoryObject> invList = databaseHandler.getNotFoundInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom,reconc_id,"rfid");
                    CustomisedRFIDScannedList adapter1 = new CustomisedRFIDScannedList(invList,model, RFIDScannerActivity.this);
                    tagList.setAdapter(adapter1);
                }else {
                    CustomisedRFIDScannedList adapter = new CustomisedRFIDScannedList(scannedInvList, model, RFIDScannerActivity.this);
                    tagList.setAdapter(adapter);

                }
                int scannedCount = databaseHandler.checkScannedDataCount(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedFacil,selectedRoom,selectedUserId,reconc_id);
                scannedTotalCount = databaseHandler.checkScannedDataFullCount(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedFacil,selectedRoom,selectedUserId,reconc_id);
                //setscancount(String.valueOf(scannedCount), scannedTotalCount);
                progressbarrecon.setVisibility(View.GONE);
                scanText.setVisibility(View.GONE);
                if(tagList!=null)
                    tagList.setVisibility(View.VISIBLE);
                scanCount.setText(scannedTotalCount);
                if(Integer.parseInt(total_inventory)>0) {
                    if(scannedCount<=Integer.parseInt(total_inventory)) {
                        int percent = (scannedCount * 100) / Integer.parseInt(total_inventory);
                        scannedProgressPercentage.setText(percent + " %");
                        scannedProgressCount.setText(String.valueOf(scannedCount) + "/" + total_inventory);
                        progressVal.setProgress(percent);
                    }
                }
            }
        });
    }

    public void setscancount(String count, String total_scan_count){
        progressbarrecon.setVisibility(View.GONE);
        scanText.setVisibility(View.GONE);
        if(tagList!=null)
        tagList.setVisibility(View.VISIBLE);
        scanCount.setText(total_scan_count);
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
    public void onBackPressed() {
    }
    private static final Pattern HEX_PATTERN = Pattern.compile("[0-9a-fA-F]+");

    public static boolean isHex(String input) {
        return HEX_PATTERN.matcher(input).matches();
    }

    public static boolean containsNonAscii(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) > 127) {
                Log.e("---",str+"---"+true);
                return true; // non-ASCII character found
            }
        }
        return false; // no non-ASCII characters found
    }
}