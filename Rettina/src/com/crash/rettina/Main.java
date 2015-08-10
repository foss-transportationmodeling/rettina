/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * Main is the heart of the app... It contains all Fragments attach to Main.
 * At the top lies a tabhost with 3 tabs used to navigate through the following fragments:
 * 
 * 	1. Search	- Used to search for Routes that fall within the coordinates of the corner of the current map view
 * 				  Triggers the "RoutesFragment" fragment which pops up on the bottom half of the screen
 * 
 * 	2. Favorites - Pressing 'Favorites' will trigger the 'Favorites' fragment to pop up on the bottom half of the screen
 * 				   Displaying all the user's favorite routes. Also allows for some more functionality
 * 
 * 	3. Settings  - Pressing 'Settings' will trigger the 'Settings' fragment to pop up and take up the entire screen.
 * 				   'Settings' is still a work in progress 
 * 
 * As mentioned before, the user can locate routes by moving around on the map and when the desired location is found
 * the user presses the 'Search' tab which will establish a connection to the server and find the routes that fall within
 * the coordinates of the top left and bottom right corners of the screen.  The Routes then pop up in the 'RoutesFragment'
 * which takes up the bottom half of the screen. 
 * 
 * If the user taps a route, it will show a preview of the route on the map. If the route is held, then the route will be
 * displayed on the map and also added to the 'Favorites' fragment which can be accessed by pressing the 'Favorites' tab
 * 
 * A 'Schedule' fragment can be toggled by pressing a the popup imagebutton.  Settings can also be toggled by pressing the
 * 'Settings' tab.
 * 
 * The route information is obtained from a server by using the Classes found in the 'com.crash.connection' package
 * 
 * 
 */

package com.crash.rettina;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.crash.connection.Connect;
import com.crash.connection.Connect_Shape;
import com.crash.connection.Connect_Stops;
import com.crash.routeinfo.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.LocationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;


public class Main extends Activity implements LocationListener {

	public GoogleMap googleMap;		// Google Map... Represents most of the screen area for this Main activity
	private Location location;		// Holds the user's location
	private LocationManager lm;		// Handles location based calls
		
	private Marker myMarker;		// Used to display the user's location on the map
	public LatLng lat_Lng;			// Used to hold the user's latitude and longitude
	private double latitude;		
	private double longitude;

	// Used to hold all the Routes and all the Route titles
	public ArrayList<Route> routes = new ArrayList<Route>();
	public ArrayList<String> routeTitles = new ArrayList<String>();

	public ImageButton imgbtn_SchedulePopup;	// Popup ImageButton that is used to display the schedule for a selected Route

	// Holds the 4 tabs up top used for navigation... One tab is invisible to the user
	// and used to make it look like none of the three main tabs is currently selected
	public TabHost tabhost;						 

	public Connect connect;		// Used to connect to the server and retrieve the routes
	public GridView gridview;	// GridView is used to display all the found routes... Displayed in the bottom half of the screen
	public ArrayAdapter<String> adapter;	// Custom adapter used for GridView

	// The following are holders for each of the Fragments
	public Favorites fragment_Favorites;
	public Schedule fragment_Schedule;
	public Navigator fragment_Navigator;
	public Sharing fragment_Sharing;
	public RoutesFragment fragment_Routes;
	public SettingsFragment fragment_Settings;

	// Used to manage and transition the fragments
	public FragmentManager manager;
	public FragmentTransaction ft;
	
	// Used to hold the user's locations into a nice bundle so it can be passed to the server
	// Easily once the user is done traveling along their route 
	public ArrayList<Location> locationHolder = new ArrayList<Location>();
	public ArrayList<String> locationTimeStamps = new ArrayList<String>();
	
	// These two variables are used to collect 'Sharing' data from the user. Since Sharing is another fragment
	// These variables are stored on the Main activity to allow for easy location transfer since this activity is always running
	private float pingTime_Selection;
	private String locationProvider_Selection;
		
	// Says when to stop tracking the user.... We give the option if the user wants to be tracked/contribute location data
	private boolean trackUser = false;

	private Main main;	// Holds the Main activity

