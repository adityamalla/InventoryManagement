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
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.TagData;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

public class BulkUpdateActivity extends AppCompatActivity implements RFIDHandlerBulkUpdate.ResponseHandlerInterface {
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;
    private boolean backPressedOnce = false;
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
    IntentModel model = null;
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
        model = new IntentModel(loggedinUserSiteId,selectedUserId,token,md5Pwd,sso,empName,site_name,loggedinUsername,"1",codelistfromIntent,"","","","","","","");
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
                try {
                    rfidHandler.reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.BARCODE_MODE, true);
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                }
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
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(BulkUpdateActivity.this);
                dlgAlert.setTitle("Safety Stratus");
                dlgAlert.setMessage("Do you confirm that you wish to remove all the items from the list?");
                dlgAlert.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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
                                return;
                            }
                        });
                dlgAlert.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                dlgAlert.create().show();
            }
        });
        if(codelistfromIntent.size()>0){
            //instantiate custom adapter
            CustomizedListViewBulkUpdate adapter = new CustomizedListViewBulkUpdate(codelistfromIntent,model, BulkUpdateActivity.this);
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
                hideKeyboard(BulkUpdateActivity.this);
                if (enteredCodeValue.getText().toString().trim().length()>0){
                    if(!codelistfromIntent.contains(enteredCodeValue.getText().toString())) {
                        codelistfromIntent.add(enteredCodeValue.getText().toString());
                        //instantiate custom adapter
                        CustomizedListViewBulkUpdate adapter = new CustomizedListViewBulkUpdate(codelistfromIntent, model, BulkUpdateActivity.this);
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
                            constraintSet.connect(R.id.scanBarcode, ConstraintSet.START, R.id.codeList, ConstraintSet.START, 0);
                            constraintSet.connect(R.id.scanBarcode, ConstraintSet.END, R.id.codeList, ConstraintSet.END, 0);
                            constraintSet.connect(R.id.scanBarcode, ConstraintSet.TOP, R.id.codeList, ConstraintSet.BOTTOM, 0);
                            constraintSet.applyTo(constraintLayout);
                            ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) scanBarcode.getLayoutParams();
                            newLayoutParams.topMargin = 20;
                            newLayoutParams.leftMargin = 0;
                            newLayoutParams.rightMargin = 0;
                            newLayoutParams.bottomMargin = 0;
                            scanBarcode.setLayoutParams(newLayoutParams);
                        }
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
                enteredCodeValue.setText("");
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
            if(!backPressedOnce){
                backPressedOnce = true;
                if(rfidHandler!=null) {
                    try {
                        rfidHandler.reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.BARCODE_MODE, true);
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }
                    //rfidHandler.onDestroy();
                }
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
                //finish();
            }
            //unregisterReceiver(myBroadcastReceiver);

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
    private static final Pattern HEX_PATTERN = Pattern.compile("[0-9a-fA-F]+");

    public static boolean isHex(String input) {
        return HEX_PATTERN.matcher(input).matches();
    }
    public static boolean containsNonAscii(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) > 127) {
                return true; // non-ASCII character found
            }
        }
        return false; // no non-ASCII characters found
    }
    @Override
    public void handleTagdata(TagData[] tagData) {
        final StringBuilder sb = new StringBuilder();
        final int[] scanCounts = {0};
        for (int index = 0; index < tagData.length; index++) {
            if(isHex(tagData[index].getTagID())) {
                if(tagData[index].getTagID().startsWith("0000000000000000")){
                    String tagId = tagData[index].getTagID().substring(16, tagData[index].getTagID().length());
                    String firstLetter = tagId.substring(0, 1);
                    if(firstLetter.equalsIgnoreCase("C")) {
                        String outputString = tagId.replaceAll("\u0000", "");
                        if (outputString.trim().length()==8) {
                            sb.append(outputString + "&&&");
                        }
                    }
                }else {
                    byte[] bytes = Hex.stringToBytes(String.valueOf(tagData[index].getTagID().toCharArray()));
                    if(!containsNonAscii(new String(bytes, StandardCharsets.UTF_8))){
                        String tag_Id = new String(bytes, StandardCharsets.UTF_8);
                        String firstLetter_tag_id = tag_Id.substring(0, 1);
                        if (firstLetter_tag_id.equalsIgnoreCase("C")) {
                            String outputString = tag_Id.replaceAll("\u0000", "");
                            if (outputString.trim().length()==8) {
                                sb.append(outputString + "&&&");
                            }
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
                    }
                }
                synchronized (newList){
                    newList.replaceAll(String::trim);
                }
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
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //instantiate custom adapter
                // Traverse through the first list
                synchronized (newList) {
                    for (String element : newList) {

                        // If this element is not present in newList
                        // then add it
                        if (!codelistfromIntent.contains(element)) {

                            codelistfromIntent.add(element);
                        }
                    }
                }
                CustomizedListViewBulkUpdate adapter = new CustomizedListViewBulkUpdate(codelistfromIntent,model, BulkUpdateActivity.this);
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