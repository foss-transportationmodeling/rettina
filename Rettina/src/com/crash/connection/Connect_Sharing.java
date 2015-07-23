package com.crash.connection;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.crash.rettina.ServiceHandler;
import com.crash.rettina.Sharing;

public class Connect_Sharing extends AsyncTask<Void, Void, Void>{

	private ProgressDialog pDialog;	
	Context context;
	Sharing sharing;


	
	public Connect_Sharing(Context c, Sharing activity){
		context = c;
		sharing = activity;
		
	}
	
	/*
	 * Async task class to get json by making HTTP call
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Showing progress dialog
		 pDialog = new ProgressDialog(context);
		 pDialog.setMessage("Please wait...");
		 pDialog.setCancelable(false);
		 pDialog.show();

	}


	@Override
	protected Void doInBackground(Void... arg0) {
		ServiceHandler sh = new ServiceHandler();

		
		String sh_Routes = null;
		
		try {
			// Need to URLEncode the trip_ID so the spaces do not mess up the POST Request
		
			
			// Error here... Null Pointer... TripIDs is null
			
//			System.out.println("sharing null: " + sharing == null);
//			System.out.println("route: " + sharing.getSelectedRoute().getRouteTitle());
//			System.out.println("TripID null: " + sharing.getSelectedRoute().getTripIDs().get(0) == null);


			
//			String tripID = URLEncoder.encode(sharing.getSelectedRoute().getTripIDs().get(0), "utf-8");
			
			// Server is down and still getting NULL for the TripID, so using this test data
			String tripID = URLEncoder.encode("71 UConn Transportation Service","utf-8");
			
			String comment = URLEncoder.encode(sharing.getEt_commentText(), "utf-8");
			float quality = sharing.getRb_driverRating();
			float openSeats = sharing.getRb_seatsRating();
			
			

			String url = "http://137.99.15.144/experiences?trip_id="+ tripID +"&comment="+ comment+ "&quality="+quality+"&open_seats="+openSeats;

			sh_Routes = sh.makeServiceCall(
					url,	// New API call, using Meriden
					ServiceHandler.POST);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}

	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		pDialog.dismiss();

		}

}
