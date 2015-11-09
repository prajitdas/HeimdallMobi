package edu.umbc.cs.ebiquity.heimdall.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import edu.umbc.cs.ebiquity.heimdall.HeimdallApplication;

/**
 * Created by Prajit on 11/9/2015.
 */
public class CurrentAppsService extends IntentService {
//	private static WebserviceHelper webserviceHelper;

    public CurrentAppsService() {
        super("CurrentAppsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//		webserviceHelper = new WebserviceHelper(getApplicationContext());
//		webserviceHelper.collectTheData();
//		webserviceHelper.sendTheData();

        /* Task complete now return */
        Intent intentOnCompletionOfDataCollection = new Intent(HeimdallApplication.getConstDataCollectionComplete());
//        intentOnCompletionOfDataCollection.putExtra(CONST_LIST_OF_RUNNING_APPS_EXTRA, output.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentOnCompletionOfDataCollection);
    }
}