package com.crash.connection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

// This Class is used to do a background JSON call to the server. It will send the bundled up 
// User's Location info at the end of their Trip

public class Connect_LocationInfo extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;
	public Context context;
	
	
	public Connect_LocationInfo(Context c){
		context = c;
	}
	
	
	@Override
	protected Void doInBackground(Void... arg0) {
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
		super.onPostExecute(result);

		pDialog.dismiss();
	}

}
