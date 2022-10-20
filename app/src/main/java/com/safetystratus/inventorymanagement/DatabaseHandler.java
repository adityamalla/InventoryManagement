package com.safetystratus.inventorymanagement;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_SCANNED_DATA);
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
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT sec_code,name,code,id  \n" +
                "FROM  chemical_inventory where room_id="+room_id), null);
        int count = 0;
        int recCount = cursor.getCount();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String product_name = "";
                String code = "";
                if(cursor.getString(cursor.getColumnIndex("name")).trim().length()>0) {
                    product_name = cursor.getString(cursor.getColumnIndex("name"));
                }else{
                    product_name = "Inv"+count;
                }
                String rfidCode = "";
                if(cursor.getString(cursor.getColumnIndex("sec_code")).trim().length()>0) {
                    rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                }else{
                    rfidCode = "rfid"+count;
                }
                if(cursor.getString(cursor.getColumnIndex("code")).trim().length()>0) {
                    code = cursor.getString(cursor.getColumnIndex("code"));
                }else{
                    code = "code"+count;
                }
                inv.add(new InventoryObject(rfidCode, product_name,id,code,null));
                cursor.moveToNext();
                count++;
            }
        }
        cursor.close();
        return inv;
    }
    public void insertScannedInvData(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from scanned_data where room_id="+cv.getAsString("room_id")+"" +
                " and location_id="+cv.getAsString("location_id")+" and inventory_id="+cv.getAsString("inventory_id")), null);
        int count = cursor1.getCount();
        cursor1.close();
        if (count==0)
        sqLiteDatabase.insert("scanned_data", null, cv);
    }
    @SuppressLint("Range")
    public int checkScannedDataCount(SQLiteDatabase sqLiteDatabase){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from scanned_data"), null);
        int count = cursor1.getCount();
        Log.e("scannedCount>>",count+"***");
        cursor1.close();
        return count;
    }

    @SuppressLint("Range")
    public ArrayList<InventoryObject> getFoundInventoryList(SQLiteDatabase sqLiteDatabase, String room_id){
        ArrayList<InventoryObject> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select ci.name,ci.sec_code,ci.code,ci.id from chemical_inventory ci \n" +
                "join scanned_data sc on ci.id = sc.inventory_id \n" +
                "where ci.room_id = "+room_id), null);
        int count = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String product_name = "";
                String code = "";
                if(cursor.getString(cursor.getColumnIndex("name")).trim().length()>0) {
                    product_name = cursor.getString(cursor.getColumnIndex("name"));
                }else{
                    product_name = "Inv"+count;
                }
                String rfidCode = "";
                if(cursor.getString(cursor.getColumnIndex("sec_code")).trim().length()>0) {
                    rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                }else{
                    rfidCode = "rfid"+count;
                }
                if(cursor.getString(cursor.getColumnIndex("code")).trim().length()>0) {
                    code = cursor.getString(cursor.getColumnIndex("code"));
                }else{
                    code = "code"+count;
                }
                inv.add(new InventoryObject(rfidCode, product_name,id,code,null));
                cursor.moveToNext();
                count++;
            }
        }
        cursor.close();
        return inv;
    }
    @SuppressLint("Range")
    public ArrayList<InventoryObject> getNotFoundInventoryList(SQLiteDatabase sqLiteDatabase, String room_id){
        ArrayList<InventoryObject> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select ci.name,ci.sec_code,ci.code,ci.id from chemical_inventory ci \n" +
                "where ci.room_id="+room_id+" and ci.id not in(select inventory_id from scanned_data sc where sc.room_id="+room_id+");"), null);
        int count = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String product_name = "";
                String code = "";
                if(cursor.getString(cursor.getColumnIndex("name")).trim().length()>0) {
                    product_name = cursor.getString(cursor.getColumnIndex("name"));
                }else{
                    product_name = "Inv"+count;
                }
                String rfidCode = "";
                if(cursor.getString(cursor.getColumnIndex("sec_code")).trim().length()>0) {
                    rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                }else{
                    rfidCode = "rfid"+count;
                }
                if(cursor.getString(cursor.getColumnIndex("code")).trim().length()>0) {
                    code = cursor.getString(cursor.getColumnIndex("code"));
                }else{
                    code = "code"+count;
                }
                inv.add(new InventoryObject(rfidCode, product_name,id,code,null));
                cursor.moveToNext();
                count++;
            }
        }
        cursor.close();
        return inv;
    }
    @SuppressLint("Range")
    public ArrayList<InventoryObject> getALLInventoryList(SQLiteDatabase sqLiteDatabase, String room_id){
        ArrayList<InventoryObject> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select ci.name,ci.sec_code,ci.code,sc.scanned,ci.id from chemical_inventory ci \n" +
                "left join scanned_data sc on ci.id = sc.inventory_id \n" +
                "where ci.room_id = "+room_id), null);
        int count = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String product_name = "";
                String code = "";
                String scanned = "";
                if(cursor.getString(cursor.getColumnIndex("name")).trim().length()>0) {
                    product_name = cursor.getString(cursor.getColumnIndex("name"));
                }else{
                    product_name = "Inv"+count;
                }
                String rfidCode = "";
                if(cursor.getString(cursor.getColumnIndex("sec_code")).trim().length()>0) {
                    rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                }else{
                    rfidCode = "rfid"+count;
                }
                if(cursor.getString(cursor.getColumnIndex("code")).trim().length()>0) {
                    code = cursor.getString(cursor.getColumnIndex("code"));
                }else{
                    code = "code"+count;
                }
                if(cursor.getString(cursor.getColumnIndex("scanned"))!=null) {
                    if (cursor.getString(cursor.getColumnIndex("scanned")).trim().length() > 0) {
                        scanned = cursor.getString(cursor.getColumnIndex("scanned"));
                    } else {
                        scanned = "0";
                    }
                } else {
                    scanned = "0";
                }
                inv.add(new InventoryObject(rfidCode, product_name,id,code,scanned));
                cursor.moveToNext();
                count++;
            }
        }
        cursor.close();
        return inv;
    }

    @SuppressLint("Range")
    public ArrayList<RFIDScanDataObj> getALLInventoryScannedList(SQLiteDatabase sqLiteDatabase){
        ArrayList<RFIDScanDataObj> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select inventory_id, scanned_by, scanned_date from scanned_data "), null);
        int count = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String inventory_id = cursor.getString(cursor.getColumnIndex("inventory_id"));
                String scanned_by = cursor.getString(cursor.getColumnIndex("scanned_by"));
                String scanned_date = cursor.getString(cursor.getColumnIndex("scanned_date"));
                inv.add(new RFIDScanDataObj(inventory_id, scanned_by,scanned_date));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return inv;
    }
    public void delAllSavedScanData(SQLiteDatabase sqLiteDatabase, String room_id){
        sqLiteDatabase.delete("scanned_data", "room_id=?", new String[]{room_id});
    }
    @SuppressLint("Range")
    public MyObject[] getAutoSearchBuildingsData(SQLiteDatabase sqLiteDatabase, String searchTerm) {
        String sql = "SELECT name,id FROM fi_facilities " +
                "where status = 'active' and name like '%"+searchTerm+"%'";
        Log.e("TESTSQL>",sql);
        Cursor cursor2 = sqLiteDatabase.rawQuery(sql,null);
        int recCount = cursor2.getCount();
        MyObject[] ObjectItemData = new MyObject[recCount];
        int x = 0;
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                String objectName = cursor2.getString(cursor2.getColumnIndex("name"));
                String objectId = cursor2.getString(cursor2.getColumnIndex("id"));
                MyObject myObject = new MyObject(objectName, objectId);
                ObjectItemData[x] = myObject;
                x++;
                Log.e("Count>>",x+"");
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        // return the list of records
        return ObjectItemData;
    }
    @SuppressLint("Range")
    public MyObject[] getAutoSearchRoomsData(SQLiteDatabase sqLiteDatabase, String searchTerm,String facil_id) {
        String sql = "SELECT room,id FROM fi_facil_rooms where status = 'active' and room like '%"+searchTerm+"%' and facil_id="+facil_id;
        Log.e("TESTSQL>",sql);
        Cursor cursor2 = sqLiteDatabase.rawQuery(sql,null);
        int recCount = cursor2.getCount();
        MyObject[] ObjectItemData = new MyObject[recCount];
        int x = 0;
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                String objectName = cursor2.getString(cursor2.getColumnIndex("room"));
                String objectId = cursor2.getString(cursor2.getColumnIndex("id"));
                MyObject myObject = new MyObject(objectName, objectId);
                ObjectItemData[x] = myObject;
                x++;
                Log.e("Count>>",x+"");
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        // return the list of records
        return ObjectItemData;
    }

}

