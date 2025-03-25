package com.example.sqlproject;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SendSms {

    public final static int MY_PERMISSIONS_REQUEST_SEND_SMS = 12;

    public static boolean hasSMSPermission(Activity activity) {
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);
    }

    public static void askForPermission(Activity activity) {
        if (!hasSMSPermission(activity)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    public static void sendSMS(String phone, String msg,Activity activity) {
        askForPermission(activity);
        if (hasSMSPermission(activity)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, msg, null, null);
        }
    }
}