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
import android.widget.ArrayAdapter;
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

public class BulkUpdateActivity extends AppCompatActivity implements RFIDHandlerBulkUpdate.ResponseHandlerInterface {
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
    RadioButton rfid;
    RadioButton barcode;
    TextView codeLabel;
    TextView empty_list_text_view;
    EditText enteredCodeValue;
    ListView codeList;
    Button addtoList;
    Button scanBarcode;
    Button updateDetails;
    Button clearAll;
    ProgressBar spinner;
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
        setContentView(R.layout.activity_bulk_update);
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
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(BulkUpdateActivity.this);
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
        //rfid = findViewById(R.id.rfidbtn);
        //barcode = findViewById(R.id.barcodebtn);
        codeLabel = findViewById(R.id.codeLabel);
        empty_list_text_view = findViewById(R.id.empty_list_text_view);
        codeList = findViewById(R.id.codeList);
        enteredCodeValue = findViewById(R.id.enteredCodeValue);
        addtoList = findViewById(R.id.addCodeToList);
        scanBarcode = findViewById(R.id.scanBarcode);
        updateDetails = findViewById(R.id.update);
        clearAll = findViewById(R.id.clearList);
        spinner = (ProgressBar)findViewById(R.id.progressBarBulkUpdate);
        rfidHandler = new RFIDHandlerBulkUpdate();
        rfidHandler.onCreate(this);
        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(codelistfromIntent.size()>0){
                    final Intent myIntent = new Intent(BulkUpdateActivity.this,
                            BulkContainerUpdate.class);
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
                }else{
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(BulkUpdateActivity.this);
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
        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent myIntent = new Intent(BulkUpdateActivity.this,
                        ScanBarcodeBulkActivity.class);
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
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //codeList.setAdapter(null);
                CustomizedListViewBulkUpdate adapter = (CustomizedListViewBulkUpdate)codeList.getAdapter();
                codeList.removeAllViewsInLayout();
                adapter.notifyDataSetChanged();
                codelistfromIntent.clear();
                enteredCodeValue.setText("");
                codeList.setVisibility(View.GONE);
                empty_list_text_view.setVisibility(View.VISIBLE);
                clearAll.setVisibility(View.GONE);
                ConstraintLayout constraintLayout = findViewById(R.id.bulkupdateConstraintLayout);
                ConstraintSet constraintSet1 = new ConstraintSet();
                constraintSet1.clone(constraintLayout);
                constraintSet1.connect(R.id.scanBarcode,ConstraintSet.START,R.id.empty_list_text_view,ConstraintSet.START,0);
                constraintSet1.connect(R.id.scanBarcode,ConstraintSet.END,R.id.empty_list_text_view,ConstraintSet.END,0);
                constraintSet1.connect(R.id.scanBarcode,ConstraintSet.TOP,R.id.empty_list_text_view,ConstraintSet.BOTTOM,0);
                constraintSet1.applyTo(constraintLayout);
                ConstraintLayout.LayoutParams newLayoutParams1 = (ConstraintLayout.LayoutParams) scanBarcode.getLayoutParams();
                newLayoutParams1.topMargin = 20;
                newLayoutParams1.leftMargin = 0;
                newLayoutParams1.rightMargin = 0;
                newLayoutParams1.bottomMargin = 0;
                scanBarcode.setLayoutParams(newLayoutParams1);
            }
        });
        if(codelistfromIntent.size()>0){
            //instantiate custom adapter
            CustomizedListViewBulkUpdate adapter = new CustomizedListViewBulkUpdate(codelistfromIntent, BulkUpdateActivity.this);
            codeList.setAdapter(adapter);
            if (codeList.getAdapter().getCount() > 0) {
                spinner.setVisibility(View.GONE);
                empty_list_text_view.setVisibility(View.GONE);
                codeList.setVisibility(View.VISIBLE);
                clearAll.setVisibility(View.VISIBLE);
                ConstraintLayout constraintLayout = findViewById(R.id.bulkupdateConstraintLayout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.scanBarcode,ConstraintSet.START,R.id.codeList,ConstraintSet.START,0);
                constraintSet.connect(R.id.scanBarcode,ConstraintSet.END,R.id.codeList,ConstraintSet.END,0);
                constraintSet.connect(R.id.scanBarcode,ConstraintSet.TOP,R.id.codeList,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) scanBarcode.getLayoutParams();
                newLayoutParams.topMargin = 20;
                newLayoutParams.leftMargin = 0;
                newLayoutParams.rightMargin = 0;
                newLayoutParams.bottomMargin = 0;
                scanBarcode.setLayoutParams(newLayoutParams);
            }
        }
        addtoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enteredCodeValue.getText().toString().trim().length()>0){
                    codelistfromIntent.add(enteredCodeValue.getText().toString());
                    //instantiate custom adapter
                    CustomizedListViewBulkUpdate adapter = new CustomizedListViewBulkUpdate(codelistfromIntent, BulkUpdateActivity.this);
                    codeList.setAdapter(adapter);
                    if (codeList.getAdapter().getCount() > 0) {
                        spinner.setVisibility(View.GONE);
                        empty_list_text_view.setVisibility(View.GONE);
                        codeList.setVisibility(View.VISIBLE);
                        clearAll.setVisibility(View.VISIBLE);
                        enteredCodeValue.setText("");
                        ConstraintLayout constraintLayout = findViewById(R.id.bulkupdateConstraintLayout);
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(R.id.scanBarcode,ConstraintSet.START,R.id.codeList,ConstraintSet.START,0);
                        constraintSet.connect(R.id.scanBarcode,ConstraintSet.END,R.id.codeList,ConstraintSet.END,0);
                        constraintSet.connect(R.id.scanBarcode,ConstraintSet.TOP,R.id.codeList,ConstraintSet.BOTTOM,0);
                        constraintSet.applyTo(constraintLayout);
                        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) scanBarcode.getLayoutParams();
                        newLayoutParams.topMargin = 20;
                        newLayoutParams.leftMargin = 0;
                        newLayoutParams.rightMargin = 0;
                        newLayoutParams.bottomMargin = 0;
                        scanBarcode.setLayoutParams(newLayoutParams);
                    }
                }else{
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(BulkUpdateActivity.this);
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
    }
    public static void hideKeyboard(BulkUpdateActivity activity) {
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
            rfidHandler.onDestroy();
            final Intent myIntent = new Intent(BulkUpdateActivity.this,
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //unregisterReceiver(myBroadcastReceiver);
        rfidHandler.onDestroy();
    }
    /*private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
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
        Log.e("TestDecodeData>>",decodedData+"---");
    }*/
    @Override
    protected void onPause() {
        super.onPause();
        rfidHandler.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        rfidHandler.onResume();
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
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //instantiate custom adapter
                // Traverse through the first list
                for (String element : newList) {

                    // If this element is not present in newList
                    // then add it
                    if (!codelistfromIntent.contains(element)) {

                        codelistfromIntent.add(element);
                    }
                }
                CustomizedListViewBulkUpdate adapter = new CustomizedListViewBulkUpdate(codelistfromIntent, BulkUpdateActivity.this);
                codeList.setAdapter(adapter);
                if (codeList.getAdapter().getCount() > 0) {
                    spinner.setVisibility(View.GONE);
                    empty_list_text_view.setVisibility(View.GONE);
                    codeList.setVisibility(View.VISIBLE);
                    clearAll.setVisibility(View.VISIBLE);
                    ConstraintLayout constraintLayout = findViewById(R.id.bulkupdateConstraintLayout);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.scanBarcode,ConstraintSet.START,R.id.codeList,ConstraintSet.START,0);
                    constraintSet.connect(R.id.scanBarcode,ConstraintSet.END,R.id.codeList,ConstraintSet.END,0);
                    constraintSet.connect(R.id.scanBarcode,ConstraintSet.TOP,R.id.codeList,ConstraintSet.BOTTOM,0);
                    constraintSet.applyTo(constraintLayout);
                    ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) scanBarcode.getLayoutParams();
                    newLayoutParams.topMargin = 20;
                    newLayoutParams.leftMargin = 0;
                    newLayoutParams.rightMargin = 0;
                    newLayoutParams.bottomMargin = 0;
                    scanBarcode.setLayoutParams(newLayoutParams);
                }
            }
        });
    }

}