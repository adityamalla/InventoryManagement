package com.safetystratus.inventorymanagement;

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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

public class StatusList extends AppCompatActivity {
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
        String selectedStatusName = "";
        String selectedStatus = "";
        String empName = "";
        String note = "";
        String comment="";
        String conc_val="";
        String quan_val="";
        String decodedData = "";
        ArrayList<MyObject> statusList=null;
        ConstraintLayout header;
        EditText statusSearch;
        String selectedConcUnitName = "";
        String selectedConcUnit = "";
        String selectedQuanUnitName = "";
        String selectedQuanUnit = "";
        String selectedOwnerName = "";
        String selectedOwner = "";
        @SuppressLint("WrongConstant")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_status_list);
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
            tv.setText("Status");
            tv.setTextSize(20);
            tv.setVisibility(View.VISIBLE);
            final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(StatusList.this);
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
            if(intent.getStringExtra("selectedStatusName")!=null) {
                selectedStatusName = intent.getStringExtra("selectedStatusName");
            }
            if(intent.getStringExtra("selectedStatus")!=null) {
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
            if (intent.getStringExtra("selectedOwner") != null) {
                selectedOwner = intent.getStringExtra("selectedOwner");
            }
            if (intent.getStringExtra("selectedOwnerName") != null) {
                selectedOwnerName = intent.getStringExtra("selectedOwnerName");
            }
            statusList = new ArrayList<MyObject>();
            if(intent.getSerializableExtra("statusList")!=null)
                statusList = (ArrayList<MyObject>) intent.getSerializableExtra("statusList");
            TableLayout tableStatus = (TableLayout) findViewById(R.id.tableStatus);
            statusSearch = (EditText) findViewById(R.id.status_search);
            for (int i = 0; i < statusList.size(); i++) {
                final TextView statusName = new TextView(this);
                statusName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        100,5));
                statusName.setGravity(Gravity.LEFT);
                statusName.setPadding(5, 30, 0, 0);
                statusName.setBackgroundResource(R.drawable.cell_shape_child);
                statusName.setText(statusList.get(i).getObjectName());
                statusName.setId(Integer.parseInt(statusList.get(i).getObjectId()));
                statusName.setTextSize(16);
                statusName.setTextColor(Color.parseColor("#000000"));
                statusName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                if(selectedStatus.trim().length()>0){
                    if(Integer.parseInt(statusList.get(i).getObjectId()) == Integer.parseInt(selectedStatus)){
                        Drawable img = getResources().getDrawable(R.drawable.ic_icons8_checkmark);
                        img.setBounds(0, 0, 60, 60);
                        statusName.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    }
                }
                final TableRow trStatus = new TableRow(this);
                trStatus.setId(Integer.parseInt(statusList.get(i).getObjectId()));
                TableLayout.LayoutParams trParamsRosters = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trStatus.setBackgroundResource(R.drawable.table_tr_border);
                //trParams.setMargins(10, 10, 10, 10);
                trStatus.setLayoutParams(trParamsRosters);
                trStatus.addView(statusName);
                tableStatus.addView(trStatus, trParamsRosters);
                statusName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int p = 0; p < tableStatus.getChildCount(); p++) {
                            View o1 = tableStatus.getChildAt(p);
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
                        statusName.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                        Intent myIntent = new Intent(StatusList.this, ContainerDetailsActivity.class);
                        myIntent.putExtra("selectedFacilName", selectedFacilName);
                        myIntent.putExtra("selectedRoomName", selectedRoomName);
                        myIntent.putExtra("selectedRoom", selectedRoom+"");
                        myIntent.putExtra("selectedStatusName", statusName.getText());
                        myIntent.putExtra("selectedStatus", statusName.getId()+"");
                        myIntent.putExtra("decodedData", decodedData+"");
                        myIntent.putExtra("selectedFacil", selectedFacil+"");
                        myIntent.putExtra("user_id", user_id);
                        myIntent.putExtra("site_id", loggedinUserSiteId);
                        myIntent.putExtra("token", request_token);
                        myIntent.putExtra("sso", sso);
                        myIntent.putExtra("md5pwd", md5Pwd);
                        myIntent.putExtra("loggedinUsername", loggedinUsername);
                        myIntent.putExtra("selectedSearchValue", selectedSearchValue);
                        myIntent.putExtra("site_name", site_name);
                        myIntent.putExtra("fromRoom", "fromRoom");
                        myIntent.putExtra("statusList",statusList);
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
            statusSearch.addTextChangedListener(new TextWatcher() {

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
                    tableStatus.removeAllViews();
                    for (int i = 0;i<myObjects.length;i++) {
                        final TextView statusName = new TextView(StatusList.this);
                        statusName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                100, 5));
                        statusName.setGravity(Gravity.LEFT);
                        statusName.setPadding(5, 30, 0, 0);
                        statusName.setBackgroundResource(R.drawable.cell_shape_child);
                        statusName.setText(myObjects[i].getObjectName());
                        statusName.setId(Integer.parseInt(myObjects[i].getObjectId()));
                        statusName.setTextSize(16);
                        statusName.setTextColor(Color.parseColor("#000000"));
                        statusName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        if (selectedStatus.length() > 0) {
                            if (Integer.parseInt(myObjects[i].getObjectId()) == Integer.parseInt(selectedStatus)) {
                                Drawable img = getResources().getDrawable(R.drawable.ic_icons8_checkmark);
                                img.setBounds(0, 0, 60, 60);
                                statusName.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                            }
                        }
                        final TableRow trStatus = new TableRow(StatusList.this);
                        trStatus.setId(Integer.parseInt(myObjects[i].getObjectId()));
                        TableLayout.LayoutParams trParamsRosters = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT);
                        trStatus.setBackgroundResource(R.drawable.table_tr_border);
                        //trParams.setMargins(10, 10, 10, 10);
                        trStatus.setLayoutParams(trParamsRosters);
                        trStatus.addView(statusName);
                        tableStatus.addView(trStatus, trParamsRosters);
                        statusName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (int p = 0; p < tableStatus.getChildCount(); p++) {
                                    View o1 = tableStatus.getChildAt(p);
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
                                statusName.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                                Intent myIntent = new Intent(StatusList.this, ContainerDetailsActivity.class);
                                myIntent.putExtra("selectedFacilName", selectedFacilName);
                                myIntent.putExtra("selectedRoomName", selectedRoomName);
                                myIntent.putExtra("selectedRoom", selectedRoom+"");
                                myIntent.putExtra("selectedStatusName", statusName.getText());
                                myIntent.putExtra("selectedStatus", statusName.getId()+"");
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
                                myIntent.putExtra("fromRoom", "fromRoom");
                                myIntent.putExtra("statusList",statusList);
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
                Intent myIntent= new Intent(StatusList.this, ContainerDetailsActivity.class);
                myIntent.putExtra("selectedFacilName", selectedFacilName);
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
                myIntent.putExtra("statusList",statusList);
                myIntent.putExtra("pageLoadTemp", "-1");
                myIntent.putExtra("selectedRoomName", selectedRoomName);
                myIntent.putExtra("selectedRoom", selectedRoom+"");
                myIntent.putExtra("selectedStatusName", selectedStatusName);
                myIntent.putExtra("selectedStatus", selectedStatus+"");
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
            return super.onOptionsItemSelected(item);
        }
        public MyObject[] getItemsFromDb(String searchTerm) {
            final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(StatusList.this);
            final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
            MyObject[] myObject = null;
            try {
                myObject = databaseHandler.getAutoSearchRoomsData(db, searchTerm,selectedFacil);
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
