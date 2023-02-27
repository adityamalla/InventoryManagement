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
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_SCANNED_JSON_DATA);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_INVENTORY_STATUS);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_UNITS_OF_MEASURE);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_TABLE_SCANNED_BARCODE_JSON_DATA);
        sqLiteDatabase.execSQL(QueryConstants.SQL_CREATE_PRIMARY_USERS);
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
    //@SuppressLint("Range")
    /*public String getUserEmployeeName(SQLiteDatabase sqLiteDatabase, String val) {
        Cursor cursor = null;
        String name = "";
        Log.e("test>>",val);
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
        Log.e("test>>",name);
        return name;
    }*/
    @SuppressLint("Range")
    public ArrayList<ScanInfo> getPendingScans(SQLiteDatabase sqLiteDatabase){
        ArrayList<ScanInfo> scanInfo = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM scanned_json_data"), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String room_id = cursor.getString(cursor.getColumnIndex("room_id"));
                String location_id = cursor.getString(cursor.getColumnIndex("location_id"));
                String roomName = getRoomName(sqLiteDatabase,room_id);
                String locName = getFacilName(sqLiteDatabase,location_id);
                scanInfo.add(
                        new ScanInfo(cursor.getString(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("room_id")),roomName,location_id,locName,cursor.getString(cursor.getColumnIndex("json_data"))));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return scanInfo;
    }
    @SuppressLint("Range")
    public boolean deletePendingScan(SQLiteDatabase sqLiteDatabase,String id){
        boolean deleted = false;
        sqLiteDatabase.delete("scanned_json_data", "id=?", new String[]{id});
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM scanned_json_data where id="+id), null);
        if(cursor.getCount()==0)
            deleted = true;
        cursor.close();
        return deleted;
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
        Cursor cursor = null;
        if(facil_id.trim().length()>0)
             cursor = sqLiteDatabase.rawQuery(String.format("SELECT room,id  \n" +
                "FROM fi_facil_rooms where status = 'active' and facil_id="+facil_id), null);
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
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM chemical_inventory where room_id="+room_id), null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }
    @SuppressLint("Range")
    public int getSavedDataCount(SQLiteDatabase sqLiteDatabase){
        int count = 0;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM scanned_json_data"), null);
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
    @SuppressLint("Range")
    public InventoryModel getScannedInventoryDetails(SQLiteDatabase sqLiteDatabase, String cde){
        InventoryModel inv = null;
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM  chemical_inventory where code='"+cde+"' or sec_code='"+cde+"' limit 1"), null);
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
                inv = new InventoryModel(id, code,name,cas,status_id,status,facil_id,room_id,room,owner,notes,comments,volume_mass,volume_mass_units_id,volume_mass_units,rfidCode,concentration,concentration_unit_abbrevation_id,concentration_unit_abbrevation,object_id,object_table);
                cursor.moveToNext();
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
    public void insertScannedInvDataOutofLocationData(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from scanned_data where room_id="+cv.getAsString("room_id")+"" +
                " and location_id="+cv.getAsString("location_id")+" and inventory_id="+cv.getAsString("inventory_id")+" and rfid_code='"+cv.getAsString("rfid_code")+"'"), null);
        int count = cursor1.getCount();
        cursor1.close();
        if (count==0)
            sqLiteDatabase.insert("scanned_data", null, cv);
    }
    public void insertScannedInvJSONData(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        sqLiteDatabase.delete("scanned_json_data", "room_id=? and location_id=? and user_id=?", new String[]{cv.getAsString("room_id"),cv.getAsString("location_id"),cv.getAsString("user_id")});
        sqLiteDatabase.insert("scanned_json_data", null, cv);
    }
    public void insertScannedBarcodeInvJSONData(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        sqLiteDatabase.delete("scanned_json_data", "code=?", new String[]{cv.getAsString("code")});
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
    public ArrayList<MyObject> getStatusList(SQLiteDatabase sqLiteDatabase){
        int count = 0;
        ArrayList<MyObject> statusList = new ArrayList<MyObject>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT id,status FROM inventory_status"), null);
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
    public int checkScannedDataCount(SQLiteDatabase sqLiteDatabase, String loc_id, String room_id){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from scanned_data where room_id="+room_id+"" +
                " and location_id="+loc_id+" and inventory_id > 0"), null);
        int count = cursor1.getCount();
        Log.e("scannedCount>>",count+"***");
        cursor1.close();
        return count;
    }
    @SuppressLint("Range")
    public boolean checkScannedBarcodeDataAvailable(SQLiteDatabase sqLiteDatabase,  String code){
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * from chemical_inventory where code='"+code+"'"), null);
        int count = cursor1.getCount();
        Log.e("scannedCount>>",count+"***");
        cursor1.close();
        if(count>0)
            return true;
        else
            return false;
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
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("select rfid_code,scanned from scanned_data where room_id = "+room_id+" and inventory_id=-1"), null);
        if (cursor1.moveToFirst()) {
            while (!cursor1.isAfterLast()) {
                String id = "-1";
                String product_name = "";
                String code = "";
                String rfidCode = cursor1.getString(cursor1.getColumnIndex("rfid_code"));
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
                inv.add(new InventoryObject(rfidCode, product_name,id,code,scanned));
                cursor1.moveToNext();
            }
        }
        cursor1.close();
        Log.e("invList>>>",inv.size()+"----");
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
        Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("select rfid_code,scanned from scanned_data where room_id = "+room_id+" and inventory_id=-1"), null);
        if (cursor1.moveToFirst()) {
            while (!cursor1.isAfterLast()) {
                String id = "-1";
                String product_name = "";
                String code = "";
                String rfidCode = cursor1.getString(cursor1.getColumnIndex("rfid_code"));
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
                inv.add(new InventoryObject(rfidCode, product_name,id,code,scanned));
                cursor1.moveToNext();
            }
        }
        cursor1.close();
        Log.e("invListAll>>>",inv.size()+"----");
        return inv;
    }

    @SuppressLint("Range")
    public ArrayList<RFIDScanDataObj> getALLInventoryScannedList(SQLiteDatabase sqLiteDatabase){
        ArrayList<RFIDScanDataObj> inv = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("select inventory_id, scanned_by, scanned_date,rfid_code from scanned_data "), null);
        int count = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String inventory_id = cursor.getString(cursor.getColumnIndex("inventory_id"));
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
                inv.add(new RFIDScanDataObj(inventory_id, scanned_by,scanned_date,rfid_code));
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
    public void delSavedScanData(SQLiteDatabase sqLiteDatabase, String user_id,String room_id){
        sqLiteDatabase.delete("scanned_json_data", "user_id=? and room_id=?", new String[]{user_id,room_id});
    }
    public void delSavedScanDatabyId(SQLiteDatabase sqLiteDatabase, String id){
        sqLiteDatabase.delete("scanned_json_data", "id=?", new String[]{id});
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
    @SuppressLint("Range")
    public MyObject[] getAutoSearchStatusData(SQLiteDatabase sqLiteDatabase, String searchTerm) {
        String sql = "SELECT status,id FROM inventory_status where status like '%"+searchTerm+"%'";
        Log.e("TESTSQL>",sql);
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
                Log.e("Count>>",x+"");
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
            sql = "SELECT owner as user_id,object_id as user FROM chemical_inventory where owner like '%"+searchTerm+"%' and object_table='site_users'";
        }
        Log.e("TESTSQL>",sql);
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
                Log.e("Count>>",x+"");
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        // return the list of records
        return ObjectItemData;
    }
    @SuppressLint("Range")
    public MyObject[] getAutoSearchUnitData(SQLiteDatabase sqLiteDatabase, String searchTerm) {
        String sql = "SELECT abbreviation,id FROM units_of_measure where abbreviation like '%"+searchTerm+"%'";
        Log.e("TESTSQL>",sql);
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
                Log.e("Count>>",x+"");
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        // return the list of records
        return ObjectItemData;
    }
    public void updateInventoryDetails(SQLiteDatabase sqLiteDatabase, ContentValues cv){
        sqLiteDatabase.update(QueryConstants.TABLE_NAME_CHEMICAL_INVENTORY, cv, "code=?", new String[]{cv.getAsString("code")});
    }

}

