package com.crash.rettina;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class GetJSON extends AsyncTask<Void, Void, Void> {
	private ProgressDialog pDialog;
	private Map<String, LatLng> vehicles = new LinkedHashMap<String, LatLng>();
	private ArrayList<Stop> stops = new ArrayList<Stop>();
	private ArrayList<Route> routes = new ArrayList<Route>();

	ArrayList<LatLng> routeLocations = new ArrayList<LatLng>();

	public MainActivity activity;
	public RouteMenu routeMenu;

	
	private Context c;
	private GoogleMap map;

	public GetJSON( MainActivity a, GoogleMap m, RouteMenu rm) {
		this.activity = a;
		routeMenu = rm;
		map = m;
	}
	

	/*
	 * Async task class to get json by making HTTP call
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Showing progress dialog
		// pDialog = new ProgressDialog(c);
		// pDialog.setMessage("Please wait...");
		// pDialog.setCancelable(false);
		// pDialog.show();

	}

	@Override
	protected Void doInBackground(Void... arg0) {
		// Creating service handler class instance
		ServiceHandler sh = new ServiceHandler();
		getVehicles();
		getStops();
		for(int i = 0; i < routes.size(); i++){
			routes.get(i).printRoute();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		// Dismiss the progress dialog
		// if (pDialog.isShowing())
		// pDialog.dismiss();
//		putVehiclesOnMap();
//		putStopsOnMap();
		 int c;
		 c = Color.argb(255, 255, 0, 0);
		 
		 // Set the mainactivity vehicles equal to this one
		 activity.vehicles = vehicles;
		 
		 
		// Used to keep track of the routes.... 
		int routeCounter = 0;
		 
		for(int i = 0; i < stops.size(); i++){
			
			if(i == 0){
//				drawPrimaryLinePath(stops.get(i).getRoutePoints(), c);
				routes.add(new Route(stops.get(i).getRouteID(), stops.get(i)));
			}
			
			
			else if(stops.get(i).getRouteID() == stops.get(i - 1).getRouteID()){
//				drawPrimaryLinePath(stops.get(i).getRoutePoints(), c);
				routes.get(routeCounter).addStop(stops.get(i));//.getRoutePoints());
			}
			else if (i > 0 && stops.get(i).getRouteID() != stops.get(i - 1).getRouteID()){
				c = Color.argb(255, (int) Math.random()*255, (int) Math.random()*255, (int) Math.random()*255);
				routes.add(new Route(stops.get(i).getRouteID(), stops.get(i)));
				routeCounter++;

			}
		}
		
		
		for(int i = 0; i < routes.size(); i++){
			c = Color.argb(255, 255, 0, 0);
			
			routes.get(i).setAllRoutePoints();
			
			// Problem is with tempPoly
			PolylineOptions tempPoly = drawPrimaryLinePath(routes.get(i).routePoints, c);
			
			
			routes.get(i).setPolyLine(tempPoly);
		}
				
		
		activity.routes = routes;
		routeMenu.setRouteData();
		
	}

	public void getVehicles() {
		ServiceHandler sh = new ServiceHandler();

		String jsonStrVehiclePts = sh
				.makeServiceCall(
						"http://www.uconnshuttle.com/Services/JSONPRelay.svc/GetMapVehiclePoints",
						ServiceHandler.GET);
		if (jsonStrVehiclePts != null) {
			try {
				JSONArray jsonArray = new JSONArray(jsonStrVehiclePts);

				// looping through All Contacts
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					LatLng lat_Lng = new LatLng(Double.parseDouble(jsonObj.get(
							"Latitude").toString()), Double.parseDouble(jsonObj
							.get("Longitude").toString()));

					vehicles.put(jsonObj.get("VehicleID").toString(), lat_Lng);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the url");
		}

	}

	public void getStops() {
		ServiceHandler sh = new ServiceHandler();

		String sh_Stops = sh.makeServiceCall(
				"http://www.uconnshuttle.com/Services/JSONPRelay.svc/GetStops",
				ServiceHandler.GET);
		if (sh_Stops != null) {
			try {
				JSONArray jsonArray = new JSONArray(sh_Stops);
				
				// looping through All Contacts
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					
					LatLng lat_Lng = new LatLng(Double.parseDouble(jsonObj.get(
							"Latitude").toString()), Double.parseDouble(jsonObj
							.get("Longitude").toString()));
					
					Stop tempStop = new Stop((Integer) jsonObj.get("RouteID"),
							(Integer) jsonObj.get("RouteStopID"), jsonObj.get(
									"Description").toString(), lat_Lng);
					
					
					// System.out.println("Stop: " +
					// jsonObj.get("Description"));
					// System.out.println("Lat: " + jsonObj.get("Latitude"));
					// System.out.println("Long: " + jsonObj.get("Longitude"));
					// System.out.println("RouteID: " + jsonObj.get("RouteID"));
					for(int j = 0; j < jsonObj.getJSONArray("MapPoints").length(); j++){
					 
					 
					 // Make a temporary lat/lng for each given point along the route.. Add this to the arrayList routeLocations
					 // Call the method drawPrimaryLinePath() and pass it the routeLocations arrayList so it can draw the route lines
					 // On the Google Map
						
				//	System.out.println("Lat: " + Double.parseDouble((String)jsonObj.getJSONArray("MapPoints").getJSONObject(j).getString("Latitude")) + "Long: " + Double.parseDouble((String)jsonObj.getJSONArray("MapPoints").getJSONObject(j).getString("Longitude")));
					 LatLng tempLatLng = new LatLng(Double.parseDouble((String)jsonObj.getJSONArray("MapPoints").getJSONObject(j).getString("Latitude")), Double.parseDouble((String)jsonObj.getJSONArray("MapPoints").getJSONObject(j).getString("Longitude")));
					 
					 tempStop.fillRouteList(tempLatLng);
					}
					

					// Create the lat/lng for the given stop
					

					// Put each stop into an array list made of Stop objects....'
					
					stops.add(tempStop);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the url");
		}

	}

	public void printVehicles() {
		for (Map.Entry<String, LatLng> entry : vehicles.entrySet()) {
	//		System.out.println(entry.getKey() + ", " + entry.getValue());

		}
	}

	public void putVehiclesOnMap() {
		for (Map.Entry<String, LatLng> entry : vehicles.entrySet()) {
			map.addMarker(new MarkerOptions().position(entry.getValue()).title(
					entry.getKey()));
		}
	}

	public void putStopsOnMap() {
		for (int i = 0; i < stops.size(); i++) {
			map.addMarker(new MarkerOptions()
					.position(stops.get(i).getLatLng()).title(
							stops.get(i).getStopDescription()));

		}
	}

	// Draw route line
	private PolylineOptions drawPrimaryLinePath(ArrayList<LatLng> listLocsToDraw, int c) {
//		if (map == null) {
//			System.out.println("Map is null");
//
//			return null;
//		}
//
//		if (listLocsToDraw.size() < 2) {
//			System.out.println("Less than 2 so null");
//
//			return null;
//		}

		PolylineOptions options = new PolylineOptions();

		options.color(c);
		options.width(7);
		options.visible(true);

		for (LatLng locRecorded : listLocsToDraw) {
			options.add(locRecorded);
		}

		//map.addPolyline(options);
		System.out.println("Temp Poly size: " + options.getPoints().size());

		return options;

	}

}
