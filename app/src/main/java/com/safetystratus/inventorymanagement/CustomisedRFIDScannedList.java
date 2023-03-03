package com.safetystratus.inventorymanagement;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomisedRFIDScannedList  extends BaseAdapter implements ListAdapter {
    private ArrayList<InventoryObject> list = new ArrayList<InventoryObject>();
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

        // Check the flag value and set the background color of the view
        if (list.get(position).isFlag()) {
            view.setBackgroundResource(R.color.invScanSuccess);
        } else {
            view.setBackgroundResource(R.color.white);
        }
        //Handle TextView and display string from your list
        TextView productName = (TextView)view.findViewById(R.id.productName);
        TextView productCode = (TextView)view.findViewById(R.id.productcode);
        TextView volumne = (TextView)view.findViewById(R.id.volumeofitem);
        productName.setText(list.get(position).getProductName());
        productCode.setText(list.get(position).getRfidCode());
        volumne.setText(list.get(position).getVolume());
        return view;
    }
}