package com.safetystratus.inventorymanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CustomizedListView extends BaseAdapter {

    private Activity activity;
    private ArrayList<ScanInfo> data;
    private IntentModel obj;
    private static LayoutInflater inflater=null;

    public CustomizedListView(Activity a, ArrayList<ScanInfo> d, IntentModel model) {
        activity = a;
        data=d;
        obj = model;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        inflater = ((ContinueActivity) activity).getLayoutInflater();
        vi = inflater.inflate(R.layout.pending_scan_list, null, true);
        TextView facilityLabel = (TextView)vi.findViewById(R.id.labelFacility);
        TextView facilName = (TextView)vi.findViewById(R.id.facilityName);
        TextView labelRoom = (TextView)vi.findViewById(R.id.labelRoom);
        TextView roomValue = (TextView)vi.findViewById(R.id.roomValue);
        TextView userLabel = (TextView)vi.findViewById(R.id.labelUser);
        TextView userValue = (TextView)vi.findViewById(R.id.userValue);
        Button continueScan = (Button)vi.findViewById(R.id.continueScanButton);
        Button discardScan = (Button)vi.findViewById(R.id.discardScanButton);
        ScanInfo scanInfo = data.get(position);
        continueScan.setId(Integer.parseInt(scanInfo.getId()));
        facilityLabel.setText("Location");
        facilName.setText(scanInfo.getFacility_name());
        labelRoom.setText("Room");
        userLabel.setText("Scanned By");
        userValue.setText(obj.getEmpName());
        roomValue.setText(scanInfo.getRoom_name());
        continueScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase.loadLibs(activity);
                final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(activity);
                final SQLiteDatabase db = databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE);
                final Intent myIntent = new Intent(activity,
                        RFIDScannerActivity.class);
                myIntent.putExtra("user_id", obj.getUser_id());
                myIntent.putExtra("site_id", obj.getSite_id());
                myIntent.putExtra("token", obj.getToken());
                myIntent.putExtra("sso", obj.getSso());
                myIntent.putExtra("md5pwd", obj.getMd5());
                myIntent.putExtra("loggedinUsername", obj.getLoggedinUsername());
                myIntent.putExtra("site_name", obj.getSite_name());
                myIntent.putExtra("fromContinueInsp","true");
                myIntent.putExtra("empName",obj.getEmpName());
                myIntent.putExtra("json_data_from_continue",scanInfo.getJson_data());
                myIntent.putExtra("selectedFacilName", scanInfo.getFacility_name());
                myIntent.putExtra("selectedFacil", scanInfo.getFacility_id()+"");
                myIntent.putExtra("selectedRoomName", scanInfo.getRoom_name());
                myIntent.putExtra("selectedRoom", scanInfo.getRoom_id()+"");
                myIntent.putExtra("reconc_id", scanInfo.getReconc_id()+"");
                int inventoryCount = databaseHandler.checkCount(db,scanInfo.getRoom_id());
                myIntent.putExtra("total_inventory", inventoryCount+"");
                activity.startActivity(myIntent);
            }
        });
        discardScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase.loadLibs(activity);
                final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(activity);
                final SQLiteDatabase db = databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE);
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);
                dlgAlert.setTitle("Safety Stratus");
                dlgAlert.setMessage("Are you sure you want to discard this reconciliation?");
                dlgAlert.setPositiveButton("Discard",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                boolean deleted = databaseHandler.deletePendingScan(db,scanInfo.getId(),scanInfo.getReconc_id());
                                if(deleted){
                                    final Intent myIntent = new Intent(activity,
                                            ContinueActivity.class);
                                    myIntent.putExtra("user_id", obj.getUser_id());
                                    myIntent.putExtra("site_id", obj.getSite_id());
                                    myIntent.putExtra("token", obj.getToken());
                                    myIntent.putExtra("sso", obj.getSso());
                                    myIntent.putExtra("md5pwd", obj.getMd5());
                                    myIntent.putExtra("loggedinUsername", obj.getLoggedinUsername());
                                    myIntent.putExtra("site_name", obj.getSite_name());
                                    myIntent.putExtra("empName", obj.getEmpName());
                                    activity.startActivity(myIntent);
                                    return;
                                }else
                                return;
                            }
                        });
                dlgAlert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                dlgAlert.create().show();
            }
        });
        return vi;
    }
}
