package com.safetystratus.inventorymanagement;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomisedRFIDScannedList  extends BaseAdapter implements ListAdapter {
    public ArrayList<InventoryObject> list = new ArrayList<InventoryObject>();
    private Context context;
    private IntentModel obj;


    public CustomisedRFIDScannedList(ArrayList<InventoryObject> list, IntentModel model, Context context) {
        this.list = list;
        this.context = context;
        this.obj = model;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.rfid_scanned_taglist, null);
        }
        //Handle TextView and display string from your list
        TextView productName = (TextView)view.findViewById(R.id.productName);
        TextView productCode = (TextView)view.findViewById(R.id.productcode);
        TextView volumne = (TextView)view.findViewById(R.id.volumeofitem);
        ImageView invinfo = (ImageView)view.findViewById(R.id.invinfo);
        productName.setText(list.get(position).getProductName());
        if(list.get(position).getRfidCode()!=null) {
            if (list.get(position).getRfidCode().trim().length() > 0) {
                if (!list.get(position).getRfidCode().equalsIgnoreCase("N/A"))
                    productCode.setText(list.get(position).getRfidCode());
                else
                    productCode.setText(list.get(position).getCode());
            } else
                productCode.setText(list.get(position).getCode());
        }else{
            productCode.setText(list.get(position).getCode());
        }
        volumne.setText(list.get(position).getVolume());
        // Check the flag value and set the background color of the view
        if (list.get(position).isFlag()) {
            view.setBackgroundResource(R.color.invScanSuccess);
        } else {
            view.setBackgroundResource(R.color.white);
        }
        /*invinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = (TextView) view.findViewById(R.id.productcode);
                final Intent myIntent = new Intent(context,
                        Container_Info.class);
                myIntent.putExtra("user_id", obj.getUser_id());
                myIntent.putExtra("site_id", obj.getSite_id());
                myIntent.putExtra("token", obj.getToken());
                myIntent.putExtra("sso", obj.getSso());
                myIntent.putExtra("md5pwd", obj.getMd5());
                myIntent.putExtra("loggedinUsername", obj.getLoggedinUsername());
                myIntent.putExtra("site_name", obj.getSite_name());
                myIntent.putExtra("empName",obj.getEmpName());
                myIntent.putExtra("selectedFacilName", obj.getSelectedFacilName());
                myIntent.putExtra("selectedFacil", obj.getSelectedFacil()+"");
                myIntent.putExtra("selectedRoomName", obj.getSelectedRoomName());
                myIntent.putExtra("selectedRoom", obj.getSelectedRoom()+"");
                myIntent.putExtra("selectedSearchValue", obj.getSelectedSearchValue());
                myIntent.putExtra("total_inventory", obj.getTotal_inventory()+"");
                myIntent.putExtra("flag",obj.getFlag()+"");
                myIntent.putExtra("codelistfromIntent",obj.getCodelistfromIntent());
                Log.e("Test999999>>>>>",tv.getText().toString());
                myIntent.putExtra("scannedCode", tv.getText().toString());
                context.startActivity(myIntent);
            }
        });*/
        return view;
    }
}