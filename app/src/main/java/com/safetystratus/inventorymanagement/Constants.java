package com.safetystratus.inventorymanagement;

import android.util.Log;

public class Constants {
    public static final String TAG_EMPTY = "Please fill Tag Id";
    public static final int TYPE_DEBUG = 60;
    public static final int TYPE_ERROR = 61;
    public static final boolean DEBUG = false;

    public static void logAsMessage(int type, String TAG, String message) {
        if (DEBUG) {
            if (type == TYPE_DEBUG)
                Log.d(TAG, (message == null || message.isEmpty()) ? "Message is Empty!!" : message);
            else if (type == TYPE_ERROR)
                Log.e(TAG, (message == null || message.isEmpty()) ? "Message is Empty!!" : message);
        }
    }
}
