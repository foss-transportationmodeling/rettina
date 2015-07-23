package com.crash.rettina;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.crash.connection.Connect;
import com.crash.connection.Connect_LocationInfo;
import com.crash.connection.Connect_Shape;
import com.crash.connection.Connect_Stops;
import com.crash.rettina.R.drawable;
import com.crash.routeinfo.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.LocationListener;
import android.R.fraction;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class Main_Tile extends Activity implements LocationListener {

	public GoogleMap googleMap;
	private Location location;
	private LocationManager lm;
		
	private Marker myMarker;
	public LatLng lat_Lng;
	private double latitude;
	private double longitude;

	public ArrayList<Route> routes = new ArrayList<Route>();

	public ArrayList<String> routeTitles = new ArrayList<String>();

	public ImageButton imgbtn_SchedulePopup;

	public TabHost tabhost;

	public Connect connect;
	public GridView gridview;
	public ArrayAdapter<String> adapter;

	public Favorites fragment_Favorites;
	public Schedule fragment_Schedule;

	public Navigator fragment_Navigator;
	public Sharing fragment_Sharing;
	public RoutesFragment fragment_Routes;

	public FragmentManager manager;

	public FragmentTransaction ft;
	
	public ArrayList<Location> locationHolder = new ArrayList<Location>();
	public ArrayList<String> locationTimeStamps = new ArrayList<String>();


	Main_Tile main;

	public Route clickedRoute = null;
	private LatLng northEast;
	private LatLng southWest;
	public double south;
	public double east;
	public double north;
	public double west;
	
	// These two variables are used to collect 'Sharing' data from the user. Since Sharing is another fragment
	// These variables are stored on the Main_Tile activity to allow for easy location transfer since this activity is always running
	private float pingTime_Selection;
	private String locationProvider_Selection;
	
	private boolean trackUser = false;

	private static final String TAG = "Test";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tile);

		manager = getFragmentManager();

		final MapFragment fm = (MapFragment) manager
				.findFragmentById(R.id.map_tile);

		fragment_Favorites = new Favorites();
		
