package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.safetystratus.inventorymanagement.RFIDLocationHandler.beeperVolume;
import static com.safetystratus.inventorymanagement.RFIDLocationHandler.toneGenerator;

public class LocateTagActivity extends AppCompatActivity implements RFIDLocationHandler.ResponseHandlerInterface{
    TabLayout tabLayout;
    String singleLocate="";
    String multiLocate="";
    String loggedinUsername = "";
    String md5Pwd = "";
    String sso = "";
    String site_name = "";
    String selectedUserId = "";
    String request_token="";
    String empName="";
    String loggedinUserSiteId = "";
    String selectedSearchValue="";
    final String[] site_id = {""};
    final String[] user_id = {""};
    final String[] token = {""};
    RFIDLocationHandler rfidHandler;
    TextView rfidStatus;
    TextView tagSearch;
    RangeGraph rangeGraph;
    public Timer locatebeep;
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
        rfidHandler.onCreate(this);
        /*tabLayout=(TabLayout)findViewById(R.id.tabLayoutSingleAndMultiTagLocate);
        viewPager=(CustomViewPager) findViewById(R.id.viewPagerLocateTags);
        viewPager.setEnableSwipe(false);
        final TabAdapterLocateTags adapter = new TabAdapterLocateTags(this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
        if(singleLocate.trim().length()>0)
        {
            if(Integer.parseInt(singleLocate)==0){
                TabLayout.Tab tab = tabLayout.getTabAt(0);
                tab.select();
            }
        }
        if(multiLocate.trim().length()>0){
            if(Integer.parseInt(multiLocate)==1){
                TabLayout.Tab tab = tabLayout.getTabAt(1);
                tab.select();
            }
        }*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

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
                    startlocatebeepingTimer(Integer.parseInt(per));
                }else{
                    stoplocatebeepingTimer();
                }
            }
        });
    }
    public  void startlocatebeepingTimer(int proximity) {
        if (beeperVolume != BEEPER_VOLUME.QUIET_BEEP) {
            int POLLING_INTERVAL1 = BEEP_DELAY_TIME_MIN + (((BEEP_DELAY_TIME_MAX - BEEP_DELAY_TIME_MIN) * (100 - proximity)) / 100);
            if (!beepONLocate) {
                beepONLocate = true;
                beep();
                if (locatebeep == null) {
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            stoplocatebeepingTimer();
                            beepONLocate = false;
                        }
                    };
                    locatebeep = new Timer();
                    locatebeep.schedule(task, POLLING_INTERVAL1, 10);
                }
            }
        }
    }

    /**
     * method to stop timer locate beep
     */
    public void stoplocatebeepingTimer() {
        if (locatebeep != null && toneGenerator != null) {
            toneGenerator.stopTone();
            locatebeep.cancel();
            locatebeep.purge();
        }
        locatebeep = null;
    }
    public void beep() {
        if (toneGenerator != null) {
            int toneType = ToneGenerator.TONE_PROP_BEEP;
            toneGenerator.startTone(toneType);
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
                            StringBuffer sb = new StringBuffer();
                            //Converting string to character array
                            char ch[] = str.toCharArray();
                            for(int i = 0; i < ch.length; i++) {
                                String hexString = Integer.toHexString(ch[i]);
                                sb.append(hexString);
                            }
                            Log.e("encoded::::",sb.toString()+"**");
                            RFIDLocationHandler.isLocatingTag = false;
                            if(!RFIDLocationHandler.isLocatingTag )
                                rfidHandler.performLocateInventory(sb.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    @Override
    public synchronized void triggerReleaseEventRecieved() {
        if (RFIDLocationHandler.isLocatingTag) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rfidHandler.stopLocateInventory();
                    rangeGraph.setValue(0);
                }
            });
        }
    }
    public void setRangeGraph(short count){
        Log.e("Test Final Range>>",count+"**");

    }
}