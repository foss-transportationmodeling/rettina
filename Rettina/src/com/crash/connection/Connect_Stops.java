/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * Connect_Stops is sued to establish a connect to the database and parse the retrieved JSON data.  This collects all the stops
 * and stop information for the given route and saves it to the route. This is done by using the getStops() method.  A Route
 * is a required parameter for this method.  getShape() is used to grab all the polyline information for that specific route passed
 * in as a parameter, which allows for the route to be drawn on the map.  
 */

package com.crash.connection;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.crash.connection.ServiceHandler;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class Connect_Stops extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;										// Displays a loading screen to the user
	private Route route;												// Holds the route that contains all the Stops
	private Context context;
	private GoogleMap map;												// Holds the Google Map which is in the Main_Tile activity

	
	// Constructor
	public Connect_Stops(Route r, Context c, GoogleMap m) {
		route = r;
		context = c;
		map = m;
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

	// Connects to the server and retrieves all the Stops and Stop Times for the given route
	@Override
	protected Void doInBackground(Void... arg0) {
		
		getStops(route);		// Gets all the stops for the given route
		getStopTimes(route);	// Gets all the stop times for the given route

		return null;
	}

	// Once the Stops and Stop Times have been retrieved,
	// remove the loading message and animate the camera to the first stop on the route
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		route.showStops(map);	// Display the stops on the map
		pDialog.dismiss();		// Remove the loading message
		
		// This animates the camera to the first stop location
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(route.getStops().get(0).getLatLng(),
				14));
	}

	// This connects to the server and grabs all the stops based on the specified Route's Trip_ID
	// The server is set up to filter the information based on the Route's Trip_ID
	public void getStops(Route r) {
		ServiceHandler sh = new ServiceHandler();
		
		String sh_Routes = null;
		
		try {
			// URL is URLEncoded to replace 'spaces' by '%20'
			String query = URLEncoder.encode(r.getTripIDs().get(0), "utf-8");
			String url = "http://137.99.15.144/stops?trip_id=" + query;
			
			sh_Routes = sh.makeServiceCall(url, ServiceHandler.GET);	// Call to server
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// If results are found, then populate given Route with the correct Stops
		if (sh_Routes != null) {
			try {
				JSONObject jsonObj = new JSONObject(sh_Routes);
				JSONArray jsonArray = jsonObj.getJSONArray("stops");

				
				// looping through the entire JSON array
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject tempObj = jsonArray.getJSONObject(i);

					// Lat/Lng for each stop
					LatLng lat_lng = new LatLng(Double.parseDouble(tempObj.get(
							"stop_lat").toString()), Double.parseDouble(tempObj
							.get("stop_lon").toString()));

					// Create a temporary stop that holds all the information... This is then saved to the route
					Stop tempStop = new Stop(
							r.getRouteID(),
							tempObj.get("stop_id").toString(),
							(tempObj.get("stop_name").toString()), lat_lng);

					// Add the stop to the given route
					r.addStop(tempStop);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the url");
		}

	}
	
	// FOR USE ONLY WHEN TESTING DATA OR WHEN THE SERVER IS DOWN AND STATIC DATA MUST BE USED
	public void getStops_Test(Route r) {
		ServiceHandler sh = new ServiceHandler();

		String sh_Routes = sh.makeServiceCall(
				"http://137.99.15.144/static/stops.json",
				ServiceHandler.GET);
		
		
		if (sh_Routes != null) {
			try {
				JSONObject jsonObj = new JSONObject(sh_Routes);
				JSONArray jsonArray = jsonObj.getJSONArray("stops");

				// looping through All Contacts
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject tempObj = jsonArray.getJSONObject(i);

					LatLng lat_lng = new LatLng(Double.parseDouble(tempObj.get(
							"stop_lat").toString()), Double.parseDouble(tempObj
							.get("stop_lon").toString()));
					Stop tempStop = new Stop(
							r.getRouteID(),
							tempObj.get("stop_id").toString(),
							(tempObj.get("stop_name").toString()), lat_lng);

					r.addStop(tempStop);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the url");
		}

	}
	
	// Establishes a connection to the server and gets the Stop Times for each Stop based on the Trip_ID
	public void getStopTimes(Route r) {
		ServiceHandler sh = new ServiceHandler();	
		
		String sh_Routes = null;

		try {
			// URL Encode the Trip_ID to replace 'spaces' by '%20'... Allowing to connect to the server
			String query = URLEncoder.encode(r.getTripIDs().get(0), "utf-8");
			String url = "http://137.99.15.144/stop_times?trip_id=" + query;
			
			sh_Routes = sh.makeServiceCall(url, ServiceHandler.GET);	// Connect to server
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// If there are results, then get the arrival and departure times for each stop
		if (sh_Routes != null) {
			try {
				JSONObject jsonObj = new JSONObject(sh_Routes);
				JSONArray jsonArray = jsonObj.getJSONArray("stop_times");

				
				// looping through the entire JSON Array of results
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject tempObj = jsonArray.getJSONObject(i);

					// Saving the arrival time
					String arrival_time = tempObj.get(
							"arrival_time").toString();
					
					// Saving the departure time
					String departure_time = tempObj.get(
							"departure_time").toString();
					
					// As long as the loop is less than the amount of stops in the route, then set the arrival
					// and departure time for that route
					if(i < r.getStops().size()){
						r.getStops().get(i).setArrival_time(arrival_time);
						r.getStops().get(i).setDeparture_time(departure_time);
					}
				}
				

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the url");
		}

	}
}