	// Holds the route that is currently clicked.. 
	// Important for communicating with the other Fragments
	public Route clickedRoute = null;	
	
	
	// The following are used to identify the coordinates of the screen corners so
	// The Routes that fall within these coordinates can be relayed to the user
	private LatLng northEast;
	private LatLng southWest;
	public double south;
	public double east;
	public double north;
	public double west;
	
	// Used for test purposes
	private static final String TAG = "Test";

	// Called when the activity is created
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Uses the main_tile.xml
		setContentView(R.layout.main_tile);	

		// Sets the Fragment manager
		manager = getFragmentManager();

		// Sets the Map fragment... Important for swapping fragments
		final MapFragment fm = (MapFragment) manager
				.findFragmentById(R.id.map_tile);

		// Sets the google map
		googleMap = fm.getMap();

		// Sets the Main activity
		main = this;
	    
	    
		// Prompt user for internet connection if none detected
	    if(isOnline() == false){
		createNetErrorDialog();
	    }
		
		
		// Initiates all the Fragments
		fragment_Favorites = new Favorites();
		fragment_Schedule = new Schedule(main);
		fragment_Navigator = new Navigator(main);
		fragment_Sharing = new Sharing(main);
		fragment_Routes = new RoutesFragment(main);
		fragment_Settings = new SettingsFragment(main);


		// Remove the compass and the zoom options that come stock on the map
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.getUiSettings().setCompassEnabled(false);
		googleMap.getUiSettings().setMapToolbarEnabled(false);

		// Sets the tabhost, the gridview, and the schedule popup button
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		gridview = (GridView) findViewById(R.id.gridView1);
		imgbtn_SchedulePopup = (ImageButton) findViewById(R.id.imgbtn_schedulepopup);

		// Adding the 'Search' tab
		tabhost.setup();
		TabSpec ts = tabhost.newTabSpec("tag1");
		ts.setContent(R.id.tab1_tile);
		ts.setIndicator("Search");
		tabhost.addTab(ts);

		// Adding the 'Favorites' tab
		ts = tabhost.newTabSpec("tag2");
		ts.setContent(R.id.tab2_tile);
		ts.setIndicator("Favorites");
		tabhost.addTab(ts);

		// Adding the 'Settings' tab
		ts = tabhost.newTabSpec("tag3");
		ts.setContent(R.id.tab3_tile);
		ts.setIndicator("Settings");
		tabhost.addTab(ts);

		// Adding the invisible tab used for showing that none of the main three tabs are currently selected
		ts = tabhost.newTabSpec("tag4");
		ts.setContent(R.id.tab4_tile);
		ts.setIndicator("Hide");
		tabhost.addTab(ts);

		// Setting the tab is invisible to the user
		tabhost.getTabWidget().getChildAt(3).setVisibility(View.GONE);

		// App starts off with the invisible tab currently selecting
		// allowing the user to select one of the main three selections
		tabhost.setCurrentTab(3);

