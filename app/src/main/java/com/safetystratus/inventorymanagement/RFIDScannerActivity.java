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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Hex;
import com.zebra.rfid.api3.TagData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class RFIDScannerActivity extends AppCompatActivity implements RFIDHandler.ResponseHandlerInterface {
    public TextView statusTextViewRFID = null;
    private ListView textrfid;
    private TextView testStatus;
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
    String request_token="";
    final String[] site_id = {""};
    final String[] user_id = {""};
    final String[] token = {""};
    String selectedFacilName = "";
    String selectedFacil = "";
    String selectedRoomName = "";
    String selectedRoom = "";
    String empName="";
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfidreader);
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
        textrfid = findViewById(R.id.tags_list);
        Intent intent = getIntent();
        sso = intent.getStringExtra("sso");
        if (intent.getStringExtra("token") != null) {
            request_token = intent.getStringExtra("token");
        }
        if(intent.getStringExtra("empName")!=null) {
            empName = intent.getStringExtra("empName");
        }
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("loggedinUsername");
        selectedUserId = intent.getStringExtra("selectedUserId");
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
        // RFID Handler
        rfidHandler = new RFIDHandler();
        rfidHandler.onCreate(this);
        adapter = new ArrayAdapter<String>(RFIDScannerActivity.this,
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
                scanCounts[0] = newList.size();
                textrfid.setAdapter(adapter);
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
                    textrfid.setAdapter(null);
                    scanCount.setText("0");
                }
            });
            rfidHandler.performInventory();
        } else
            rfidHandler.stopInventory();
    }

    public void setscancount(String count){
        scanCount.setText(count);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            final Intent myIntent = new Intent(RFIDScannerActivity.this,
                    RFIDActivity.class);
            myIntent.putExtra("user_id", user_id);
            myIntent.putExtra("site_id", site_id);
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