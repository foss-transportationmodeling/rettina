package com.crash.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.crash.rettina.Main_Tile;
import com.crash.rettina.ServiceHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

// This Class is used to do a background JSON call to the server. It will send the bundled up 
// User's Location info at the end of their Trip



// Get the posting to work tomorrow!!!

public class Connect_LocationInfo extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;
	public Context context;
	public Main_Tile main_Tile;
	public JSONObject jsonObj;
	
	public Connect_LocationInfo(Main_Tile mt, Context c, JSONObject json) {
		main_Tile = mt;
		context = c;
		jsonObj = json;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		
//		ServiceHandler sh = new ServiceHandler();
//		String sh_Routes = null;
		System.out.println("Testing do in background");

		POST(jsonObj);

		return null;
	}

	// Async task class to get json by making HTTP call

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Showing progress dialog
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);
		pDialog.show();
		// getScreenCornerCoordinates();

	}

	@Override
	protected void onPostExecute(Void result) {
		pDialog.dismiss();
	}

	public static String POST(JSONObject jsonObject) {
		InputStream inputStream = null;

		String result = "";
		String url = "http://137.99.15.144/locations";

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			//String json = "";

			// 3. build jsonObject
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.accumulate("name", person.getName());
//			jsonObject.accumulate("country", person.getCountry());
//			jsonObject.accumulate("twitter", person.getTwitter());

			// 4. convert JSONObject to JSON to String
			String json = jsonObject.toString();

			// ** Alternative way to convert Person object to JSON string usin
			// Jackson Lib
			// ObjectMapper mapper = new ObjectMapper();
			// json = mapper.writeValueAsString(person);

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
			
			System.out.println("Testing the Location Pass to Server");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpclient.execute(httpPost, responseHandler);
			
			System.out.println(response);

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
