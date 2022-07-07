package com.safetystratus.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    public static final String PASS_PHRASE = DatabaseConstants.PASS_PHRASE;
    boolean connected = false;
    String loggedinUsername = "";
    String loggedinUserSiteId = "";
    String md5Pwd = "";
    String selectedUserId = "";
    String selectedSearchValue = "";
    String lastCompletedInspectionSiteInfo = "";
    String sso = "";
    String site_name = "";
    String request_token="";
    final String[] site_id = {""};
    final String[] user_id = {""};
    final String[] token = {""};
    ProgressDialog progressSynStart = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SQLiteDatabase.loadLibs(this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(HomeActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo result = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(result!=null) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            } else
                connected = false;
        }else{
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                connected = true;
            }else{
                connected = false;
            }
        }
        Intent intent = getIntent();
        sso = intent.getStringExtra("sso");
        if (intent.getStringExtra("token") != null) {
            request_token = intent.getStringExtra("token");
        }
        site_name = intent.getStringExtra("site_name");
        loggedinUsername = intent.getStringExtra("username");
        selectedUserId = intent.getStringExtra("selectedUserId");
        loggedinUserSiteId = intent.getStringExtra("site_id");
        lastCompletedInspectionSiteInfo = loggedinUserSiteId;
        md5Pwd = intent.getStringExtra("md5pwd");
        if (intent.getStringExtra("selectedSearchValue") != null) {
            selectedSearchValue = intent.getStringExtra("selectedSearchValue");
        }
        if (connected) {
            //Log.e("under page null>>", "**"+sso);
            progressSynStart = new ProgressDialog(HomeActivity.this);
            progressSynStart.setTitle("");
            progressSynStart.setMessage("Synchronizing..");
            progressSynStart.setCancelable(false);
            progressSynStart. show();
            progressSynStart.getWindow().setLayout(800, 300);
            if (sso.equals("false")) {
                //Log.e("under page null1>>", "**"+sso);
               /* getAccessToken(new CallBackInterface() {
                    @Override
                    public void data(String msg) throws JSONException {
                        JSONObject jsonObj = new JSONObject(msg);
                        site_id[0] = jsonObj.get("site_id").toString();
                        user_id[0] = jsonObj.get("user_id").toString();
                        token[0] = jsonObj.get("access_token").toString();
                        insertDbData(site_id[0], user_id[0], token[0]);
                    }
                });*/
                getAccessToken();
            } else {
                site_id[0] = lastCompletedInspectionSiteInfo;
                user_id[0] = selectedUserId;
                token[0] = request_token;
                /*if(my_first_run_key_exists.equals("0"))
                    insertDbData(site_id[0], user_id[0], token[0]);
                else
                    insertDbDataDelta(site_id[0], user_id[0], token[0]);*/
            }
        } else {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(HomeActivity.this);
            dlgAlert.setTitle("Not getting connection");
            dlgAlert.setMessage("Please check your wifi or mobile data!!");
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            dlgAlert.create().show();
        }
    }
    public void getAccessToken() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = ApiConstants.accessTokenUrl;
            HashMap<String, String> params = new HashMap<String, String>();
            // params.put("password", ApiConstants.password);
            if (sso.equals("false")) {
                params.put("password", md5Pwd);
            }else{
                params.put("request_token", request_token);
            }
            params.put("username", loggedinUsername);
            params.put("site_id", loggedinUserSiteId);
            params.put("device_id", "test_device_id");
            JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Process os success response
                            try {
                                String res = response.toString();
                                if(res.contains("Success:")){
                                    JSONObject jsonObj = new JSONObject(res);
                                    String site_id = jsonObj.get("site_id").toString();
                                    String user_id = jsonObj.get("user_id").toString();
                                    String token = jsonObj.get("access_token").toString();
                                    insertDbData(site_id, user_id, token);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(HomeActivity.this);
                        dlgAlert.setTitle("Safety Stratus");
                        dlgAlert.setMessage("Error response: Request timed out please check your network and try again");
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (progressSynStart != null && progressSynStart.isShowing())
                                            progressSynStart.dismiss();
                                        return;
                                    }
                                });
                        dlgAlert.create().show();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String jsonError = new String(error.networkResponse.data);
                            Log.e("error res***", jsonError);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            int socketTimeout = 60000;//3 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
            request_json.setRetryPolicy(policy);
