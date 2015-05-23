package com.crash.rettina;

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
import android.util.Log;

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

public class GetJSON extends AsyncTask<Void, Void, Void> {
	private ProgressDialog pDialog;
	private Map<String, LatLng> vehicles = new LinkedHashMap<String, LatLng>();
	private Map<Integer, ArrayList<String>> routeColors = new LinkedHashMap<Integer, ArrayList<String>>();

	private ArrayList<Stop> stops = new ArrayList<Stop>();
	private ArrayList<Route> routes = new ArrayList<Route>();

	ArrayList<LatLng> routeLocations = new ArrayList<LatLng>();
	
	// Variables to hold the coordinates of the NorthEast and SouthWest corners of the screen
	// They are set by calling getScreenCornerCoordinates()
	private LatLng southWest;
	private LatLng northEast;

	public MainActivity activity;
	public RouteMenu routeMenu;

	
	private Context c;
	private GoogleMap map;

	public GetJSON( MainActivity a, GoogleMap m, RouteMenu rm) {
		this.activity = a;
		routeMenu = rm;
		map = m;
	}
	
	public GetJSON( MainActivity a, GoogleMap m) {
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

	}

	@Override
	protected Void doInBackground(Void... arg0) {
		// Creating service handler class instance
		ServiceHandler sh = new ServiceHandler();
		getVehicles();
		getStops();
		getRoutes();
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
			
			 for (Entry<Integer, ArrayList<String>> entry : routeColors.entrySet()) {
			        String key = entry.getKey().toString();;
			        ArrayList<String> value = entry.getValue();			

				if(key.equals(Integer.toString((routes.get(i).getRouteID())))){
										
					routes.get(i).setRouteTitle(value.get(0));
					
					c = Color.parseColor(value.get(1));
					break;
				}
				
			}
				 
							
			routes.get(i).setAllRoutePoints();
			
			PolylineOptions tempPoly = drawPrimaryLinePath(routes.get(i).routePoints, c);
			
			
			routes.get(i).setPolyLineOptions(tempPoly);
		}
				
		
		activity.routes = routes;
		//routeMenu.setRouteData();
		
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
					
					
					// Create new Stop object so it can be stored in a route
					// Temporarily creating the markers so the marker information can be set later in the Route class for now...
					
//					Marker temp = map.addMarker(new MarkerOptions()
//					.position(null).title(
//							""));
					
					Stop tempStop = new Stop((Integer) jsonObj.get("RouteID"),
							(Integer) jsonObj.get("RouteStopID"), jsonObj.get(
									"Description").toString(), lat_Lng);
					
//					tempStop.setMarker(temp);
					
					
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
	
	public void getRoutes() {
		ServiceHandler sh = new ServiceHandler();

		String sh_Routes = sh.makeServiceCall(
				"http://www.uconnshuttle.com/Services/JSONPRelay.svc/GetRoutes",
				ServiceHandler.GET);
		if (sh_Routes != null) {
			try {
				JSONArray jsonArray = new JSONArray(sh_Routes);
				
				// looping through All Contacts
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					
					Integer routeID =  (Integer)jsonObj.get("RouteID");
					ArrayList<String> tempArray = new ArrayList<String>();
					
					String description = jsonObj.get(
							"Description").toString();
					
					String routeColor = jsonObj.get("MapLineColor").toString();
					tempArray.add(description);
					tempArray.add(routeColor);
					
					
					routeColors.put(routeID, tempArray);
					

							
//					System.out.println("Description: " + tempArray.get(0));
//					System.out.println("Line Color: " + tempArray.get(1));
//					System.out.println("RouteID: " + routeID);


					
					// System.out.println("Stop: " +
					// jsonObj.get("Description"));
					// System.out.println("Lat: " + jsonObj.get("Latitude"));
					// System.out.println("Long: " + jsonObj.get("Longitude"));
					// System.out.println("RouteID: " + jsonObj.get("RouteID"));
//					for(int j = 0; j < jsonObj.getJSONArray("MapPoints").length(); j++){
					 
					 
					 // Make a temporary lat/lng for each given point along the route.. Add this to the arrayList routeLocations
					 // Call the method drawPrimaryLinePath() and pass it the routeLocations arrayList so it can draw the route lines
					 // On the Google Map
						
				//	System.out.println("Lat: " + Double.parseDouble((String)jsonObj.getJSONArray("MapPoints").getJSONObject(j).getString("Latitude")) + "Long: " + Double.parseDouble((String)jsonObj.getJSONArray("MapPoints").getJSONObject(j).getString("Longitude")));
//					 LatLng tempLatLng = new LatLng(Double.parseDouble((String)jsonObj.getJSONArray("MapPoints").getJSONObject(j).getString("Latitude")), Double.parseDouble((String)jsonObj.getJSONArray("MapPoints").getJSONObject(j).getString("Longitude")));
//					 
//					 tempStop.fillRouteList(tempLatLng);
//					}
					

					// Create the lat/lng for the given stop
					

					// Put each stop into an array list made of Stop objects....'
					
//					stops.add(tempStop);
				}
//				
//				 for (Integer key : routeColors.keySet()) {
//				        System.out.println(key + " " + routeColors.get(key));
//				    }
//
//				    for (Entry<Integer, ArrayList<String>> entry : routeColors.entrySet()) {
//				        String key = entry.getKey().toString();;
//				        ArrayList<String> value = entry.getValue();
//				        System.out.println("key, " + key + " value " + value.get(0) + "value " + value.get(1));
//				    }

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
		//System.out.println("Temp Poly size: " + options.getPoints().size());

		return options;

	}
	
	// This method gets the latitude and longitude of the northeast and southwest coordinates of the screen
	// When viewing the GoogleMap
	public void getScreenCornerCoordinates(){
		LatLngBounds llBounds = map.getProjection().getVisibleRegion().latLngBounds;
		southWest = llBounds.southwest;
		northEast = llBounds.northeast;
				
		double north = northEast.latitude;
		double south = southWest.latitude;
		double east = northEast.longitude;
		double west = southWest.longitude;
		
		System.out.println("NorthEast: " + northEast + " SouthWest: " + southWest);
	}
	
//	public void setStopMarkers(){
//		for(int i = 0; i < stops.size(); i++){
//			Marker temp = map.addMarker(new MarkerOptions()
//			.position(null).title(
//					""));
//			
//			stops.get(i).setMarker(temp);
//		}
//	}

}
