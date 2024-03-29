package edu.umbc.cs.ebiquity.heimdall.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import edu.umbc.cs.ebiquity.heimdall.HeimdallApplication;
import edu.umbc.cs.ebiquity.heimdall.data.ContextData;

public class WebserviceSendDataHelper {
	private Context context;
	
	private static HeimdallDBHelper HeimdallDBHelper;
	private static SQLiteDatabase HeimdallDB;

	private static String recentlyChangedAppPackageName = new String();
	private static List<String> currentlyInstalledAppsList = new ArrayList<String>();

	private static boolean isPackageAdded;
	private static boolean isPackageChanged;
	private static boolean isPackageRemoved;
	private static boolean isPackageReplaced;

	/**
	 * 												Data upload portion
	 * ------------------------------------------------------------------------------------------------------------------------
	 */

	public WebserviceSendDataHelper(Context context) {
		this.context = context;
		setPackageAdded(false);
		setPackageChanged(false);
		setPackageRemoved(false);
		setPackageReplaced(false);
		initDB();
	}

	/**
	 * Send the data to the server
	 */
	public void sendTheData() {
		if(isOnline())
			new SendDataToServerAsyncTask().execute();// for older method HeimdallApplication.getConstWebserviceUri());
	}

	private class SendDataToServerAsyncTask extends AsyncTask<String, String, String> {
		private String resp;
		// Do the long-running work in here
		@Override
	    protected String doInBackground(String... params) {
//				Log.d(HeimdallApplication.getCurrentAppsDebugTag(), "Loading contents...");
			publishProgress("Loading contents..."); // Calls onProgressUpdate()
			try {
//					Log.d(HeimdallApplication.getCurrentAppsDebugTag(), "Loading SOAP...");
				String reqXMLPrefix = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:printString xmlns:ns2=\"http://webservice.hma.mithril.android.ebiquity.cs.umbc.edu/\"><arg0>";
				String reqXMLPostfix = "</arg0></ns2:printString></S:Body></S:Envelope>";
				
				String request = reqXMLPrefix+writeDataToStream()+reqXMLPostfix;
				
				Log.d(HeimdallApplication.getDebugTag(), request);
				
				URL url;		
				HttpURLConnection httpURLConnection = null;
				try {
					//Create connection
					url = new URL(HeimdallApplication.getConstWebserviceUri());
					httpURLConnection = (HttpURLConnection)url.openConnection();
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setRequestProperty("Content-type", "text/xml; charset=utf-8");
					httpURLConnection.setRequestProperty("SOAPAction", "http://eb4.cs.umbc.edu:1234/ws/datamanager#printString");
					httpURLConnection.setChunkedStreamingMode(0);

					httpURLConnection.setUseCaches (false);
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(true);
					httpURLConnection.connect();
					
//						Log.d(HeimdallApplication.getCurrentAppsDebugTag(), "Hardcoded call starts...");
					//Send request
					BufferedOutputStream out = new BufferedOutputStream(httpURLConnection.getOutputStream());
					out.write(request.getBytes());
//						Log.d(HeimdallApplication.getCurrentAppsDebugTag(), out.toString());
					out.flush();
					out.close();
//						Log.d(HeimdallApplication.getCurrentAppsDebugTag(), "Hardcoded call ends...");

					//Get Response	
					InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
//						Log.d(HeimdallApplication.getCurrentAppsDebugTag(), "Input stream reading...");
					resp = convertInputStreamToString(in);
//						Log.d(HeimdallApplication.getCurrentAppsDebugTag(), "Read from server: "+resp);
				} catch (IOException e) {
					// writing exception to log
					e.printStackTrace();//(HeimdallApplication.getCurrentAppsDebugTag(), e.getStackTrace().toString());
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				} finally {
					if(httpURLConnection != null) {
						httpURLConnection.disconnect(); 
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				resp = e.getMessage();
			}
			return resp;
		}			
	    // This is called when doInBackground() is finished
	    @Override
	    protected void onPostExecute(String resp) {
	    	Toast.makeText(context, "This data was received: "+resp, Toast.LENGTH_LONG).show();
	    }

		  	/**
			 * For older method
			 * return HTTPPOST(params[0]);
			 */
	}

	private String writeDataToStream() throws JSONException, IOException {
		ContextData tempContextData = new ContextData(context);
		// Add your data
		//Create JSONObject here 
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("identity", tempContextData.getIdentity());
		jsonParam.put("modifiedApp",getRecentlyChangedAppPackageName());
		jsonParam.put("deviceId",getDeviceId());
		jsonParam.put("installFlag",getInstallFlag().toString());
//			jsonParam.put("location", appContextData.getLocation());
//			jsonParam.put("activity", appContextData.getActivity());
//			jsonParam.put("time", appContextData.getTime());
//			jsonParam.put("purpose", appContextData.getPurpose());

//		collectTheData();
		JSONArray jsonArray = new JSONArray();
		for(String applicationInfo : getCurrentlyInstalledAppsList()) {
			jsonArray.put(applicationInfo);
//			HeimdallApplication.addToAppList(applicationInfo);
//			        	jsonArray.put("Facebook");
//			        	jsonArray.put("Twitter");
//			        	jsonArray.put("G+");
//			Log.d(HeimdallApplication.getDebugTag(), "in application info writing JSON");
		}
		jsonParam.put("currentApps",jsonArray);
		
//		Log.d(HeimdallApplication.getDebugTag(), jsonParam.toString());
		return jsonParam.toString();
	}

    private String getInstallFlag() {
    	if(isPackageAdded()) {
    		setPackageAdded(false);
    		return "true";
    	}
    	if(isPackageChanged())
    		setPackageChanged(false);
		if(isPackageReplaced())
			setPackageReplaced(false);
    	if(isPackageRemoved())
    		setPackageRemoved(false);
    	return "false";
    }

	private String getDeviceId() {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		 
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId(); 
		tmSerial = "" + tm.getSimSerialNumber(); 
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		 
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();
		return deviceId;
    }

	private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
	 * Collect running app info
	 * @return 
	 */
	public void collectTheData() {
		for(String app:HeimdallDBHelper.readAppPackageNames(HeimdallDB)) {
			currentlyInstalledAppsList.add(app);
		}
	}
	
	private void initDB() {
		/**
		 * Database creation and default data insertion, happens only once.
		 */
		HeimdallDBHelper = new HeimdallDBHelper(context);
		HeimdallDB = HeimdallDBHelper.getWritableDatabase();
	}
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
//		Log.d(HeimdallApplication.getCurrentAppsDebugTag(), "Input stream reading complete...");
        return result;
    }
	
