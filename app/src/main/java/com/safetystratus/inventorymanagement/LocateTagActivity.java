package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.util.Hex;
import com.google.android.material.tabs.TabLayout;
import com.zebra.rfid.api3.BEEPER_VOLUME;
import com.zebra.rfid.api3.TagData;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LocateTagActivity extends AppCompatActivity implements RFIDLocationHandler.ResponseHandlerInterface{
    TabLayout tabLayout;
    String singleLocate="";
    String multiLocate="";
    String loggedinUsername = "";
    String md5Pwd = "";
    String sso = "";
    String site_name = "";
    String selectedUserId = "";
    String token="";
    String empName="";
    String loggedinUserSiteId = "";
    String selectedSearchValue="";
    RFIDLocationHandler rfidHandler;
    TextView rfidStatus;
    TextView tagSearch;
    RangeGraph rangeGraph;
    private int volumeLevel;
    public Timer locatebeep;
    public static ToneGenerator toneGenerator;
    private boolean beepONLocate = false;
    private static final int BEEP_DELAY_TIME_MIN = 0;
    private static final int BEEP_DELAY_TIME_MAX = 300;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_tag);
        rfidStatus = (TextView) findViewById(R.id.rfidStatusText);
        tagSearch = (TextView) findViewById(R.id.tag);
        rangeGraph = (RangeGraph)findViewById(R.id.locationBar);
        rangeGraph.setValue(0);
        rfidHandler = new RFIDLocationHandler();
        volumeLevel = 0; // Minimum volume level
        // Initialize the tone generator with the initial volume level
        beeperSettings(volumeLevel);
        rfidHandler.onCreate(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.header);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        SQLiteDatabase.loadLibs(this);
        final TextView tv = (TextView) findViewById(R.id.headerId);
        tv.setText(Html.fromHtml("Locate Tag"));
        tv.setTextSize(20);
        tv.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if(intent.getStringExtra("empName")!=null) {
            empName = intent.getStringExtra("empName");
        }
        if(intent.getStringExtra("singleLocate")!=null)
            singleLocate = intent.getStringExtra("singleLocate");
        if(intent.getStringExtra("multiLocate")!=null)
            multiLocate = intent.getStringExtra("multiLocate");
        sso = intent.getStringExtra("sso");
        if (intent.getStringExtra("token") != null) {
            token = intent.getStringExtra("token");
        }
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("loggedinUsername");
        selectedUserId = intent.getStringExtra("user_id");
        loggedinUserSiteId = intent.getStringExtra("site_id");
        md5Pwd = intent.getStringExtra("md5pwd");
        if (intent.getStringExtra("selectedSearchValue") != null) {
            selectedSearchValue = intent.getStringExtra("selectedSearchValue");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            final Intent myIntent = new Intent(LocateTagActivity.this,
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
    protected void onPause() {
        super.onPause();
        rfidHandler.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        String status = rfidHandler.onResume();
        rfidStatus.setText(status);
        // rangeGraph.setValue(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rfidHandler.onDestroy();
    }
    @Override
    public void handleTagdata(final String per) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(Integer.parseInt(per)>0) {
                    rangeGraph.setValue(Integer.parseInt(per));
                    rangeGraph.invalidate();
                    rangeGraph.requestLayout();
                    startlocatebeeping(Integer.parseInt(per));
                }else{
                    stopbeep();
                }
            }
        });
    }
    public  void startlocatebeeping(int proximity) {
        if(proximity>0){
            volumeLevel = proximity;
            increaseVolume();
            beep();
        }else{
            stopbeep();
        }
    }

    /**
     * method to stop timer locate beep
     */
    public void stopbeep() {
        if (toneGenerator != null) {
            rangeGraph.setValue(0);
            rangeGraph.invalidate();
            rangeGraph.requestLayout();
            toneGenerator.stopTone();
        }
    }
    public void increaseVolume() {
        if (volumeLevel < ToneGenerator.MAX_VOLUME) {
            volumeLevel++; // Increase the volume level
            // Recreate the tone generator with the new volume level
            if (toneGenerator!=null)
            toneGenerator.release();
            toneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, volumeLevel);
        }
    }
    public void beep() {
        if (toneGenerator != null) {
            int toneType = ToneGenerator.TONE_PROP_BEEP;
            toneGenerator.startTone(toneType);
        }
    }
    private void beeperSettings(int volume) {
        int streamType = AudioManager.STREAM_DTMF;
        int percantageVolume = 100;
        if (volume == 0) {
            beeperVolume = BEEPER_VOLUME.HIGH_BEEP;
            percantageVolume = 100;
        }
        if (volume == 1) {
            beeperVolume = BEEPER_VOLUME.MEDIUM_BEEP;
            percantageVolume = 75;
        }
        if (volume == 2) {
            beeperVolume = BEEPER_VOLUME.LOW_BEEP;
            percantageVolume = 50;
        }
        if (volume == 3) {
            beeperVolume = BEEPER_VOLUME.QUIET_BEEP;
            percantageVolume = 0;
        }

        try {
            toneGenerator = new ToneGenerator(streamType, percantageVolume);
        } catch (RuntimeException exception) {
            toneGenerator = null;
        }
    }
    @Override
    public void handleTriggerPress(boolean pressed) {
        if (pressed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String str = tagSearch.getText().toString();
                        if(str.length()>0){
                            rangeGraph.setValue(0);
                            rangeGraph.invalidate();
                            rangeGraph.requestLayout();
                            str = str.toUpperCase();
                            if (str.toUpperCase().contains("LBL ")){
                                str = str.toUpperCase().replaceAll("LBL\\s+","0000000000000000");
                                if(!RFIDLocationHandler.isLocatingTag )
                                    rfidHandler.performLocateInventory(str);
                            }else{
                                StringBuffer sb = new StringBuffer();
                                //Converting string to character array
                                char ch[] = str.toCharArray();
                                for(int i = 0; i < ch.length; i++) {
                                    String hexString = Integer.toHexString(ch[i]);
                                    sb.append(hexString);
                                }
                                if(!RFIDLocationHandler.isLocatingTag )
                                    rfidHandler.performLocateInventory(sb.toString());
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            triggerReleaseEventRecieved();
        }
    }
    public void triggerReleaseEventRecieved() {
        if (RFIDLocationHandler.isLocatingTag) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rfidHandler.stopLocateInventory();
                    resetRangeGraph();
                }
            });
        }
    }
    public void resetRangeGraph(){
        rangeGraph.setValue(0);
        rangeGraph.invalidate();
        rangeGraph.requestLayout();
    }
    public static BEEPER_VOLUME beeperVolume = BEEPER_VOLUME.HIGH_BEEP;
}