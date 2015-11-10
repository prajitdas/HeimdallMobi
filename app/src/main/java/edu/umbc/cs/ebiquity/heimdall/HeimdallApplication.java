package edu.umbc.cs.ebiquity.heimdall;

import android.Manifest;
import android.app.Application;
import android.content.SharedPreferences;

import java.util.jar.JarFile;

/**
 * Created by Prajit on 11/9/2015.
 */
public class HeimdallApplication extends Application {
    public static String[] getPermissionsHeimdallApp() {
        return PermissionsHeimdallApp;
    }

    private static final String [] PermissionsHeimdallApp = {
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int CONST_PERMISSION_GET_ACCOUNTS = 1;
    private static final int CONST_PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private static final int CONST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    private static final int CONST_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static final int CONST_PERMISSION_READ_PHONE_STATE = 1;
    private static final String APPLIST_FILENAME = "applist.json";
    private static final String CONST_DEBUG_TAG = "Heimdall_APPLICATION_DEBUG_TAG";
    private static final String CONST_DATA_COLLECTION_COMPLETE = "CONST_DATA_COLLECTION_COMPLETE";
    private static final String CONST_ACCEPT_DECISION_KEY = "acceptDecisionKey";
    private static final String CONST_WEBSERVICE_URI = "http://eb4.cs.umbc.edu:1234/ws/datamanager";
    private static final String CONST_NOTIFICATION_TITLE = "HeimdallApplication notification";
    private final static String CONST_DATABASE_NAME = "HeimdallDB";
    private static SharedPreferences preferences;

    public static int getConstPermissionGetAccounts() { return CONST_PERMISSION_GET_ACCOUNTS; }
    public static int getConstPermissionAccessCoarseLocation() { return CONST_PERMISSION_ACCESS_COARSE_LOCATION; }
    public static int getConstPermissionWriteExternalStorage() { return CONST_PERMISSION_WRITE_EXTERNAL_STORAGE; }
    public static int getConstPermissionReadExternalStorage() { return CONST_PERMISSION_READ_EXTERNAL_STORAGE; }
    public static String getConstDebugTag() { return CONST_DEBUG_TAG; }
    public static int getConstPermissionReadPhoneState() { return CONST_PERMISSION_READ_PHONE_STATE; }
    public static String getConstDataCollectionComplete() {
        return CONST_DATA_COLLECTION_COMPLETE;
    }
    public static String getConstAcceptDecisionKey() {
        return CONST_ACCEPT_DECISION_KEY;
    }
    public static String getConstNotificationTitle() {
        return CONST_NOTIFICATION_TITLE;
    }
    public static String getConstWebserviceUri() {
        return CONST_WEBSERVICE_URI;
    }
    public static String getDebugTag() {
        return CONST_DEBUG_TAG;
    }
    public static String getApplistFilename() {
        return APPLIST_FILENAME;
    }
    public static SharedPreferences getPreferences() {
        return preferences;
    }
    public static void setPreferences(SharedPreferences preferences) { HeimdallApplication.preferences = preferences; }
    public static String getConstDatabaseName() {
        return CONST_DATABASE_NAME;
    }
}