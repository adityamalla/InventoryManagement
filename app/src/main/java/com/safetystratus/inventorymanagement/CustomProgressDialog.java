package com.safetystratus.inventorymanagement;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {

    private String message;
    ProgressBar text;
    TextView text2;
    TextView text3;
    TextView text4;

    public CustomProgressDialog(Context context, String message) {
        super(context);
        this.message = message;
    }
    public void setPercentageAndProgress(int per, String count, String totalCount, String msg) {
        text2.setText(String.valueOf(per));
        text.setProgress(per);
        if (totalCount!=null) {
            text3.setText(count + "/" + totalCount);
            text4.setText(msg);
        }
        else {
            text3.setText("");
            text4.setText("Uploading in progress...");
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        text = (ProgressBar) findViewById(R.id.progress_horizontal);
        text2 = (TextView) findViewById(R.id.value123);
        text3 = (TextView) findViewById(R.id.textView5);
        text4 = (TextView) findViewById(R.id.downloadMsg);

    }
}

