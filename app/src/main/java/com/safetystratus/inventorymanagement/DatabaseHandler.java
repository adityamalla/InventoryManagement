package com.safetystratus.inventorymanagement;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler instance;
    private SQLiteStatement insertStatement;
    private static final String INSERT_QUERY = "INSERT INTO scanned_data (location_id, room_id, inventory_id, scanned_by, scanned, reconc_id, rfid_code) VALUES (?, ?, ?,?,?,?,?)";
    private static final String INSERT_QUERY_CHEM_INVENTORY = "INSERT INTO chemical_inventory" +
            " (id,\n" +
            "opened_date,\n" +
            "name,\n" +
            "room_id,\n" +
            "sec_code,\n" +
            "object_table,\n" +
            "modified_user_id,\n" +
            "modified_date,\n" +
            "last_test_date,\n" +
            "primary_user_id,\n" +
            "lot,\n" +
            "create_date,\n" +
            "code,\n" +
            "expiration_date,\n" +
            "create_user_id,\n" +
            "object_id,\n" +
            "facil_id,\n" +
            "room,\n" +
            "receipt_date,\n" +
            "notes,\n" +
            "comment,\n" +
            "quantity,\n" +
            "concentration,\n" +
            "quantity_unit_abbreviation,\n" +
            "quantity_unit_abbreviation_id,\n" +
            "concentration_unit_abbrevation,\n" +
            "concentration_unit_abbrevation_id,\n" +
            "cas_number,\n" +
            "status,\n" +
            "status_id,\n" +
            "loc,\n" +
            "loc_id,test_frequency,\n" +
            "owner) VALUES (?, ?, ?,?,?,?,?,?, ?, ?,?,?,?,?,?, ?, ?,?,?,?,?,?, ?, ?,?,?,?,?,?, ?, ?,?,?,?)";
    private static final String INSERT_QUERY_PRIMARY_USERS = "INSERT INTO primary_users (primary_user, primary_user_id) VALUES (?, ?)";
    private static final String INSERT_QUERY_ROOMS = "INSERT INTO fi_facil_rooms (" +
            "room,\n" +
            "area, \n" +
            "img_src, \n" +
            "type_id, \n" +
            "id,\n" +
            "status,\n" +
            "notes,\n" +
            "facil_id) VALUES (?, ?, ?,?,?,?,?,?)";

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
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_SCANNED_JSON_DATA);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_INVENTORY_STATUS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_UNITS_OF_MEASURE);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_SCANNED_BARCODE_JSON_DATA);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_PRIMARY_USERS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_RECONCILIATION_DATA);
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
        try{
            cursor = sqLiteDatabase.rawQuery(String.format("select username from site_users " +
                    "where user_id = " + val), null);
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex("username"));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return name;
    }
    //@SuppressLint("Range")
    /*public String getUserEmployeeName(SQLiteDatabase sqLiteDatabase, String val) {
        Cursor cursor = null;
        String name = "";
        try{
            cursor = sqLiteDatabase.rawQuery(String.format("select firstname||' '||lastname as name from site_users " +
                    "where user_id = " + val), null);
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return name;
    }*/
    @SuppressLint("Range")
    public ArrayList<ScanInfo> getPendingScans(SQLiteDatabase sqLiteDatabase){
        ArrayList<ScanInfo> scanInfo = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM scanned_json_data where scan_type='rfid'"), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String room_id = cursor.getString(cursor.getColumnIndex("room_id"));
                String location_id = cursor.getString(cursor.getColumnIndex("location_id"));
                String reconc_id = cursor.getString(cursor.getColumnIndex("reconc_id"));
                String roomName = getRoomName(sqLiteDatabase,room_id);
                String locName = getFacilName(sqLiteDatabase,location_id);
                scanInfo.add(
                        new ScanInfo(cursor.getString(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("room_id")),roomName,location_id,locName,cursor.getString(cursor.getColumnIndex("json_data")),reconc_id));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return scanInfo;
    }
    @SuppressLint("Range")
    public ScanInfo getPendingReconcScans(SQLiteDatabase sqLiteDatabase,String rec_id){
        ScanInfo scanInfo = null;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM scanned_json_data where reconc_id="+rec_id), null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String room_id = cursor.getString(cursor.getColumnIndex("room_id"));
                String location_id = cursor.getString(cursor.getColumnIndex("location_id"));
                String reconc_id = cursor.getString(cursor.getColumnIndex("reconc_id"));
                String roomName = getRoomName(sqLiteDatabase,room_id);
                String locName = getFacilName(sqLiteDatabase,location_id);
                scanInfo = new ScanInfo(cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("room_id")),roomName,location_id,locName,cursor.getString(cursor.getColumnIndex("json_data")),reconc_id);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scanInfo;
    }
    @SuppressLint("Range")
    public boolean deletePendingScan(SQLiteDatabase sqLiteDatabase,String id, String reconc_id){
        boolean deleted = false;
        sqLiteDatabase.delete("scanned_json_data", "id=?", new String[]{id});
        sqLiteDatabase.delete("scanned_data", "reconc_id=?", new String[]{reconc_id});
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM scanned_json_data where id="+id), null);
        if(cursor.getCount()==0)
            deleted = true;
        cursor.close();
        return deleted;
    }
    @SuppressLint("Range")
    public void deletePendingScanByReconc_id(SQLiteDatabase sqLiteDatabase,String reconc_id){
        sqLiteDatabase.delete("scanned_json_data", "reconc_id=?", new String[]{reconc_id});
        sqLiteDatabase.delete("scanned_data", "reconc_id=?", new String[]{reconc_id});
    }
    @SuppressLint("Range")
    public ArrayList<MyObject> getBuildingList(SQLiteDatabase sqLiteDatabase){
        ArrayList<MyObject> facil = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT name,id  \n" +
                "FROM    fi_facilities where status = 'active' order by name asc"), null);

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
        Cursor cursor = null;
        if(facil_id.trim().length()>0)
             cursor = sqLiteDatabase.rawQuery(String.format("SELECT room,id  \n" +
                "FROM fi_facil_rooms where status = 'active' and facil_id="+facil_id+" order by room asc"), null);
        else
            cursor = sqLiteDatabase.rawQuery(String.format("SELECT room,id  \n" +
                    "FROM fi_facil_rooms where status = 'active'"), null);
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
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM chemical_inventory where room_id="+room_id+" and status_id!=2 and status_id!=5"), null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }
    @SuppressLint("Range")
    public int getSavedDataCount(SQLiteDatabase sqLiteDatabase, String user_id){
        int count = 0;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM scanned_json_data where user_id="+user_id), null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }
    @SuppressLint("Range")
    public int getSavedBarcodeDataCount(SQLiteDatabase sqLiteDatabase){
        int count = 0;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM scanned_json_data where scan_type='barcode'"), null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }
    @SuppressLint("Range")
    public int getSavedBulkDataUpdateCount(SQLiteDatabase sqLiteDatabase){
        int count = 0;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM scanned_json_data where scan_type='bulkupdate'"), null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    @SuppressLint("Range")
    public int getInventoryId(SQLiteDatabase sqLiteDatabase, String code){
        int id = -1;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM chemical_inventory where sec_code='"+code+"' or code='"+code+"'"), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                id = cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        cursor.close();
        return id;
    }

    @SuppressLint("Range")
    public ArrayList<InventoryObject> getInventoryList(SQLiteDatabase sqLiteDatabase, String room_id){
        ArrayList<InventoryObject> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT sec_code,name,code,id,quantity, quantity_unit_abbreviation,test_frequency \n" +
                "FROM  chemical_inventory where room_id="+room_id+" and status_id != 2 and status_id != 5 "), null);
        int count = 0;
        int recCount = cursor.getCount();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String product_name = "";
                String code = "";
                product_name = cursor.getString(cursor.getColumnIndex("name"));
                String rfidCode = "";
                rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                code = cursor.getString(cursor.getColumnIndex("code"));
                String test_frequency = "";
                if(cursor.getString(cursor.getColumnIndex("test_frequency")).trim().length()>0){
                    test_frequency = cursor.getString(cursor.getColumnIndex("test_frequency"));
                }else {
                    test_frequency = "0";
                }
                String vol = cursor.getString(cursor.getColumnIndex("quantity"))+" "+cursor.getString(cursor.getColumnIndex("quantity_unit_abbreviation"));
                inv.add(new InventoryObject(rfidCode, product_name,id,code,null,vol,false,test_frequency));
                cursor.moveToNext();
                count++;
            }
        }
        cursor.close();
        return inv;
    }
    @SuppressLint("Range")
    public ArrayList<InventoryObject> getDisposedInventoryList(SQLiteDatabase sqLiteDatabase, String room_id){
        ArrayList<InventoryObject> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT sec_code,name,code,id,quantity, quantity_unit_abbreviation,test_frequency \n" +
                "FROM  chemical_inventory where room_id="+room_id+" and status_id = 2"), null);
        int count = 0;
        int recCount = cursor.getCount();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String product_name = "";
                String code = "";
                product_name = cursor.getString(cursor.getColumnIndex("name"));
                String rfidCode = "";
                rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                code = cursor.getString(cursor.getColumnIndex("code"));
                String test_frequency = "";
                if(cursor.getString(cursor.getColumnIndex("test_frequency")).trim().length()>0){
                    test_frequency = cursor.getString(cursor.getColumnIndex("test_frequency"));
                }else {
                    test_frequency = "0";
                }
                String vol = cursor.getString(cursor.getColumnIndex("quantity"))+" "+cursor.getString(cursor.getColumnIndex("quantity_unit_abbreviation"));
                inv.add(new InventoryObject(rfidCode, product_name,id,code,null,vol,false,test_frequency));
                cursor.moveToNext();
                count++;
            }
        }
        cursor.close();
        return inv;
    }
    @SuppressLint("Range")
    public InventoryModel getScannedInventoryDetails(SQLiteDatabase sqLiteDatabase, String cde,String flag){
        InventoryModel inv = null;
        String sql = "";
        if(flag.trim().length()>0){
            if (flag.equalsIgnoreCase("2")||flag.equalsIgnoreCase("3")){
                sql = "SELECT * FROM  chemical_inventory where id= "+cde;
            }else{
                sql = "SELECT * FROM  chemical_inventory where lower(code)=lower('"+cde+"') or lower(sec_code)=lower('"+cde+"') limit 1";
            }
        }else{
            sql = "SELECT * FROM  chemical_inventory where lower(code)=lower('"+cde+"') or lower(sec_code)=lower('"+cde+"') limit 1";
        }
        Cursor cursor = sqLiteDatabase.rawQuery(String.format(sql), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String code = cursor.getString(cursor.getColumnIndex("code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String cas = cursor.getString(cursor.getColumnIndex("cas_number"));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                String status_id = cursor.getString(cursor.getColumnIndex("status_id"));
                String room = cursor.getString(cursor.getColumnIndex("room"));
                String room_id = cursor.getString(cursor.getColumnIndex("room_id"));
                String facil_id = cursor.getString(cursor.getColumnIndex("facil_id"));
                String object_id = cursor.getString(cursor.getColumnIndex("object_id"));
                String object_table = cursor.getString(cursor.getColumnIndex("object_table"));
                String owner = cursor.getString(cursor.getColumnIndex("owner"));
                String notes = cursor.getString(cursor.getColumnIndex("notes"));
                String comments = cursor.getString(cursor.getColumnIndex("comment"));
                String volume_mass = cursor.getString(cursor.getColumnIndex("quantity"));
                String volume_mass_units = cursor.getString(cursor.getColumnIndex("quantity_unit_abbreviation"));
                String volume_mass_units_id = cursor.getString(cursor.getColumnIndex("quantity_unit_abbreviation_id"));
                String concentration = cursor.getString(cursor.getColumnIndex("concentration"));
                String concentration_unit_abbrevation = cursor.getString(cursor.getColumnIndex("concentration_unit_abbrevation"));
                String concentration_unit_abbrevation_id = cursor.getString(cursor.getColumnIndex("concentration_unit_abbrevation_id"));
                String rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                String primary_user_id = cursor.getString(cursor.getColumnIndex("primary_user_id"));
                inv = new InventoryModel(id, code,name,cas,status_id,status,facil_id,room_id,room,owner,notes,comments,volume_mass,volume_mass_units_id,volume_mass_units,rfidCode,concentration,concentration_unit_abbrevation_id,concentration_unit_abbrevation,object_id,object_table,primary_user_id);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return inv;
    }

    public void insertScannedInvData(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from scanned_data where room_id="+cv.getAsString("room_id")+" and reconc_id="+cv.getAsString("reconc_id") +
                " and location_id="+cv.getAsString("location_id")+" and scanned_by="+cv.getAsString("scanned_by")+" and inventory_id="+cv.getAsString("inventory_id")), null);
        int count = cursor1.getCount();
        cursor1.close();
        if (count==0)
        sqLiteDatabase.insert("scanned_data", null, cv);
    }
    public void insertScannedInvDataOutofLocationData(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from scanned_data where room_id="+cv.getAsString("room_id")+" and reconc_id="+cv.getAsString("reconc_id") +
                " and location_id="+cv.getAsString("location_id")+" and scanned_by="+cv.getAsString("scanned_by")+" and inventory_id="+cv.getAsString("inventory_id")+" and rfid_code='"+cv.getAsString("rfid_code")+"'"), null);
        int count = cursor1.getCount();
        cursor1.close();
        if (count==0)
            sqLiteDatabase.insert("scanned_data", null, cv);
    }
    public boolean insertScannedInvDataOutofLocationDataBarcode(SQLiteDatabase sqLiteDatabase, ContentValues cv) {
        boolean isInserted = false;
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from scanned_data where room_id=" + cv.getAsString("room_id") + " and reconc_id=" + cv.getAsString("reconc_id") +
                " and location_id=" + cv.getAsString("location_id") + " and scanned_by=" + cv.getAsString("scanned_by") + " and inventory_id=" + cv.getAsString("inventory_id") + " and code='" + cv.getAsString("code") + "'"), null);
        int count = cursor1.getCount();
        cursor1.close();
        if (count == 0) {
            sqLiteDatabase.insert("scanned_data", null, cv);
            isInserted = true;
        }
        return isInserted;
    }
    public void insertScannedInvJSONData(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        sqLiteDatabase.delete("scanned_json_data", "room_id=? and location_id=? and user_id=? and reconc_id=?", new String[]{cv.getAsString("room_id"),cv.getAsString("location_id"),cv.getAsString("user_id"),cv.getAsString("reconc_id")});
        sqLiteDatabase.insert("scanned_json_data", null, cv);
    }
    @SuppressLint("Range")
    public int insertReconciliaionData(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        int reconc_id = 0;
        sqLiteDatabase.insert("reconciliation_data", null, cv);
        String sql = "SELECT max(id) as id FROM reconciliation_data";
        Cursor cursor2 = sqLiteDatabase.rawQuery(sql,null);
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                reconc_id = cursor2.getInt(cursor2.getColumnIndex("id"));
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        return reconc_id;
    }
    public void insertScannedBarcodeInvJSONData(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        sqLiteDatabase.delete("scanned_json_data", "code=? and scan_type=? and user_id=?", new String[]{cv.getAsString("code"),cv.getAsString("scan_type"),cv.getAsString("user_id")});
        sqLiteDatabase.insert("scanned_json_data", null, cv);
    }
    public void deleteBarcodeInventoryDetails(SQLiteDatabase sqLiteDatabase, String code){
        sqLiteDatabase.delete("scanned_json_data", "code=?", new String[]{code});
    }
    @SuppressLint("Range")
    public ArrayList<MyObject> getSavedJsonData(SQLiteDatabase sqLiteDatabase){
        int count = 0;
        ArrayList<MyObject> jsonList = new ArrayList<MyObject>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT id,json_data FROM scanned_json_data"), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                jsonList.add(new MyObject(cursor.getString(cursor.getColumnIndex("json_data")).trim(),
                                cursor.getString(cursor.getColumnIndex("id")))
                        );
                cursor.moveToNext();
            }
        }
        cursor.close();
        return jsonList;
    }
    @SuppressLint("Range")
    public ArrayList<MyObject> getSavedJsonDataBulkUpdate(SQLiteDatabase sqLiteDatabase){
        int count = 0;
        ArrayList<MyObject> jsonList = new ArrayList<MyObject>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT id,json_data FROM scanned_json_data where scan_type='bulkupdate'"), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                jsonList.add(new MyObject(cursor.getString(cursor.getColumnIndex("json_data")).trim(),
                        cursor.getString(cursor.getColumnIndex("id")))
                );
                cursor.moveToNext();
            }
        }
        cursor.close();
        return jsonList;
    }
    @SuppressLint("Range")
    public ArrayList<MyObject> getSavedJsonDataBarcodeUpdate(SQLiteDatabase sqLiteDatabase){
        int count = 0;
        ArrayList<MyObject> jsonList = new ArrayList<MyObject>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT id,json_data FROM scanned_json_data where scan_type='barcode'"), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                jsonList.add(new MyObject(cursor.getString(cursor.getColumnIndex("json_data")).trim(),
                        cursor.getString(cursor.getColumnIndex("id")))
                );
                cursor.moveToNext();
            }
        }
        cursor.close();
        return jsonList;
    }

    @SuppressLint("Range")
    public ArrayList<MyObject> getStatusList(SQLiteDatabase sqLiteDatabase, String role){
        int count = 0;
        ArrayList<MyObject> statusList = new ArrayList<MyObject>();
        String sql = "";
        if(Integer.parseInt(role)!=4){
            sql = "SELECT id,status FROM inventory_status where id in (1,10)";
        }else if (Integer.parseInt(role)==4){
            sql = "SELECT id,status FROM inventory_status where id in (1,2,0,10)";
        }
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                statusList.add(new MyObject(cursor.getString(cursor.getColumnIndex("status")).trim(),
                        cursor.getString(cursor.getColumnIndex("id")))
                );
                cursor.moveToNext();
            }
        }
        cursor.close();
        return statusList;
    }
    @SuppressLint("Range")
    public ArrayList<MyObject> getOwnerList(SQLiteDatabase sqLiteDatabase){
        int count = 0;
        ArrayList<MyObject> ownerList = new ArrayList<MyObject>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select distinct object_id, owner from chemical_inventory where object_table='site_users'"), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                ownerList.add(new MyObject(cursor.getString(cursor.getColumnIndex("owner")).trim(),
                        cursor.getString(cursor.getColumnIndex("object_id")))
                );
                cursor.moveToNext();
            }
        }
        cursor.close();
        return ownerList;
    }
    @SuppressLint("Range")
    public ArrayList<MyObject> getPrimaryUsersList(SQLiteDatabase sqLiteDatabase){
        int count = 0;
        ArrayList<MyObject> primaryUsers = new ArrayList<MyObject>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select distinct primary_user_id, primary_user from primary_users"), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                primaryUsers.add(new MyObject(cursor.getString(cursor.getColumnIndex("primary_user")).trim(),
                        cursor.getString(cursor.getColumnIndex("primary_user_id")))
                );
                cursor.moveToNext();
            }
        }
        cursor.close();
        return primaryUsers;
    }
    @SuppressLint("Range")
    public String getPrimaryUserName(SQLiteDatabase sqLiteDatabase, String id){
        int count = 0;
        String name = "";
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select distinct primary_user_id, primary_user from primary_users where primary_user_id="+id), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                name = cursor.getString(cursor.getColumnIndex("primary_user")).trim();
                cursor.moveToNext();
            }
        }
        cursor.close();
        return name;
    }

    @SuppressLint("Range")
    public ArrayList<MyObject> getUnitList(SQLiteDatabase sqLiteDatabase){
        int count = 0;
        ArrayList<MyObject> unitList = new ArrayList<MyObject>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT id,abbreviation FROM units_of_measure"), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                unitList.add(new MyObject(cursor.getString(cursor.getColumnIndex("abbreviation")).trim(),
                        cursor.getString(cursor.getColumnIndex("id")))
                );
                cursor.moveToNext();
            }
        }
        cursor.close();
        return unitList;
    }
    @SuppressLint("Range")
    public int checkScannedDataCount(SQLiteDatabase sqLiteDatabase, String loc_id, String room_id,String scanned_by, String reconc_id){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from scanned_data where room_id="+room_id+"" +
                " and location_id="+loc_id+" and inventory_id > 0 and reconc_id = "+reconc_id+" and scanned_by="+scanned_by+" and inventory_id in (select id from chemical_inventory where status_id!=2 and status_id!=5)"), null);
        int count = cursor1.getCount();
        cursor1.close();
        return count;
    }

    @SuppressLint("Range")
    public int checkReconciliationStarted(SQLiteDatabase sqLiteDatabase, String loc_id, String room_id,String scanned_by){
        int reconc_id = -1;
        String sql = "SELECT reconc_id from scanned_json_data where room_id="+room_id+"" +
                " and location_id="+loc_id+" and user_id="+scanned_by;
        Cursor cursor2 = sqLiteDatabase.rawQuery(sql,null);
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                reconc_id = cursor2.getInt(cursor2.getColumnIndex("reconc_id"));
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        return reconc_id;
    }
    @SuppressLint("Range")
    public String checkScannedDataFullCount(SQLiteDatabase sqLiteDatabase, String loc_id, String room_id, String scanned_by, String reconc_id){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from scanned_data where room_id="+room_id+"" +
                " and location_id="+loc_id+" and reconc_id = "+reconc_id+" and scanned_by="+scanned_by), null);
        int count = cursor1.getCount();
        cursor1.close();
        return String.valueOf(count);
    }
    @SuppressLint("Range")
    public String checkSavedScannedDataFullCount(SQLiteDatabase sqLiteDatabase, String reconc_id){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from scanned_json_data where reconc_id = "+reconc_id), null);
        int count = cursor1.getCount();
        cursor1.close();
        return String.valueOf(count);
    }
    @SuppressLint("Range")
    public boolean checkScannedBarcodeDataAvailable(SQLiteDatabase sqLiteDatabase,  String code){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from chemical_inventory where lower(code)=lower('"+code+"')"), null);
        int count = cursor1.getCount();
        cursor1.close();
        if(count>0)
            return true;
        else
            return false;
    }

    @SuppressLint("Range")
    public boolean checkScannedRFIDCodeDataAvailable(SQLiteDatabase sqLiteDatabase,  String code){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from chemical_inventory where lower(sec_code)=lower('"+code+"') and status_id!=5"), null);
        int count = cursor1.getCount();
        cursor1.close();
        if(count>0)
            return true;
        else
            return false;
    }

    @SuppressLint("Range")
    public CopyOnWriteArrayList<String> getScannedRFIDCodes(SQLiteDatabase sqLiteDatabase, String room_id, String rec_id){
        CopyOnWriteArrayList<String> invCodes = new CopyOnWriteArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select " +
                "ci.sec_code from chemical_inventory ci \n" +
                "join scanned_data sc on ci.id = sc.inventory_id \n" +
                "where ci.room_id = "+room_id+" and sc.reconc_id="+rec_id), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if(cursor.getString(cursor.getColumnIndex("sec_code"))!=null) {
                    if (cursor.getString(cursor.getColumnIndex("sec_code")).trim().length() > 0) {
                        invCodes.add(cursor.getString(cursor.getColumnIndex("sec_code")));
                    }
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("select rfid_code,scanned,code from scanned_data where room_id = "+room_id+" and inventory_id=-1 and reconc_id="+rec_id), null);
        if (cursor1.moveToFirst()) {
            while (!cursor1.isAfterLast()) {
                if(cursor1.getString(cursor1.getColumnIndex("rfid_code"))!=null) {
                    if (cursor1.getString(cursor1.getColumnIndex("rfid_code")).trim().length() > 0) {
                        invCodes.add(cursor1.getString(cursor1.getColumnIndex("rfid_code")));
                    }
                }
                if(cursor1.getString(cursor1.getColumnIndex("code"))!=null) {
                    if (cursor1.getString(cursor1.getColumnIndex("code")).trim().length() > 0) {
                        invCodes.add(cursor1.getString(cursor1.getColumnIndex("code")));
                    }
                }
                cursor1.moveToNext();
            }
        }
        cursor1.close();
        return invCodes;
    }

    @SuppressLint("Range")
    public ArrayList<InventoryObject> getFoundInventoryList(SQLiteDatabase sqLiteDatabase, String room_id, String rec_id, String scanType){
        ArrayList<InventoryObject> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select " +
                "ci.name,ci.sec_code,ci.code,sc.scanned,ci.id,ci.quantity, ci.quantity_unit_abbreviation,ci.test_frequency from chemical_inventory ci \n" +
                "join scanned_data sc on ci.id = sc.inventory_id \n" +
                "where ci.room_id = "+room_id+" and sc.reconc_id="+rec_id), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String test_frequency = "";
                if(cursor.getString(cursor.getColumnIndex("test_frequency")).trim().length()>0){
                    test_frequency = cursor.getString(cursor.getColumnIndex("test_frequency"));
                }else {
                    test_frequency = "0";
                }
                String product_name = "";
                String code = "";
                product_name = cursor.getString(cursor.getColumnIndex("name"));
                String rfidCode = "";
                rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                code = cursor.getString(cursor.getColumnIndex("code"));
                boolean flag = false;
                String scanned = "";
                if(cursor.getString(cursor.getColumnIndex("scanned"))!=null) {
                    if (cursor.getString(cursor.getColumnIndex("scanned")).trim().length() > 0) {
                        scanned = cursor.getString(cursor.getColumnIndex("scanned"));
                        flag = true;
                    } else {
                        scanned = "0";
                    }
                } else {
                    scanned = "0";
                }
                String vol = cursor.getString(cursor.getColumnIndex("quantity"))+" "+cursor.getString(cursor.getColumnIndex("quantity_unit_abbreviation"));
                inv.add(new InventoryObject(rfidCode, product_name,id,code,scanned,vol,true,test_frequency));
                cursor.moveToNext();
            }
        }
        cursor.close();
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("select rfid_code,scanned,code from scanned_data where room_id = "+room_id+" and inventory_id=-1 and reconc_id="+rec_id), null);
        if (cursor1.moveToFirst()) {
            while (!cursor1.isAfterLast()) {
                String id = "-1";
                String product_name = "N/A";
                String rfidCode = cursor1.getString(cursor1.getColumnIndex("rfid_code"));
                String code = cursor1.getString(cursor1.getColumnIndex("code"));
                String scanned="";
                if(cursor1.getString(cursor1.getColumnIndex("scanned"))!=null) {
                    if (cursor1.getString(cursor1.getColumnIndex("scanned")).trim().length() > 0) {
                        scanned = cursor1.getString(cursor1.getColumnIndex("scanned"));
                    } else {
                        scanned = "0";
                    }
                } else {
                    scanned = "0";
                }
                inv.add(new InventoryObject(rfidCode, product_name,id,code,scanned,"N/A",true,"0"));
                cursor1.moveToNext();
            }
        }
        cursor1.close();
        return inv;
    }
    @SuppressLint("Range")
    public ArrayList<InventoryObject> getNotFoundInventoryList(SQLiteDatabase sqLiteDatabase, String room_id, String rec_id, String scanType){
        ArrayList<InventoryObject> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select ci.name,ci.sec_code,ci.code,ci.id,ci.quantity, ci.quantity_unit_abbreviation,ci.test_frequency from chemical_inventory ci \n" +
                "where ci.room_id="+room_id+" and ci.status_id != 2 and ci.status_id != 5 and ci.id not in(select inventory_id from scanned_data sc where sc.room_id="+room_id+" and sc.reconc_id="+rec_id+");"), null);
        int count = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String test_frequency = "";
                if(cursor.getString(cursor.getColumnIndex("test_frequency")).trim().length()>0){
                    test_frequency = cursor.getString(cursor.getColumnIndex("test_frequency"));
                }else {
                    test_frequency = "0";
                }
                String product_name = "";
                String code = "";
                product_name = cursor.getString(cursor.getColumnIndex("name"));
                String rfidCode = "";
                rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                code = cursor.getString(cursor.getColumnIndex("code"));
                String vol = cursor.getString(cursor.getColumnIndex("quantity"))+" "+cursor.getString(cursor.getColumnIndex("quantity_unit_abbreviation"));
                inv.add(new InventoryObject(rfidCode, product_name,id,code,null,vol,false,test_frequency));
                cursor.moveToNext();
                count++;
            }
        }
        cursor.close();
        return inv;
    }
    @SuppressLint("Range")
    public ArrayList<InventoryObject> getALLInventoryList(SQLiteDatabase sqLiteDatabase, String room_id, String rec_id, String scanType){
        ArrayList<InventoryObject> inv = new ArrayList<>();
        ArrayList<InventoryObject> invListNotFound = getNotFoundInventoryList(sqLiteDatabase,room_id,rec_id,scanType);
        for (int y = 0;y<invListNotFound.size();y++){
            inv.add(invListNotFound.get(y));
        }
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select ci.name,ci.sec_code,ci.code,sc.scanned,ci.id,ci.quantity, ci.quantity_unit_abbreviation,ci.test_frequency from chemical_inventory ci \n" +
                "left join scanned_data sc on ci.id = sc.inventory_id \n" +
                "where ci.room_id = "+room_id+" and ci.status_id != 2 and ci.status_id != 5 and sc.reconc_id="+rec_id), null);
        int count = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String test_frequency = "";
                if(cursor.getString(cursor.getColumnIndex("test_frequency")).trim().length()>0){
                    test_frequency = cursor.getString(cursor.getColumnIndex("test_frequency"));
                }else {
                    test_frequency = "0";
                }
                String product_name = "";
                String code = "";
                String scanned = "";
                product_name = cursor.getString(cursor.getColumnIndex("name"));
                String rfidCode = "";
                rfidCode = cursor.getString(cursor.getColumnIndex("sec_code"));
                code = cursor.getString(cursor.getColumnIndex("code"));
                boolean flag = false;
                if(cursor.getString(cursor.getColumnIndex("scanned"))!=null) {
                    if (cursor.getString(cursor.getColumnIndex("scanned")).trim().length() > 0) {
                        scanned = cursor.getString(cursor.getColumnIndex("scanned"));
                        flag = true;
                    } else {
                        scanned = "0";
                    }
                } else {
                    scanned = "0";
                }
                String vol = cursor.getString(cursor.getColumnIndex("quantity"))+" "+cursor.getString(cursor.getColumnIndex("quantity_unit_abbreviation"));
                inv.add(new InventoryObject(rfidCode, product_name,id,code,scanned,vol,flag,test_frequency));
                cursor.moveToNext();
                count++;
            }
        }
        cursor.close();
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("select rfid_code,scanned,code from scanned_data where room_id = "+room_id+" and inventory_id=-1 and reconc_id="+rec_id), null);
        if (cursor1.moveToFirst()) {
            while (!cursor1.isAfterLast()) {
                String id = "-1";
                String product_name = "N/A";
                String rfidCode = cursor1.getString(cursor1.getColumnIndex("rfid_code"));
                String code = cursor1.getString(cursor1.getColumnIndex("code"));
                String scanned="";
                if(cursor1.getString(cursor1.getColumnIndex("scanned"))!=null) {
                    if (cursor1.getString(cursor1.getColumnIndex("scanned")).trim().length() > 0) {
                        scanned = cursor1.getString(cursor1.getColumnIndex("scanned"));
                    } else {
                        scanned = "0";
                    }
                } else {
                    scanned = "0";
                }
                inv.add(new InventoryObject(rfidCode, product_name,id,code,scanned,"N/A",true,"0"));
                cursor1.moveToNext();
            }
        }
        cursor1.close();
        return inv;
    }
    @SuppressLint("Range")
    public ArrayList<RFIDScanDataObj> getALLInventoryScannedList(SQLiteDatabase sqLiteDatabase,String rec_id){
        ArrayList<RFIDScanDataObj> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select inventory_id, scanned_by, scanned_date,rfid_code,code from scanned_data where reconc_id="+rec_id), null);
        int count = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String inventory_id = cursor.getString(cursor.getColumnIndex("inventory_id"));
                String code = cursor.getString(cursor.getColumnIndex("code"));
                String scanned_by = cursor.getString(cursor.getColumnIndex("scanned_by"));
                String rfid_code="";
                if(cursor.getString(cursor.getColumnIndex("rfid_code"))!=null) {
                    if (cursor.getString(cursor.getColumnIndex("rfid_code")).trim().length() > 0) {
                        rfid_code = cursor.getString(cursor.getColumnIndex("rfid_code"));
                    } else {
                        rfid_code = "";
                    }
                } else {
                    rfid_code = "";
                }
                String scanned_date = cursor.getString(cursor.getColumnIndex("scanned_date"));
                inv.add(new RFIDScanDataObj(inventory_id, scanned_by,scanned_date,rfid_code,code));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return inv;
    }
    @SuppressLint("Range")
    public String getRoomName(SQLiteDatabase sqLiteDatabase, String room_id){
        String sql = "SELECT room,id FROM fi_facil_rooms " +
                "where status = 'active' and id = "+room_id;
        Cursor cursor2 = sqLiteDatabase.rawQuery(sql,null);
        String roomName="";
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                 roomName = cursor2.getString(cursor2.getColumnIndex("room"));
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        return roomName;
    }
    @SuppressLint("Range")
    public String getFacilName(SQLiteDatabase sqLiteDatabase, String facil_id){
        String sql = "SELECT name,id FROM fi_facilities " +
                "where status = 'active' and id = "+facil_id;
        Cursor cursor2 = sqLiteDatabase.rawQuery(sql,null);
        String facilName="";
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                facilName = cursor2.getString(cursor2.getColumnIndex("name"));
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        return facilName;
    }
    @SuppressLint("Range")
    public String getScanType(SQLiteDatabase sqLiteDatabase, String id){
        String sql = "SELECT scan_type FROM scanned_json_data " +
                "where id = "+id;
        Cursor cursor2 = sqLiteDatabase.rawQuery(sql,null);
        String scan_type="";
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                scan_type = cursor2.getString(cursor2.getColumnIndex("scan_type"));
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        return scan_type;
    }
    public void delAllSavedScanData(SQLiteDatabase sqLiteDatabase, String room_id){
        sqLiteDatabase.delete("scanned_data", "room_id=?", new String[]{room_id});
        sqLiteDatabase.delete("scanned_json_data", "room_id=?", new String[]{room_id});
    }
    public void delSavedScanData(SQLiteDatabase sqLiteDatabase, String user_id,String room_id, String reconc_id){
        sqLiteDatabase.delete("scanned_json_data", "user_id=? and room_id=? and reconc_id=?", new String[]{user_id,room_id,reconc_id});
        sqLiteDatabase.delete("scanned_data", "room_id=? and reconc_id=?", new String[]{room_id,reconc_id});
    }
    public void delSavedScanDataOnly(SQLiteDatabase sqLiteDatabase, String user_id,String room_id, String reconc_id){
        sqLiteDatabase.delete("scanned_data", "room_id=? and reconc_id=?", new String[]{room_id,reconc_id});
    }
    public void delSavedScanDatabyId(SQLiteDatabase sqLiteDatabase, String id, String rec_id){
        sqLiteDatabase.delete("scanned_json_data", "id=?", new String[]{id});
        sqLiteDatabase.delete("scanned_data", "reconc_id=?", new String[]{rec_id});
    }
    @SuppressLint("Range")
    public MyObject[] getAutoSearchBuildingsData(SQLiteDatabase sqLiteDatabase, String searchTerm) {
        String sql = "SELECT name,id FROM fi_facilities " +
                "where status = 'active' and name like '%"+searchTerm+"%' order by name asc";
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
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        // return the list of records
        return ObjectItemData;
    }
    @SuppressLint("Range")
    public MyObject[] getAutoSearchRoomsData(SQLiteDatabase sqLiteDatabase, String searchTerm,String facil_id) {
        String sql = "";
        if(facil_id.trim().length()>0)
            sql = "SELECT room,id FROM fi_facil_rooms where status = 'active' and room like '%"+searchTerm+"%' and facil_id="+facil_id+" order by room asc";
        else
            sql = "SELECT room,id FROM fi_facil_rooms where status = 'active' and room like '%"+searchTerm+"%' order by room asc ";
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
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        // return the list of records
        return ObjectItemData;
    }
    @SuppressLint("Range")
    public MyObject[] getAutoSearchStatusData(SQLiteDatabase sqLiteDatabase, String searchTerm, String role) {
        String sql = "";
        if(Integer.parseInt(role)!=4){
            sql = "SELECT status,id FROM inventory_status where status like '%"+searchTerm+"%' and id in (1,10)";
        }else if (Integer.parseInt(role)==4){
            sql = "SELECT status,id FROM inventory_status where status like '%"+searchTerm+"%' and id in (1,2,0,10)";
        }
        Cursor cursor2 = sqLiteDatabase.rawQuery(sql,null);
        int recCount = cursor2.getCount();
        MyObject[] ObjectItemData = new MyObject[recCount];
        int x = 0;
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                String objectName = cursor2.getString(cursor2.getColumnIndex("status"));
                String objectId = cursor2.getString(cursor2.getColumnIndex("id"));
                MyObject myObject = new MyObject(objectName, objectId);
                ObjectItemData[x] = myObject;
                x++;
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        // return the list of records
        return ObjectItemData;
    }
    @SuppressLint("Range")
    public MyObject[] getAutoSearchOwnerData(SQLiteDatabase sqLiteDatabase, String searchTerm, String pu) {
        String sql = "";
        if(pu.trim().length()>0){
            sql = "SELECT primary_user_id as user_id,primary_user as user FROM primary_users where primary_user like '%"+searchTerm+"%'";
        }else{
            sql = "SELECT distinct object_id as user_id,owner as user FROM chemical_inventory where owner like '%"+searchTerm+"%' and object_table='site_users'";
        }
        Cursor cursor2 = sqLiteDatabase.rawQuery(sql,null);
        int recCount = cursor2.getCount();
        MyObject[] ObjectItemData = new MyObject[recCount];
        int x = 0;
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                String objectName = cursor2.getString(cursor2.getColumnIndex("user"));
                String objectId = cursor2.getString(cursor2.getColumnIndex("user_id"));
                MyObject myObject = new MyObject(objectName, objectId);
                ObjectItemData[x] = myObject;
                x++;
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        // return the list of records
        return ObjectItemData;
    }
    @SuppressLint("Range")
    public MyObject[] getAutoSearchUnitData(SQLiteDatabase sqLiteDatabase, String searchTerm) {
        String sql = "SELECT abbreviation,id FROM units_of_measure where abbreviation like '%"+searchTerm+"%'";
        Cursor cursor2 = sqLiteDatabase.rawQuery(sql,null);
        int recCount = cursor2.getCount();
        MyObject[] ObjectItemData = new MyObject[recCount];
        int x = 0;
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                String objectName = cursor2.getString(cursor2.getColumnIndex("abbreviation"));
                String objectId = cursor2.getString(cursor2.getColumnIndex("id"));
                MyObject myObject = new MyObject(objectName, objectId);
                ObjectItemData[x] = myObject;
                x++;
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        // return the list of records
        return ObjectItemData;
    }
    public void updateInventoryDetails(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        sqLiteDatabase.update(QueryConstants.TABLE_NAME_CHEMICAL_INVENTORY, cv, "code=?", new String[]{cv.getAsString("code")});
    }

    public void batchInsert(ArrayList<BatchInsertionObject> dataList, SQLiteDatabase sqLiteDatabase) {
        insertStatement = sqLiteDatabase.compileStatement(INSERT_QUERY);
        sqLiteDatabase.beginTransaction();
        try {
            for (BatchInsertionObject data : dataList) {
                insertStatement.bindString(1, data.getLocation_id());
                insertStatement.bindString(2, data.getRoom_id());
                insertStatement.bindString(3, data.getInventory_id());
                insertStatement.bindString(4, data.getScanned_by());
                insertStatement.bindString(5, data.getScanned());
                insertStatement.bindString(6, data.getReconc_id());
                insertStatement.bindString(7, data.getRfid_code());
                insertStatement.execute();
                insertStatement.clearBindings();
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            insertStatement = null;
        }
    }
    public void batchInsertChemInventory(ArrayList<BatchInsertionObjectInventory> dataList, SQLiteDatabase sqLiteDatabase) {
        insertStatement = sqLiteDatabase.compileStatement(INSERT_QUERY_CHEM_INVENTORY);
        sqLiteDatabase.beginTransaction();
        StringBuilder values = new StringBuilder();
        try {
            for (BatchInsertionObjectInventory data : dataList) {
                /*String pu = null;
                if (data.getPrimary_user_id().trim().length()>0){
                    pu = data.getPrimary_user_id();
                }
                String room_id = null;
                if (data.getRoom_id().trim().length()>0){
                    room_id = data.getRoom_id();
                }
                String object_id = null;
                if (data.getObject_id().trim().length()>0){
                    object_id = data.getObject_id();
                }
                String loc_id = null;
                if (data.getLoc_id().trim().length()>0){
                    loc_id = data.getLoc_id();
                }
                String modified_user_id = null;
                if (data.getModified_user_id().trim().length()>0){
                    modified_user_id = data.getModified_user_id();
                }
                String facil_id = null;
                if (data.getFacil_id().trim().length()>0){
                    facil_id = data.getFacil_id();
                }
                String status_id = null;
                if (data.getStatus_id().trim().length()>0){
                    status_id = data.getStatus_id();
                }
                String quan_abb_id = null;
                if (data.getQuantity_unit_abbreviation_id().trim().length()>0){
                    quan_abb_id = data.getQuantity_unit_abbreviation_id();
                }
                String conc_abb_id = null;
                if (data.getConcentration_unit_abbrevation_id().trim().length()>0){
                    conc_abb_id = data.getConcentration_unit_abbrevation_id();
                }
                String quan = null;
                if (data.getQuantity().trim().length()>0){
                    quan = data.getQuantity();
                }
                String conc = null;
                if (data.getConcentration().trim().length()>0){
                    conc = data.getConcentration();
                }


                values.append("("+data.getId()+",'"
                        +data.getOpened_date().replaceAll("'","")+"'"+
                        ",'"+data.getName().replaceAll("'","")+"'"+
                        ","+room_id+
                        ",'"+data.getSec_code().replaceAll("'","")+"'"+
                        ",'"+data.getObject_table().replaceAll("'","")+"'"+
                        ","+modified_user_id+
                        ",'"+data.getModified_date().replaceAll("'","")+"'"+
                        ",'"+data.getLast_test_date().replaceAll("'","")+"'"+
                        ","+pu+
                        ",'"+data.getLot().replaceAll("'","")+"'"+
                        ",'"+data.getCreate_date().replaceAll("'","")+"'"+
                        ",'"+data.getCode().replaceAll("'","")+"'"+
                        ",'"+data.getExpiration_date().replaceAll("'","")+"'"+
                        ","+data.getCreate_user_id()+
                        ","+object_id+
                        ","+facil_id+
                        ",'"+data.getRoom().replaceAll("'","")+"'"+
                        ",'"+data.getReceipt_date().replaceAll("'","")+"'"+
                        ",'"+data.getNotes().replaceAll("'","")+"'"+
                        ",'"+data.getComment().replaceAll("'","")+"'"+
                        ","+quan+
                        ","+conc+
                        ",'"+data.getQuantity_unit_abbreviation().replaceAll("'","")+"'"+
                        ","+quan_abb_id+
                        ",'"+data.getConcentration_unit_abbrevation().replaceAll("'","")+"'"+
                        ","+conc_abb_id+
                        ",'"+data.getCas_number().replaceAll("'","")+"'"+
                        ",'"+data.getStatus().replaceAll("'","")+"'"+
                        ","+status_id+
                        ",'"+data.getLoc().replaceAll("'","")+"'"+
                        ","+loc_id+
                        ",'"+data.getOwner().replaceAll("'","")+"'),"
                );*/
                insertStatement.bindString(1, data.getId());
                insertStatement.bindString(2, data.getOpened_date());
                insertStatement.bindString(3, data.getName());
                insertStatement.bindString(4, data.getRoom_id());
                insertStatement.bindString(5, data.getSec_code());
                insertStatement.bindString(6, data.getObject_table());
                insertStatement.bindString(7, data.getModified_user_id());
                insertStatement.bindString(8, data.getModified_date());
                insertStatement.bindString(9, data.getLast_test_date());
                insertStatement.bindString(10, data.getPrimary_user_id());
                insertStatement.bindString(11, data.getLot());
                insertStatement.bindString(12, data.getCreate_date());
                insertStatement.bindString(13, data.getCode());
                insertStatement.bindString(14, data.getExpiration_date());
                insertStatement.bindString(15, data.getCreate_user_id());
                insertStatement.bindString(16, data.getObject_id());
                insertStatement.bindString(17, data.getFacil_id());
                insertStatement.bindString(18, data.getRoom());
                insertStatement.bindString(19, data.getReceipt_date());
                insertStatement.bindString(20, data.getNotes());
                insertStatement.bindString(21, data.getComment());
                insertStatement.bindString(22, data.getQuantity());
                insertStatement.bindString(23, data.getConcentration());
                insertStatement.bindString(24, data.getQuantity_unit_abbreviation());
                insertStatement.bindString(25, data.getQuantity_unit_abbreviation_id());
                insertStatement.bindString(26, data.getConcentration_unit_abbrevation());
                insertStatement.bindString(27, data.getConcentration_unit_abbrevation_id());
                insertStatement.bindString(28, data.getCas_number());
                insertStatement.bindString(29, data.getStatus());
                insertStatement.bindString(30, data.getStatus_id());
                insertStatement.bindString(31, data.getLoc());
                insertStatement.bindString(32, data.getLoc_id());
                insertStatement.bindString(33, data.getTest_frequency());
                insertStatement.bindString(34, data.getOwner());
                insertStatement.execute();
                insertStatement.clearBindings();
            }
            /*sqLiteDatabase.execSQL("INSERT INTO chemical_inventory \n" +
                    "(id, \n" +
                    "opened_date, \n" +
                    "name, \n" +
                    "room_id, \n" +
                    "sec_code, \n" +
                    "object_table, \n" +
                    "modified_user_id, \n" +
                    "modified_date, \n" +
                    "last_test_date, \n" +
                    "primary_user_id, \n" +
                    "lot, \n" +
                    "create_date, \n" +
                    "code, \n" +
                    "expiration_date, \n" +
                    "create_user_id, \n" +
                    "object_id, \n" +
                    "facil_id, \n" +
                    "room, \n" +
                    "receipt_date, \n" +
                    "notes, \n" +
                    "comment, \n" +
                    "quantity, \n" +
                    "concentration, \n" +
                    "quantity_unit_abbreviation, \n" +
                    "quantity_unit_abbreviation_id, \n" +
                    "concentration_unit_abbrevation, \n" +
                    "concentration_unit_abbrevation_id, \n" +
                    "cas_number, \n" +
                    "status, \n" +
                    "status_id, \n" +
                    "loc, \n" +
                    "loc_id, \n" +
                    "owner) VALUES " + values.toString().substring(0,values.toString().length()-1));*/
            //db.setTransactionSuccessful();
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            insertStatement = null;
        }
    }
    public void batchInsertRooms(ArrayList<BatchInsertRooms> dataList, SQLiteDatabase sqLiteDatabase) {
        insertStatement = sqLiteDatabase.compileStatement(INSERT_QUERY_ROOMS);
        sqLiteDatabase.beginTransaction();
        try {
            for (BatchInsertRooms data : dataList) {
                insertStatement.bindString(1, data.getRoom());
                insertStatement.bindString(2, data.getArea());
                insertStatement.bindString(3, data.getImg_src());
                insertStatement.bindString(4, data.getType_id());
                insertStatement.bindString(5, data.getId());
                insertStatement.bindString(6, data.getStatus());
                insertStatement.bindString(7, data.getNotes());
                insertStatement.bindString(8, data.getFacil_id());
                insertStatement.execute();
                insertStatement.clearBindings();
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            insertStatement = null;
        }
    }
    public void batchInsertPUs(ArrayList<BatchInsertPUs> dataList, SQLiteDatabase sqLiteDatabase) {
        insertStatement = sqLiteDatabase.compileStatement(INSERT_QUERY_PRIMARY_USERS);
        sqLiteDatabase.beginTransaction();
        try {
            for (BatchInsertPUs data : dataList) {
                insertStatement.bindString(1, data.getPrimary_user());
                insertStatement.bindString(2, data.getPrimary_user_id());
                insertStatement.execute();
                insertStatement.clearBindings();
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            insertStatement = null;
        }
    }
}

