package com.crash.connection;

/*
 * Mitch Thornton
 * Karthik Konduri
 * Rettina - 2015
 * Connect_Shape is used to gather the GTFS Shape information from the server.
 * This will be used to draw the polyline, and will be called when the user
 * Either touches/holds the desire route
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.crash.rettina.Main;
import com.crash.rettina.Schedule;
import com.crash.connection.ServiceHandler;
import com.crash.routeinfo.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class Connect_Shape extends AsyncTask<Void, Void, Void>{
	
		private ProgressDialog pDialog;	
		private ArrayList<LatLng> tempPolyPoints = new ArrayList<LatLng>();
		public Schedule sched;

		Route route;
		Context context;
		Main mainTile;
		GoogleMap map;


		// Constructor
		public Connect_Shape(Route r, GoogleMap googleMap, Context c) {
			
			route = r;
			context = c;
			mainTile = (Main) c;
			map = googleMap;					// Map refers to the Google Map in the Main_Tile activity
			sched = mainTile.fragment_Schedule; // Schedule Fragment
		}
	
		// Launches the progress dialog to alert the user that Connect_Shape is running the background
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			 pDialog = new ProgressDialog(context);
			 pDialog.setMessage("Please wait...");
			 pDialog.setCancelable(false);
			 pDialog.show();
		}

		// Get's the shape information from the server
		@Override
		protected Void doInBackground(Void... arg0) {
			
			getShape(route);	// Method to get the Shape information from server
			
		return null;
		}
		
		// Once the Shape information has been retrieved from the server, set the polyine and dismiss the loading message
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// If the route's polylineOptions is not null, then set the polyline
			if(route.getPolyLineOptions() != null){
				
				route.setPolyLine(map.addPolyline(route.getPolyLineOptions()));

			}

			pDialog.dismiss();	// Close the loading message
			
			
			// If the polyine is not null, then show the polyline on the Google Map which is in the Main_Tile activity
			if(route.getPolyLine() != null){
				mainTile.showRoute(route);				
			}

			
			// If the route is currently being navigated by the user, set the camera position for navigation mode
			if(route.isNavMode() == true){
				navMode(route);
			}

		}
			
		
		public void getShape(Route r) {
			ServiceHandler sh = new ServiceHandler();
			
			// Making the connection to grab the shape points based on the TripID
			String sh_Routes = null;
		
			try {
				// Set the URL for the API request to the server
				String query = URLEncoder.encode(r.getTripIDs().get(0), "utf-8");
				String url = "http://137.99.15.144/shapes?trip_id=" + query;

				sh_Routes = sh.makeServiceCall(url, ServiceHandler.GET);
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			// If the server has found a shape, then loop through the server JSON results, saving the polyline points
			if (sh_Routes != null) {
				try {
					
					JSONObject jsonObj = new JSONObject(sh_Routes);
					JSONArray jsonArray = jsonObj.getJSONArray("shapes");

					// looping through the entire polyline lat/lng points, saving the points to tempPolyPoints
					for (int i = 0; i < jsonArray.length(); i++) {
						
						JSONObject tempObj = jsonArray.getJSONObject(i);
						LatLng tempLatLng = new LatLng(Double.parseDouble(tempObj.get("shape_pt_lat").toString()), Double.parseDouble(tempObj.get("shape_pt_lon").toString()));
						
						// Add the polyline lat/lng points to tempPolyPoints
						tempPolyPoints.add(tempLatLng);
					    	  
					}
					
					// Set the polyline and draw the polyline based on the route's specified polyline color retrieved from GTFS data
					r.setPolyLineOptions(drawPrimaryLinePath(tempPolyPoints, r.getColor()));

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
			else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

		}
		
		
		// For Testing or when the server is down only!
		public void getShape_Test(Route r) {
			ServiceHandler sh = new ServiceHandler();
			
			String sh_Routes = sh.makeServiceCall(
					"http://137.99.15.144/static/shapes.json",
					ServiceHandler.GET);

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
										
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
			else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			
		}
		
		
		public void navMode(Route r){
			// Construct a CameraPosition and animate the camera to that position.
			CameraPosition cameraPosition = new CameraPosition.Builder()
			    .target(r.getStops().get(0).getLatLng())      // Sets the center of the map to Mountain View
			    .zoom(19)                   // Sets the zoom
			    .bearing(90)                // Sets the orientation of the camera to east
			    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
			    .build();                   // Creates a CameraPosition from the builder
			
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			
			// Display the first stop's information to the user
			 r.getStops().get(0).getMarker().showInfoWindow();
		}
	

		// Create the polyline
		private PolylineOptions drawPrimaryLinePath(ArrayList<LatLng> listLocsToDraw, int c) {
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

