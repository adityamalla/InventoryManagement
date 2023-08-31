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
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.gson.Gson;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Container_Info extends AppCompatActivity {
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;
    boolean connected = false;
    String loggedinUsername = "";
    String loggedinUserSiteId = "";
    String md5Pwd = "";
    String reconc_id = "";
    String selectedUserId = "";
    String sso = "";
    String site_name = "";
    String token="";
    String empName = "";
    String scannedCode = "";
    String scannedRFIDCode = "";
    String scannedbarcode = "";
    String flag = "";
    String fromReconc = "";
    public String selectedSearchValue="";
    public String selectedFacilName="";
    public String selectedFacil="";
    public String selectedRoomName="";
    public String selectedRoom="";
    public String total_inventory="";
    ConstraintLayout header;
    ArrayList<String> codelistfromIntent;
    TextView productName;
    TextView barcode;
    TextView rfidcode;
    TextView owner;
    TextView primaryUser;
    TextView notes;
    TextView comments;
    TextView volume;
    Button locateTag;
    String scannedTotalCount="0";
    //generate list
    ArrayList<String> newList = new ArrayList<String>();
    ArrayList<InventoryObject> scannedInvList =null;
    ProgressDialog progressSynStart = null;
    RFIDHandlerBulkUpdate rfidHandler;
    IntentModel model = null;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_info);
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
        tv.setText("Details");
        tv.setTextSize(20);
        tv.setVisibility(View.VISIBLE);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(Container_Info.this);
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
        if (intent.getStringExtra("reconc_id") != null) {
            reconc_id = intent.getStringExtra("reconc_id");
        }
        if(intent.getStringExtra("empName")!=null) {
            empName = intent.getStringExtra("empName");
        }
        if(intent.getStringExtra("scannedCode")!=null) {
            scannedCode = intent.getStringExtra("scannedCode");
        }
        if(intent.getStringExtra("flag")!=null) {
            flag = intent.getStringExtra("flag");
        }
        if(intent.getStringExtra("fromReconc")!=null) {
            fromReconc = intent.getStringExtra("fromReconc");
        }
        if(intent.getStringExtra("scannedTotalCount")!=null) {
            scannedTotalCount = intent.getStringExtra("scannedTotalCount");
        }
        if(intent.getStringExtra("selectedSearchValue")!=null) {
            selectedSearchValue = intent.getStringExtra("selectedSearchValue");
        }
        if(intent.getStringExtra("selectedFacilName")!=null) {
            selectedFacilName = intent.getStringExtra("selectedFacilName");
        }
        if(intent.getStringExtra("selectedFacil")!=null) {
            selectedFacil = intent.getStringExtra("selectedFacil");
        }
        if(intent.getStringExtra("selectedRoomName")!=null) {
            selectedRoomName = intent.getStringExtra("selectedRoomName");
        }
        if(intent.getStringExtra("selectedRoom")!=null) {
            selectedRoom = intent.getStringExtra("selectedRoom");
        }
        if(intent.getStringExtra("total_inventory")!=null) {
            total_inventory = intent.getStringExtra("total_inventory");
        }
        if(intent.getStringExtra("scannedRFIDCode")!=null) {
            scannedRFIDCode = intent.getStringExtra("scannedRFIDCode");
        }
        if(intent.getStringExtra("scannedbarcode")!=null) {
            scannedbarcode = intent.getStringExtra("scannedbarcode");
        }
        scannedInvList = new ArrayList<InventoryObject>();
        if(intent.getSerializableExtra("scannedInvList")!=null)
            scannedInvList = (ArrayList<InventoryObject>) intent.getSerializableExtra("scannedInvList");
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("loggedinUsername");
        selectedUserId = intent.getStringExtra("user_id");
        loggedinUserSiteId = intent.getStringExtra("site_id");
        md5Pwd = intent.getStringExtra("md5pwd");
        codelistfromIntent = new ArrayList<String>();
        if(intent.getSerializableExtra("codelistfromIntent")!=null)
            codelistfromIntent = (ArrayList<String>) intent.getSerializableExtra("codelistfromIntent");
        productName = findViewById(R.id.productName);
        barcode = findViewById(R.id.barcodeValue);
        rfidcode = findViewById(R.id.rfid);
        owner = findViewById(R.id.owner);
        primaryUser = findViewById(R.id.pu);
        notes = findViewById(R.id.notes);
        comments = findViewById(R.id.comm);
        volume = findViewById(R.id.volume);
        locateTag = findViewById(R.id.locateScannedTag);
        InventoryModel inv = databaseHandler.getScannedInventoryDetails(db,scannedCode,flag);
        if(inv!=null){
            productName.setText(inv.getProductName());
            if(inv.getCode().trim().length()>0){
                barcode.setText(inv.getCode());
            }else{
                barcode.setText("N/A");

            }
            if(inv.getRfidCode().trim().length()>0) {
                rfidcode.setText(inv.getRfidCode());
            }else{
                rfidcode.setText("N/A");
            }
            if(inv.getOwner().trim().length()>0) {
                owner.setText(inv.getOwner());
            }else{
                owner.setText("N/A");
            }
            if(inv.getPrimary_user_id().trim().length()>0){
                if(Integer.parseInt(inv.getPrimary_user_id())>0) {
                    primaryUser.setText(databaseHandler.getPrimaryUserName(db, inv.getPrimary_user_id()));
                }
                else{
                    primaryUser.setText("N/A");
                }
            }else{
                primaryUser.setText("N/A");
            }
            if(inv.getNotes().trim().length()>0) {
                notes.setText(inv.getNotes());
            }else{
                notes.setText("N/A");
            }
            if(inv.getComments().trim().length()>0) {
                comments.setText(inv.getComments());
            }else{
                comments.setText("N/A");
            }
            if(inv.getVolume_mass().trim().length()>0) {
                volume.setText(inv.getVolume_mass() + " " + inv.getVolume_mass_unit());
            }else{
                volume.setText("N/A");
            }
        }else{
            productName.setText("N/A");
            if (scannedbarcode.length()>0) {
                barcode.setText(scannedbarcode);
            }else{
                barcode.setText("N/A");
            }
            if (scannedRFIDCode.length()>0) {
                rfidcode.setText(scannedRFIDCode);
            }else{
                rfidcode.setText("N/A");
            }
            owner.setText("N/A");
            primaryUser.setText("N/A");
            notes.setText("N/A");
            comments.setText("N/A");
            volume.setText("N/A");
        }
        if (scannedRFIDCode.length()>0){
            locateTag.setVisibility(View.VISIBLE);
        }else
        {
            locateTag.setVisibility(View.INVISIBLE);
        }
        locateTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent myIntent = new Intent(Container_Info.this,
                        LocateTagActivity.class);
                myIntent.putExtra("user_id", selectedUserId);
                myIntent.putExtra("site_id", loggedinUserSiteId);
                myIntent.putExtra("token", token);
                myIntent.putExtra("sso", sso);
                myIntent.putExtra("md5pwd", md5Pwd);
                myIntent.putExtra("loggedinUsername", loggedinUsername);
                myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                myIntent.putExtra("site_name", site_name);
                myIntent.putExtra("empName", empName);
                myIntent.putExtra("scannedRFIDCode", scannedRFIDCode);
                myIntent.putExtra("selectedFacilName", selectedFacilName);
                myIntent.putExtra("selectedFacil", selectedFacil+"");
                myIntent.putExtra("selectedRoomName", selectedRoomName);
                myIntent.putExtra("selectedRoom", selectedRoom+"");
                myIntent.putExtra("scannedInvList", scannedInvList);
                myIntent.putExtra("scannedCode", scannedCode+"");
                myIntent.putExtra("scannedTotalCount", scannedTotalCount+"");
                myIntent.putExtra("total_inventory", total_inventory+"");
                myIntent.putExtra("fromReconciliation", "1");
                myIntent.putExtra("reconc_id", reconc_id);
                startActivity(myIntent);
            }
        });
    }
    public static void hideKeyboard(Container_Info activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }
    @Override
    public void onBackPressed() {
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent myIntent = null;
        if (id == android.R.id.home) {
            if (flag.equalsIgnoreCase("1")){
                myIntent = new Intent(Container_Info.this,
                        BulkUpdateActivity.class);
            }else if (flag.equalsIgnoreCase("2")){
                myIntent = new Intent(Container_Info.this,
                        RFIDScannerActivity.class);
            }else if (flag.equalsIgnoreCase("3")){
                myIntent = new Intent(Container_Info.this,
                        ScanBarcodeReconciliation.class);
            }else{
                myIntent = new Intent(Container_Info.this,
                        ScanBarcodeBulkActivity.class);
            }
            myIntent.putExtra("user_id", selectedUserId);
            myIntent.putExtra("reconc_id", reconc_id);
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
            myIntent.putExtra("codelistfromIntent", codelistfromIntent);
            myIntent.putExtra("scannedInvList", scannedInvList);
            myIntent.putExtra("scannedTotalCount", scannedTotalCount+"");
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}