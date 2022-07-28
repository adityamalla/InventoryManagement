package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import net.sqlcipher.database.SQLiteDatabase;

public class LocateTagActivity extends AppCompatActivity {
    TabLayout tabLayout;
    CustomViewPager viewPager;
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

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_tag);
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
            request_token = intent.getStringExtra("token");
        }
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("loggedinUsername");
        selectedUserId = intent.getStringExtra("selectedUserId");
        loggedinUserSiteId = intent.getStringExtra("site_id");
        md5Pwd = intent.getStringExtra("md5pwd");
        if (intent.getStringExtra("selectedSearchValue") != null) {
            selectedSearchValue = intent.getStringExtra("selectedSearchValue");
        }
        tabLayout=(TabLayout)findViewById(R.id.tabLayoutSingleAndMultiTagLocate);
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
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            final Intent myIntent = new Intent(LocateTagActivity.this,
                    HomeActivity.class);
            myIntent.putExtra("user_id", user_id);
            myIntent.putExtra("site_id", site_id);
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
}