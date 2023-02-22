package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

public class RoomList extends AppCompatActivity {
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;
    boolean connected = false;
    String loggedinUsername = "";
    String loggedinUserSiteId = "";
    String md5Pwd = "";
    String user_id = "";
    String selectedSearchValue = "";
    String lastCompletedInspectionSiteInfo = "";
    String sso = "";
    String site_name = "";
    String request_token="";
    String selectedFacilName = "";
    String selectedFacil = "";
    String selectedRoomName = "";
    String selectedRoom = "";
    String fromBulkUpdate = "";
    String note = "";
    String comment="";
    String conc_val="";
    String quan_val="";
    String empName = "";
    String decodedData = "";
    String selectedStatusName = "";
    String selectedStatus = "";
    ArrayList<MyObject> roomlist=null;
    ConstraintLayout header;
    EditText roomSearch;
    String selectedConcUnitName = "";
    String selectedConcUnit = "";
    String selectedQuanUnitName = "";
    String selectedQuanUnit = "";
    String selectedOwnerName = "";
    String selectedOwner = "";
    ArrayList<String> codelistfromIntent=null;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
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
        tv.setText("Rooms");
        tv.setTextSize(20);
        tv.setVisibility(View.VISIBLE);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RoomList.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        Intent intent = getIntent();
        sso = intent.getStringExtra("sso");
        if (intent.getStringExtra("token") != null) {
            request_token = intent.getStringExtra("token");
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
        if(intent.getStringExtra("comment")!=null) {
            comment = intent.getStringExtra("comment");
        }
        if(intent.getStringExtra("conc_val")!=null) {
            conc_val = intent.getStringExtra("conc_val");
        }
        if(intent.getStringExtra("quan_val")!=null) {
            quan_val = intent.getStringExtra("quan_val");
        }
        if(intent.getStringExtra("fromBulkUpdate")!=null) {
            fromBulkUpdate = intent.getStringExtra("fromBulkUpdate");
        }
        codelistfromIntent = new ArrayList<String>();
        if(intent.getSerializableExtra("codelistfromIntent")!=null)
            codelistfromIntent = (ArrayList<String>) intent.getSerializableExtra("codelistfromIntent");
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("loggedinUsername");
        user_id = intent.getStringExtra("user_id");
        loggedinUserSiteId = intent.getStringExtra("site_id");
        lastCompletedInspectionSiteInfo = loggedinUserSiteId;
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
        if (intent.getStringExtra("selectedStatusName") != null) {
            selectedStatusName = intent.getStringExtra("selectedStatusName");
        }
        if (intent.getStringExtra("selectedStatus") != null) {
            selectedStatus = intent.getStringExtra("selectedStatus");
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
        roomlist = new ArrayList<MyObject>();
        if(intent.getSerializableExtra("roomlist")!=null)
            roomlist = (ArrayList<MyObject>) intent.getSerializableExtra("roomlist");
        TableLayout tableRooms = (TableLayout) findViewById(R.id.tableRooms);
        roomSearch = (EditText) findViewById(R.id.room_search);
        for (int i = 0; i < roomlist.size(); i++) {
            final TextView roomName = new TextView(this);
            roomName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    100,5));
            roomName.setGravity(Gravity.LEFT);
            roomName.setPadding(5, 30, 0, 0);
            roomName.setBackgroundResource(R.drawable.cell_shape_child);
            roomName.setText(roomlist.get(i).getObjectName());
            roomName.setId(Integer.parseInt(roomlist.get(i).getObjectId()));
            roomName.setTextSize(16);
            roomName.setTextColor(Color.parseColor("#000000"));
            roomName.setBackgroundColor(Color.parseColor("#FFFFFF"));
            if(selectedRoom.length()>0){
                if(Integer.parseInt(roomlist.get(i).getObjectId()) == Integer.parseInt(selectedRoom)){
                    Drawable img = getResources().getDrawable(R.drawable.ic_icons8_checkmark);
                    img.setBounds(0, 0, 60, 60);
                    roomName.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                }
            }
            final TableRow trRoom = new TableRow(this);
            trRoom.setId(Integer.parseInt(roomlist.get(i).getObjectId()));
            TableLayout.LayoutParams trParamsRosters = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trRoom.setBackgroundResource(R.drawable.table_tr_border);
            //trParams.setMargins(10, 10, 10, 10);
            trRoom.setLayoutParams(trParamsRosters);
            trRoom.addView(roomName);
            tableRooms.addView(trRoom, trParamsRosters);
            roomName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int p = 0; p < tableRooms.getChildCount(); p++) {
                        View o1 = tableRooms.getChildAt(p);
                        if (o1 instanceof TableRow) {
                            for (int j=0;j<((TableRow) o1).getChildCount();j++){
                                View u1 = ((TableRow) o1).getChildAt(j);
                                Boolean match =false;
                                if (u1 instanceof TextView) {
                                    ((TextView)u1).setCompoundDrawablesWithIntrinsicBounds( null, null, null, null );
                                }
                            }
                        }
                    }
                    Drawable img = getResources().getDrawable(R.drawable.ic_icons8_checkmark);
                    img.setBounds(0, 0, 60, 60);
                    roomName.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    Intent myIntent = null;
                    if(decodedData.trim().length()>0){
                        myIntent = new Intent(RoomList.this, ContainerDetailsActivity.class);
                    }else if(fromBulkUpdate.trim().length()>0){
                        myIntent = new Intent(RoomList.this,
                                BulkContainerUpdate.class);
                    }else{
                        myIntent = new Intent(RoomList.this,
                                RFIDActivity.class);
                    }
                    myIntent.putExtra("selectedFacilName", selectedFacilName);
                    myIntent.putExtra("selectedStatusName", selectedStatusName);
                    myIntent.putExtra("selectedStatus", selectedStatus);
                    myIntent.putExtra("selectedRoomName", roomName.getText());
                    myIntent.putExtra("selectedRoom", roomName.getId()+"");
                    myIntent.putExtra("decodedData", decodedData+"");
                    myIntent.putExtra("selectedFacil", selectedFacil+"");
                    myIntent.putExtra("user_id", user_id);
                    myIntent.putExtra("site_id", loggedinUserSiteId);
                    myIntent.putExtra("token", request_token);
                    myIntent.putExtra("sso", sso);
                    myIntent.putExtra("md5pwd", md5Pwd);
                    myIntent.putExtra("loggedinUsername", loggedinUsername);
                    myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                    myIntent.putExtra("codelistfromIntent", codelistfromIntent);
                    myIntent.putExtra("site_name", site_name);
                    myIntent.putExtra("fromRoom", "fromRoom");
                    myIntent.putExtra("roomlist",roomlist);
                    myIntent.putExtra("pageLoadTemp", "-1");
                    myIntent.putExtra("empName", empName);
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
                    startActivity(myIntent);
                }
            });
        }
        roomSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                // if(s.length() != 0)
                //{
                MyObject[] myObjects = null;
                myObjects = getItemsFromDb(String.valueOf(s));
                tableRooms.removeAllViews();
                for (int i = 0;i<myObjects.length;i++) {
                    final TextView roomName = new TextView(RoomList.this);
                    roomName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            100, 5));
                    roomName.setGravity(Gravity.LEFT);
                    roomName.setPadding(5, 30, 0, 0);
                    roomName.setBackgroundResource(R.drawable.cell_shape_child);
                    roomName.setText(myObjects[i].getObjectName());
                    roomName.setId(Integer.parseInt(myObjects[i].getObjectId()));
                    roomName.setTextSize(16);
                    roomName.setTextColor(Color.parseColor("#000000"));
                    roomName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    if (selectedRoom.length() > 0) {
                        if (Integer.parseInt(myObjects[i].getObjectId()) == Integer.parseInt(selectedRoom)) {
                            Drawable img = getResources().getDrawable(R.drawable.ic_icons8_checkmark);
                            img.setBounds(0, 0, 60, 60);
                            roomName.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                        }
                    }
                    final TableRow trRoom = new TableRow(RoomList.this);
                    trRoom.setId(Integer.parseInt(myObjects[i].getObjectId()));
                    TableLayout.LayoutParams trParamsRosters = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    trRoom.setBackgroundResource(R.drawable.table_tr_border);
                    //trParams.setMargins(10, 10, 10, 10);
                    trRoom.setLayoutParams(trParamsRosters);
                    trRoom.addView(roomName);
                    tableRooms.addView(trRoom, trParamsRosters);
                    roomName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int p = 0; p < tableRooms.getChildCount(); p++) {
                                View o1 = tableRooms.getChildAt(p);
                                if (o1 instanceof TableRow) {
                                    for (int j = 0; j < ((TableRow) o1).getChildCount(); j++) {
                                        View u1 = ((TableRow) o1).getChildAt(j);
                                        Boolean match = false;
                                        if (u1 instanceof TextView) {
                                            ((TextView) u1).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                        }
                                    }
                                }
                            }
                            Drawable img = getResources().getDrawable(R.drawable.ic_icons8_checkmark);
                            img.setBounds(0, 0, 60, 60);
                            roomName.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                            Intent myIntent = null;
                            if(decodedData.trim().length()>0){
                                myIntent = new Intent(RoomList.this, ContainerDetailsActivity.class);
                            }
                            else if(fromBulkUpdate.trim().length()>0){
                                myIntent = new Intent(RoomList.this,
                                        BulkContainerUpdate.class);
                            }
                            else{
                                myIntent = new Intent(RoomList.this,
                                        RFIDActivity.class);
                            }
                            myIntent.putExtra("selectedFacilName", selectedFacilName);
                            myIntent.putExtra("selectedStatusName", selectedStatusName);
                            myIntent.putExtra("selectedStatus", selectedStatus);
                            myIntent.putExtra("selectedRoomName", roomName.getText());
                            myIntent.putExtra("selectedRoom", roomName.getId()+"");
                            myIntent.putExtra("selectedFacil", selectedFacil+"");
                            myIntent.putExtra("decodedData", decodedData+"");
                            myIntent.putExtra("user_id", user_id);
                            myIntent.putExtra("site_id", loggedinUserSiteId);
                            myIntent.putExtra("token", request_token);
                            myIntent.putExtra("sso", sso);
                            myIntent.putExtra("md5pwd", md5Pwd);
                            myIntent.putExtra("loggedinUsername", loggedinUsername);
                            myIntent.putExtra("codelistfromIntent", codelistfromIntent);
                            myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                            myIntent.putExtra("site_name", site_name);
                            myIntent.putExtra("fromRoom", "fromRoom");
                            myIntent.putExtra("roomlist",roomlist);
                            myIntent.putExtra("pageLoadTemp", "-1");
                            myIntent.putExtra("empName", empName);
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
                            startActivity(myIntent);
                        }
                    });
                }
                //}
            }
        });
    }
    @Override
    public void onBackPressed() {
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent myIntent = null;
            if(decodedData.trim().length()>0){
                myIntent = new Intent(RoomList.this, ContainerDetailsActivity.class);
            }
            else if(fromBulkUpdate.trim().length()>0){
                myIntent = new Intent(RoomList.this,
                        BulkContainerUpdate.class);
            }
            else{
                myIntent = new Intent(RoomList.this,
                        RFIDActivity.class);
            }
            myIntent.putExtra("selectedFacilName", selectedFacilName);
            myIntent.putExtra("selectedStatusName", selectedStatusName);
            myIntent.putExtra("selectedStatus", selectedStatus);
            myIntent.putExtra("selectedFacil", selectedFacil+"");
            myIntent.putExtra("decodedData", decodedData+"");
            myIntent.putExtra("user_id", user_id);
            myIntent.putExtra("site_id", loggedinUserSiteId);
            myIntent.putExtra("token", request_token);
            myIntent.putExtra("sso", sso);
            myIntent.putExtra("md5pwd", md5Pwd);
            myIntent.putExtra("loggedinUsername", loggedinUsername);
            myIntent.putExtra("selectedSearchValue", selectedSearchValue);
            myIntent.putExtra("site_name", site_name);
            myIntent.putExtra("fromFacil", "fromFacil");
            myIntent.putExtra("roomlist",roomlist);
            myIntent.putExtra("pageLoadTemp", "-1");
            myIntent.putExtra("selectedRoomName", selectedRoomName);
            myIntent.putExtra("selectedRoom", selectedRoom+"");
            myIntent.putExtra("empName", empName);
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
            myIntent.putExtra("codelistfromIntent", codelistfromIntent);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }
    public MyObject[] getItemsFromDb(String searchTerm) {
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(RoomList.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        MyObject[] myObject = null;
        try {
            myObject = databaseHandler.getAutoSearchStatusData(db, searchTerm);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            if (databaseHandler != null) {
                databaseHandler.close();
            }
        }
        return myObject;
    }
}