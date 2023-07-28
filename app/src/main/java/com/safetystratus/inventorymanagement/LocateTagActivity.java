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
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.util.Hex;
import com.google.android.material.tabs.TabLayout;
import com.zebra.rfid.api3.BEEPER_VOLUME;
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
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
    Button locateTagButton;
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
        locateTagButton = (Button) findViewById(R.id.locateTagButton);
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
        locateTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locateTagButton.getText().equals("Start"))
                    handleTriggerPress(true);
                else
                    handleTriggerPress(false);
            }
        });

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
            if (proximity>0&&proximity<30){
                BEEP_STOP_TIME = 2000;
            }else if (proximity>30&&proximity<50){
                BEEP_STOP_TIME = 1000;
            }else if (proximity>50&&proximity<70){
                BEEP_STOP_TIME = 600;
            }else if (proximity>70){
                BEEP_STOP_TIME = 20;
            }
            startbeepingTimer();
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
            toneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 100);
        }
    }
    public void beep() {
        if (toneGenerator != null) {
            int toneType = ToneGenerator.TONE_PROP_BEEP;
            toneGenerator.startTone(toneType);
        }
    }

    private boolean beepON = false;
    public Timer tbeep;
    private long BEEP_STOP_TIME = 20;

    public void startbeepingTimer() {
        if (beeperVolume != BEEPER_VOLUME.QUIET_BEEP) {
            if (!beepON) {
                beepON = true;
                beep();
                if (tbeep == null) {
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            stopbeepingTimer();
                            beepON = false;
                        }
                    };
                    tbeep = new Timer();
                    tbeep.schedule(task, BEEP_STOP_TIME);
                }
            }
        }
    }
    public void stopbeepingTimer() {
        if (tbeep != null) {
            //if (toneGenerator != null)
             //   toneGenerator.stopTone();
            tbeep.cancel();
            tbeep.purge();
        }
        tbeep = null;
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
    ArrayMap multiTagLocateTagMap = new ArrayMap();
    @Override
    public synchronized void handleTriggerPress(boolean pressed) {
        if (pressed) {
            try {
                String str = tagSearch.getText().toString();
                if (str.length() > 0) {
                    if (!RFIDLocationHandler.isLocatingTag) {
                        str = str.toUpperCase();
                        multiTagLocateTagMap.clear();
                        String flatTypeTagStyle = "0000000000000000" + str;
                        multiTagLocateTagMap.put(flatTypeTagStyle, "");
                        StringBuffer sb = new StringBuffer();
                        //Converting string to character array
                        char ch[] = str.toCharArray();
                        for (int i = 0; i < ch.length; i++) {
                            String hexString = Integer.toHexString(ch[i]);
                            sb.append(hexString);
                        }
                        String flagTypeStyle = sb.toString() + "00000000";
                        multiTagLocateTagMap.put(flagTypeStyle, "");
                        rfidHandler.reader.Actions.MultiTagLocate.purgeItemList();
                        rfidHandler.reader.Actions.MultiTagLocate.importItemList(multiTagLocateTagMap);
                        rfidHandler.performLocateInventory("");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                locateTagButton.setText("Stop");
                                resetRangeGraph();
                            }
                        });
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            triggerReleaseEventRecieved();
        }
    }
    public synchronized void triggerReleaseEventRecieved() {
        if (RFIDLocationHandler.isLocatingTag) {
            rfidHandler.stopLocateInventory();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resetRangeGraph();
                    locateTagButton.setText("Start");
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