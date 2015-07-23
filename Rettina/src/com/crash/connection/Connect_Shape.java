

package com.crash.connection;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import com.crash.rettina.Main_Tile;
import com.crash.rettina.RouteMenu;
import com.crash.rettina.Schedule;
import com.crash.rettina.ServiceHandler;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public class Connect_Shape extends AsyncTask<Void, Void, Void>{
	
		private ProgressDialog pDialog;	
		Route route;
		private ArrayList<LatLng> tempPolyPoints = new ArrayList<LatLng>();
		Context context;
		MainActivity ma;
	//	Schedule sched;
		Main_Tile mainTile;
		GoogleMap map;
		public Schedule sched;


		public Connect_Shape(Route r, GoogleMap googleMap, Context c) {
			route = r;
			context = c;
			
			// Commented out... This works with MainActivity
			// ma = (MainActivity) c;
			
			// This works for the Tile UI
			mainTile = (Main_Tile) c;
			map = googleMap;
			sched = mainTile.fragment_Schedule;
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
			//getScreenCornerCoordinates();


		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			//ServiceHandler sh = new ServiceHandler();
			
			getShape(route);
			//getShape_Test(route);

		return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//getScreenCornerCoordinates();

			// If the route's polylineOptions is not null, then set the polyline
			if(route.getPolyLineOptions() != null){
				System.out.println("Setting the polyline");

				route.setPolyLine(map.addPolyline(route.getPolyLineOptions()));
				
				// Setting the schedule for this route... May need to change this up a little bit so when the route has already
				// Found all the stops and is clicked again, skip the connect_shape call and just use the information for that route

			}

			pDialog.dismiss();
			
			
			// Trying to handle drawing on the map and updating the schedule from here.. Not sure if it will work!
			//ma.drawRoute(route);
			// Still not showing
			if(route.getPolyLine() != null){
				
			
				System.out.println("Showing the polyline");
				mainTile.showRoute(route);
				
				// Seeing if this fixes the schedule problem
				
			}
			
			// Used for MainActivity UI
			//ma.showStops(route);		
			//ma.getSched().setRoutes(route);
			
			// Setting the schedule

			if(route.isNavMode() == true){
				navMode(route);
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
			
			// Error: When trying to get the meriden data... Error 500 on Trevor's behalf
//			String sh_Routes = sh.makeServiceCall(
//					"http://137.99.15.144/shapes?trip_id=" + r.getTripIDs().get(5),
//					ServiceHandler.GET);
			
			
//			System.out.println("http://137.99.15.144/shapes/UConn?trip_id=" + r.getTripIDs().get(5));

			
			String sh_Routes = null;

			// Need to make it so it actually gets the real tripID and calls it
//			String sh_Routes = sh.makeServiceCall(
//					"http://137.99.15.144/stops?trip_id=" + r.getTripIDs().get(5),	// New API call, using Meriden
//					ServiceHandler.GET);
			
			try {
				String query = URLEncoder.encode(r.getTripIDs().get(0), "utf-8");
				String url = "http://137.99.15.144/shapes?trip_id=" + query;

				sh_Routes = sh.makeServiceCall(
						url,	// New API call, using Meriden
						ServiceHandler.GET);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
//			String sh_Routes = sh.makeServiceCall(
//					"http://137.99.15.144/stops?trip_id=6%20UConn%20Transportation%20Service",
//					ServiceHandler.GET);
			
			
			
			
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
					
					
					

					System.out.println("Size of polyline " + tempPolyPoints.size());
					
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
					
					

					System.out.println("PolyLineOptions Size: " + tempPolyPoints.size());
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
			else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			
		}
		
		
		public void navMode(Route r){
			// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
			CameraPosition cameraPosition = new CameraPosition.Builder()
			    .target(r.getStops().get(0).getLatLng())      // Sets the center of the map to Mountain View
			    .zoom(19)                   // Sets the zoom
			    .bearing(90)                // Sets the orientation of the camera to east
			    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
			    .build();                   // Creates a CameraPosition from the builder
			ma.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			
			//Null Pointer here
			 r.getStops().get(0).getMarker().showInfoWindow();
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

			return options;

		}
	}

