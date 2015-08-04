/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * Connect is used to establish a connection to our server.  Data is presented as JSON and is parsed via the getRoutes(), getStops(),
 * and getShape().  There are plans to also develop methods for getVehicles() so the vehicles can be tracked in real time
 */

package com.crash.connection;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.crash.rettina.Main;
import com.crash.connection.ServiceHandler;
import com.crash.routeinfo.Route;
import com.google.android.gms.maps.GoogleMap;

public class Connect extends AsyncTask<Void, Void, Void> {

	// The main activity... Everything is connected activity_tile
	public Main activity_tile;

	// Holds the coordinates of the corners of the screen when the user is viewing the Google Map
	public double north, east, west, south;

	// Used to pass information to the Main_Tile activity
	private Context c;
	private GoogleMap map;

	// Used for connecting to the server
	private String sh_Routes;
	private ServiceHandler sh;
	
	// Constructor
	public Connect(Main a, GoogleMap m) {
		activity_tile = a;		// The Main activity... All fragments and connections are linked to the Main actvity
		map = m;				// Allows to work with the GoogleMap on the Main activity
	}

	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	// Gets all the routes that fall within the corner of the screen's coordinates
	@Override
	protected Void doInBackground(Void... arg0) {

		getRoutes();
		return null;
	}

	// Once the routes have been retrieved from the server, update the other fragments with the new routes
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		activity_tile.updateRoutes();
		activity_tile.fragment_Routes.searching(false); 						// Sets searching to be false, so the default searching text will be displayed
		activity_tile.fragment_Routes.tv_searching.setVisibility(View.GONE);	// Hides the 'Searching' text that alerts the user when searching for routes
	}

	// Gets all the routes that fall within the coordinates of the corner of the screen when viewing the map
	public void getRoutes() {
		
		// Grabs the coordinates of the corner of the screen, and makes the 
		// API request with this information, retrieving all the routes within the coordinates
		getScreenCornerCoordinates();	


		// If there are routes found by the server, then loop through the results and add
		// the found routes to the 'Routes' which is found in the Main activity..('activity_tile')
		if (sh_Routes != null) {
			try {
				JSONObject jsonObj = new JSONObject(sh_Routes);
				JSONArray jsonArray = jsonObj.getJSONArray("routes");

				// looping through the entire JSON results
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject tempObj = jsonArray.getJSONObject(i);

					// Create a temporary route to hold all the route information
					Route tempRoute = new Route(tempObj.get(
							"route_id").toString());

					// Set the route title for this route
					tempRoute.setRouteTitle(tempObj.get("route_long_name")
							.toString());

					// Set the color of the route.. (Used for the route's polyline)
					tempRoute.setColor(tempObj.get("route_text_color")
							.toString());

					
				//// ERROR PROTECTION: CHECKS FOR DUPLICATE ROUTES ////
					
					// DuplicateRoute is used to ensure a route is not added
					// twice, which would cause problems... This is based off of the routeID, which is unique to each route
					boolean duplicateRoute = false;

					// Goes through all the already found routes, checking to see if the current "new" route is actually a duplicate
					// If it is a duplicate, it sets the boolean to true and will not add the route
					// Checks the new Temporary route against all the existing routes to see if it is a duplicate route
					for (int z = 0; z < activity_tile.routes.size(); z++) {
						
						// If a duplicate route, then set the boolean to true and do not add the route
						if (tempRoute.getRouteID().equals(activity_tile.routes.get(z)
								.getRouteID())) {
							duplicateRoute = true;
						}

					}

					// If it is not a duplicate route, then add the tempRoute to 'Routes' found in activity_tile
					if (duplicateRoute == false) {

						// Looping through the Trip ID's found in the JSON Call
						// and setting the TripID's for that given route
						for (int z = 0; z < tempObj.getJSONArray("trip_ids")
								.length(); z++) {

							tempRoute.addTripID(tempObj.getJSONArray("trip_ids")
											.get(z).toString());
						}

						activity_tile.routes.add(tempRoute);

					} 
					// Route was a duplicate so report the error
					else {
						System.out.println("This is a duplicate Route!!!!");
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the url");
		}

	}
	

	// This method gets the latitude and longitude of the northeast and southwest coordinates of the 
	// screen when viewing the GoogleMap, then makes a call to the server to return all routes that 
	// fall within these coordinates
	public void getScreenCornerCoordinates() {
			
		// Used for connecting to the server
		sh = new ServiceHandler();	

		// Formats the time so it can be properly read by the server
		SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm:ss");
		
		
		// Makes the call to the server with the corner of the screen coordinates as well as the current formatted time
		
		// Need to make it so the second longitude is the one that is closest to 0 in order for the server to return correct values
		if(Math.abs(east) > Math.abs(west)){
			sh_Routes = sh.makeServiceCall("http://137.99.15.144/routes?lat1="+activity_tile.north +"&lat2=" + activity_tile.south + 
			"&lon1=" + activity_tile.east + "&lon2=" + activity_tile.west + "&start=" + localDateFormat.format(new Date()),
			ServiceHandler.GET);
		}
		else{
			sh_Routes = sh.makeServiceCall("http://137.99.15.144/routes?lat1="+activity_tile.south +"&lat2=" + activity_tile.north + 
			"&lon1=" + activity_tile.west + "&lon2=" + activity_tile.east  + "&start=" + localDateFormat.format(new Date()),
			ServiceHandler.GET);
		}

	}

}