// add the request object to the queue to be executed
            requestQueue.add(request_json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void insertDbData(String siteId, String userId, String token) {
        String url = String.format(ApiConstants.downloadDbUrl, siteId, userId, token);
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (progressSynStart != null && progressSynStart.isShowing()){
                    progressSynStart.dismiss();
                    progressSynStart = null;
                }
                Log.e("respomseDB>>",response.toString());
                SyncDbDialogs sdb = new SyncDbDialogs();
                sdb.execute(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(HomeActivity.this);
                dlgAlert.setTitle("Safety Stratus");
                dlgAlert.setMessage("Error response: Request timed out please check your network and try again");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                dlgAlert.create().show();
            }
        });
        int socketTimeout = 60000;//3 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
        objectRequest.setRetryPolicy(policy);
        requestQueue.add(objectRequest);
    }

    class SyncDbDialogs extends AsyncTask<String, String, String> {

        private ProgressDialog progressSync = new ProgressDialog(HomeActivity.this);
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(HomeActivity.this);
        final SQLiteDatabase db = databaseHandler.getWritableDatabase(PASS_PHRASE);

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // disable dismiss by tapping outside of the dialog
            progressSync.setTitle("");
            progressSync.setMessage("Uploading..");
            progressSync.setCancelable(false);
            progressSync.show();
            progressSync.getWindow().setLayout(800, 300);
            super.onPreExecute();
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            databaseHandler.getWritableDatabase(PASS_PHRASE).beginTransaction();
            try {
                JSONObject obj = new JSONObject(params[0].toString());
                ContentValues values = new ContentValues();
                JSONArray jsonArrayOtDepartments = obj.getJSONArray("ot_department");
                JSONArray jsonArraySiteUsers = obj.getJSONArray("site_users");
                JSONArray jsonArrayFiLocations = obj.getJSONArray("fi_locations");
                JSONArray jsonArrayMenuItems = obj.getJSONArray("menu_items");
                JSONArray jsonArrayFiFacilRooms = obj.getJSONArray("fi_facil_rooms");
                JSONArray jsonArrayFiRoomDept = obj.getJSONArray("fi_room_dept");
                JSONArray jsonArrayFiRoomTypes = obj.getJSONArray("fi_room_types");
                JSONArray jsonArraySettings = obj.getJSONArray("settings");
                JSONArray jsonArrayLabels = obj.getJSONArray("labels");
                JSONArray jsonArrayFiFacilities = obj.getJSONArray("fi_facilities");
                JSONArray jsonArrayMenuCategories = obj.getJSONArray("menu_categories");
                JSONArray jsonArrayOtOrganization = obj.getJSONArray("ot_organization");
                JSONArray jsonArrayChemicalInventory = obj.getJSONArray("chemical_inventory");
               // Log.e("TESRRR11>>",jsonArrayChemicalInventory.toString()+"**");
               // JSONArray jsonArrayFiRoomRoster = obj.getJSONArray("fi_room_roster");
                db.delete(QueryConstants.TABLE_NAME_SITE_USERS, null, null);
                db.delete(QueryConstants.TABLE_NAME_OT_ORGANIZATION, null, null);
                db.delete(QueryConstants.TABLE_NAME_OT_DEPARTMENT, null, null);
                db.delete(QueryConstants.TABLE_NAME_FI_LOCATIONS, null, null);
                db.delete(QueryConstants.TABLE_NAME_FI_FACILITIES, null, null);
                db.delete(QueryConstants.TABLE_NAME_FI_ROOM_TYPES, null, null);
                db.delete(QueryConstants.TABLE_NAME_FI_FACIL_ROOMS, null, null);
                db.delete(QueryConstants.TABLE_NAME_FI_ROOM_DEPT, null, null);
                //db.delete(QueryConstants.TABLE_NAME_FI_ROOM_ROSTER, null, null);
                db.delete(QueryConstants.TABLE_NAME_LABELS, null, null);
                db.delete(QueryConstants.TABLE_NAME_MENU_CATEGORIES, null, null);
                db.delete(QueryConstants.TABLE_NAME_MENU_ITEMS, null, null);
                db.delete(QueryConstants.TABLE_NAME_SETTINGS, null, null);
                db.delete(QueryConstants.TABLE_NAME_CHEMICAL_INVENTORY, null, null);
                for (int i = 0, size = jsonArrayChemicalInventory.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayChemicalInventory.getJSONObject(i);
                    String id = objectInArray.getString("id");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_OT_ORGANIZATION, "id", id) == 0) {
                        values.put("id", id);
                        values.put("opened_date", objectInArray.getString("opened_date"));
                        values.put("sec_code", objectInArray.getString("sec_code"));
                        values.put("object_table", objectInArray.getString("object_table"));
                        values.put("modified_user_id", objectInArray.getString("modified_user_id"));
                        values.put("modified_date", objectInArray.getString("modified_date"));
                        values.put("last_test_date", objectInArray.getString("last_test_date"));
                        values.put("lot", objectInArray.getString("lot"));
                        values.put("create_date", objectInArray.getString("create_date"));
                        values.put("code", objectInArray.getString("code"));
                        values.put("expiration_date", objectInArray.getString("expiration_date"));
                        values.put("create_user_id", objectInArray.getString("create_user_id"));
                        values.put("object_id", objectInArray.getString("object_id"));
                        values.put("receipt_date", objectInArray.getString("receipt_date"));
                        db.insert(QueryConstants.TABLE_NAME_CHEMICAL_INVENTORY, null, values);
                        values.clear();
                    }
                }
                for (int i = 0, size = jsonArrayOtOrganization.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayOtOrganization.getJSONObject(i);
                    String id = objectInArray.getString("id");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_OT_ORGANIZATION, "id", id) == 0) {
                        values.put("id", id);
                        values.put("location_id", objectInArray.getString("location_id"));
                        values.put("name", objectInArray.getString("name"));
                        values.put("org_cd", objectInArray.getString("org_cd"));
                        values.put("short_name", objectInArray.getString("short_name"));
                        values.put("status", objectInArray.getString("status"));
                        db.insert(QueryConstants.TABLE_NAME_OT_ORGANIZATION, null, values);
                        values.clear();
                    }
                }
                for (int i = 0, size = jsonArrayMenuCategories.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayMenuCategories.getJSONObject(i);
                    String id = objectInArray.getString("id");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_MENU_CATEGORIES, "id", id) == 0) {
                        values.put("id", id);
                        values.put("name", objectInArray.getString("name"));
                        values.put("sort", objectInArray.getString("sort"));
                        db.insert(QueryConstants.TABLE_NAME_MENU_CATEGORIES, null, values);
                        values.clear();
                    }
                }
                for (int i = 0, size = jsonArrayFiFacilities.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayFiFacilities.getJSONObject(i);
                    String id = objectInArray.getString("id");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_FI_FACILITIES, "id", id) == 0) {
                        values.put("location_id", objectInArray.getString("location_id"));
                        values.put("name", objectInArray.getString("name"));
                        values.put("id", id);
                        values.put("short_name", objectInArray.getString("short_name"));
                        values.put("status", objectInArray.getString("status"));
                        db.insert(QueryConstants.TABLE_NAME_FI_FACILITIES, null, values);
                        values.clear();
                    }
                }
                for (int i = 0, size = jsonArrayLabels.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayLabels.getJSONObject(i);
                    values.put("value", objectInArray.getString("value"));
                    values.put("label", objectInArray.getString("label"));
                    if (objectInArray.has("last_updated")) {
                        values.put("last_updated", objectInArray.getString("last_updated"));
                    }
                    db.insert(QueryConstants.TABLE_NAME_LABELS, null, values);
                    values.clear();
                }
                for (int i = 0, size = jsonArraySettings.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArraySettings.getJSONObject(i);
                    String setting = objectInArray.getString("setting");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_SETTINGS, "setting", setting) == 0) {
                        values.put("setting", setting);
                        values.put("value", objectInArray.getString("value"));
                        db.insert(QueryConstants.TABLE_NAME_SETTINGS, null, values);
                        values.clear();
                    }
                }
                /*for (int i = 0, size = jsonArrayFiRoomRoster.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayFiRoomRoster.getJSONObject(i);
                    String user_id = objectInArray.getString("user_id");
                    String roster_type_id = objectInArray.getString("roster_type_id");
                    String room_id = objectInArray.getString("room_id");
                    if (databaseHandler.checkDuplicatesThreePrimaryKeys(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_FI_ROOM_ROSTER, "user_id",
                            "room_id", "roster_type_id", user_id, room_id, roster_type_id) == 0) {
                        values.put("user_id", user_id);
                        values.put("room_id", room_id);
                        values.put("latch", objectInArray.getString("latch"));
                        values.put("roster_type_id", roster_type_id);
                        values.put("type", objectInArray.getString("type"));
                        db.insert(QueryConstants.TABLE_NAME_FI_ROOM_ROSTER, null, values);
                        values.clear();
                    }
                }*/
                for (int i = 0, size = jsonArrayFiRoomTypes.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayFiRoomTypes.getJSONObject(i);
                    String type_id = objectInArray.getString("type_id");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_FI_ROOM_TYPES, "type_id", type_id) == 0) {
                        values.put("cycle_default", objectInArray.getString("cycle_default"));
                        values.put("cycle_duration", objectInArray.getString("cycle_duration"));
                        values.put("required", objectInArray.getString("required"));
                        values.put("type_id", type_id);
                        values.put("status", objectInArray.getString("status"));
                        values.put("type", objectInArray.getString("type"));
                        values.put("cycle_buffer", objectInArray.getString("cycle_buffer"));
                        db.insert(QueryConstants.TABLE_NAME_FI_ROOM_TYPES, null, values);
                        values.clear();
                    }
                }
                for (int i = 0, size = jsonArrayFiRoomDept.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayFiRoomDept.getJSONObject(i);
                    String dept_id = objectInArray.getString("dept_id");
                    String room_id = objectInArray.getString("room_id");
                    if (databaseHandler.checkDuplicatesTwoPrimaryKeys(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_FI_ROOM_DEPT,
                            "dept_id", "room_id",
                            dept_id, room_id) == 0) {
                        values.put("dept_id", dept_id);
                        values.put("room_id", room_id);
                        db.insert(QueryConstants.TABLE_NAME_FI_ROOM_DEPT, null, values);
                        values.clear();
                    }
                }
                for (int i = 0, size = jsonArrayFiFacilRooms.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayFiFacilRooms.getJSONObject(i);
                    String id = objectInArray.getString("id");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_FI_FACIL_ROOMS, "id", id) == 0) {
                        values.put("room", objectInArray.getString("room"));
                        values.put("area", objectInArray.getString("area"));
                        values.put("img_src", objectInArray.getString("img_src"));
                        values.put("type_id", objectInArray.getString("type_id"));
                        values.put("id", id);
                        values.put("status", objectInArray.getString("status"));
                        values.put("notes", objectInArray.getString("notes"));
                        values.put("facil_id", objectInArray.getString("facil_id"));
                        db.insert(QueryConstants.TABLE_NAME_FI_FACIL_ROOMS, null, values);
                        values.clear();
                    }
                }
                for (int i = 0, size = jsonArrayMenuItems.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayMenuItems.getJSONObject(i);
                    String id = objectInArray.getString("id");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_MENU_ITEMS, "id", id) == 0) {
                        values.put("sort", objectInArray.getString("sort"));
                        values.put("name", objectInArray.getString("name"));
                        values.put("id", id);
                        values.put("m_cat_id", objectInArray.getString("m_cat_id"));
                        values.put("descr", objectInArray.getString("descr"));
                        db.insert(QueryConstants.TABLE_NAME_MENU_ITEMS, null, values);
                        values.clear();
                    }
                }
                for (int i = 0, size = jsonArrayOtDepartments.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayOtDepartments.getJSONObject(i);
                    String id = objectInArray.getString("id");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_OT_DEPARTMENT, "id", id) == 0) {
                        values.put("dept_cd", objectInArray.getString("dept_cd"));
                        values.put("name", objectInArray.getString("name"));
                        values.put("id", id);
                        values.put("short_name", objectInArray.getString("short_name"));
                        values.put("status", objectInArray.getString("status"));
                        values.put("org_id", objectInArray.getString("org_id"));
                        db.insert(QueryConstants.TABLE_NAME_OT_DEPARTMENT, null, values);
                        values.clear();
                    }
                }
                for (int i = 0, size = jsonArraySiteUsers.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArraySiteUsers.getJSONObject(i);
                    String user_id = objectInArray.getString("user_id");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_SITE_USERS, "user_id", user_id) == 0) {
                        values.put("phone", objectInArray.getString("phone"));
                        values.put("firstname", objectInArray.getString("firstname"));
                        values.put("user_id", user_id);
                        values.put("primary_group", objectInArray.getString("primary_group"));
                        values.put("username", objectInArray.getString("username"));
                        values.put("active", objectInArray.getString("active"));
                        values.put("email_address", objectInArray.getString("email_address"));
                        values.put("lastname", objectInArray.getString("lastname"));
                        db.insert(QueryConstants.TABLE_NAME_SITE_USERS, null, values);
                        values.clear();
                    }
                }
                for (int i = 0, size = jsonArrayFiLocations.length(); i < size; i++) {
                    JSONObject objectInArray = jsonArrayFiLocations.getJSONObject(i);
                    String id = objectInArray.getString("id");
                    if (databaseHandler.checkDuplicates(databaseHandler.getWritableDatabase(DatabaseConstants.PASS_PHRASE), QueryConstants.TABLE_NAME_FI_LOCATIONS, "id", id) == 0) {
                        values.put("location_cd", objectInArray.getString("location_cd"));
                        values.put("name", objectInArray.getString("name"));
                        values.put("id", id);
                        values.put("short_name", objectInArray.getString("short_name"));
                        values.put("status", objectInArray.getString("status"));
                        db.insert(QueryConstants.TABLE_NAME_FI_LOCATIONS, null, values);
                        values.clear();
                    }
                }
                db.setTransactionSuccessful();

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                // progress.dismiss();
                db.close();
                if (databaseHandler != null) {
                    databaseHandler.close();
                }
                if(progressSync!=null && progressSync.isShowing()){
                    progressSync.dismiss();
                    progressSync = null;
                }
            }
            return "completed";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //HashMap<String, String> settings = databaseHandler.getPermissionDetails(databaseHandler.getWritableDatabase(PASS_PHRASE));
            if (progressSync != null && progressSync.isShowing()){
                progressSync.dismiss();
                progressSync = null;
            }
        }

    }

}