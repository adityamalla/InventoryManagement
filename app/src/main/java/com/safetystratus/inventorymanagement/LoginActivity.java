package com.safetystratus.inventorymanagement;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    TextView errorText;
    TextView errorUnameText;
    TextView errorPwdText;
    TextView forgotpassword;
    TextView supportLink;
    Switch sso;
    Button login;
    ListView sites;
    boolean connected = false;
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_SSO = "false";
    String fcmToken = "false";
    SharedPreferences pref;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SQLiteDatabase.loadLibs(this);
        getSupportActionBar().hide();
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        forgotpassword = (TextView) findViewById(R.id.forgetPassword);
        supportLink = (TextView) findViewById(R.id.supportLink);
        login = (Button) findViewById(R.id.button_login);
        ConstraintLayout loginLayout = (ConstraintLayout) findViewById(R.id.loginActivityLayout);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*final Intent myIntent = new Intent(LoginActivity.this,
                        Incident.class);
                myIntent.putExtra("url", ApiConstants.forgotPasswordURL);
                myIntent.putExtra("tempVar", "0");
                startActivity(myIntent);*/
            }
        });
        supportLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "This functionality will be available from next release", Toast.LENGTH_SHORT).show();
            }
        });
        errorText = (TextView) findViewById(R.id.errorText);
        errorUnameText = (TextView) findViewById(R.id.errorUnameText);
        errorPwdText = (TextView) findViewById(R.id.errorPwdText);
        sso = (Switch) findViewById(R.id.sso);
        sso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setVisibility(View.GONE);
                    ConstraintLayout constraintLayout = findViewById(R.id.loginActivityLayout);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.button_login,ConstraintSet.START,R.id.username,ConstraintSet.START,0);
                    constraintSet.connect(R.id.button_login,ConstraintSet.END,R.id.username,ConstraintSet.END,0);
                    constraintSet.connect(R.id.button_login,ConstraintSet.TOP,R.id.username,ConstraintSet.BOTTOM,0);
                    constraintSet.applyTo(constraintLayout);
                    ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) login.getLayoutParams();
                    newLayoutParams.topMargin = 16;
                    newLayoutParams.leftMargin = 0;
                    newLayoutParams.rightMargin = 0;
                    newLayoutParams.bottomMargin = 0;
                    login.setLayoutParams(newLayoutParams);
                } else {
                    password.setVisibility(View.VISIBLE);
                    ConstraintLayout constraintLayout = findViewById(R.id.loginActivityLayout);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.button_login,ConstraintSet.START,R.id.password,ConstraintSet.START,0);
                    constraintSet.connect(R.id.button_login,ConstraintSet.END,R.id.password,ConstraintSet.END,0);
                    constraintSet.connect(R.id.button_login,ConstraintSet.TOP,R.id.password,ConstraintSet.BOTTOM,0);
                    constraintSet.applyTo(constraintLayout);
                    ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) login.getLayoutParams();
                    newLayoutParams.topMargin = 16;
                    newLayoutParams.leftMargin = 0;
                    newLayoutParams.rightMargin = 0;
                    newLayoutParams.bottomMargin = 0;
                    login.setLayoutParams(newLayoutParams);
                }
            }
        });
        pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionCode = packageInfo.versionCode;
        String version = pref.getString("versionCode", null);
        if(version==null){
            SharedPreferences.Editor myEdit = pref.edit();
            myEdit.clear();
            myEdit.commit();
            myEdit.putString("versionCode", versionCode+"");
            myEdit.commit();
        }else if(version!=null){
            if(Integer.parseInt(version)<versionCode){
                SharedPreferences.Editor myEdit = pref.edit();
                myEdit.clear();
                myEdit.commit();
                myEdit.putString("versionCode", versionCode+"");
                myEdit.commit();
            }
        }
        String uname = pref.getString(PREF_USERNAME, null);
        String prefSSO = pref.getString(PREF_SSO, null);

        if (uname != null) {
            username.setText(uname);
        }
        if (prefSSO != null) {
            if (prefSSO.equals("True")) {
                sso.setChecked(true);
            } else {
                sso.setChecked(false);
            }
        }
        //SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile",MODE_PRIVATE);
        String my_first_run_key_exists = pref.getString("my_first_run",null);
        if(my_first_run_key_exists==null){
            SharedPreferences.Editor myEdit = pref.edit();
            myEdit.putString("my_first_run", "0");
            myEdit.commit();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void authenticationCheck(View v) {
        String logged_in_site = pref.getString("logged_in_site",null);
        String logged_in_user = pref.getString("logged_in_user_id",null);
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
        if (connected) {
            hideKeyboard(LoginActivity.this);
            try {
                final ProgressDialog progress = new ProgressDialog(this);
                progress.setTitle("");
                progress.setMessage("Loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                progress.getWindow().setLayout(400, 200);
                String singleSignOn = "";
                boolean singleSign = false;
                if (sso.isChecked()) {
                    singleSignOn = "True";
                    singleSign = true;
                } else {
                    singleSignOn = "False";
                    singleSign = false;
                }
                String uname = username.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .edit()
                        .putString(PREF_USERNAME, uname)
                        .putString(PREF_SSO, singleSignOn)
                        .commit();
                if (!singleSign) {
                    if (!validateInputField(uname)) {
                        errorUnameText.setVisibility(View.VISIBLE);
                        if (errorPwdText.isShown()) {
                            errorPwdText.setVisibility(View.GONE);
                        }
                        progress.dismiss();
                    }
                    else if (!validateInputField(pwd)) {
                        if (errorUnameText.isShown()) {
                            errorUnameText.setVisibility(View.GONE);
                        }
                        errorPwdText.setVisibility(View.VISIBLE);
                        progress.dismiss();
                    }
                    else {
                        if (errorUnameText.isShown()) {
                            errorUnameText.setVisibility(View.GONE);
                        }
                        else if (errorPwdText.isShown()) {
                            errorPwdText.setVisibility(View.GONE);
                        }
                        final String md5pwd = md5(pwd);
                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        String URL = ApiConstants.signInUrl;
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("password", md5pwd);
                        params.put("email", uname);
                        params.put("sso", singleSignOn);
                        Log.e("Test Response0>>",params.toString());
                        final boolean finalSingleSign = singleSign;
                        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Process os success response
                                        try {
                                            Log.e("Test Response>>",response.toString());
                                            if (response.getString("Message").contains("Success")) {
                                                if (errorText.isShown()) {
                                                    errorText.setVisibility(View.GONE);
                                                }
                                                String siteIds = response.getString("site_ids");
                                                String username = response.getString("usernames");
                                                String uid = response.getString("user_ids");
                                                String site_names = response.getString("site_names");
                                                String siteIdsArray[] = siteIds.split(",");
                                                String siteNamesArray[] = site_names.split(",");
                                                String userIdsArray[] = uid.split(",");
                                                String res = response.toString();
                                                if (siteIdsArray.length > 1 && siteNamesArray.length > 1) {
                                                    username = username.split(",")[0];
                                                    ArrayList<SiteInfo> siteInfo = new ArrayList<>();
                                                    for (int i = 0; i < siteIdsArray.length; i++) {
                                                        SiteInfo obj = new SiteInfo(siteIdsArray[i], siteNamesArray[i], userIdsArray[i]);
                                                        siteInfo.add(obj);
                                                    }
                                                    if (siteInfo.size() >= 1) {
                                                        final Dialog dialog = new Dialog(LoginActivity.this);
                                                        dialog.setContentView(R.layout.site_ids_listview);
                                                        dialog.setTitle(response.getString("Message"));
                                                        sites = (ListView) dialog.findViewById(R.id.siteIdsList);
                                                        ArrayAdapter<SiteInfo> adapter = new ArrayAdapter<SiteInfo>(LoginActivity.this, R.layout.siteids_textview, R.id.rowSiteIdsTextView,
                                                                siteInfo);
                                                        adapter.sort(new Comparator<SiteInfo>() {
                                                            @Override
                                                            public int compare(SiteInfo lhs, SiteInfo rhs) {
                                                                return lhs.getSiteName().compareTo(rhs.getSiteName());
                                                            }
                                                        });
                                                        sites.setAdapter(adapter);
                                                        progress.dismiss();
                                                        dialog.show();
                                                        final String finalUsername = username;
                                                        sites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                Object o = sites.getItemAtPosition(position);
                                                                SiteInfo str = (SiteInfo) o; //As you are using Default String Adapter
                                                                    Intent myIntent = new Intent(LoginActivity.this,
                                                                            HomeActivity.class);
                                                                    myIntent.putExtra("username", finalUsername);
                                                                    myIntent.putExtra("site_id", str.getSiteId());
                                                                    myIntent.putExtra("site_name", str.getSiteName());
                                                                    myIntent.putExtra("md5pwd", md5pwd);
                                                                    myIntent.putExtra("sso", "false");
                                                                    myIntent.putExtra("selectedUserId", str.getUserId());
                                                                    myIntent.putExtra("fromLogin", "1");
                                                                    startActivity(myIntent);
                                                            }
                                                        });
                                                    }
                                                }
                                                else {
                                                    Intent myIntent = new Intent(LoginActivity.this,
                                                            HomeActivity.class);
                                                    myIntent.putExtra("username", username);
                                                    myIntent.putExtra("site_id", siteIds);
                                                    myIntent.putExtra("md5pwd", md5pwd);
                                                    myIntent.putExtra("sso", "false");
                                                    myIntent.putExtra("selectedUserId", uid);
                                                    myIntent.putExtra("site_name", site_names);
                                                    myIntent.putExtra("fromLogin", "1");
                                                    progress.dismiss();
                                                    startActivity(myIntent);
                                                }

                                            } else {
                                                errorText.setVisibility(View.VISIBLE);
                                                progress.dismiss();
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (error.networkResponse.statusCode == 401) {
                                            errorText.setVisibility(View.VISIBLE);
                                            progress.dismiss();
                                        } else {
                                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LoginActivity.this);
                                            dlgAlert.setTitle("Safety Stratus");
                                            dlgAlert.setMessage("Slow internet connection, please check your internet connection or try again");
                                            dlgAlert.setPositiveButton("Ok",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (progress != null && progress.isShowing())
                                                                progress.dismiss();
                                                            //errorText.setVisibility(View.VISIBLE);
                                                            return;
                                                        }
                                                    });
                                            dlgAlert.create().show();
                                        }
                                    }
                                });
                        int socketTimeout = 30000;//3 seconds - change to what you want
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        request_json.setRetryPolicy(policy);
                        requestQueue.add(request_json);
                    }
                }
                else {
                    if (!validateInputField(uname)) {
                        errorUnameText.setVisibility(View.VISIBLE);
                        if (errorPwdText.isShown()) {
                            errorPwdText.setVisibility(View.GONE);
                        }
                        progress.dismiss();
                    }
                    else {
                        if (errorUnameText.isShown()) {
                            errorUnameText.setVisibility(View.GONE);
                        } else if (errorPwdText.isShown()) {
                            errorPwdText.setVisibility(View.GONE);
                        }
                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        String URL = ApiConstants.signInUrl;

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("email", uname);
                        params.put("sso", singleSignOn);
                        final boolean finalSingleSign = singleSign;
                        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Process os success response
                                        try {
                                            if (response.getString("Message").contains("Success")) {
                                                if (errorText.isShown()) {
                                                    errorText.setVisibility(View.GONE);
                                                }
                                                String siteIds = response.getString("site_ids");
                                                String username = response.getString("usernames");
                                                String uid = response.getString("user_ids");
                                                String site_names = response.getString("site_names");
                                                String siteIdsArray[] = siteIds.split(",");
                                                String siteNamesArray[] = site_names.split(",");
                                                String userIdsArray[] = uid.split(",");
                                                String res = response.toString();
                                                if (finalSingleSign) {
                                                    Uri.Builder builder = new Uri.Builder();
                                                    builder.scheme("https")
                                                            .authority("labcliq.com")
                                                            .appendPath("common")
                                                            .appendPath("app_sso_request.cfm")
                                                            //.appendPath("appdemo_bypass.cfm")
                                                            .appendQueryParameter("request_token", UUID.randomUUID().toString())
                                                            .appendQueryParameter("site_id", siteIds)
                                                            .appendQueryParameter("username", username);
                                                    Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                                                    intent.putExtra("ssoUrlString", builder.toString());
                                                    intent.putExtra("selectedSiteId", siteIds);
                                                    intent.putExtra("selectedUserId", uid);
                                                    intent.putExtra("site_name", site_names);
                                                    startActivity(intent);
                                                    return;
                                                }
                                            } else {
                                                errorText.setVisibility(View.VISIBLE);
                                                progress.dismiss();
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LoginActivity.this);
                                        dlgAlert.setTitle("Safety Stratus");
                                        dlgAlert.setMessage("Slow internet connection, please check your internet connection or try again");
                                        dlgAlert.setPositiveButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (progress != null && progress.isShowing())
                                                            progress.dismiss();
                                                        // errorText.setVisibility(View.VISIBLE);
                                                        return;
                                                    }
                                                });
                                        dlgAlert.create().show();
                                        error.printStackTrace();
                                    }
                                });
                        int socketTimeout = 30000;//3 seconds - change to what you want
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        request_json.setRetryPolicy(policy);
                        requestQueue.add(request_json);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LoginActivity.this);
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

    public static void hideKeyboard(LoginActivity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    private boolean validateInputField(String fieldValue) {

        if (fieldValue.length() == 0) {
            return false;
        }
        return true;

    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        //
    }

    boolean isActivityRunning;

    @Override
    public void onResume() {
        super.onResume();
        ////Log.e("under on resume","***");
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}