package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

public class ScanBarcodeActivity extends AppCompatActivity {
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
    EditText enteredBarcode;
    Button viewBarcodeDetails;
    String empName = "";
    ConstraintLayout header;
    final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(ScanBarcodeActivity.this);
    final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
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
        tv.setText("Scan Container");
        tv.setTextSize(18);
        tv.setVisibility(View.VISIBLE);
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
        loggedinUserSiteId = intent.getStringExtra("site_id");
        md5Pwd = intent.getStringExtra("md5pwd");
        if (intent.getStringExtra("selectedSearchValue") != null) {
            selectedSearchValue = intent.getStringExtra("selectedSearchValue");
        }
        enteredBarcode = (EditText) findViewById(R.id.barcode);
        viewBarcodeDetails = (Button) findViewById(R.id.viewData);
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);
        viewBarcodeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enteredBarcode.getText().toString().trim().length()==0){
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ScanBarcodeActivity.this);
                    dlgAlert.setTitle("Safety Stratus");
                    dlgAlert.setMessage("Please enter barcode details and click on view button or scan the barcode directly to view the details!");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    dlgAlert.create().show();
                }else{
                    if(databaseHandler.checkScannedBarcodeDataAvailable(db,enteredBarcode.getText().toString().trim())){
                        unregisterReceiver(myBroadcastReceiver);
                        final Intent myIntent = new Intent(ScanBarcodeActivity.this,
                                ContainerDetailsActivity.class);
                        myIntent.putExtra("user_id", selectedUserId);
                        myIntent.putExtra("site_id", loggedinUserSiteId);
                        myIntent.putExtra("token", token);
                        myIntent.putExtra("sso", sso);
                        myIntent.putExtra("md5pwd", md5Pwd);
                        myIntent.putExtra("loggedinUsername", loggedinUsername);
                        myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                        myIntent.putExtra("site_name", site_name);
                        myIntent.putExtra("decodedData", enteredBarcode.getText().toString().trim());
                        myIntent.putExtra("empName", empName);
                        startActivity(myIntent);
                    }else{
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ScanBarcodeActivity.this);
                        dlgAlert.setTitle("Safety Stratus");
                        dlgAlert.setMessage("Information for the container is not available on this device!");
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                        dlgAlert.create().show();
                    }
                }
                enteredBarcode.setText("");
            }
        });
    }
    @Override
    public void onBackPressed() {
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
        if(!scanInProgress) {
            scanInProgress = true;
            String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
            String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
            String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));

            if (null == decodedSource) {
                decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source_legacy));
                decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data_legacy));
                decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type_legacy));
            }
            if (decodedData.contains("LBL")) {
                decodedData = decodedData.replaceAll("LBL", "");
            }
            decodedData = decodedData.replaceAll("\u0000", "");
            decodedData = decodedData.trim();
            boolean validtag = false;
            String firstLetterTest = decodedData.trim().substring(0, 1);
            if (firstLetterTest.equalsIgnoreCase("C")) {
                validtag = true;
            }
            if (validtag) {
                Log.e("testing000","***");
                /*if (databaseHandler.checkScannedBarcodeDataAvailable(db, decodedData)) {
                    Log.e("testing0001","***");
                    unregisterReceiver(myBroadcastReceiver);
                    Log.e("testing0002","***");
                    final Intent myIntent = new Intent(ScanBarcodeActivity.this,
                            ContainerDetailsActivity.class);
                    myIntent.putExtra("user_id", selectedUserId);
                    myIntent.putExtra("site_id", loggedinUserSiteId);
                    myIntent.putExtra("token", token);
                    myIntent.putExtra("sso", sso);
                    myIntent.putExtra("md5pwd", md5Pwd);
                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                    myIntent.putExtra("site_name", site_name);
                    myIntent.putExtra("decodedData", decodedData);
                    myIntent.putExtra("empName", empName);
                    startActivity(myIntent);
                } else {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ScanBarcodeActivity.this);
                    dlgAlert.setTitle("Safety Stratus");
                    dlgAlert.setMessage("Information for the container is not available on this device!");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    dlgAlert.create().show();
                }*/
                Showinprogressmessage cobj = new Showinprogressmessage();
                cobj.execute(decodedData);
            } else {
                scanInProgress = false;
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ScanBarcodeActivity.this);
                dlgAlert.setTitle("Safety Stratus");
                dlgAlert.setMessage("Information for the scanned barcode is not available on this device!");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                dlgAlert.create().show();
            }
        }

    }
    public static void hideKeyboard(ScanBarcodeActivity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        unregisterReceiver(myBroadcastReceiver);
        if (id == android.R.id.home) {
            final Intent myIntent = new Intent(ScanBarcodeActivity.this,
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
    Boolean scanInProgress = false;
    boolean scannedBarcodeExists = false;
    class Showinprogressmessage extends AsyncTask<String, String, String> {
        private ProgressDialog progressSync = new ProgressDialog(ScanBarcodeActivity.this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(ScanBarcodeActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        String decodedDataScanned = "";
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // disable dismiss by tapping outside of the dialog
            progressSync.setTitle("");
            progressSync.setMessage("Scanning in progress..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(450, 200);
            super.onPreExecute();
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            // db.beginTransaction();
            try {
                decodedDataScanned = params[0];
                scannedBarcodeExists = databaseHandler.checkScannedBarcodeDataAvailable(db, decodedDataScanned);
            } finally {
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
            scanInProgress = false;
            if (scannedBarcodeExists){
                unregisterReceiver(myBroadcastReceiver);
                if (progressSync != null && progressSync.isShowing()){
                    progressSync.dismiss();
                    progressSync = null;
                }
                final Intent myIntent = new Intent(ScanBarcodeActivity.this,
                        ContainerDetailsActivity.class);
                myIntent.putExtra("user_id", selectedUserId);
                myIntent.putExtra("site_id", loggedinUserSiteId);
                myIntent.putExtra("token", token);
                myIntent.putExtra("sso", sso);
                myIntent.putExtra("md5pwd", md5Pwd);
                myIntent.putExtra("loggedinUsername", loggedinUsername);
                myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                myIntent.putExtra("site_name", site_name);
                myIntent.putExtra("decodedData", decodedDataScanned);
                myIntent.putExtra("empName", empName);
                startActivity(myIntent);
            } else {
                if (progressSync != null && progressSync.isShowing()){
                    progressSync.dismiss();
                    progressSync = null;
                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ScanBarcodeActivity.this);
                dlgAlert.setTitle("Safety Stratus");
                dlgAlert.setMessage("Information for the container is not available on this device!");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                dlgAlert.create().show();
            }
        }
    }
}