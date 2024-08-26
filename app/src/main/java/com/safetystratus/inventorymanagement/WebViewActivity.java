package com.safetystratus.inventorymanagement;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class WebViewActivity extends AppCompatActivity {

    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private SiteAuthResponse authResponse;
    private ProgressBar webviewProgressBar;
    private String ssoUrlString;
    private String selectedSiteId;
    private String selectedUserId;
    private String site_name;
    private String loggedinUsername;
    private String selectedUserRoleId;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.header);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handler = new Handler();
        r = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(WebViewActivity.this);
                dlgAlert.setTitle("SafetyStratus");
                dlgAlert.setMessage("Session Timedout!!");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(WebViewActivity.this, LoginActivity.class)); //Go back to home page
                                finish();
                            }
                        });
                dlgAlert.create().show();
            }
        };
        startHandler();
        TextView tv = (TextView) findViewById(R.id.headerId);
        tv.setText(Html.fromHtml(getString(R.string.header_name)));
        webviewProgressBar = (ProgressBar) findViewById(R.id.webViewProgressBar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        authResponse = getIntent().getParcelableExtra("SiteAuthResponse");
        ssoUrlString = getIntent().getStringExtra("ssoUrlString");
        selectedSiteId = getIntent().getStringExtra("selectedSiteId");
        selectedUserId = getIntent().getStringExtra("selectedUserId");
        loggedinUsername = getIntent().getStringExtra("username");
        selectedUserRoleId = getIntent().getStringExtra("selectedUserRoleId");
        site_name = getIntent().getStringExtra("site_name");
        int cameraResult = ContextCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.CAMERA);
        int storageResult = ContextCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (cameraResult != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WebViewActivity.this, new String[]{Manifest.permission.CAMERA}, 999);
        }

        if (storageResult != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WebViewActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 777);
        }
        final WebView ssWebview = (WebView) findViewById(R.id.safetyStratusWebView);
        ssWebview.getSettings().setUserAgentString("labcliq-android-native");
        ssWebview.getSettings().setJavaScriptEnabled(true);
        ssWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //ssWebview.getSettings().setUseWideViewPort(true);
        ssWebview.getSettings().setJavaScriptEnabled(true);
        ssWebview.getSettings().setLoadWithOverviewMode(true);
        ssWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        ssWebview.getSettings().setUseWideViewPort(true);
        ssWebview.getSettings().setAllowFileAccess(true);
        ssWebview.getSettings().setAllowContentAccess(true);
        clearCache(this);
        ssWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        if (authResponse == null) {
            ssWebview.loadUrl(ssoUrlString);
        } else {
            // Log.e("auth response notnull","*******");
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("labcliq.com")
                    .appendPath("android")
                    .appendPath("index.cfm")
                    .appendQueryParameter("site_id", authResponse.getSiteId().toString())
                    .appendQueryParameter("user_id", authResponse.getUserId().toString())
                    .appendQueryParameter("token", authResponse.getAccessToken());
            ssWebview.loadUrl(builder.build().toString());
        }
        ssWebview.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request( Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "myPDFfile.pdf");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
            }
        });
        //Native Webkit
        ssWebview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                if (url.contains(".pdf")) {
                    // Log.e("under pdf","**");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/pdf");
                    try {
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                    }
                }else if (url.contains("print.cfm")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/pdf");
                    try {
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                    }
                } else if (url.contains("logout.cfm")) {
                    // Log.e("under logout","**");
                    Intent loginIntent = new Intent(WebViewActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                } else if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mt.getTo() });
                    intent.putExtra(Intent.EXTRA_TEXT, mt.getBody());
                    intent.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
                    intent.putExtra(Intent.EXTRA_CC, mt.getCc());
                    intent.setType("message/rfc822");
                    view.getContext().startActivity(intent);
                    view.reload();
                } else {
                    ssWebview.loadUrl(url);
                }
                return false; // then it is not handled by default action
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("about:blank");
                webviewProgressBar.setVisibility(View.GONE);
                if (failingUrl.contains("VALID")) {
                    String req_vales = failingUrl.split("//")[1];
                    String request_token = req_vales.split("/")[1];
                    String firstname = req_vales.split("/")[2];
                    String lastname = req_vales.split("/")[3];
                    String host = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).getString("site_api_host", "services.labcliq.com");
                    /*try {
                        RequestQueue requestQueue = Volley.newRequestQueue(WebViewActivity.this);
                        String URL = "https://"+host+ApiConstants.updateSSOAccessTokenDetails;
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("token", request_token);
                        params.put("user_id", selectedUserId);
                        params.put("site_id", selectedSiteId);
                        params.put("device_id", "test_device_id");
                        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Process os success response
                                        try {
                                            String res = response.toString();
                                            if(res.contains("Success")){
                                                JSONObject jsonObj = new JSONObject(res);
                                                String expiration_date = jsonObj.get("expiration_date").toString();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(WebViewActivity.this);
                                    dlgAlert.setTitle("Safety Stratus");
                                    dlgAlert.setMessage("Error response: Request timed out please check your network and try again");
                                    dlgAlert.setPositiveButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    return;
                                                }
                                            });
                                    dlgAlert.create().show();
                                    if (error.networkResponse != null && error.networkResponse.data != null) {
                                        String jsonError = new String(error.networkResponse.data);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        int socketTimeout = 60000;//3 seconds - change to what you want
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
                        request_json.setRetryPolicy(policy);
                        requestQueue.add(request_json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    Intent intent = new Intent(WebViewActivity.this, HomeActivity.class);
                    intent.putExtra("username", loggedinUsername);
                    intent.putExtra("sso", "true");
                    intent.putExtra("token", request_token);
                    intent.putExtra("firstname", firstname);
                    intent.putExtra("lastname", lastname);
                    intent.putExtra("empName", firstname);
                    intent.putExtra("selectedUserId", selectedUserId);
                    intent.putExtra("site_id", selectedSiteId);
                    intent.putExtra("site_name", site_name);
                    intent.putExtra("selectedUserRoleId",selectedUserRoleId);
                    intent.putExtra("fromLogin", "1");
                    startActivity(intent);
                }
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {        //show progressbar here
                super.onPageStarted(view, url, favicon);
                webviewProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //super.onPageFinished(view,url);
                webviewProgressBar.setVisibility(View.GONE);

            }
        });
        //ssWebview.setWebViewClient(new MyWebViewClient(activity);

        //Chrome Client Google
        ssWebview.setWebChromeClient(new MyChromeClient());
    }
    public static void clearCache(Context context) {
        // Clear cache for the application context
        if (context != null) {
            WebView webView = new WebView(context);
            webView.clearCache(true);
            webView.clearHistory();
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("requested", "onRequestPermissionsResult:");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Log.e("Lollypop","**");
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            // Log.e("KITKAT","**");
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            if (requestCode == FILECHOOSER_RESULTCODE) {

                if (null == this.mUploadMessage) {
                    return;

                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {

                        result = null;

                    } else {

                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;

            }
        }

        return;
    }

    class MyChromeClient extends WebChromeClient {

        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks


            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {

                } catch (Exception ex) {
                    // Error occurred while creating the File
                    //  Log.e("", "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

            return true;

        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard

            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");

            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }

            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");

            mCapturedImageURI = Uri.fromFile(file);

            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");

            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[]{captureIntent});

            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);


        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {

            openFileChooser(uploadMsg, acceptType);
        }
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress == 100) {
                //A fully loaded url will come here
                String StrNewUrl = view.getUrl();
            }
            super.onProgressChanged(view, progress);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public class MyWebViewClient extends WebViewClient {
        private final WeakReference<Activity> mActivityRef;

        public MyWebViewClient(Activity activity) {
            mActivityRef = new WeakReference<Activity>(activity);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:")) {
                final Activity activity = mActivityRef.get();
                if (activity != null) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = newEmailIntent(activity, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    activity.startActivity(i);
                    view.reload();
                    return true;
                }
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        private Intent newEmailIntent(Activity context, String address, String subject, String body, String cc) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_CC, cc);
            intent.setType("message/rfc822");
            return intent;
        }
    }
    @Override
    public void onBackPressed() {
    }
    Handler handler;
    Runnable r;
    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        stopHandler();//stop first and then start
        startHandler();
    }
    public void stopHandler() {
        handler.removeCallbacks(r);
    }
    public void startHandler() {
        handler.postDelayed(r, 300*60*1000); //for 5 hrs
    }
}