package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONObject;

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
    Button save;
    String empName = "";
    String note = "";
    String rfidCde = "";
    String comment="";
    String conc_val="";
    String quan_val="";
    String decodedData = "";
    String selectedFacilName = "";
    String selectedFacil = "";
    String selectedStatusName = "";
    String selectedStatus = "";
    String selectedRoomName = "";
    String selectedRoom = "";
    String selectedConcUnitName = "";
    String selectedConcUnit = "";
    String selectedQuanUnitName = "";
    String selectedQuanUnit = "";
    String host="";
    String selectedOwnerName = "";
    String selectedOwner = "";
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
    EditText rfidCode;
    EditText concentration;
    EditText concentrationUnit;
    ConstraintLayout header;
    TextView badge_notification;
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences pref;
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
        pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        host = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).getString("site_api_host", "services.labcliq.com");
        //Log.e("Host-->",host);
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
        if(intent.getStringExtra("note")!=null) {
            note = intent.getStringExtra("note");
        }
        if(intent.getStringExtra("rfidCde")!=null) {
            rfidCde = intent.getStringExtra("rfidCde");
        }
        if(intent.getStringExtra("comment")!=null) {
            comment = intent.getStringExtra("comment");
        }
        if(intent.getStringExtra("conc_val")!=null) {
            conc_val = intent.getStringExtra("conc_val");
        }
        if(intent.getStringExtra("quan_val")!=null) {
            quan_val = intent.getStringExtra("quan_val");
        }
        if (intent.getStringExtra("selectedFacilName") != null) {
            selectedFacilName = intent.getStringExtra("selectedFacilName");
        }
        if (intent.getStringExtra("selectedFacil") != null) {
            selectedFacil = intent.getStringExtra("selectedFacil");
        }
        if (intent.getStringExtra("selectedStatus") != null) {
            selectedStatus = intent.getStringExtra("selectedStatus");
        }
        if (intent.getStringExtra("selectedStatusName") != null) {
            selectedStatusName = intent.getStringExtra("selectedStatusName");
        }
        if (intent.getStringExtra("selectedRoom") != null) {
            selectedRoom = intent.getStringExtra("selectedRoom");
        }
        if (intent.getStringExtra("selectedRoomName") != null) {
            selectedRoomName = intent.getStringExtra("selectedRoomName");
        }
        if (intent.getStringExtra("selectedConcUnitName") != null) {
            selectedConcUnitName = intent.getStringExtra("selectedConcUnitName");
        }
        if (intent.getStringExtra("selectedConcUnit") != null) {
            selectedConcUnit = intent.getStringExtra("selectedConcUnit");
        }
        if (intent.getStringExtra("selectedQuanUnitName") != null) {
            selectedQuanUnitName = intent.getStringExtra("selectedQuanUnitName");
        }
        if (intent.getStringExtra("selectedQuanUnit") != null) {
            selectedQuanUnit = intent.getStringExtra("selectedQuanUnit");
        }
        if (intent.getStringExtra("selectedOwner") != null) {
            selectedOwner = intent.getStringExtra("selectedOwner");
        }
        if (intent.getStringExtra("selectedOwnerName") != null) {
            selectedOwnerName = intent.getStringExtra("selectedOwnerName");
        }
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("loggedinUsername");
        selectedUserId = intent.getStringExtra("user_id");
        loggedinUserSiteId = intent.getStringExtra("site_id");
        md5Pwd = intent.getStringExtra("md5pwd");
        if (intent.getStringExtra("selectedSearchValue") != null) {
            selectedSearchValue = intent.getStringExtra("selectedSearchValue");
        }
        uploadData = (Button) findViewById(R.id.uploadToWeb);
        save = (Button) findViewById(R.id.saveLocal);
        name = (EditText)findViewById(R.id.productName);
        cas = (EditText)findViewById(R.id.cas);
        code = (EditText)findViewById(R.id.barcode);
        owner = (EditText)findViewById(R.id.owner);
        quantity = (EditText)findViewById(R.id.volumeMass);
        unit = (EditText)findViewById(R.id.volumeMassUnit);
        notes = (EditText)findViewById(R.id.locationNotes);
        location = (EditText)findViewById(R.id.location);
        comments = (EditText)findViewById(R.id.comment);
        rfidCode = (EditText)findViewById(R.id.rfidCode);
        TextView rfidlabel = (TextView)findViewById(R.id.rfidlabel);
        TextView barcodelabel = (TextView)findViewById(R.id.barcodelabel);
        status = (EditText)findViewById(R.id.status);
        concentration = (EditText)findViewById(R.id.concentration);
        concentrationUnit = (EditText)findViewById(R.id.concUnit);
        badge_notification = findViewById(R.id.badge_notification);
        int scannedJsonData = databaseHandler.getSavedBarcodeDataCount(databaseHandler.getWritableDatabase(PASS_PHRASE));
        if(scannedJsonData>0){
            badge_notification.setVisibility(View.VISIBLE);
            badge_notification.setText(String.valueOf(scannedJsonData));
        }else{
            badge_notification.setVisibility(View.GONE);
            badge_notification.setText("");
        }
        code.setVisibility(View.GONE);
        barcodelabel.setVisibility(View.GONE);
        ConstraintLayout constraintLayout = findViewById(R.id.containerdetailsactivityupdate);
        ConstraintSet constraintSet1 = new ConstraintSet();
        constraintSet1.clone(constraintLayout);
        constraintSet1.connect(R.id.rfidlabel,ConstraintSet.START,R.id.containerdetailsactivityupdate,ConstraintSet.START,0);
        constraintSet1.connect(R.id.rfidlabel,ConstraintSet.END,R.id.containerdetailsactivityupdate,ConstraintSet.END,0);
        constraintSet1.connect(R.id.rfidlabel,ConstraintSet.TOP,R.id.containerdetailsactivityupdate,ConstraintSet.TOP,0);
        constraintSet1.applyTo(constraintLayout);
        ConstraintLayout.LayoutParams newLayoutParams1 = (ConstraintLayout.LayoutParams) rfidlabel.getLayoutParams();
        newLayoutParams1.topMargin = 20;
        newLayoutParams1.leftMargin = 10;
        newLayoutParams1.rightMargin = 10;
        newLayoutParams1.bottomMargin = 0;
        rfidlabel.setLayoutParams(newLayoutParams1);
        if (decodedData.trim().length()>0){
            InventoryModel inv = databaseHandler.getScannedInventoryDetails(db,decodedData,"");
            name.setText(inv.getProductName());
            cas.setText(inv.getCas_number());
            code.setText(inv.getCode());
            if (inv.getRfidCode().trim().length()>0){
                rfidCode.setText(inv.getRfidCode());
            }else{
                rfidCode.setText("");
                rfidCode.setEnabled(true);
                rfidCode.setFocusableInTouchMode(true);
            }
            if (rfidCde.trim().length() == 0){
                rfidCde = inv.getRfidCode();
            }
            if(selectedFacil.trim().length()==0){
                selectedFacil = inv.getFacil_id();
            }
            if(selectedRoom.trim().length()==0){
                selectedRoom = inv.getRoom_id();
                selectedRoomName = inv.getRoom();
            }
            if(selectedStatus.trim().length()==0){
                selectedStatus = inv.getStatus_id();
                selectedStatusName = inv.getStatus();
            }
            if(selectedConcUnit.trim().length()==0&&!inv.getConcentration_unit_abbrevation_id().equalsIgnoreCase("-1")){
                selectedConcUnit = inv.getConcentration_unit_abbrevation_id();
                selectedConcUnitName = inv.getConcentration_unit_abbrevation();
            }
            if(selectedQuanUnit.trim().length()==0&&!inv.getVolume_mass_unit_id().equalsIgnoreCase("-1")){
                selectedQuanUnit = inv.getVolume_mass_unit_id();
                selectedQuanUnitName = inv.getVolume_mass_unit();
            }
            if(quan_val.trim().length()==0&&!inv.getVolume_mass().equalsIgnoreCase("-1")){
                quan_val = inv.getVolume_mass();
            }
            if(conc_val.trim().length()==0&&!inv.getConcentration().equalsIgnoreCase("-1")){
                conc_val = inv.getConcentration();
            }
            if(note.trim().length()==0){
                note = inv.getNotes();
            }
            if(comment.trim().length()==0){
                comment = inv.getComments();
            }
            if (selectedOwner.trim().length()==0){
                if(inv.getOwner().trim().length()>0) {
                    selectedOwnerName = inv.getOwner();
                    selectedOwner = inv.getObject_id();
                }
            }
        }else{
            name.setText("");
            cas.setText("");
            code.setText("");
            rfidCode.setText("");
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
        status.setText(selectedStatusName);
        concentrationUnit.setText(selectedConcUnitName);
        unit.setText(selectedQuanUnitName);
        quantity.setText(quan_val);
        concentration.setText(conc_val);
        notes.setText(note);
        comments.setText(comment);
        owner.setText(selectedOwnerName);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try {
                        quan_val = quantity.getText().toString();
                        conc_val = concentration.getText().toString();
                        note = notes.getText().toString();
                        comment = comments.getText().toString();
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
                        myIntent.putExtra("selectedStatusName", selectedStatusName);
                        myIntent.putExtra("selectedStatus", selectedStatus+"");
                        myIntent.putExtra("selectedConcUnitName", selectedConcUnitName);
                        myIntent.putExtra("selectedConcUnit", selectedConcUnit+"");
                        myIntent.putExtra("selectedQuanUnitName", selectedQuanUnitName);
                        myIntent.putExtra("selectedQuanUnit", selectedQuanUnit+"");
                        myIntent.putExtra("selectedOwnerName", selectedOwnerName);
                        myIntent.putExtra("selectedOwner", selectedOwner+"");
                        myIntent.putExtra("quan_val", quan_val+"");
                        myIntent.putExtra("conc_val", conc_val+"");
                        myIntent.putExtra("note", note+"");
                        if (rfidCode.isEnabled()) {
                            myIntent.putExtra("rfidCode", rfidCde + "");
                        }
                        myIntent.putExtra("comment", comment+"");
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
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    quan_val = quantity.getText().toString();
                    conc_val = concentration.getText().toString();
                    note = notes.getText().toString();
                    comment = comments.getText().toString();
                    String user_role_id = pref.getString("logged_in_user_role_id", null);
                    ArrayList<MyObject> statusList = databaseHandler.getStatusList(db,user_role_id);
                    boolean selectedStausExist = false;
                    for(int h=0;h<statusList.size();h++){
                        if (Integer.parseInt(statusList.get(h).getObjectId()) == Integer.parseInt(selectedStatus)){
                            selectedStausExist = true;
                            break;
                        }
                    }
                    if (!selectedStausExist){
                        statusList.add(new MyObject(selectedStatusName,selectedStatus));
                    }
                    final Intent myIntent = new Intent(ContainerDetailsActivity.this,
                            StatusList.class);
                    myIntent.putExtra("user_id", selectedUserId);
                    myIntent.putExtra("site_id", loggedinUserSiteId);
                    myIntent.putExtra("token", token);
                    myIntent.putExtra("sso", sso);
                    myIntent.putExtra("md5pwd", md5Pwd);
                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                    myIntent.putExtra("site_name", site_name);
                    myIntent.putExtra("statusList",statusList);
                    myIntent.putExtra("decodedData", decodedData);
                    myIntent.putExtra("selectedRoomName", selectedRoomName);
                    myIntent.putExtra("selectedRoom", selectedRoom+"");
                    myIntent.putExtra("selectedStatusName", selectedStatusName);
                    myIntent.putExtra("selectedStatus", selectedStatus+"");
                    myIntent.putExtra("selectedConcUnitName", selectedConcUnitName);
                    myIntent.putExtra("selectedConcUnit", selectedConcUnit+"");
                    myIntent.putExtra("selectedQuanUnitName", selectedQuanUnitName);
                    myIntent.putExtra("selectedQuanUnit", selectedQuanUnit+"");
                    myIntent.putExtra("selectedOwnerName", selectedOwnerName);
                    myIntent.putExtra("selectedOwner", selectedOwner+"");
                    myIntent.putExtra("quan_val", quan_val+"");
                    myIntent.putExtra("conc_val", conc_val+"");
                    myIntent.putExtra("note", note+"");
                    myIntent.putExtra("comment", comment+"");
                    myIntent.putExtra("empName", empName);
                    if (rfidCode.isEnabled()) {
                        myIntent.putExtra("rfidCode", rfidCde + "");
                    }
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
        unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    quan_val = quantity.getText().toString();
                    conc_val = concentration.getText().toString();
                    note = notes.getText().toString();
                    comment = comments.getText().toString();
                    ArrayList<MyObject> unitList = databaseHandler.getUnitList(db);
                    final Intent myIntent = new Intent(ContainerDetailsActivity.this,
                            UnitsList.class);
                    myIntent.putExtra("user_id", selectedUserId);
                    myIntent.putExtra("site_id", loggedinUserSiteId);
                    myIntent.putExtra("token", token);
                    myIntent.putExtra("sso", sso);
                    myIntent.putExtra("md5pwd", md5Pwd);
                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                    myIntent.putExtra("site_name", site_name);
                    myIntent.putExtra("unitList",unitList);
                    myIntent.putExtra("decodedData", decodedData);
                    myIntent.putExtra("selectedRoomName", selectedRoomName);
                    myIntent.putExtra("selectedRoom", selectedRoom+"");
                    myIntent.putExtra("selectedStatusName", selectedStatusName);
                    myIntent.putExtra("selectedStatus", selectedStatus+"");
                    myIntent.putExtra("selectedConcUnitName", selectedConcUnitName);
                    myIntent.putExtra("selectedConcUnit", selectedConcUnit+"");
                    myIntent.putExtra("selectedQuanUnitName", selectedQuanUnitName);
                    myIntent.putExtra("selectedQuanUnit", selectedQuanUnit+"");
                    myIntent.putExtra("selectedOwnerName", selectedOwnerName);
                    myIntent.putExtra("selectedOwner", selectedOwner+"");
                    myIntent.putExtra("empName", empName);
                    myIntent.putExtra("quan_val", quan_val+"");
                    myIntent.putExtra("conc_val", conc_val+"");
                    myIntent.putExtra("note", note+"");
                    myIntent.putExtra("comment", comment+"");
                    myIntent.putExtra("fromUnit", "yes");
                    if (rfidCode.isEnabled()) {
                        myIntent.putExtra("rfidCode", rfidCde + "");
                    }
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
        concentrationUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    quan_val = quantity.getText().toString();
                    conc_val = concentration.getText().toString();
                    note = notes.getText().toString();
                    comment = comments.getText().toString();
                    ArrayList<MyObject> unitList = databaseHandler.getUnitList(db);
                    final Intent myIntent = new Intent(ContainerDetailsActivity.this,
                            UnitsList.class);
                    myIntent.putExtra("user_id", selectedUserId);
                    myIntent.putExtra("site_id", loggedinUserSiteId);
                    myIntent.putExtra("token", token);
                    myIntent.putExtra("sso", sso);
                    myIntent.putExtra("md5pwd", md5Pwd);
                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                    myIntent.putExtra("site_name", site_name);
                    myIntent.putExtra("unitList",unitList);
                    myIntent.putExtra("decodedData", decodedData);
                    myIntent.putExtra("selectedRoomName", selectedRoomName);
                    myIntent.putExtra("selectedRoom", selectedRoom+"");
                    myIntent.putExtra("selectedStatusName", selectedStatusName);
                    myIntent.putExtra("selectedStatus", selectedStatus+"");
                    myIntent.putExtra("selectedConcUnitName", selectedConcUnitName);
                    myIntent.putExtra("selectedConcUnit", selectedConcUnit+"");
                    myIntent.putExtra("selectedQuanUnitName", selectedQuanUnitName);
                    myIntent.putExtra("selectedQuanUnit", selectedQuanUnit+"");
                    myIntent.putExtra("selectedOwnerName", selectedOwnerName);
                    myIntent.putExtra("selectedOwner", selectedOwner+"");
                    myIntent.putExtra("quan_val", quan_val+"");
                    myIntent.putExtra("conc_val", conc_val+"");
                    myIntent.putExtra("note", note+"");
                    myIntent.putExtra("comment", comment+"");
                    myIntent.putExtra("empName", empName);
                    if (rfidCode.isEnabled()) {
                        myIntent.putExtra("rfidCode", rfidCde + "");
                    }
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
        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    quan_val = quantity.getText().toString();
                    conc_val = concentration.getText().toString();
                    note = notes.getText().toString();
                    comment = comments.getText().toString();
                    ArrayList<MyObject> ownerList = databaseHandler.getOwnerList(db);
                    final Intent myIntent = new Intent(ContainerDetailsActivity.this,
                            OwnerList.class);
                    myIntent.putExtra("user_id", selectedUserId);
                    myIntent.putExtra("site_id", loggedinUserSiteId);
                    myIntent.putExtra("token", token);
                    myIntent.putExtra("sso", sso);
                    myIntent.putExtra("md5pwd", md5Pwd);
                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                    myIntent.putExtra("site_name", site_name);
                    myIntent.putExtra("ownerList",ownerList);
                    myIntent.putExtra("decodedData", decodedData);
                    myIntent.putExtra("selectedRoomName", selectedRoomName);
                    myIntent.putExtra("selectedRoom", selectedRoom+"");
                    myIntent.putExtra("selectedStatusName", selectedStatusName);
                    myIntent.putExtra("selectedStatus", selectedStatus+"");
                    myIntent.putExtra("selectedConcUnitName", selectedConcUnitName);
                    myIntent.putExtra("selectedConcUnit", selectedConcUnit+"");
                    myIntent.putExtra("selectedQuanUnitName", selectedQuanUnitName);
                    myIntent.putExtra("selectedQuanUnit", selectedQuanUnit+"");
                    myIntent.putExtra("selectedOwnerName", selectedOwnerName);
                    myIntent.putExtra("selectedOwner", selectedOwner+"");
                    myIntent.putExtra("quan_val", quan_val+"");
                    myIntent.putExtra("conc_val", conc_val+"");
                    myIntent.putExtra("note", note+"");
                    myIntent.putExtra("comment", comment+"");
                    myIntent.putExtra("empName", empName);
                    if (rfidCode.isEnabled()) {
                        myIntent.putExtra("rfidCode", rfidCde + "");
                    }
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
        uploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                quan_val = quantity.getText().toString();
                conc_val = concentration.getText().toString();
                note = notes.getText().toString();
                comment = comments.getText().toString();
                rfidCde = rfidCode.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put("code", code.getText().toString());
                if(selectedRoom.trim().length()==0||selectedRoom==null||selectedRoom=="null"){
                    selectedRoom = "-1";
                }
                if(selectedOwner.trim().length()==0||selectedOwner==null||selectedOwner=="null"){
                    selectedOwner = "-1";
                }
                if(selectedStatus.trim().length()==0||selectedStatus==null||selectedStatus=="null"){
                    selectedStatus = "-1";
                }
                cv.put("room_id", Integer.parseInt(selectedRoom));
                cv.put("object_id", Integer.parseInt(selectedOwner));
                cv.put("object_table", "site_users");
                cv.put("owner", selectedOwnerName);
                cv.put("room", selectedRoomName);
                cv.put("status_id", Integer.parseInt(selectedStatus));
                cv.put("status", selectedStatusName);
                cv.put("notes", note);
                cv.put("comment", comment);
                cv.put("quantity_unit_abbreviation", selectedQuanUnitName);
                cv.put("concentration_unit_abbrevation", selectedConcUnitName);
                if(quan_val!=null) {
                    if (quan_val.trim().length() > 0 && quan_val != "null") {
                        cv.put("quantity", Float.parseFloat(quan_val));
                    }
                    else {
                        cv.put("quantity", -1);
                        quan_val = "-1";
                    }
                }else{
                    cv.put("quantity", -1);
                    quan_val = "-1";
                }
                if(rfidCde!=null) {
                    if (rfidCde.trim().length() > 0 && rfidCde != "null") {
                        cv.put("sec_code", rfidCde);
                    }
                    else {
                        cv.put("sec_code", "");
                        rfidCde = "";
                    }
                }else{
                    cv.put("sec_code", "");
                    rfidCde = "";
                }
                if(conc_val!=null) {
                    if (conc_val.trim().length() > 0 && conc_val != "null") {
                        cv.put("concentration", Float.parseFloat(conc_val));
                    }
                    else {
                        cv.put("concentration", -1);
                        conc_val = "-1";
                    }
                }else{
                    cv.put("concentration",-1);
                    conc_val = "-1";
                }
                if(selectedQuanUnit!=null) {
                    if (selectedQuanUnit.trim().length() > 0 && selectedQuanUnit != "null")
                        cv.put("quantity_unit_abbreviation_id", Integer.parseInt(selectedQuanUnit));
                    else {
                        cv.put("quantity_unit_abbreviation_id", -1);
                        selectedQuanUnit = "-1";
                    }
                }else{
                    cv.put("quantity_unit_abbreviation_id", -1);
                    selectedQuanUnit = "-1";
                }
                if(selectedConcUnit!=null) {
                    if (selectedConcUnit.trim().length() > 0 && selectedConcUnit != "null")
                        cv.put("concentration_unit_abbrevation_id", Integer.parseInt(selectedConcUnit));
                    else {
                        cv.put("concentration_unit_abbrevation_id", -1);
                        selectedConcUnit = "-1";
                    }
                }else{
                    cv.put("concentration_unit_abbrevation_id", -1);
                    selectedConcUnit = "-1";
                }
                //Log.e("^^^^^^",rfidCde);
                databaseHandler.updateInventoryDetails(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                InventoryModel inv = databaseHandler.getScannedInventoryDetails(db,code.getText().toString(),"");
                BracodeScanAPIObject obj = new BracodeScanAPIObject(
                        selectedUserId,token,loggedinUserSiteId,code.getText().toString(),
                        selectedStatus,selectedRoom,note,comment, quan_val, selectedQuanUnit, selectedConcUnit, conc_val,selectedOwner,"site_users",rfidCde
                );
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = "";
                try {
                    jsonString = mapper.writeValueAsString(obj);
                    ContentValues cv_save = new ContentValues();
                    cv_save.put("code", code.getText().toString());
                    cv_save.put("user_id", Integer.parseInt(selectedUserId));
                    cv_save.put("location_id", Integer.parseInt(selectedFacil));
                    cv_save.put("room_id", Integer.parseInt(selectedRoom));
                    cv_save.put("scan_type", "barcode");
                    cv_save.put("json_data", jsonString);
                    databaseHandler.insertScannedBarcodeInvJSONData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv_save);
                    if (connected) {
                        ArrayList<MyObject> jsonList = databaseHandler.getSavedJsonDataBarcodeUpdate(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                        //SyncInventory sdb = new SyncInventory();
                        //sdb.execute(jsonList);
                        uploadInventoryData(jsonList);
                        /*String URL = ApiConstants.syncbarcodeScannedData;
                        RequestQueue requestQueue = Volley.newRequestQueue(ContainerDetailsActivity.this);
                        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(jsonString),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Process os success response
                                        String res = response.toString();
                                        Log.e("res from complete>>", res + "**");
                                        databaseHandler.deleteBarcodeInventoryDetails(db,code.getText().toString());
                                        final Intent myIntent = new Intent(ContainerDetailsActivity.this,
                                                PostSuccess.class);
                                        myIntent.putExtra("user_id", selectedUserId);
                                        myIntent.putExtra("site_id", loggedinUserSiteId);
                                        myIntent.putExtra("token", token);
                                        myIntent.putExtra("sso", sso);
                                        myIntent.putExtra("md5pwd", md5Pwd);
                                        myIntent.putExtra("loggedinUsername", loggedinUsername);
                                        myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                                        myIntent.putExtra("site_name", site_name);
                                        myIntent.putExtra("empName", empName);
                                        myIntent.putExtra("fromBarcodeScan", "yes");
                                        startActivity(myIntent);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ContainerDetailsActivity.this);
                                dlgAlert.setTitle("Safety Stratus");
                                dlgAlert.setMessage("Error response: Request timed out! Your data is saved offline");
                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                final Intent myIntent = new Intent(ContainerDetailsActivity.this,
                                                        PostSuccess.class);
                                                myIntent.putExtra("user_id", selectedUserId);
                                                myIntent.putExtra("site_id", loggedinUserSiteId);
                                                myIntent.putExtra("token", token);
                                                myIntent.putExtra("sso", sso);
                                                myIntent.putExtra("md5pwd", md5Pwd);
                                                myIntent.putExtra("loggedinUsername", loggedinUsername);
                                                myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                                                myIntent.putExtra("site_name", site_name);
                                                myIntent.putExtra("empName", empName);
                                                myIntent.putExtra("fromBarcodeScan", "yes");
                                                startActivity(myIntent);
                                                return;
                                            }
                                        });
                                dlgAlert.create().show();
                            }
                        });
                        int socketTimeout = 60000;//30 seconds - change to what you want
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
                        request_json.setRetryPolicy(policy);
                        // add the request object to the queue to be executed
                        requestQueue.add(request_json);*/
                    }
                    else{
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ContainerDetailsActivity.this);
                        dlgAlert.setTitle("Safety Stratus");
                        dlgAlert.setMessage("No Internet!! Your data is saved offline");
                        String finalJsonString1 = jsonString;
                        dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                final Intent myIntent = new Intent(ContainerDetailsActivity.this,
                                                        PostSuccess.class);
                                                myIntent.putExtra("user_id", selectedUserId);
                                                myIntent.putExtra("site_id", loggedinUserSiteId);
                                                myIntent.putExtra("token", token);
                                                myIntent.putExtra("sso", sso);
                                                myIntent.putExtra("md5pwd", md5Pwd);
                                                myIntent.putExtra("loggedinUsername", loggedinUsername);
                                                myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                                                myIntent.putExtra("site_name", site_name);
                                                myIntent.putExtra("empName", empName);
                                                myIntent.putExtra("fromBarcodeScan", "yes");
                                                startActivity(myIntent);
                                                return;
                                            }
                                        });
                                dlgAlert.create().show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                quan_val = quantity.getText().toString();
                conc_val = concentration.getText().toString();
                note = notes.getText().toString();
                comment = comments.getText().toString();
                rfidCde = rfidCode.getText().toString();
                if(selectedRoom.trim().length()==0||selectedRoom==null||selectedRoom=="null"){
                    selectedRoom = "-1";
                }
                if(selectedOwner.trim().length()==0||selectedOwner==null||selectedOwner=="null"){
                    selectedOwner = "-1";
                }
                if(selectedStatus.trim().length()==0||selectedStatus==null||selectedStatus=="null"){
                    selectedStatus = "-1";
                }
                ContentValues cv = new ContentValues();
                cv.put("code", code.getText().toString());
                cv.put("room_id", Integer.parseInt(selectedRoom));
                cv.put("object_id", Integer.parseInt(selectedOwner));
                cv.put("object_table", "site_users");
                cv.put("owner", selectedOwnerName);
                cv.put("room", selectedRoomName);
                cv.put("status_id", Integer.parseInt(selectedStatus));
                cv.put("status", selectedStatusName);
                cv.put("notes", note);
                cv.put("comment", comment);
                cv.put("quantity_unit_abbreviation", selectedQuanUnitName);
                cv.put("concentration_unit_abbrevation", selectedConcUnitName);
                if(quan_val!=null) {
                    if (quan_val.trim().length() > 0 && quan_val != "null") {
                        cv.put("quantity", Float.parseFloat(quan_val));
                    }
                    else {
                        cv.put("quantity", -1);
                        quan_val = "-1";
                    }
                }else{
                    cv.put("quantity", -1);
                    quan_val = "-1";
                }
                if(rfidCde!=null) {
                    if (rfidCde.trim().length() > 0 && rfidCde != "null") {
                        cv.put("sec_code", rfidCde);
                    }
                    else {
                        cv.put("sec_code", "");
                        rfidCde = "";
                    }
                }else{
                    cv.put("sec_code", "");
                    rfidCde = "";
                }
                if(conc_val!=null) {
                    if (conc_val.trim().length() > 0 && conc_val != "null") {
                        cv.put("concentration", Float.parseFloat(conc_val));
                    }
                    else {
                        cv.put("concentration", -1);
                        conc_val = "-1";
                    }
                }else{
                    cv.put("concentration",-1);
                    conc_val = "-1";
                }
                if(selectedQuanUnit!=null) {
                    if (selectedQuanUnit.trim().length() > 0 && selectedQuanUnit != "null")
                        cv.put("quantity_unit_abbreviation_id", Integer.parseInt(selectedQuanUnit));
                    else {
                        cv.put("quantity_unit_abbreviation_id", -1);
                        selectedQuanUnit = "-1";
                    }
                }else{
                    cv.put("quantity_unit_abbreviation_id", -1);
                    selectedQuanUnit = "-1";
                }
                if(selectedConcUnit!=null) {
                    if (selectedConcUnit.trim().length() > 0 && selectedConcUnit != "null")
                        cv.put("concentration_unit_abbrevation_id", Integer.parseInt(selectedConcUnit));
                    else {
                        cv.put("concentration_unit_abbrevation_id", -1);
                        selectedConcUnit = "-1";
                    }
                }else{
                    cv.put("concentration_unit_abbrevation_id", -1);
                    selectedConcUnit = "-1";
                }
                databaseHandler.updateInventoryDetails(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv);
                InventoryModel inv = databaseHandler.getScannedInventoryDetails(db,code.getText().toString(),"");
                BracodeScanAPIObject obj = new BracodeScanAPIObject(
                        selectedUserId,token,loggedinUserSiteId,code.getText().toString(),
                        selectedStatus,selectedRoom,note,comment, quan_val, selectedQuanUnit, selectedConcUnit, conc_val, selectedOwner,"site_users",rfidCde
                );
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = "";
                try {
                    jsonString = mapper.writeValueAsString(obj);
                    ContentValues cv_save = new ContentValues();
                    cv_save.put("code", code.getText().toString());
                    cv_save.put("user_id", Integer.parseInt(selectedUserId));
                    cv_save.put("location_id", Integer.parseInt(selectedFacil));
                    cv_save.put("room_id", Integer.parseInt(selectedRoom));
                    cv_save.put("scan_type", "barcode");
                    cv_save.put("json_data", jsonString);
                    databaseHandler.insertScannedBarcodeInvJSONData(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), cv_save);
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ContainerDetailsActivity.this);
                    dlgAlert.setTitle("Safety Stratus");
                    dlgAlert.setMessage("Data saved successfully!");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int scannedJsonData = databaseHandler.getSavedBarcodeDataCount(databaseHandler.getWritableDatabase(PASS_PHRASE));
                                    if(scannedJsonData>0){
                                        badge_notification.setVisibility(View.VISIBLE);
                                        badge_notification.setText(String.valueOf(scannedJsonData));
                                    }else{
                                        badge_notification.setVisibility(View.GONE);
                                        badge_notification.setText("");
                                    }
                                    return;
                                }
                            });
                    dlgAlert.create().show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public void uploadInventoryData(ArrayList<MyObject> jsonList){
        try {
            ProgressDialog progressSync = new ProgressDialog(ContainerDetailsActivity.this);
            progressSync.setTitle("");
            progressSync.setMessage("Uploading..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(400, 200);
            RequestQueue requestQueue = Volley.newRequestQueue(ContainerDetailsActivity.this);
            for (int k=0;k<jsonList.size();k++){
                int finalK = k;
                String URL = "https://"+host+ApiConstants.syncbarcodeScannedData;
                JSONObject obj = new JSONObject(jsonList.get(k).getObjectName());
                String reconc_id = "-4";
                if (obj.has("reconc_id")) {
                    reconc_id = obj.getString("reconc_id");
                }
                String finalReconc_id = reconc_id;
                JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(jsonList.get(k).getObjectName()),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Process os success response
                                String res = response.toString();
                                databaseHandler.delSavedScanDatabyId(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), jsonList.get(finalK).getObjectId(),finalReconc_id);
                                ArrayList<MyObject> jsonListModified = databaseHandler.getSavedJsonDataBarcodeUpdate(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE));
                                if (jsonListModified.size()==0){
                                    progressSync.dismiss();
                                    final Intent myIntent = new Intent(ContainerDetailsActivity.this,
                                            PostSuccess.class);
                                    myIntent.putExtra("user_id", selectedUserId);
                                    myIntent.putExtra("site_id", loggedinUserSiteId);
                                    myIntent.putExtra("token", token);
                                    myIntent.putExtra("sso", sso);
                                    myIntent.putExtra("md5pwd", md5Pwd);
                                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                                    myIntent.putExtra("site_name", site_name);
                                    myIntent.putExtra("empName", empName);
                                    myIntent.putExtra("fromBarcodeScan", "yes");
                                    startActivity(myIntent);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressSync.dismiss();
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ContainerDetailsActivity.this);
                        dlgAlert.setTitle("Safety Stratus");
                        dlgAlert.setMessage("Error response: Request timed out! Your data is saved offline");
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                    }
                                });
                    }
                });
                int socketTimeout = 60000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
                request_json.setRetryPolicy(policy);
                // add the request object to the queue to be executed
                requestQueue.add(request_json);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        //}
        //});
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