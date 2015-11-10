package edu.umbc.cs.ebiquity.heimdall.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import edu.umbc.cs.ebiquity.heimdall.HeimdallApplication;

/**
 * Created by Prajit on 11/9/2015.
 */
public final class CheckPermissionsHelper {
    /**
     * Since we are using Marshmallow we have to check for these permissions now
     * <uses-permission android:name="android.permission.GET_ACCOUNTS" />
     * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     */
    public static void checkMarshMallowPermissions(Context context, Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            checkGetAccountsPermission(context, activity);
            checkAccessCoarseLocationPermission(context, activity);
            checkReadExternalStoragePermission(context, activity);
            checkWriteExternalStoragePermission(context, activity);
            checkReadPhoneStateStoragePermission(context, activity);
            // only for Marshmallow and newer versions
        }
    }

    private static void checkGetAccountsPermission(Context context, Activity activity) {
        String[] tempList = new String[]{HeimdallApplication.getPermissionsHeimdallApp()[0]};
        // Here, contextActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.GET_ACCOUNTS)) {
                Log.d(HeimdallApplication.getDebugTag(),"shouldShowRequestPermissionRationale");
                // Show an expanation to the user *asynchronously* -- don't block
                // context thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, tempList, HeimdallApplication.getConstPermissionGetAccounts());
                Log.d(HeimdallApplication.getDebugTag(),"requestPermissions");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public static void checkAccessCoarseLocationPermission(Context context, Activity activity) {
        String[] tempList = new String[]{HeimdallApplication.getPermissionsHeimdallApp()[1]};
        // Here, contextActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.d(HeimdallApplication.getDebugTag(),"shouldShowRequestPermissionRationale");
                // Show an expanation to the user *asynchronously* -- don't block
                // context thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, tempList, HeimdallApplication.getConstPermissionAccessCoarseLocation());
                Log.d(HeimdallApplication.getDebugTag(),"requestPermissions");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public static void checkReadExternalStoragePermission(Context context, Activity activity) {
        String[] tempList = new String[]{HeimdallApplication.getPermissionsHeimdallApp()[2]};
        // Here, contextActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.d(HeimdallApplication.getDebugTag(),"shouldShowRequestPermissionRationale");
                // Show an expanation to the user *asynchronously* -- don't block
                // context thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, tempList, HeimdallApplication.getConstPermissionReadExternalStorage());
                Log.d(HeimdallApplication.getDebugTag(),"requestPermissions");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public static void checkWriteExternalStoragePermission(Context context, Activity activity) {
        String[] tempList = new String[]{HeimdallApplication.getPermissionsHeimdallApp()[3]};
        // Here, contextActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(HeimdallApplication.getDebugTag(),"shouldShowRequestPermissionRationale");
                // Show an expanation to the user *asynchronously* -- don't block
                // context thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, tempList, HeimdallApplication.getConstPermissionWriteExternalStorage());
                Log.d(HeimdallApplication.getDebugTag(),"requestPermissions");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public static void checkReadPhoneStateStoragePermission(Context context, Activity activity) {
        String[] tempList = new String[]{HeimdallApplication.getPermissionsHeimdallApp()[4]};
        // Here, contextActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
                Log.d(HeimdallApplication.getDebugTag(),"shouldShowRequestPermissionRationale");
                // Show an expanation to the user *asynchronously* -- don't block
                // context thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, tempList, HeimdallApplication.getConstPermissionReadPhoneState());
                Log.d(HeimdallApplication.getDebugTag(),"requestPermissions");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}