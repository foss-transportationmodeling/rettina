package com.crash.connection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class Connect extends AsyncTask<Void, Void, Void>{
	
		private ProgressDialog pDialog;
	
		//private ArrayList<Route> routes;

		
		// Variables to hold the coordinates of the NorthEast and SouthWest corners of the screen
		// They are set by calling getScreenCornerCoordinates()
		private LatLng southWest;
		private LatLng northEast;

		public MainActivity activity;
		public RouteMenu routeMenu;
		public double north, east, west, south;
		
		private Context c;
		private GoogleMap map;
		
		private ArrayList<LatLng> tempPolyPoints = new ArrayList<LatLng>();


		public Connect( MainActivity a, GoogleMap m, RouteMenu rm) {
			this.activity = a;
			routeMenu = rm;
			map = m;
		}
		
		public Connect( MainActivity a, GoogleMap m) {
			this.activity = a;
			//routeMenu = rm;
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
			getScreenCornerCoordinates();


		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			//ServiceHandler sh = new ServiceHandler();
			getRoutes();

		return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//getScreenCornerCoordinates();
			routeMenu.setRouteData();

			}
			

		public void getVehicles() {

		}

		public void getStops(Route r) {
			ServiceHandler sh = new ServiceHandler();
			
			// Making the connection to grab all the stops based on the TripID...
			// Temporary: Right now I am just using the first element of the TripID ArrayList
			// But this will change when the user will specify which trip they want based on the
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
	
						LatLng lat_lng = new LatLng(Double.parseDouble(tempObj.get("stop_lat").toString()), Double.parseDouble(tempObj.get("stop_lon").toString()));
						Stop tempStop = new Stop(r.getRouteID(), Integer.parseInt(tempObj.get("stop_id").toString()) , (tempObj.get("stop_name").toString()) , lat_lng);

						r.addStop(tempStop);
					}

					//System.out.println("First Stop: " + r.getStops().get(0).getStopDescription());
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
			else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

		}
		
		public void getShape(Route r) {
			ServiceHandler sh = new ServiceHandler();
			
			// Making the connection to grab all the stops based on the TripID...
			// Temporary: Right now I am just using the first element of the TripID ArrayList
			// But this will change when the user will specify which trip they want based on the
			// Time of day
			
			
			// For some reason Blue Line and probably some others only have a couple stops in some of there trips..
			// Using arrayLocation 5 for temporary in order to get all the stops since location 0 doesn't have them all...
			String sh_Routes = sh.makeServiceCall(
					"http://137.99.15.144/shapes?trip_id=" + r.getTripIDs().get(5),
					ServiceHandler.GET);
			
			System.out.println("http://137.99.15.144/shapes?trip_id=" + r.getTripIDs().get(5));
			if (sh_Routes != null) {
				try {
					JSONObject jsonObj = new JSONObject(sh_Routes);
					JSONArray jsonArray = jsonObj.getJSONArray("shapes");

					
					// looping through All Contacts
					for (int i = 0; i < jsonArray.length(); i++) {
						
						JSONObject tempObj = jsonArray.getJSONObject(i);
						
						LatLng tempLatLng = new LatLng(Double.parseDouble(tempObj.get("shape_pt_lat").toString()), Double.parseDouble(tempObj.get("shape_pt_lon").toString()));
						
						tempPolyPoints.add(tempLatLng);
					    	  
					}
					
					r.setPolyLineOptions(drawPrimaryLinePath(tempPolyPoints, r.getColor()));
					
					

					//System.out.println("First Stop: " + r.getStops().get(0).getStopDescription());
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
			else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

		}

		
		
		// Error on getRoutes... Not sure why yet...
		public void getRoutes() {
			
			
			System.out.println("http://137.99.15.144/routes?lat1=" + north + "&lat2=" + south
					+ "&lon1=" + east + "&lon2=" + west + "&time=9:00:00");
			
			ServiceHandler sh = new ServiceHandler();
			
//			String sh_Routes = sh.makeServiceCall(
//					"http://137.99.15.144/routes?lat1=" + north + "&lat2=" + south
//					+ "&lon1=" + east + "&lon2=" + west + "&time=9:00:00",
//					ServiceHandler.GET);
			
			
			// Using this call instead of filling in the GPS parameters because I could not get any routes to show up
			// When inputting the Lat/Long/Time
			String sh_Routes = sh.makeServiceCall(
					"http://137.99.15.144/routes",
					ServiceHandler.GET);
			
			
			
			if (sh_Routes != null) {
				try {
//					JSONArray jsonArray = new JSONArray(sh_Routes);
					JSONObject jsonObj = new JSONObject(sh_Routes);
					JSONArray jsonArray = jsonObj.getJSONArray("routes");
					

					
					//jsonObj.getJSONObject("route_long_name");
					
//					JSONArray jsonArray = jsonObj.getJSONArray("");
					
					
					// looping through All Contacts
					for (int i = 0; i < jsonArray.length(); i++) {
						
						JSONObject tempObj = jsonArray.getJSONObject(i);
						
	//					System.out.println(tempObj.get("route_long_name"));
	//					System.out.println(tempObj.get("route_id")); 
	//					System.out.println(tempObj.get("route_long_name"));
	//					System.out.println(tempObj.get("route_text_color"));
						
	//					System.out.println("TripID's: " + tempObj.getJSONArray("trip_ids"));

						
						
						
						
						Route tempRoute = new Route(Integer.parseInt(tempObj.get("route_id").toString()));
						
						tempRoute.setRouteTitle(tempObj.get("route_long_name").toString());
						tempRoute.setColor(tempObj.get("route_text_color").toString());
						

						
			// Looping through the Trip ID's found in the JSON Call and setting the TripID's
			// For that given route.  Need to set the TripID's because retrieving all the associated
			// Stops as well as other information is tied to the TripID
						
			// Performance is slow.. need to make it so when a route is clicked it will get the stops
			// Not get the stops and routes all at once...
			
						for(int z = 0; z < tempObj.getJSONArray("trip_ids").length(); z++){
							tempRoute.addTripID(Integer.parseInt(tempObj.getJSONArray("trip_ids").get(z).toString()));
						}
						
						// This may be a temporary call depending on the performance we achieve... This will get all the stops
						// For each route even though we do not need the stop information until that route has been selected
						// and moved to the favorites section....
						
						// These calls are too expensive, this is doing a connection on every single route
						
		//				getStops(tempRoute);
		//				getShape(tempRoute);


						activity.routes.add(tempRoute);
						
					}
					
					System.out.println("Routes Size: " + activity.routes.size());
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
			else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

		}

	

		// Draw route line
		private PolylineOptions drawPrimaryLinePath(ArrayList<LatLng> listLocsToDraw, int c) {


			PolylineOptions options = new PolylineOptions();

			options.color(c);
			options.width(7);
			options.visible(true);

			for (LatLng locRecorded : listLocsToDraw) {
				options.add(locRecorded);
			}

			//map.addPolyline(options);
			//System.out.println("Temp Poly size: " + options.getPoints().size());

			return options;

		}
		
		// This method gets the latitude and longitude of the northeast and southwest coordinates of the screen
		// When viewing the GoogleMap
		public void getScreenCornerCoordinates(){
			LatLngBounds llBounds = map.getProjection().getVisibleRegion().latLngBounds;
			southWest = llBounds.southwest;
			northEast = llBounds.northeast;
					
			north = northEast.latitude;
			south = southWest.latitude;
			east = northEast.longitude;
			west = southWest.longitude;
			
			System.out.println("NorthEast: " + northEast + " SouthWest: " + southWest);
			
		}
		
//		public void setStopMarkers(){
//			for(int i = 0; i < stops.size(); i++){
//				Marker temp = map.addMarker(new MarkerOptions()
//				.position(null).title(
//						""));
//				
//				stops.get(i).setMarker(temp);
//			}
//		}

	}