//		Intent gpsOptionsIntent = new Intent(  
//			    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
//			startActivity(gpsOptionsIntent);

		googleMap = fm.getMap();

		main = this;

		fragment_Schedule = new Schedule(main);
		fragment_Navigator = new Navigator(main);
		fragment_Sharing = new Sharing(main);
		fragment_Routes = new RoutesFragment(main);

		// Remove the compass and the zoom options that come stock on the map
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.getUiSettings().setCompassEnabled(false);
		googleMap.getUiSettings().setMapToolbarEnabled(false);

		tabhost = (TabHost) findViewById(android.R.id.tabhost);

		// GridView gridview = (GridView) findViewById(R.id.gridView1);

		gridview = (GridView) findViewById(R.id.gridView1);

		imgbtn_SchedulePopup = (ImageButton) findViewById(R.id.imgbtn_schedulepopup);

		tabhost.setup();
		TabSpec ts = tabhost.newTabSpec("tag1");
		ts.setContent(R.id.tab1_tile);
		ts.setIndicator("Search");

		tabhost.addTab(ts);

		ts = tabhost.newTabSpec("tag2");
		ts.setContent(R.id.tab2_tile);
		ts.setIndicator("Favorites");

		tabhost.addTab(ts);

		ts = tabhost.newTabSpec("tag3");
		ts.setContent(R.id.tab3_tile);
		ts.setIndicator("Settings");
		tabhost.addTab(ts);

		ts = tabhost.newTabSpec("tag4");
		ts.setContent(R.id.tab4_tile);
		ts.setIndicator("Hide");
		tabhost.addTab(ts);

		tabhost.getTabWidget().getChildAt(3).setVisibility(View.GONE);

		tabhost.setCurrentTab(3);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, routeTitles);

		imgbtn_SchedulePopup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ft = manager.beginTransaction();
				ft.remove(fragment_Routes);
				ft.add(R.id.fragment_group_tile, fragment_Schedule);
				// ft.addToBackStack("Schedule");
				ft.commit();

				// if(fragment_Schedule.isVisible()){
				imgbtn_SchedulePopup.setVisibility(View.GONE);

				fragment_Schedule.lv_arr = new String[fragment_Routes.clickedRoute
						.getStops().size()];

				for (int i = 0; i < fragment_Routes.clickedRoute.getStops()
						.size(); i++) {
					fragment_Schedule.lv_arr[i] = fragment_Routes.clickedRoute
							.getStops().get(i).getStopDescription();
				}

				fragment_Schedule.tempRoute = clickedRoute;

				// fragment_Schedule.seekSetDotPosition(fragment_Schedule.adapter.dots);

				// fragment_Schedule.setRoutes(clickedRoute);

			}
		});

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		Location mostRecentLocation = lm.getLastKnownLocation(provider);

		if (mostRecentLocation != null) {

			latitude = mostRecentLocation.getLatitude();
			longitude = mostRecentLocation.getLongitude();

			lat_Lng = new LatLng(latitude, longitude);

			// This zooms into the user's location

			// googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_Lng,
			// 13));

			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lat_Lng,
					14));

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

		lm.requestLocationUpdates(provider, 1, 0, this);

		tabhost.getTabWidget().getChildAt(0)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						System.out.println("Clicked Search!");

						// Grab the corner of the screens coordinates so the
						// routes can be found based on GPS coordinates
						getScreenCornerCoordinates();

						if (tabhost.getCurrentTab() == 0
								&& fragment_Routes.isVisible()) {

							// Set for the new navigation fragment
							// mLayout.setPanelState(PanelState.COLLAPSED);

							ft = manager.beginTransaction();
							ft.remove(fragment_Routes);
							ft.commit();

							tabhost.setCurrentTab(3);

							if (clickedRoute != null
									&& clickedRoute.getPolyLine().isVisible()) {
								// hideStops(clickedRoute);
								// hideRoute(clickedRoute);

							}

						} else {

							if (fragment_Schedule.isVisible()) {
								ft = manager.beginTransaction();
								ft.remove(fragment_Schedule);
								ft.commit();
							}

							if (tabhost.getCurrentTab() == 1
									&& fragment_Favorites.isVisible()) {
								// Need to make it so it only connects when
								// unique routes will be shown, otherwise, it
								// will just be
								// Doing more work by getting the same routes
								// over and over whenver search is clicked

								ft = manager.beginTransaction();
								// ft.replace(R.id.main_fragmentgroup,
								// (Fragment)fm);
								ft.remove(fragment_Favorites);
							}

							// Use for new Nav Fragment
							// mLayout.setPanelState(PanelState.ANCHORED);

							findViewById(R.id.main_fragmentgroup)
									.setVisibility(View.VISIBLE);

							ft = manager.beginTransaction();

							if (fragment_Favorites.isAdded()) {
								ft.remove(fragment_Favorites);
							}

							else if (fragment_Navigator.isAdded()) {
								ft.remove(fragment_Navigator);
							}

//							getScreenCornerCoordinates();

							ft.add(R.id.main_fragmentgroup, fragment_Routes);
							ft.addToBackStack("Schedule");
							ft.commit();

							connect = new Connect(main, googleMap);
							connect.execute();
							tabhost.setCurrentTab(0);

						}

					}
				});

		tabhost.getTabWidget().getChildAt(1)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						System.out.println("Clicked Favorites!");

						if (tabhost.getCurrentTab() == 1
								&& v.getTag().equals(1)) {

							ft = manager.beginTransaction();
							ft.remove(fragment_Favorites);
							ft.commit();
							tabhost.setCurrentTab(3);
						} else {
							ft = manager.beginTransaction();

							if (fragment_Schedule.isVisible()) {
								ft = manager.beginTransaction();
								ft.remove(fragment_Schedule);
							}

							if (fragment_Routes.isAdded()) {
								System.out.println("Routes is aleady added!");
								ft.remove(fragment_Routes);
							} else if (fragment_Favorites.isAdded()) {
								ft.remove(fragment_Favorites);
							}

							else if (fragment_Navigator.isAdded()) {
								ft.remove(fragment_Navigator);
							}

							ft.add(R.id.main_fragmentgroup, fragment_Favorites);
							ft.addToBackStack("Favorites");
							ft.commit();

							tabhost.setCurrentTab(1);

						}

					}
				});

		tabhost.getTabWidget().getChildAt(2)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						System.out.println("Clicked Settings!");

						if (tabhost.getCurrentTab() == 1
								&& fragment_Favorites.isVisible()) {
							ft = manager.beginTransaction();
							ft.remove(fragment_Favorites);

							ft.add(R.id.fragment_group_tile, fragment_Sharing);
							ft.addToBackStack("Sharing");
							ft.commit();

							tabhost.setCurrentTab(2);
						}

						else if (tabhost.getCurrentTab() == 2) {

							// Set for next fragment
							// mLayout.setPanelState(PanelState.COLLAPSED);

							tabhost.setCurrentTab(3);

							if (fragment_Sharing.isAdded()) {
								ft = manager.beginTransaction();
								ft.remove(fragment_Sharing);
								ft.commit();
							}

							else if (fragment_Schedule.isAdded()) {
								ft = manager.beginTransaction();
								ft.remove(fragment_Schedule);
								ft.commit();
							}

						} else {
							if (fragment_Schedule.isVisible()) {
								ft = manager.beginTransaction();
								ft.remove(fragment_Schedule);
							}
							// Set for new nav fragment
							// mLayout.setPanelState(PanelState.COLLAPSED);

							// FragmentTransaction ft =
							// manager.beginTransaction();
							// ft.add(R.id.main_fragmentgroup,
							// fragment_Schedule);
							// ft.commit();

							ft = manager.beginTransaction();

							if (fragment_Routes.isAdded()) {
								ft.remove(fragment_Routes);
							} else if (fragment_Favorites.isAdded()) {
								ft.remove(fragment_Favorites);
							}

							else if (fragment_Navigator.isAdded()) {
								ft.remove(fragment_Navigator);
							}

							ft.add(R.id.fragment_group_tile, fragment_Sharing);
							ft.addToBackStack("Sharing");
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

	@Override
	public void onLocationChanged(Location location) {
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		LatLng latlng = new LatLng(lat, lon);

		// googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_Lng, 13));
		myMarker.setPosition(latlng);
		
		
		// Add to the user's location tracker
		if(trackUser == true){
		locationHolder.add(location);
		
		SimpleDateFormat localDateFormat = new SimpleDateFormat("MMM dd yyyy hh:mm:ssa");
		
		locationTimeStamps.add(localDateFormat.format(new Date()));
		
		System.out.println("Saving location, locationHolder size: " + locationHolder.size());
		System.out.println(localDateFormat.format(new Date()));
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

	public void drawRoute(Route r) {
		// System.out.println("Testing Size: " + r.routePoints.size());
		r.printRoute();
		// for(int i = 0; i < r.getPolyLine().getPoints().size(); i++){
		// System.out.println("PolyPoint: " +
		// r.getPolyLine().getPoints().get(i));

		// }
		r.setPolyLine(googleMap.addPolyline(r.getPolyLineOptions()));
		// Polyline pl = googleMap.addPolylineOptions(r.getPolyLine());
		// googleMap.addPolyline(r.getPolyLine());

	}

	public void hideRoute(Route r) {
		System.out.println("Hiding Route: " + r.getRouteTitle());

		// r.getPolyLine().remove();
		r.getPolyLine().setVisible(false);
		r.getPolyLineOptions().visible(false);
		System.out.println("IS POLY VISIBLE: " + r.getPolyLine().isVisible());

	}

	public void showRoute(Route r) {
		r.getPolyLine().setVisible(true);
		System.out.println("Showing Route: " + r.getRouteTitle());

	}

	public void showStops(Route r) {
		r.showStops(googleMap);

	}

	public void hideStops(Route r) {
		r.hideStops(googleMap);

	}

	public void updateRoutes() {
		if (routeTitles.size() < routes.size()) {
			routeTitles.clear();
			for (int i = 0; i < routes.size(); i++) {
				routeTitles.add(routes.get(i).getRouteTitle());
			}
			fragment_Routes.adapter.notifyDataSetChanged();
			fragment_Routes.gridview.setAdapter(adapter);
		}
	}

	public void getStops(Route r) {
		System.out.println("Getting the routes!!!!!");
		Connect_Stops tempConnect = new Connect_Stops(r, main, googleMap);
		tempConnect.execute();
	}

	public void getShape(Route r) {
		Connect_Shape tempConnect = new Connect_Shape(r, googleMap, main);
		tempConnect.execute();
	}

	public void hideFavorites() {
		ft = manager.beginTransaction();
		// ft.replace(R.id.main_fragmentgroup, (Fragment)fm);
		ft.remove(fragment_Favorites);
		ft.commit();
		tabhost.setCurrentTab(3);

	}

	public void hideSchedule() {
		ft = manager.beginTransaction();
		ft.remove(fragment_Schedule);
		ft.commit();

	}

	public void showNavigation() {
		ft = manager.beginTransaction();
		ft.replace(R.id.main_fragmentgroup, fragment_Navigator);
		ft.commit();

	}

	public void getScreenCornerCoordinates() {

		LatLngBounds llBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
		southWest = llBounds.southwest;
		northEast = llBounds.northeast;

		north = northEast.latitude;
		south = southWest.latitude;
		east = northEast.longitude;
		west = southWest.longitude;

		System.out.println("NorthEast: " + northEast + " SouthWest: "
				+ southWest);

	}

	// This method will remove the Navigator Fragment... This is used because I
	// had trouble getting the fragment to be removed
	// Inside of the Navigator Class because Navigator is not a Main Activity,
	// so all fragment removing must be done through Main_Tile
	public void removeNavigator() {
		ft = manager.beginTransaction();
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
		ft.add(R.id.fragment_group_tile, fragment_Sharing);
		ft.addToBackStack("Sharing");
		ft.commit();
		
		tabhost.setCurrentTab(3);
		imgbtn_SchedulePopup.setVisibility(View.GONE);
	}
	
	public void removeSharing() {
		ft = manager.beginTransaction();
		ft.remove(fragment_Sharing);
		
		ft.add(R.id.main_fragmentgroup, fragment_Navigator);
		ft.addToBackStack("Navigator");
		
		ft.commit();
		
		
	}

	public float getPingTime_Selection() {
		return pingTime_Selection;
	}

	public void setPingTime_Selection(float pingTime_Selection) {
		this.pingTime_Selection = pingTime_Selection;
	}

	public String getLocationProvider_Selection() {
		return locationProvider_Selection;
	}

	public void setLocationProvider_Selection(String locationProvider_Selection) {
		this.locationProvider_Selection = locationProvider_Selection;
	}
	
	public void collectUsersLocation(long time){
		System.out.println("Ping time is: " + time);
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
		
		// Using this temporary Trip_ID until I sort out getting the correct trip_ID from Navigator
		try {			
			outerJSONObj.put("trip_id", clickedRoute.getTripIDs().get(0));	// Add the Trip_ID and Locations Array to the outer JSON Obj
			outerJSONObj.put("locations", locations);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("Printing JSON!");

		for(int i = 0; i < locations.length(); i++){					// Add a JSON Obj to each element of the JSON Array
			try {
				System.out.println(locations.getJSONObject(i).get("x"));
				System.out.println(locations.getJSONObject(i).get("y"));
				System.out.println(locations.getJSONObject(i).get("timestamp"));
				System.out.println(locations.getJSONObject(i).get("location_technology"));


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Connect_LocationInfo connectLocation = new Connect_LocationInfo(main, main, outerJSONObj);
		connectLocation.execute();
		
		// Stop tracking the user once the user has exited navigation
		trackUser = false;
	}
	
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

}