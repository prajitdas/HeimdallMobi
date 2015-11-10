package edu.umbc.cs.ebiquity.heimdall.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import edu.umbc.cs.ebiquity.heimdall.HeimdallApplication;
import edu.umbc.cs.ebiquity.heimdall.data.ContextData;
import edu.umbc.cs.ebiquity.heimdall.ui.DeleteAppsActivity;

public class WebserviceCheckDataHelper {
	private Context context;
	private ArrayList<String> appListToUninstall;

	/**
	 * 												Data poll portion
	 * ------------------------------------------------------------------------------------------------------------------------
	 */

	public WebserviceCheckDataHelper(Context context) {
		this.context = context;
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

	public ArrayList<String> getAppListToUninstall() {
		return appListToUninstall;
	}

	public void setAppListToUninstall(ArrayList<String> appListToUninstall) {
		this.appListToUninstall = appListToUninstall;
	}

	/**
	 * Send the data to the server
	 */
	public void sendTheData() {
		if(isOnline())
			new SendDataToServerAsyncTask().execute();// for older method HeimdallApplication.getConstWebserviceUri());
	}

	private String writeDataToStream() {
		StringBuffer temp = new StringBuffer();
		ContextData tempContextData = new ContextData(context);
		temp.append(tempContextData.getIdentity());
		temp.append("</arg1><arg2>");
		temp.append(getDeviceId());
		return temp.toString();
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

	public Context getContext() {
		return context;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}

    private ArrayList<String> getListOfAppsToDelete(String resp) {
        ArrayList<String> tempList = new ArrayList<>();
		String [] list = resp.split(">");
        String [] appList = list[5].split(";");
        for(int i = 0; i < appList.length; i++)
            tempList.add("package:"+appList[0]);
        return tempList;
    }

//    /**
//     * Getting XML DOM element
//     * @param XML string
//     * */
//    public Document getDomElement(String xml){
//        Document doc = null;
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        try {
//            DocumentBuilder db = dbf.newDocumentBuilder();
//
//            InputSource is = new InputSource();
//            is.setCharacterStream(new StringReader(xml));
//            doc = db.parse(is);
//
//        } catch (ParserConfigurationException e) {
//            Log.e("Error: ", e.getMessage());
//            return null;
//        } catch (SAXException e) {
//            Log.e("Error: ", e.getMessage());
//            return null;
//        } catch (IOException e) {
//            Log.e("Error: ", e.getMessage());
//            return null;
//        }
//
//        return doc;
//    }
//
//    /** Getting node value
//     * @param elem element
//     */
//    public String getElementValue(Node elem) {
//        Node child;
//        if( elem != null){
//            if (elem.hasChildNodes()){
//                for(child = elem.getFirstChild(); child != null; child = child.getNextSibling()){
//                    if(child.getNodeType() == Node.TEXT_NODE){
//                        return child.getNodeValue();
//                    }
//                }
//            }
//        }
//        return "";
//    }
//
//    /**
//     * Getting node value
//     * @param Element node
//     * @param key string
//     * */
//    public String getValue(Element item, String str) {
//        NodeList n = item.getElementsByTagName(str);
//        return this.getElementValue(n.item(0));
//    }

	private class SendDataToServerAsyncTask extends AsyncTask<String, String, String> {
		private String resp;
		// Do the long-running work in here
		@Override
	    protected String doInBackground(String... params) {
//				Log.d(HeimdallApplication.getCurrentAppsDebugTag(), "Loading contents...");
			publishProgress("Loading contents..."); // Calls onProgressUpdate()
			try {
//					Log.d(HeimdallApplication.getCurrentAppsDebugTag(), "Loading SOAP...");
				String reqXMLPrefix = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:pollForUninstall xmlns:ns2=\"http://webservice.hma.mithril.android.ebiquity.cs.umbc.edu/\"><arg0></arg0><arg1>";
				String reqXMLPostfix = "</arg2></ns2:pollForUninstall></S:Body></S:Envelope>";

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
			ArrayList<String> tempList = new ArrayList<String>();
            tempList.addAll(getListOfAppsToDelete(resp));
	    	Toast.makeText(context, "This data was received: "+resp, Toast.LENGTH_LONG).show();
            Log.d(HeimdallApplication.getDebugTag(),"This data was received: "+resp);
            /**
             * This is WRONG! You are supposed to read the json
             */
//            tempList.add("package:at.markushi.expensemanager");
			setAppListToUninstall(tempList);
            Intent intent = new Intent(context, DeleteAppsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        /**
         * For older method
         * return HTTPPOST(params[0]);
         */
	}
}