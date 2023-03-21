package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.zebra.rfid.api3.TagData;
import net.sqlcipher.database.SQLiteDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;

public class ScanBarcodeReconciliation extends AppCompatActivity {
    public TextView statusTextViewRFID = null;
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;
    private ListView textrfid;
    private TextView testStatus;
    private TextView scannedProgressCount;
    private TextView scannedProgressPercentage;
    private ProgressBar progressVal;
    public TextView scanCount;
    ArrayList<String> newList = new ArrayList<String>();
    final static String TAG = "RFID_SAMPLE";
    ArrayAdapter adapter = null;
    ConstraintLayout header;
    String loggedinUsername = "";
    String loggedinUserSiteId = "";
    String reconc_id = "";
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
    String json_data_from_continue="";
    String total_inventory = "120";
    ListView tagList;
    EditText enteredBarCodeValue;
    NestedScrollView invScrollview;
    RadioButton all;
    RadioButton found;
    RadioButton notfound;
    Button saveScanData;
    Button scanRFID;
    Button completeScan;
    ProgressBar spinner;
    Button backToHome;
    IntentModel model;
    Button addtoList;
    String scannedTotalCount="0";
    ProgressDialog progressSynStart= null;
    ArrayList<InventoryObject> scannedInvList = null;
    ArrayList<String> scannedListfromContinue = new ArrayList<String>();
    ArrayList<String> scannedOutOflocationListfromContinue = new ArrayList<String>();
    final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(ScanBarcodeReconciliation.this);
    final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode_reconciliation);
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
        // UI
        statusTextViewRFID = findViewById(R.id.rfidStatusText);
        scanCount = findViewById(R.id.scanCount);
        //textrfid = findViewById(R.id.tags_list);
        scannedProgressCount = findViewById(R.id.scannedProgressCount);
        scannedProgressPercentage = findViewById(R.id.scannedProgressPercentage);
        progressVal = findViewById(R.id.scanProgress);
        all = findViewById(R.id.showall);
        found = findViewById(R.id.found);
        notfound = findViewById(R.id.notfound);
        completeScan = findViewById(R.id.completeScan);
        saveScanData = findViewById(R.id.saveScan);
        backToHome = (Button) findViewById(R.id.backToHome);
        scanRFID = (Button) findViewById(R.id.scanRFIDReconc);
        enteredBarCodeValue = (EditText) findViewById(R.id.enteredbarcodeValue);
        addtoList = findViewById(R.id.addBarCodeToList);
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
        scannedProgressCount.setText("0/" + total_inventory);
        progressVal.setProgress(0);
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
        if(intent.getStringExtra("reconc_id")!=null) {
            reconc_id = intent.getStringExtra("reconc_id");
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
        if (scannedTotalCount.trim().length() > 0){
            int scanned = databaseHandler.checkScannedDataCount(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedFacil, selectedRoom,selectedUserId,reconc_id);
            setscancount(String.valueOf(scanned), scannedTotalCount);
        }else
            scanCount.setText("0");
        all.setChecked(true);
        scannedInvList = new ArrayList<InventoryObject>();
        if(intent.getSerializableExtra("scannedInvList")!=null)
            scannedInvList = (ArrayList<InventoryObject>) intent.getSerializableExtra("scannedInvList");
        else
            scannedInvList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
        //ArrayList<InventoryObject> invList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
        tagList = (ListView)findViewById(R.id.invList);
        //spinner = (ProgressBar)findViewById(R.id.progressBar1);
        model = new IntentModel(loggedinUserSiteId,selectedUserId,token,md5Pwd,sso,empName,site_name,loggedinUsername,"2",null,selectedSearchValue,selectedFacilName,selectedFacil,selectedRoomName,selectedRoom,total_inventory);
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);
        CustomisedRFIDScannedList adapter = new CustomisedRFIDScannedList(scannedInvList,model, ScanBarcodeReconciliation.this);
        tagList.setAdapter(adapter);
        found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(found.isChecked()){
                    CustomisedRFIDScannedList adapter = (CustomisedRFIDScannedList)tagList.getAdapter();
                    tagList.removeAllViewsInLayout();
                    adapter.notifyDataSetChanged();
                    ArrayList<InventoryObject> invList = databaseHandler.getFoundInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
                    CustomisedRFIDScannedList adapter1 = new CustomisedRFIDScannedList(invList,model, ScanBarcodeReconciliation.this);
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
                    ArrayList<InventoryObject> invList = databaseHandler.getNotFoundInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
                    CustomisedRFIDScannedList adapter1 = new CustomisedRFIDScannedList(invList,model, ScanBarcodeReconciliation.this);
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
                    ArrayList<InventoryObject> invList = databaseHandler.getALLInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
                    CustomisedRFIDScannedList adapter1 = new CustomisedRFIDScannedList(invList,model, ScanBarcodeReconciliation.this);
                    tagList.setAdapter(adapter1);
                }
            }
        });
        scanRFID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ScanBarcodeReconciliation.this,
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
            myIntent.putExtra("selectedRoomName", selectedRoomName);
            myIntent.putExtra("selectedRoom", selectedRoom+"");
            myIntent.putExtra("empName", empName);
            myIntent.putExtra("total_inventory", total_inventory+"");
            myIntent.putExtra("pageLoadTemp", "-1");
            myIntent.putExtra("scannedInvList", scannedInvList);
            myIntent.putExtra("scannedTotalCount", scannedTotalCount+"");
            startActivity(myIntent);
            }
        });
        saveScanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                ArrayList<RFIDScanDataObj> rfidScanDataObjs = databaseHandler.getALLInventoryScannedList(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                String rfidJson = gson.toJson(rfidScanDataObjs);
                RFIDPostScanObj postScanObj = new RFIDPostScanObj(selectedUserId,
                        token,loggedinUserSiteId,selectedRoom,rfidJson
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
                    cv.put("scan_type", "rfid");
                    cv.put("reconc_id", reconc_id);
                    databaseHandler.insertScannedInvJSONData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ScanBarcodeReconciliation.this);
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
                ArrayList<RFIDScanDataObj> rfidScanDataObjs = databaseHandler.getALLInventoryScannedList(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                RFIDPostScanObj postScanObj = new RFIDPostScanObj(selectedUserId,
                        token,loggedinUserSiteId,selectedRoom,gson.toJson(rfidScanDataObjs)
                );
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = "";
                try {
                    jsonString = mapper.writeValueAsString(postScanObj);
                    Log.e("barcodejson>>",jsonString);
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
                        RequestQueue requestQueue = Volley.newRequestQueue(ScanBarcodeReconciliation.this);
                        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(jsonString),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Process os success response
                                        String res = response.toString();
                                        databaseHandler.delSavedScanData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedUserId,selectedRoom,reconc_id);
                                        final Intent myIntent = new Intent(ScanBarcodeReconciliation.this,
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
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ScanBarcodeReconciliation.this);
                                dlgAlert.setTitle("Safety Stratus");
                                dlgAlert.setMessage("Error response: Request timed out! Your data is saved offline");
                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                final Intent myIntent = new Intent(ScanBarcodeReconciliation.this,
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
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ScanBarcodeReconciliation.this);
                        dlgAlert.setTitle("Safety Stratus");
                        dlgAlert.setMessage("Your internet connection is not active! Your data is saved offline");
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Intent myIntent = new Intent(ScanBarcodeReconciliation.this,
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

                backToHome();
            }
        });
        addtoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enteredBarCodeValue.getText().toString().trim().length()>0){
                    CustomisedRFIDScannedList adapter = (CustomisedRFIDScannedList)tagList.getAdapter();
                    tagList.removeAllViewsInLayout();
                    adapter.notifyDataSetChanged();
                    int count = 0;
                    Log.e("oooooo",databaseHandler.checkScannedBarcodeDataAvailable(db,enteredBarCodeValue.getText().toString())+"**");
                    if(!databaseHandler.checkScannedBarcodeDataAvailable(db,enteredBarCodeValue.getText().toString())){
                        scannedInvList.add(new InventoryObject("","","-1",enteredBarCodeValue.getText().toString(),"1","",true));
                        ContentValues cv = new ContentValues();
                        cv.put("location_id", selectedFacil);
                        cv.put("room_id", selectedRoom);
                        cv.put("inventory_id", -1);
                        cv.put("scanned_by", selectedUserId);
                        cv.put("code", enteredBarCodeValue.getText().toString());
                        cv.put("scanned", 1);
                        cv.put("reconc_id", reconc_id);
                        databaseHandler.insertScannedInvDataOutofLocationData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                        count = Integer.parseInt(scannedTotalCount)+1;
                        scannedTotalCount = String.valueOf(count);
                        scanCount.setText(String.valueOf(count));
                    }else{
                        for (int g=0;g<scannedInvList.size();g++){
                            Log.e("TESGGGGGG1",scannedInvList.get(g).getCode());
                            if (scannedInvList.get(g).getCode().equalsIgnoreCase(enteredBarCodeValue.getText().toString())){
                                Log.e("TESGGGGGG2",enteredBarCodeValue.getText().toString()+"---"+scannedInvList.get(g).isFlag());
                                if(!scannedInvList.get(g).isFlag()) {
                                    Log.e("TESGGGGGG3",enteredBarCodeValue.getText().toString());
                                    scannedInvList.get(g).setFlag(true);
                                    ContentValues cv = new ContentValues();
                                    cv.put("location_id", selectedFacil);
                                    cv.put("room_id", selectedRoom);
                                    cv.put("inventory_id", Integer.parseInt(scannedInvList.get(g).getInv_id()));
                                    cv.put("scanned_by", selectedUserId);
                                    cv.put("scanned", 1);
                                    cv.put("reconc_id", reconc_id);
                                    databaseHandler.insertScannedInvData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                                    int scanned = databaseHandler.checkScannedDataCount(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedFacil, selectedRoom,selectedUserId,reconc_id);
                                    count = Integer.parseInt(scannedTotalCount)+1;
                                    scannedTotalCount = String.valueOf(count);
                                    setscancount(String.valueOf(count),String.valueOf(scanned));
                                }
                            }
                        }
                    }
                    CustomisedRFIDScannedList adapter1 = new CustomisedRFIDScannedList(scannedInvList,model, ScanBarcodeReconciliation.this);
                    tagList.setAdapter(adapter1);
                }
                else{
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ScanBarcodeReconciliation.this);
                    dlgAlert.setTitle("Safety Stratus");
                    dlgAlert.setMessage("Please scan the codes or enter the code details!");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    dlgAlert.create().show();
                }
            }
        });
        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InventoryObject selectedItem = (InventoryObject) parent.getItemAtPosition(position);
                final Intent myIntent = new Intent(ScanBarcodeReconciliation.this,
                        Container_Info.class);
                myIntent.putExtra("user_id", selectedUserId);
                myIntent.putExtra("site_id", loggedinUserSiteId);
                myIntent.putExtra("token", token);
                myIntent.putExtra("sso", sso);
                myIntent.putExtra("md5pwd", md5Pwd);
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
                myIntent.putExtra("flag","3");
                myIntent.putExtra("scannedCode", selectedItem.getInv_id()+"");
                startActivity(myIntent);
            }
        });
    }
    public void backToHome() {
        final Intent myIntent = new Intent(ScanBarcodeReconciliation.this,
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
    }
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                //  Received a barcode scan
                try {
                    CustomisedRFIDScannedList adapter = (CustomisedRFIDScannedList)tagList.getAdapter();
                    tagList.removeAllViewsInLayout();
                    adapter.notifyDataSetChanged();
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast... this is not designed to be a production app
                }
            }
        }
    };

    private void displayScanResult(Intent initiatingIntent, String howDataReceived)
    {
        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));

        if (null == decodedSource)
        {
            decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source_legacy));
            decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data_legacy));
            decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type_legacy));
        }
        Log.e("TestDecodeData>>",decodedData+"---");
        int count = 0;
        if(!databaseHandler.checkScannedBarcodeDataAvailable(db,decodedData)){
            scannedInvList.add(new InventoryObject("","","-1",decodedData,"1","",true));
            ContentValues cv = new ContentValues();
            cv.put("location_id", selectedFacil);
            cv.put("room_id", selectedRoom);
            cv.put("inventory_id", -1);
            cv.put("scanned_by", selectedUserId);
            cv.put("reconc_id", reconc_id);
            cv.put("code", decodedData);
            cv.put("scanned", 1);
            databaseHandler.insertScannedInvDataOutofLocationData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
            count = Integer.parseInt(scannedTotalCount)+1;
            scannedTotalCount = String.valueOf(count);
            scanCount.setText(count);
        }else{
            for (int g=0;g<scannedInvList.size();g++){
                if (scannedInvList.get(g).getCode().equalsIgnoreCase(decodedData)){
                    if(!scannedInvList.get(g).isFlag()) {
                        scannedInvList.get(g).setFlag(true);
                        ContentValues cv = new ContentValues();
                        cv.put("location_id", selectedFacil);
                        cv.put("room_id", selectedRoom);
                        cv.put("inventory_id", Integer.parseInt(scannedInvList.get(g).getInv_id()));
                        cv.put("scanned_by", selectedUserId);
                        cv.put("reconc_id", reconc_id);
                        cv.put("scanned", 1);
                        databaseHandler.insertScannedInvData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                        int scanned = databaseHandler.checkScannedDataCount(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), selectedFacil, selectedRoom,selectedUserId,reconc_id);
                        count = Integer.parseInt(scannedTotalCount)+1;
                        scannedTotalCount = String.valueOf(count);
                        setscancount(String.valueOf(count),String.valueOf(scanned));
                    }
                }
            }
        }
        CustomisedRFIDScannedList adapter = new CustomisedRFIDScannedList(scannedInvList,model, ScanBarcodeReconciliation.this);
        tagList.setAdapter(adapter);
    }
    public void setscancount(String count, String total_scan_count){
        if(progressSynStart!=null) {
            if(progressSynStart.isShowing())
                progressSynStart.dismiss();
        }
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
}