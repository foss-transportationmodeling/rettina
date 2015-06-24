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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import com.crash.rettina.MainActivity;
import com.crash.rettina.RouteMenu;
import com.crash.rettina.ServiceHandler;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public class Connect_Stops extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;
	private ArrayList<LatLng> tempPolyPoints = new ArrayList<LatLng>();
	private Route route;
	private Context context;
	private GoogleMap map;

	public Connect_Stops(Route r, Context c, GoogleMap m) {

		route = r;
		context = c;
		map = m;
		
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
		// getScreenCornerCoordinates();

	}

	@Override
	protected Void doInBackground(Void... arg0) {
		// Creating service handler class instance
		// ServiceHandler sh = new ServiceHandler();
		
		getStops(route);
		//getStops_Test(route);  // <---- Static data call
		
		
		//getShape(route);


		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		// getScreenCornerCoordinates();
		// routeMenu.setRouteData();
		route.showStops(map);
		pDialog.dismiss();
		
		// This zooms into the first stops location.. It should zoom so it fits the whole route in but this can be solved later
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(route.getStops().get(0).getLatLng(), 13));

	}

	public void getStops(Route r) {
		ServiceHandler sh = new ServiceHandler();

		// Making the connection to grab all the stops based on the TripID...
		// Temporary: Right now I am just using the first element of the TripID
		// ArrayList
		// But this will change when the user will specify which trip they want
		// based on the
		// Time of day

//		String sh_Routes = sh.makeServiceCall(
//				"http://137.99.15.144/stops?trip_id=" + r.getTripIDs().get(5),
//				ServiceHandler.GET);
		
		
		// Need to make it so it actually gets the real tripID and calls it
		String sh_Routes = sh.makeServiceCall(
				"http://137.99.15.144/stops/UConn?trip_id=" + r.getTripIDs().get(5),	// New API call, using Meriden
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
							Integer.parseInt(tempObj.get("stop_id").toString()),
							(tempObj.get("stop_name").toString()), lat_lng);

					r.addStop(tempStop);
				}
				
				System.out.println("ROUTE: " + r.getRouteTitle() +  ", SIZE OF STOPS: " + r.getStops().size());

				// System.out.println("First Stop: " +
				// r.getStops().get(0).getStopDescription());

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
							Integer.parseInt(tempObj.get("stop_id").toString()),
							(tempObj.get("stop_name").toString()), lat_lng);

					r.addStop(tempStop);
				}

				// System.out.println("First Stop: " +
				// r.getStops().get(0).getStopDescription());

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the url");
		}

	}
	
	

	public void getShape(Route r) {
		ServiceHandler sh = new ServiceHandler();

		// Making the connection to grab all the stops based on the TripID...
		// Temporary: Right now I am just using the first element of the TripID
		// ArrayList
		// But this will change when the user will specify which trip they want
		// based on the
		// Time of day

		// For some reason Blue Line and probably some others only have a couple
		// stops in some of there trips..
		// Using arrayLocation 5 for temporary in order to get all the stops
		// since location 0 doesn't have them all...
		String sh_Routes = sh.makeServiceCall(
				"http://137.99.15.144/shapes?trip_id=" + r.getTripIDs().get(5),
				ServiceHandler.GET);

		System.out.println("http://137.99.15.144/shapes?trip_id="
				+ r.getTripIDs().get(5));
		if (sh_Routes != null) {
			try {
				JSONObject jsonObj = new JSONObject(sh_Routes);
				JSONArray jsonArray = jsonObj.getJSONArray("shapes");

				// looping through All Contacts
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject tempObj = jsonArray.getJSONObject(i);

					LatLng tempLatLng = new LatLng(Double.parseDouble(tempObj
							.get("shape_pt_lat").toString()),
							Double.parseDouble(tempObj.get("shape_pt_lon")
									.toString()));

					tempPolyPoints.add(tempLatLng);

				}

				r.setPolyLineOptions(drawPrimaryLinePath(tempPolyPoints,
						r.getColor()));

				// System.out.println("First Stop: " +
				// r.getStops().get(0).getStopDescription());

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the url");
		}

	}

	// Draw route line
	private PolylineOptions drawPrimaryLinePath(
			ArrayList<LatLng> listLocsToDraw, int c) {

		PolylineOptions options = new PolylineOptions();

		options.color(c);
		options.width(7);
		options.visible(true);

		for (LatLng locRecorded : listLocsToDraw) {
			options.add(locRecorded);
		}

		return options;

	}
}
