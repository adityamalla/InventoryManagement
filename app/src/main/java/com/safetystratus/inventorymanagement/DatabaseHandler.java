package com.safetystratus.inventorymanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler instance;
    public DatabaseHandler(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
    }
    static public synchronized DatabaseHandler getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHandler(context);
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_SITE_USERS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_OT_ORGANIZATION);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_OT_DEPARTMENTS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_FI_LOCATIONS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_FI_FACILITIES);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_FI_ROOM_TYPES);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_FI_FACIL_ROOMS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_FI_ROOM_DEPT);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_LABELS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_SETTINGS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_MENU_CATEGORIES);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_MENU_ITEMS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_CHEMICAL_INVENTORY);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_FI_ROOM_ROSTER);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
    public long checkDuplicates(SQLiteDatabase sqLiteDatabase, String TABLE_NAME, String COLUMN_NAME, String value) {
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("Select count(*) from %s where %s=?;", TABLE_NAME, COLUMN_NAME), new String[]{value});
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    public long checkDuplicatesThreePrimaryKeys(SQLiteDatabase sqLiteDatabase, String TABLE_NAME, String COLUMN_NAME1, String COLUMN_NAME2, String COLUMN_NAME3, String val1, String val2, String val3) {
        Cursor cursor4 = sqLiteDatabase.rawQuery(String.format("Select count(*) from %s where " + COLUMN_NAME1 + "=? and " + COLUMN_NAME2 + "=? and " + COLUMN_NAME3 + "=?;", TABLE_NAME), new String[]{val1, val2, val3});
        cursor4.moveToFirst();
        long count = cursor4.getLong(0);
        cursor4.close();
        return count;
    }

    public long checkDuplicatesTwoPrimaryKeys(SQLiteDatabase sqLiteDatabase, String TABLE_NAME, String COLUMN_NAME1, String COLUMN_NAME2, String val1, String val2) {
        Cursor cursor5 = sqLiteDatabase.rawQuery(String.format("Select count(*) from %s where " + COLUMN_NAME1 + "=? and " + COLUMN_NAME2 + "=?;", TABLE_NAME), new String[]{val1, val2});
        cursor5.moveToFirst();
        long count = cursor5.getLong(0);
        cursor5.close();
        return count;
    }
    @SuppressLint("Range")
    public HashMap getPermissionDetails(SQLiteDatabase sqLiteDatabase) {
        List<String> settings = new ArrayList<String>();
        settings.add("pi_inspect");
        settings.add("asset_permit");
        settings.add("insp_groups_disabled");
        settings.add("incident_permit");
        Cursor cursor1 = null;
        HashMap<String, String> hm = new HashMap<String, String>();
        for (int i = 0; i < settings.size(); i++) {
            cursor1 = sqLiteDatabase.rawQuery(String.format("Select setting,value from settings where setting=?;"), new String[]{settings.get(i)});
            if (cursor1.moveToFirst()) {
                while (!cursor1.isAfterLast()) {
                    String setting = cursor1.getString(cursor1.getColumnIndex("setting"));
                    String value = cursor1.getString(cursor1.getColumnIndex("value"));
                    hm.put(setting, value);
                    cursor1.moveToNext();
                }
            } else {
                hm.put(settings.get(i), "donot exists");
            }
            cursor1.close();
        }

        return hm;
    }
    public boolean isTableExists(SQLiteDatabase sqLiteDatabase,String tableName) {
        String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'";
        try (Cursor cursor = sqLiteDatabase.rawQuery(query, null)) {
            if(cursor!=null) {
                if(cursor.getCount()>0) {
                    return true;
                }
            }
            return false;
        }
    }
    @SuppressLint("Range")
    public String getUserEmployeeUsername(SQLiteDatabase sqLiteDatabase, String val) {
        Cursor cursor = null;
        String name = "";
        Log.e("name0>>>",val+"**");
        try{
            cursor = sqLiteDatabase.rawQuery(String.format("select username from site_users " +
                    "where user_id = " + val), null);
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex("username"));
            }
            Log.e("name1>>>",name+"**");
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return name;
    }

    @SuppressLint("Range")
    public ArrayList<MyObject> getBuildingList(SQLiteDatabase sqLiteDatabase){
        ArrayList<MyObject> facil = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT name,id  \n" +
                "FROM    fi_facilities where status = 'active'"), null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                facil.add(
                        new MyObject(cursor.getString(cursor.getColumnIndex("name")),
                                cursor.getString(cursor.getColumnIndex("id"))));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return facil;
    }
    @SuppressLint("Range")
    public ArrayList<MyObject> getRoomList(SQLiteDatabase sqLiteDatabase,String facil_id){
        ArrayList<MyObject> rooms = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT room,id  \n" +
                "FROM fi_facil_rooms where status = 'active' and facil_id="+facil_id), null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                rooms.add(
                        new MyObject(cursor.getString(cursor.getColumnIndex("room")),
                                cursor.getString(cursor.getColumnIndex("id"))));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return rooms;
    }

    @SuppressLint("Range")
    public int checkCount(SQLiteDatabase sqLiteDatabase, String room_id){
        int count = 0;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM chemical_inventory where room_id="+room_id), null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    @SuppressLint("Range")
    public ArrayList<InventoryObject> getInventoryList(SQLiteDatabase sqLiteDatabase, String room_id){
        ArrayList<InventoryObject> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT sec_code,code  \n" +
                "FROM  chemical_inventory where room_id="+room_id), null);
        int count = 0;
        int recCount = cursor.getCount();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String product_name = "";
                if(cursor.getString(cursor.getColumnIndex("code")).trim().length()>0) {
                    product_name = cursor.getString(cursor.getColumnIndex("code"));
                }else{
                    product_name = "Inv"+count;
                }
                String rfidCode = "";
                if(cursor.getString(cursor.getColumnIndex("sec_code")).trim().length()>0) {
                    rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                }else{
                    if(count==0)
                        rfidCode = "C0058466";
                    else if(count == recCount-1)
                        rfidCode = "C0058467";
                    else
                        rfidCode = "rfid"+count;
                }
                inv.add(new InventoryObject(rfidCode, product_name));
                cursor.moveToNext();
                count++;
            }
        }
        cursor.close();
        return inv;
    }

}

