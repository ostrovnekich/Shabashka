package com.example.shabashka;

import android.content.Context;
import android.widget.Toast;

public final class ToastHelper {
    private static Toast currentToast;

    private ToastHelper() {}

    public static void show(Context context, String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}
