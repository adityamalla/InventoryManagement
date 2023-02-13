package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class ContainerDetailsActivity extends AppCompatActivity {
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
    Button uploadData;
    String empName = "";
    String decodedData = "";
    String selectedFacilName = "";
    String selectedFacil = "";
    String selectedRoomName = "";
    String selectedRoom = "";
    EditText name;
    EditText code;
    EditText cas;
    EditText owner;
    EditText quantity;
    EditText unit;
    EditText location;
    EditText status;
    EditText notes;
    EditText comments;
    EditText concentration;
    EditText concentrationUnit;
    ConstraintLayout header;
    final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(ContainerDetailsActivity.this);
    final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_details);
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
        tv.setText("Edit Container");
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
        if(intent.getStringExtra("decodedData")!=null) {
            decodedData = intent.getStringExtra("decodedData");
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
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("loggedinUsername");
        selectedUserId = intent.getStringExtra("user_id");
        Log.e("selecteduserid1>>",selectedUserId+"**");
        loggedinUserSiteId = intent.getStringExtra("site_id");
        md5Pwd = intent.getStringExtra("md5pwd");
        if (intent.getStringExtra("selectedSearchValue") != null) {
            selectedSearchValue = intent.getStringExtra("selectedSearchValue");
        }
        uploadData = (Button) findViewById(R.id.uploadToWeb);
        name = (EditText)findViewById(R.id.productName);
        cas = (EditText)findViewById(R.id.cas);
        code = (EditText)findViewById(R.id.barcode);
        owner = (EditText)findViewById(R.id.owner);
        quantity = (EditText)findViewById(R.id.volumeMass);
        unit = (EditText)findViewById(R.id.volumeMassUnit);
        notes = (EditText)findViewById(R.id.locationNotes);
        location = (EditText)findViewById(R.id.location);
        comments = (EditText)findViewById(R.id.comment);
        status = (EditText)findViewById(R.id.status);
        concentration = (EditText)findViewById(R.id.concentration);
        concentrationUnit = (EditText)findViewById(R.id.concUnit);
        if (decodedData.trim().length()>0){
            InventoryModel inv = databaseHandler.getScannedInventoryDetails(db,decodedData);
            name.setText(inv.getProductName());
            cas.setText(inv.getCas_number());
            code.setText(inv.getCode());
            comments.setText(inv.getComments());
            notes.setText(inv.getNotes());
            owner.setText(inv.getOwner());
            quantity.setText(inv.getVolume_mass());
            unit.setText(inv.getVolume_mass_unit());
            status.setText(inv.getStatus());
            concentration.setText(inv.getConcentration());
            concentrationUnit.setText(inv.getConcentration_unit_abbrevation());
            if(selectedFacil.trim().length()==0){
                selectedFacil = inv.getFacil_id();
            }
            if(selectedRoom.trim().length()==0){
                selectedRoom = inv.getRoom_id();
                selectedRoomName = inv.getRoom();
            }
        }else{
            name.setText("");
            cas.setText("");
            code.setText("");
            comments.setText("");
            notes.setText("");
            owner.setText("");
            location.setText("");
            quantity.setText("");
            unit.setText("");
            status.setText("");
            concentration.setText("");
            concentrationUnit.setText("");
        }
        location.setText(selectedRoomName);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try {
                        ArrayList<MyObject> roomlist = databaseHandler.getRoomList(db,selectedFacil);
                        final Intent myIntent = new Intent(ContainerDetailsActivity.this,
                                RoomList.class);
                        myIntent.putExtra("user_id", selectedUserId);
                        myIntent.putExtra("site_id", loggedinUserSiteId);
                        myIntent.putExtra("token", token);
                        myIntent.putExtra("sso", sso);
                        myIntent.putExtra("md5pwd", md5Pwd);
                        myIntent.putExtra("loggedinUsername", loggedinUsername);
                        myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                        myIntent.putExtra("site_name", site_name);
                        myIntent.putExtra("roomlist",roomlist);
                        myIntent.putExtra("decodedData", decodedData);
                        myIntent.putExtra("selectedRoomName", selectedRoomName);
                        myIntent.putExtra("selectedRoom", selectedRoom+"");
                        myIntent.putExtra("empName", empName);
                        startActivity(myIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        // progress.dismiss();
                        db.close();
                        if (databaseHandler != null) {
                            databaseHandler.close();
                        }
                    }
            }
        });
    }
    @Override
    public void onBackPressed() {
    }
    public static void hideKeyboard(ContainerDetailsActivity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            final Intent myIntent = new Intent(ContainerDetailsActivity.this,
                    ScanBarcodeActivity.class);
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
}