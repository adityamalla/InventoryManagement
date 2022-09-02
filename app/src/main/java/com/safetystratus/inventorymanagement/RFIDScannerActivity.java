package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Hex;
import com.zebra.rfid.api3.TagData;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
        /*adapter = new ArrayAdapter<String>(RFIDScannerActivity.this,
                R.layout.activity_tag_list_row, newList);
        textrfid.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });*/
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RFIDScannerActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        ArrayList<InventoryObject> invList = databaseHandler.getInventoryList(databaseHandler.getWritableDatabase(PASS_PHRASE),selectedRoom);
        tableInv = (TableLayout) findViewById(R.id.tableInv);
        final TextView invNameHeader = new TextView(this);
        invNameHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                100,5));
        invNameHeader.setGravity(Gravity.LEFT);
        invNameHeader.setPadding(15, 30, 0, 0);
        invNameHeader.setBackgroundResource(R.drawable.cell_shape_child);
        invNameHeader.setText("Product Name");
        invNameHeader.setTextSize(16);
        invNameHeader.setTextColor(Color.parseColor("#FFFFFF"));
        invNameHeader.setBackgroundColor(Color.parseColor("#0F74BC"));
        final TextView invRFIDCodeHeader = new TextView(this);
        invRFIDCodeHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                100,5));
        invRFIDCodeHeader.setGravity(Gravity.LEFT);
        invRFIDCodeHeader.setPadding(5, 30,20, 0);
        invRFIDCodeHeader.setBackgroundResource(R.drawable.cell_shape_child);
        invRFIDCodeHeader.setText("RFID Code");
        invRFIDCodeHeader.setTextSize(16);
        invRFIDCodeHeader.setTextColor(Color.parseColor("#FFFFFF"));
        invRFIDCodeHeader.setBackgroundColor(Color.parseColor("#0F74BC"));
        final TableRow trInvHeader = new TableRow(this);
        TableLayout.LayoutParams trParamsHeader = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        trInvHeader.setBackgroundResource(R.drawable.table_tr_border);
        //trParams.setMargins(10, 10, 10, 10);
        trInvHeader.setLayoutParams(trParamsHeader);
        trInvHeader.addView(invNameHeader);
        trInvHeader.addView(invRFIDCodeHeader);
        tableInv.addView(trInvHeader, trParamsHeader);
        for (int i = 0; i < invList.size(); i++) {
            final TextView invName = new TextView(this);
            invName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    100,5));
            invName.setGravity(Gravity.LEFT);
            invName.setPadding(10, 30, 0, 0);
            invName.setBackgroundResource(R.drawable.cell_shape_child);
            invName.setText(invList.get(i).getProductName());
            invName.setId(i);
            invName.setTextSize(16);
            invName.setTextColor(Color.parseColor("#000000"));
            invName.setBackgroundColor(Color.parseColor("#FFFFFF"));
            final TextView invRFIDCode = new TextView(this);
            invRFIDCode.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    100,5));
            invRFIDCode.setGravity(Gravity.LEFT);
            invRFIDCode.setPadding(5, 30, 20, 0);
            invRFIDCode.setBackgroundResource(R.drawable.cell_shape_child);
            invRFIDCode.setText(invList.get(i).getRfidCode());
            invRFIDCode.setId(i+1);
            invRFIDCode.setTextSize(16);
            invRFIDCode.setTextColor(Color.parseColor("#000000"));
            invName.setBackgroundColor(Color.parseColor("#FFFFFF"));
            final TableRow trInv = new TableRow(this);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trInv.setBackgroundResource(R.drawable.table_tr_border);
            //trParams.setMargins(10, 10, 10, 10);
            trInv.setLayoutParams(trParams);
            trInv.addView(invName);
            trInv.addView(invRFIDCode);
            tableInv.addView(trInv, trParams);
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
                for(int i = 0; i < tableInv.getChildCount(); i++) {
                    View view = tableInv.getChildAt(i);
                    if (view instanceof TableRow) {
                        TableRow row = (TableRow) view;
                        for(int j = 0;j< row.getChildCount();j++) {
                            View view1 = row.getChildAt(j);
                            if(view1 instanceof TextView){
                                TextView tv  = (TextView) view1;
                                for (String ele : newList){
                                    if(ele.trim().equals(tv.getText().toString())){
                                        Log.e("color check--",ele.trim()+"--"+tv.getText().toString());
                                        for(int k = 0;k< row.getChildCount();k++) {
                                            View view2 = row.getChildAt(k);
                                            view2.setBackgroundResource(R.color.invScanSuccess);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                scanCounts[0] = newList.size();
                //textrfid.setAdapter(adapter);
                setscancount(String.valueOf(scanCounts[0]));
            }
        });
    }

    @Override
    public void handleTriggerPress(boolean pressed) {
        if (pressed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //textrfid.setAdapter(null);
                    scanCount.setText("0");
                    scannedProgressPercentage.setText("0 %");
                    scannedProgressCount.setText("0/"+total_inventory);
                    progressVal.setProgress(0);
                }
            });
            rfidHandler.performInventory();
        } else
            rfidHandler.stopInventory();
    }

    public void setscancount(String count){
        scanCount.setText(count);
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
}