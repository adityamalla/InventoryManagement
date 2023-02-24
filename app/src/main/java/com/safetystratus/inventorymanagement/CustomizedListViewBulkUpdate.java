package com.safetystratus.inventorymanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

public class CustomizedListViewBulkUpdate  extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private IntentModel obj;


    public CustomizedListViewBulkUpdate(ArrayList<String> list, IntentModel model, Context context) {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bulk_update_list, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.codeAdded);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        ImageView deleteBtn = (ImageView) view.findViewById(R.id.removeContainer);
        ImageView info = (ImageView) view.findViewById(R.id.containerinfo);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                dlgAlert.setTitle("Safety Stratus");
                dlgAlert.setMessage("Do you confirm that you want to remove this item from the list?");
                dlgAlert.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                notifyDataSetChanged();
                                return;
                            }
                        });
                dlgAlert.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                dlgAlert.create().show();
            }
        });
        info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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
                myIntent.putExtra("flag",obj.getFlag()+"");
                myIntent.putExtra("codelistfromIntent",obj.getCodelistfromIntent());
                myIntent.putExtra("scannedCode", listItemText.getText().toString());
                context.startActivity(myIntent);
            }
        });

        return view;
    }
}