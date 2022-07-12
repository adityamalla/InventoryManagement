package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.util.Hex;
import com.zebra.rfid.api3.TagData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class RFIDScannerActivity extends AppCompatActivity implements RFIDHandler.ResponseHandlerInterface {
    public TextView statusTextViewRFID = null;
    private ListView textrfid;
    private TextView testStatus;
    ArrayList<String> newList = new ArrayList<String>();
    RFIDHandler rfidHandler;
    final static String TAG = "RFID_SAMPLE";
    ArrayAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfidreader);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        // UI
        statusTextViewRFID = findViewById(R.id.rfidStatusText);
        textrfid = findViewById(R.id.tags_list);
        // RFID Handler
        rfidHandler = new RFIDHandler();
        rfidHandler.onCreate(this);
        adapter = new ArrayAdapter<String>(RFIDScannerActivity.this,
                R.layout.activity_tag_list_row, newList);

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
                textrfid.setAdapter(adapter);

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
                }
            });
            rfidHandler.performInventory();
        } else
            rfidHandler.stopInventory();
    }
}