	public String findNewlyInstalledApp(String extraUid) {
		setPackageAdded(true);
//		Log.d(HeimdallApplication.getDebugTag(), "finding new app");
		Collection<String> appListPrev = new ArrayList<String>();
		appListPrev = HeimdallDBHelper.readAppPackageNames(HeimdallDB);
		Collection<String> appListNow = new ArrayList<String>();
		for(ApplicationInfo appInfo : context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)) {
			try {
				if(appInfo.packageName != null) {
					appListNow.add(appInfo.packageName);
					HeimdallDBHelper.createApp(HeimdallDB, appInfo); // Add new app to database
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		// Remove all elements in appListNow from appListPrev
		appListNow.removeAll(appListPrev);
		if (!appListNow.isEmpty())
			for(String appName:appListNow) {
				setRecentlyChangedAppPackageName(appName);
				return appName;
			}
		return null;
//		HeimdallDBHelper.createApp(HeimdallDB, getAppNameFromUid(extraUid));
	}

	public String findPackageChanged(String extraUid) {
		setPackageChanged(true);
		String appName = getAppNameFromUid(extraUid);
		HeimdallDBHelper.updateApp(HeimdallDB, appName);
		return appName;
	}
	
	public String findPackageRemoved(String extraUid) {
		setPackageRemoved(true);
		Log.d(HeimdallApplication.getDebugTag(), "finding removed app");
		Collection<String> appListPrev = new ArrayList<String>();
		appListPrev = HeimdallDBHelper.readAppPackageNames(HeimdallDB);
		Collection<String> appListNow = new ArrayList<String>();
		for(ApplicationInfo appInfo : context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)) {
			try {
				if(appInfo.packageName != null) {
					appListNow.add(appInfo.packageName);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		// Remove all elements in appListNow from appListPrev
		appListPrev.removeAll(appListNow);
		if (!appListPrev.isEmpty())
			for(String appName:appListPrev) {
				setRecentlyChangedAppPackageName(appName);
				HeimdallDBHelper.deleteApp(HeimdallDB, appName);// Remove app from DB
				return appName;
			}
		return null;
//		HeimdallDBHelper.createApp(HeimdallDB, getAppNameFromUid(extraUid));
//		String appName = getAppNameFromUid(extraUid);
//		HeimdallDBHelper.deleteApp(HeimdallDB, appName);
//		return appName;
	}
	
	public String findPackageReplaced(String extraUid) {
		setPackageReplaced(true);
		String appName = getAppNameFromUid(extraUid);
		HeimdallDBHelper.updateApp(HeimdallDB, appName);
		return appName;
	}

	private String getAppNameFromUid(String extraUid) {
		int uid = -1;
		try {
			uid = Integer.parseInt(extraUid);
		} catch(NumberFormatException e) {
			return null;
		}
		final PackageManager pm = context.getPackageManager();
		//get a list of installed apps. 
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		String packageName = new String();
		//loop through the list of installed packages and see if the selected 
		//app is in the list 
		for (ApplicationInfo packageInfo:packages) {
			if(packageInfo.uid == uid) {
				packageName = packageInfo.packageName; //get the package info for the selected app
				break; //found a match, don't need to search anymore
			}
		}
		setRecentlyChangedAppPackageName(packageName);
		return packageName;
	}

	public String getRecentlyChangedAppPackageName() {
		return recentlyChangedAppPackageName;
	}

	public void setRecentlyChangedAppPackageName(
			String recentlyChangedAppPackageName) {
		WebserviceSendDataHelper.recentlyChangedAppPackageName = recentlyChangedAppPackageName;
	}

	public List<String> getCurrentlyInstalledAppsList() {
		return currentlyInstalledAppsList;
	}

	public void setCurrentlyInstalledAppsList(
			List<String> currentlyInstalledAppsList) {
		WebserviceSendDataHelper.currentlyInstalledAppsList = currentlyInstalledAppsList;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public static HeimdallDBHelper getHeimdallDBHelper() {
		return HeimdallDBHelper;
	}

	public static void setHeimdallDBHelper(HeimdallDBHelper HeimdallDBHelper) {
		WebserviceSendDataHelper.HeimdallDBHelper = HeimdallDBHelper;
	}

	public static SQLiteDatabase getHeimdallDB() {
		return HeimdallDB;
	}

	public static void setHeimdallDB(SQLiteDatabase HeimdallDB) {
		WebserviceSendDataHelper.HeimdallDB = HeimdallDB;
	}

	public static boolean isPackageAdded() {
		return isPackageAdded;
	}

	public static void setPackageAdded(boolean isPackageAdded) {
		WebserviceSendDataHelper.isPackageAdded = isPackageAdded;
	}

	public static boolean isPackageChanged() {
		return isPackageChanged;
	}

	public static void setPackageChanged(boolean isPackageChanged) {
		WebserviceSendDataHelper.isPackageChanged = isPackageChanged;
	}

	public static boolean isPackageRemoved() {
		return isPackageRemoved;
	}

	public static void setPackageRemoved(boolean isPackageRemoved) {
		WebserviceSendDataHelper.isPackageRemoved = isPackageRemoved;
	}

	public static boolean isPackageReplaced() {
		return isPackageReplaced;
	}

	public static void setPackageReplaced(boolean isPackageReplaced) {
		WebserviceSendDataHelper.isPackageReplaced = isPackageReplaced;
	}
}