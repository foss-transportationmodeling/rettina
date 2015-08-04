package com.crash.connection;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.crash.connection.ServiceHandler;
import com.crash.rettina.Sharing;

/*
 * Mitch Thornton
 * Karthik Konduri
 * Rettina - 2015
 * 
 * Connect_Sharing is used to establish a connection to the server and POST
 * the user's sharing information, such as comments, ride experience, and 
 * available seating
 */

public class Connect_Sharing extends AsyncTask<Void, Void, Void>{

	private ProgressDialog pDialog;		// Used to display a loading message to the user
	Context context;
	Sharing sharing;


	// Constructor
	public Connect_Sharing(Context c, Sharing activity){
		context = c;
		sharing = activity;
		
	}
	
	// Displays the loading message to the user
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Showing progress dialog
		 pDialog = new ProgressDialog(context);
		 pDialog.setMessage("Please wait...");
		 pDialog.setCancelable(false);
		 pDialog.show();
	}


	// This method is doing the majority of the work.. It establishes the connection and POSTS the sharing data
	@Override
	protected Void doInBackground(Void... arg0) {
		ServiceHandler sh = new ServiceHandler();

		String sh_Routes = null;
		
		try {
			// These Strings and floats hold the information that the user has shared about the given route
			// They have to be URL Encoded due to the spaces in order to be POSTED to the server
			
			//!!!! Need to actually get the real Trip_ID's!!!!!!
			String tripID = URLEncoder.encode("71 UConn Transportation Service","utf-8");	// Route's trip_ID
			
			String comment = URLEncoder.encode(sharing.getEt_commentText(), "utf-8");		// Comment about the Trip
			float quality = sharing.getRb_driverRating();									// Quality of the trip	
			float openSeats = sharing.getRb_seatsRating();									// Open seats available

			// API call to the server containing all the above information
			String url = "http://137.99.15.144/experiences?trip_id="+ tripID +"&comment="+ comment+ "&quality="+quality+"&open_seats="+openSeats;

			sh_Routes = sh.makeServiceCall(url, ServiceHandler.POST);
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}

	
	// Once the data has been passed to the server, dismiss the loading notification
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		pDialog.dismiss();					// Close the loading warning
		}

}
