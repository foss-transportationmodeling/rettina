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

	public Connect_Stops(Route r) {

		route = r;
		
	}

	/*
	 * Async task class to get json by making HTTP call
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Showing progress dialog
//		 pDialog = new ProgressDialog(c);
//		 pDialog.setMessage("Please wait...");
//		 pDialog.setCancelable(false);
//		 pDialog.show();
		// getScreenCornerCoordinates();

	}

	@Override
	protected Void doInBackground(Void... arg0) {
		// Creating service handler class instance
		// ServiceHandler sh = new ServiceHandler();
		getStops(route);
		//getShape(route);


		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		// getScreenCornerCoordinates();
		// routeMenu.setRouteData();
		//pDialog.dismiss();

	}

	public void getStops(Route r) {
		ServiceHandler sh = new ServiceHandler();

		// Making the connection to grab all the stops based on the TripID...
		// Temporary: Right now I am just using the first element of the TripID
		// ArrayList
		// But this will change when the user will specify which trip they want
		// based on the
		// Time of day

		String sh_Routes = sh.makeServiceCall(
				"http://137.99.15.144/stops?trip_id=" + r.getTripIDs().get(5),
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
