package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.util.Hex;
import com.zebra.rfid.api3.TagData;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;

public class ScanBarcodeBulkActivity extends AppCompatActivity{
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;
    boolean connected = false;
    String loggedinUsername = "";
    String loggedinUserSiteId = "";
    String md5Pwd = "";
    String selectedUserId = "";
    String sso = "";
    String site_name = "";
    String token="";
    String empName = "";
    ConstraintLayout header;
    TextView codeLabel;
    TextView empty_list_text_view;
    EditText enteredBarCodeValue;
    ListView barcodeList;
    Button addtoList;
    Button scanRFID;
    ArrayList<String> codelistfromIntent;
    CustomizedListViewBulkUpdate adapter;
    //generate list
    ArrayList<String> newList = new ArrayList<String>();
    ProgressDialog progressSynStart = null;
    RFIDHandlerBulkUpdate rfidHandler;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode_bulk);
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
        tv.setText("Bulk Update");
        tv.setTextSize(20);
        tv.setVisibility(View.VISIBLE);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(ScanBarcodeBulkActivity.this);
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
        codelistfromIntent = new ArrayList<String>();
        if(intent.getSerializableExtra("codelistfromIntent")!=null)
            codelistfromIntent = (ArrayList<String>) intent.getSerializableExtra("codelistfromIntent");
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("loggedinUsername");
        selectedUserId = intent.getStringExtra("user_id");
        Log.e("selecteduserid1>>",selectedUserId+"**");
        loggedinUserSiteId = intent.getStringExtra("site_id");
        md5Pwd = intent.getStringExtra("md5pwd");
        codeLabel = findViewById(R.id.codeLabel);
        empty_list_text_view = findViewById(R.id.empty_barcodelist_text_view);
        barcodeList = findViewById(R.id.barcodeList);
        enteredBarCodeValue = findViewById(R.id.enteredbarcodeValue);
        addtoList = findViewById(R.id.addBarCodeToList);
        scanRFID = findViewById(R.id.scanRFID);
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);
        scanRFID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent myIntent = new Intent(ScanBarcodeBulkActivity.this,
                        BulkUpdateActivity.class);
                myIntent.putExtra("user_id", selectedUserId);
                myIntent.putExtra("site_id", loggedinUserSiteId);
                myIntent.putExtra("token", token);
                myIntent.putExtra("sso", sso);
                myIntent.putExtra("md5pwd", md5Pwd);
                myIntent.putExtra("loggedinUsername", loggedinUsername);
                myIntent.putExtra("site_name", site_name);
                myIntent.putExtra("pageLoadTemp", "-1");
                myIntent.putExtra("pageLoadTemp", "-1");
                myIntent.putExtra("empName", empName);
                myIntent.putExtra("codelistfromIntent",codelistfromIntent);
                startActivity(myIntent);
            }
        });
        if(codelistfromIntent.size()>0){
            //instantiate custom adapter
            CustomizedListViewBulkUpdate adapter = new CustomizedListViewBulkUpdate(codelistfromIntent, ScanBarcodeBulkActivity.this);
            barcodeList.setAdapter(adapter);
            if (barcodeList.getAdapter().getCount() > 0) {
                empty_list_text_view.setVisibility(View.GONE);
                barcodeList.setVisibility(View.VISIBLE);
                ConstraintLayout constraintLayout = findViewById(R.id.bulkupdatebarcodeConstraintLayout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.scanRFID,ConstraintSet.START,R.id.barcodeList,ConstraintSet.START,0);
                constraintSet.connect(R.id.scanRFID,ConstraintSet.END,R.id.barcodeList,ConstraintSet.END,0);
                constraintSet.connect(R.id.scanRFID,ConstraintSet.TOP,R.id.barcodeList,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) scanRFID.getLayoutParams();
                newLayoutParams.topMargin = 20;
                newLayoutParams.leftMargin = 0;
                newLayoutParams.rightMargin = 0;
                newLayoutParams.bottomMargin = 0;
                scanRFID.setLayoutParams(newLayoutParams);
            }
        }
        addtoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelistfromIntent.add(enteredBarCodeValue.getText().toString());
                //instantiate custom adapter
                CustomizedListViewBulkUpdate adapter = new CustomizedListViewBulkUpdate(codelistfromIntent, ScanBarcodeBulkActivity.this);
                barcodeList.setAdapter(adapter);
                if (barcodeList.getAdapter().getCount() > 0) {
                    empty_list_text_view.setVisibility(View.GONE);
                    barcodeList.setVisibility(View.VISIBLE);
                    ConstraintLayout constraintLayout = findViewById(R.id.bulkupdatebarcodeConstraintLayout);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.scanRFID,ConstraintSet.START,R.id.barcodeList,ConstraintSet.START,0);
                    constraintSet.connect(R.id.scanRFID,ConstraintSet.END,R.id.barcodeList,ConstraintSet.END,0);
                    constraintSet.connect(R.id.scanRFID,ConstraintSet.TOP,R.id.barcodeList,ConstraintSet.BOTTOM,0);
                    constraintSet.applyTo(constraintLayout);
                    ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) scanRFID.getLayoutParams();
                    newLayoutParams.topMargin = 20;
                    newLayoutParams.leftMargin = 0;
                    newLayoutParams.rightMargin = 0;
                    newLayoutParams.bottomMargin = 0;
                    scanRFID.setLayoutParams(newLayoutParams);
                }
            }
        });
    }
    public static void hideKeyboard(ScanBarcodeBulkActivity activity) {
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
        if (id == android.R.id.home) {
            //unregisterReceiver(myBroadcastReceiver);
            final Intent myIntent = new Intent(ScanBarcodeBulkActivity.this,
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
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            //  This is useful for debugging to verify the format of received intents from DataWedge
            //for (String key : b.keySet())
            //{
            //    Log.v(LOG_TAG, key);
            //}
            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                //  Received a barcode scan
                try {
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
        codelistfromIntent.add(decodedData);
        //instantiate custom adapter
        CustomizedListViewBulkUpdate adapter = new CustomizedListViewBulkUpdate(codelistfromIntent, ScanBarcodeBulkActivity.this);
        barcodeList.setAdapter(adapter);
        if (barcodeList.getAdapter().getCount() > 0) {
            empty_list_text_view.setVisibility(View.GONE);
            barcodeList.setVisibility(View.VISIBLE);
            ConstraintLayout constraintLayout = findViewById(R.id.bulkupdatebarcodeConstraintLayout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.scanRFID,ConstraintSet.START,R.id.barcodeList,ConstraintSet.START,0);
            constraintSet.connect(R.id.scanRFID,ConstraintSet.END,R.id.barcodeList,ConstraintSet.END,0);
            constraintSet.connect(R.id.scanRFID,ConstraintSet.TOP,R.id.barcodeList,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
            ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) scanRFID.getLayoutParams();
            newLayoutParams.topMargin = 20;
            newLayoutParams.leftMargin = 0;
            newLayoutParams.rightMargin = 0;
            newLayoutParams.bottomMargin = 0;
            scanRFID.setLayoutParams(newLayoutParams);
        }
    }

}