		// Creating custom adapter that holds all the routeTitles
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, routeTitles);

		// Handles when the Schedule popup button is clicked
		imgbtn_SchedulePopup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// Used to handle fragment transactions
				ft = manager.beginTransaction();
				
				// If the Fragment 'RoutesFragment' is visible, the remove RoutesFragment and add Fragment Schedule
				if(fragment_Routes.isVisible()){
					
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
					transaction.add(R.id.fragment_group_tile, fragment_Schedule);
					transaction.addToBackStack("Schedule");
					transaction.commit();
					
				ft.remove(fragment_Routes);
				ft.commit();

				}
				// If the 'Favorites' fragment is visible, then remove that and add the 'Schedule' fragment
				else if(fragment_Favorites.isVisible()){
					
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
					transaction.replace(R.id.fragment_group_tile, fragment_Schedule);
					transaction.addToBackStack("Schedule");
					transaction.commit();
					
					ft.remove(fragment_Favorites);
					ft.commit();

				}
				
				// Set the 'Schedule' popup button to invisible
				imgbtn_SchedulePopup.setVisibility(View.GONE);

				// Setting the "Schedule's" ListView to be the size of the amount of Stops inside of the clicked route
				fragment_Schedule.lv_arr = new String[fragment_Routes.clickedRoute
						.getStops().size()];

				// Populate the "Schedule's" ListView with the clickedroutes Stops
				for (int i = 0; i < fragment_Routes.clickedRoute.getStops()
						.size(); i++) {
					fragment_Schedule.lv_arr[i] = fragment_Routes.clickedRoute
							.getStops().get(i).getStopDescription();
				}

				// Set "Schedule's" tempRoute = to the clickedRoute
				fragment_Schedule.tempRoute = clickedRoute;
			}
		});

		// Initializing the location manager
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		boolean gps_enabled = false;
		boolean network_enabled = false;

		try {
		    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch(Exception ex) {}

		try {
		    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch(Exception ex) {}

		if(!gps_enabled && !network_enabled) {
		    // notify user
		    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		    dialog.setMessage("GPS or Network is highly recommended");
		    dialog.setPositiveButton("Open Location Services", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		                // TODO Auto-generated method stub
		                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		                startActivity(myIntent);
		                //get gps
		            }
		        });
		    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		                // TODO Auto-generated method stub

		            }
		        });
		    dialog.show();      
		}		

		// Setting the criteria specifications and getting the best provider
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		
		// Getting the most recent location known
		Location mostRecentLocation = lm.getLastKnownLocation(provider);

		// If the most recent location is not null, then get the lat/lng and animate the camera to that position
		if (mostRecentLocation != null) {
			latitude = mostRecentLocation.getLatitude();
			longitude = mostRecentLocation.getLongitude();

			lat_Lng = new LatLng(latitude, longitude);

			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lat_Lng,
					14));

			// Place a marker on the map to denote the user's location
			userLocMarker(lat_Lng);
		}

		// This sets an ontouch listener for each of the tabs... This is used to
		// detect when an already focused
		// Tab is clicked again. This way, the tab can be unselected and go back
		// to the Map main view
		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
			View v = tabhost.getTabWidget().getChildAt(i);
			v.setTag(i);
		}

		// Request location updates
		lm.requestLocationUpdates(provider, 1, 0, this);

		// Handles when "Search" tab is clicked... Should search for the routes that fall within the screen's coordinates
				// And place the routes fragment at the bottom of the screen unless it is already present
				tabhost.getTabWidget().getChildAt(0)
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								
								// If the user is connected to the internet, then continue searching, otherwise prompt internet connection
								if(isOnline()){
								
								// Grab the corner of the screens coordinates so the
								// routes can be found based on GPS coordinates
								getScreenCornerCoordinates();
								
								// Set the fragment transactions and custom animations for the transactions
								ft = manager.beginTransaction();
								ft.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);


								// If the Search tab is already pressed and the Routes Fragment is visible, then remove the Routes Fragment
								if (tabhost.getCurrentTab() == 0
										&& fragment_Routes.isVisible()) {

									ft.remove(fragment_Routes);
									ft.commit();
									tabhost.setCurrentTab(3);
								}
								
								// If the Favorites Fragment is visible, remove it
								else if (fragment_Routes.isVisible()) {
									ft.remove(fragment_Routes);
									ft.commit();
								}
								
								// Otherwise, remove all other fragments and display the Routes fragment
								else {
									

									// If the schedule fragment is visible, the remove the schedule fragment
									if (fragment_Schedule.isVisible()) {
										ft.remove(fragment_Schedule);
									}			
									
									// If the Settings fragment is visible, remove it
									else if (fragment_Settings.isVisible()) {
										ft.remove(fragment_Settings);
									}
									
									// If the Navigator Fragment is visible, remove it
									else if (fragment_Navigator.isVisible()) {
										ft.remove(fragment_Navigator);
									}
									
									if(fragment_Routes.routeTitles.size() > 0){
									// Remove the routes, so it can be refreshed by the newly found routes
									routes.clear();
									fragment_Routes.routeTitles.clear();
									fragment_Routes.adapter.notifyDataSetChanged();
									}

									// Get all the routes that fall within the maps coordinates
									connect = new Connect(main, googleMap);
									
									fragment_Routes.searching(true); // Sets searching to be true, so the default searching text will be displayed
									connect.execute();
									
									tabhost.setCurrentTab(0);
									
									// Custom Animation.. May need to change it so it slides upwards instead of sideways									
									// Add the Routes Fragment at the bottom of the screen				
									ft.add(R.id.main_fragmentgroup, fragment_Routes);
									ft.addToBackStack("Routes");
									ft.commit();

								}

								}
								else{
									createNetErrorDialog();
								}
							}
						});

				// Handles when the "Favorites" tab is clicked. Should display the favorite routes at the bottom of the screen
				// And remove any other fragments
				tabhost.getTabWidget().getChildAt(1)
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								
								// If the user is connected to the internet, then continue searching, otherwise prompt internet connection
								if(isOnline()){
								
								ft = manager.beginTransaction();
								ft.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);


								fragment_Favorites.are_there_favorites = false;
								
								// If the "Favorites" tab has already been clicked and is currently being displayed
								// At the bottom of the screen, then remove the Favorites fragment and set the tab to hidden
								if (tabhost.getCurrentTab() == 1
										&& fragment_Favorites.isVisible()) {

									ft.remove(fragment_Favorites);		// Remove the "Favorites" fragment
									ft.commit();
									tabhost.setCurrentTab(3);			// This tab is hidden to the user
									
								}
								
								// If the Favorites Fragment is visible, remove it
								else if (fragment_Favorites.isVisible()) {
									ft.remove(fragment_Favorites);
									ft.commit();
								}
								
								// Otherwise, remove all other fragments, and display the Favorites fragment
								else {

									// If the Schedule Fragment is visible, remove it
									if (fragment_Schedule.isVisible()) {
										ft.remove(fragment_Schedule);
									}
									
									// If the Routes Fragment is visible, remove it
									else if (fragment_Routes.isVisible()) {
										System.out.println("Routes is aleady added!");
										ft.remove(fragment_Routes);
									}
									
									// If the Navigator Fragment is visible, remove it
									else if (fragment_Navigator.isVisible()) {
										ft.remove(fragment_Navigator);
									}
									
									// If the Settings Fragment is visible, remove it
									else if (fragment_Settings.isVisible()) {
										ft.remove(fragment_Settings);
									}
									
									// If "Navigator" is visible, remove it
									else if (fragment_Favorites.isVisible()) {
										ft.remove(fragment_Favorites);
										ft.commit();
									}

									// Add the Favorites fragment to the bottom of the screen and set the tab to "Favorites"
									ft.add(R.id.main_fragmentgroup, fragment_Favorites);
									ft.addToBackStack("Favorites");
									ft.commit();

									tabhost.setCurrentTab(1);

								}
								}
								else{
									createNetErrorDialog();
								}

							}
						});

				// Handles when the "Settings" tab is clicked... Should display the Settings Fragment and remove
				// Other fragments.... If Settings is already visible, remove it
				tabhost.getTabWidget().getChildAt(2)
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								System.out.println("Clicked Settings!");

								ft = manager.beginTransaction();
								ft.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);

								
								fragment_Routes.searching(false);
								
								// If the "Settings" tab is already highlighted, and is the "Settings" fragment
								// Is already active, remove it
								if (tabhost.getCurrentTab() == 2
										&& fragment_Settings.isVisible()) {
									
									ft.remove(fragment_Settings);
									ft.commit();

									tabhost.setCurrentTab(3);

								}
								
								// If Settings is already visibile, remove it
								else if(fragment_Settings.isVisible()){
									ft.remove(fragment_Settings);
									ft.commit();

								}

								// Otherwise, remove any other visible fragment and display the Settings fragment
								else{

									// If "Sharing" is visible, remove it
									if (fragment_Sharing.isVisible()) {
										ft.remove(fragment_Sharing);
									}
									// If "Schedule" is visible, remove it
									else if (fragment_Schedule.isVisible()) {
										ft.remove(fragment_Schedule);
									}
									// If "Navigator" is visible, remove it
									else if (fragment_Navigator.isVisible()) {
										ft.remove(fragment_Navigator);
									}
									// If "Favorites" is visible, remove it
									else if (fragment_Favorites.isVisible()) {
										ft.remove(fragment_Favorites);
									}
									// If "Routes" is visible, remove it
									else if (fragment_Routes.isVisible()) {
										ft.remove(fragment_Routes);
									}
									
									
									// Add the Settings fragment and set the tab to "Settings"
									ft.add(R.id.fragment_group_tile, fragment_Settings);
									ft.addToBackStack("Settings");
									ft.commit();
										
									tabhost.setCurrentTab(2);

								}

							}
						});
		
	}

	// Marks the user's location
	private void userLocMarker(LatLng lat_lng) {

		myMarker = googleMap.addMarker(new MarkerOptions().position(lat_lng)
				.title("You"));
	}

	// When the user's location moves
	@Override
	public void onLocationChanged(Location location) {
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		LatLng latlng = new LatLng(lat, lon);

		
		// Added to prevent location null pointer exception
		if(latlng != null){
		myMarker.setPosition(latlng);
		
		
			// Add to the user's location tracker
			if(trackUser == true){
			locationHolder.add(location);
			
			SimpleDateFormat localDateFormat = new SimpleDateFormat("MMM dd yyyy hh:mm:ssa");
			
			locationTimeStamps.add(localDateFormat.format(new Date()));
	
			}
		}

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	// Set the polyline
	public void drawRoute(Route r) {
		r.setPolyLine(googleMap.addPolyline(r.getPolyLineOptions()));
	}

	// Hide the polyline
	public void hideRoute(Route r) {
		r.getPolyLine().setVisible(false);
		r.getPolyLineOptions().visible(false);
	}

	// Show the polyline
	public void showRoute(Route r) {
		r.getPolyLine().setVisible(true);
	}

	// Show the stops
	public void showStops(Route r) {
		r.showStops(googleMap);
	}

	// Hide the stops
	public void hideStops(Route r) {
		r.hideStops(googleMap);
	}

	// If the routeTitles size is less than the amount of routes, then clear the routes and re-add the routes
	public void updateRoutes() {
		if (routeTitles.size() < routes.size()) {
			routeTitles.clear();
			
			for (int i = 0; i < routes.size(); i++) {
				routeTitles.add(routes.get(i).getRouteTitle());
			}
			
			// Notify the adapter
			fragment_Routes.adapter.notifyDataSetChanged();
		}
	}

	// Get the Stops
	public void getStops(Route r) {
		Connect_Stops tempConnect = new Connect_Stops(r, main, googleMap);
		tempConnect.execute();
	}

	// Get the Routes shape ('polyline')
	public void getShape(Route r) {
		Connect_Shape tempConnect = new Connect_Shape(r, googleMap, main);
		tempConnect.execute();
	}

	// Hide the Favorite Fragment
	public void hideFavorites() {
		ft = manager.beginTransaction();
		ft.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);

		ft.remove(fragment_Favorites);
		ft.commit();
		tabhost.setCurrentTab(3);

	}

	// Hide the Schedule fragment
	public void hideSchedule() {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
		transaction.remove(fragment_Schedule);
		transaction.commit();
	}

	// Show the Navigation fragment
	public void showNavigation() {
		ft = manager.beginTransaction();
		ft.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
		ft.replace(R.id.main_fragmentgroup, fragment_Navigator);
		ft.commit();

	}

	// Get the coordinates of the corners of the screen
	public void getScreenCornerCoordinates() {
		LatLngBounds llBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
		southWest = llBounds.southwest;
		northEast = llBounds.northeast;

		north = northEast.latitude;
		south = southWest.latitude;
		east = northEast.longitude;
		west = southWest.longitude;

	}

	// This method will remove the Navigator Fragment... This is used because I
	// had trouble getting the fragment to be removed
	// Inside of the Navigator Class because Navigator is not a Main Activity,
	// so all fragment removing must be done through Main_Tile
	public void removeNavigator() {
		ft = manager.beginTransaction();
		ft.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);

		ft.remove(fragment_Navigator);
		ft.commit();
	}
	
	/*
	 * This method will add the Sharing Fragment.. This is used because I
	 * had trouble getting the fragment to be added
	 * Inside of the Navigator Class because Navigator is not a Main Activity,
	 * so all fragment removing must be done through Main_Tile
	 */
	public void addSharing() {
		ft = manager.beginTransaction();
		ft.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);

		ft.add(R.id.fragment_group_tile, fragment_Sharing);
		ft.addToBackStack("Sharing");
		ft.commit();
		
		tabhost.setCurrentTab(3);
		imgbtn_SchedulePopup.setVisibility(View.GONE);
	}
	
	// Will remove the 'Sharing' fragment
	public void removeSharing() {
		ft = manager.beginTransaction();
		ft.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);

		ft.remove(fragment_Sharing);
		
		ft.add(R.id.main_fragmentgroup, fragment_Navigator);
		ft.addToBackStack("Navigator");
		
		ft.commit();
	}

	// Gets the ping time the user selected from the 'Share' fragment
	public float getPingTime_Selection() {
		return pingTime_Selection;
	}
	
	// Sets the ping selection time
	public void setPingTime_Selection(float pingTime_Selection) {
		this.pingTime_Selection = pingTime_Selection;
	}

	// Gets the location provider the user selected... i.e GPS, Network, or Best Available
	public String getLocationProvider_Selection() {
		return locationProvider_Selection;
	}

	// Set the location provider
	public void setLocationProvider_Selection(String locationProvider_Selection) {
		this.locationProvider_Selection = locationProvider_Selection;
	}
	
	// Collect the user's location information based on their preferences
	public void collectUsersLocation(long time){
		trackUser = true; // User has agreed to have their location tracked and stored
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);

		
		lm.removeUpdates(this);
		
		// Setting the new location updates based on the user's preference
		lm.requestLocationUpdates(provider, time, 0, this);
				
		if(location != null){
			onLocationChanged(location);
		}
			
	}
	
	// This method is used to alert that the Navigation fragment has been terminated and information such as the user's
	// Location on the trip can be sent in a final bundle... This is responsible for sending a bundle of location information
	public void exitNavigation(){
		
		JSONObject outerJSONObj = new JSONObject(); 					// Outer JSON container
		JSONArray locations = new JSONArray();							// Inner JSON Array
		
		for(int i = 0; i < locationHolder.size(); i++){					// Add a JSON Obj to each element of the JSON Array
			JSONObject tempObj = new JSONObject();
			try {
				tempObj.put("x", locationHolder.get(i).getLatitude());
				tempObj.put("y", locationHolder.get(i).getLongitude());
				tempObj.put("timestamp", locationTimeStamps.get(i));
				tempObj.put("location_technology", getDeviceName());
				
				locations.put(i, tempObj);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
		
		// This method is used to alert that the Search fragment has been terminated... Used here because
		// Main_Tile is the main Fragment/Activity that all the framents run off of
	public void exitSearch(){
		
		ft = manager.beginTransaction();
		ft.remove(fragment_Routes);
		ft.commit();
	}
	
	// Get's the device the user is using... Displays it in a readable format
	public String getDeviceName() {
	    String manufacturer = Build.MANUFACTURER;
	    String model = Build.MODEL;
	    if (model.startsWith(manufacturer)) {
	        return capitalize(model);
	    } else {
	        return capitalize(manufacturer) + " " + model;
	    }
	}


	private String capitalize(String s) {
	    if (s == null || s.length() == 0) {
	        return "";
	    }
	    char first = s.charAt(0);
	    if (Character.isUpperCase(first)) {
	        return s;
	    } else {
	        return Character.toUpperCase(first) + s.substring(1);
	    }
	} 
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	
	
	protected void createNetErrorDialog() {

	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
	        .setTitle("Unable to connect")
	        .setCancelable(false)
	        .setPositiveButton("Settings",
	        new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                Intent i = new Intent(Settings.ACTION_SETTINGS);
	                startActivity(i);
	            }
	        }
	    )
	    .setNegativeButton("Cancel",
	        new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                Main.this.finish();
	            }
	        }
	    );
	    AlertDialog alert = builder.create();
	    alert.show();
	}

}