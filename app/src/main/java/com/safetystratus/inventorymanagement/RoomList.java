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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
    ArrayList<MyObject> roomlist=null;
    ConstraintLayout header;
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
        roomlist = new ArrayList<MyObject>();
        if(intent.getSerializableExtra("roomlist")!=null)
            roomlist = (ArrayList<MyObject>) intent.getSerializableExtra("roomlist");
        TableLayout tableRooms = (TableLayout) findViewById(R.id.tableRooms);
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
                    final Intent myIntent = new Intent(RoomList.this,
                            RFIDActivity.class);
                    myIntent.putExtra("selectedFacilName", selectedFacilName);
                    myIntent.putExtra("selectedRoomName", roomName.getText());
                    myIntent.putExtra("selectedRoom", roomName.getId()+"");
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
                    myIntent.putExtra("roomlist",roomlist);
                    myIntent.putExtra("pageLoadTemp", "-1");
                    startActivity(myIntent);
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            final Intent myIntent = new Intent(RoomList.this,
                    RFIDActivity.class);
            myIntent.putExtra("selectedFacilName", selectedFacilName);
            myIntent.putExtra("selectedFacil", selectedFacil+"");
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
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}