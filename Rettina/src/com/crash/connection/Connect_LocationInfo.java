package com.crash.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.crash.rettina.Main;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/*
 * Mitch Thornton
 * Karthik Konduri
 * Rettina - 2015
 *This Class is used to do a background JSON call to the server. It will send the bundled up 
 *User's Location info at the end of their Trip
 */

public class Connect_LocationInfo extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;			// ProgressDialog used to notify when the location info has started and  finished
	public Context context;
	public Main main_Tile;
	public JSONObject jsonObj;
	
	// Constructor
	public Connect_LocationInfo(Main mt, Context c, JSONObject json) {
		main_Tile = mt;
		context = c;
		jsonObj = json;
	}

	// This is done in the background. Used to POST the location information
	@Override
	protected Void doInBackground(Void... arg0) {
		System.out.println("Testing do in background");
		POST(jsonObj);

		return null;
	}

	// Starts the ProgressDialog before everything else
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Showing progress dialog
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);
		pDialog.show();
	}

	// Once the POST has finished, stop the pDialog (The loading notification)
	@Override
	protected void onPostExecute(Void result) {
		pDialog.dismiss();
	}

	// The POST method, is used to make an HttpPost... It packages the user's location into a JSON object
	// Converts it into a String and then POSTS it to the server
	public static String POST(JSONObject jsonObject) {
		InputStream inputStream = null;

		String result = "";
		String url = "http://137.99.15.144/locations";

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);


			// 4. convert JSONObject to JSON to String
			String json = jsonObject.toString();

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(json);

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();
						
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpclient.execute(httpPost, responseHandler);
			
			// 10. convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		// 11. return result
		return result;
	}

	// Convert the inputStream into a String
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }


}
