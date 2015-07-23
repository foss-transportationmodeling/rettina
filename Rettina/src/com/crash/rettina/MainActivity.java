/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * MainActivity is the class that pieces everything together. It functions on the use of fragments.  The base fragment is a GoogleMap
 * That the user will extract the majority of their information.  There are also two fragments on both sides of the screen that are toggled
 * Via imagebuttons in the top left and top right corners.  The left and right fragments are based on a drawerlayout which allows for,
 * Sliding and hiding the left and right fragments.  The only one of the left/right fragments may be toggled on the screen at a time.  If
 * One fragment is already toggled, the other fragment will automatically 'detoggle.' The left fragment is composed of two listviews, "routes" and "favorites".
 * "routes" is used to show all the possible routes that fall within the GPS coordinates of the top-left and bottom-right corners of the screen. The "routes" listview 
 * is populated once the user presses the find routes button that overlays the GoogleMap in the bottom right corner. The find routes button is responsible for finding 
 * the GPS Coordinates of the top left and bottom right corners of the screen. The "favorites" listview is used to contain all the routes that the user clicks under the
 * "Routes" listview.  The app is designed to only show the routes that the user clicks that are currently under the favorites listview. Routes can be toggled as well as
 * removed from the "Favorites." Whenever routes are toggled under the "Favorites" listview, they will be displayed on the Map fragment. 
 * 
 * The right fragment is used to display all the schedule information for all the selected routes.  This is populated with stop names and stop times for each route. In essence,
 * the right fragment is dependent upon the left fragment, and is updated accordingly as the "favorite" routes are changed.  When a stop is clicked on the right fragment, the map
 * will automatically zoom to that stop
 */

// To Do //
// 1. Search Icon is pixelated!!
// 2. When drawing multiple routes, if the stops overlap then only draw one marker
// 3. Crashes when first trying to get the location since user's location is unknown.. After it crashes, then it works
// 4. Ask the user to enable location services
// 5. Get the user's location faster.... GPS is very slow so it should default to low accuracy first to get a quick read

package com.crash.rettina;


import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.crash.connection.Connect;
import com.crash.connection.Connect_Shape;
import com.crash.connection.Connect_Stops;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;
import com.google.android.gms.appindexing.AndroidAppUri;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

public class MainActivity extends ActionBarActivity implements
		LocationListener, OnItemClickListener{
	
    private static final String TAG = "Test";

	ArrayList<String> dataArray_right = new ArrayList<String>();
	ArrayList<Object> objectArray_right = new ArrayList<Object>();
	ArrayList<String> dataArray_left = new ArrayList<String>();
	ArrayList<Object> objectArray_left = new ArrayList<Object>();
	public ArrayList<Route> routes = new ArrayList<Route>();

	public Map<String, LatLng> vehicles = new LinkedHashMap<String, LatLng>();
	public GoogleMap googleMap;
	private Location location;
	private LocationManager lm;
	private Marker myMarker;
	public LatLng lat_Lng;
	private double latitude;
	private double longitude;
	
	private int stopPosition;
	
	public Connect connect;
	
	public Schedule sched;
	
	
	public DrawerLayout mDrawerlayout;
	public ViewGroup mDrawerList_Left;
	public ViewGroup mDrawerList_Favorites;
	public ViewGroup mDrawerList_Right;
	
    public SlidingUpPanelLayout mLayout;
    


	@SuppressWarnings("deprecation")
	ActionBarDrawerToggle mDrawerToggle;
	ImageButton imgLeftMenu, imgFavoriteMenu, imgRightMenu, imgSearchRoutes, imgClose, imgBtn_nextStop, imgBtn_previousStop;
	
	Button btnShare;
	
	
	// Create the textviews for creating a new account and resetting password
	private TextView tv_CreateAccount, tv_ForgotPassword;
	
	
	//EditText et_Search;
	
	// Test Code //
	AutoCompleteTextView et_Search;
	/////////////////
	
	MainActivity main;
	
	// ------------------------ Test Code -------------- //
	private static final String LOG_TAG = "Google Places Autocomplete";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "------your api key here -------";
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
	
		
		final FragmentManager manager = getFragmentManager(); 
		
		final MapFragment fm = (MapFragment) manager
				.findFragmentById(R.id.map3);
		
				
		googleMap = fm.getMap();
		
		main = this;
	
		final RouteMenu routeFragment =  (RouteMenu) manager.findFragmentById(R.id.drawer_list_left);
		sched =  (Schedule) manager.findFragmentById(R.id.drawer_list_right);

		
		// Remove the compass and the zoom options that come stock on the map
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.getUiSettings().setCompassEnabled(false);
		googleMap.getUiSettings().setMapToolbarEnabled(false);

		
		// ===============Initialization of Variables========================= //

		mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList_Left = (ViewGroup) findViewById(R.id.drawer_list_left);		
		mDrawerList_Right = (ViewGroup) findViewById(R.id.drawer_list_right);
		
		mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

		
		imgLeftMenu = (ImageButton) findViewById(R.id.imgLeftMenu);
		imgRightMenu = (ImageButton) findViewById(R.id.imgRightMenu);
		imgSearchRoutes = (ImageButton) findViewById(R.id.imgBtn_searchRoutes);
		imgClose = (ImageButton) findViewById(R.id.imgbtn_close);
		imgBtn_previousStop = (ImageButton) findViewById(R.id.imgbtn_previousStop);
		imgBtn_nextStop = (ImageButton) findViewById(R.id.imgbtn_nextStop);

		btnShare = (Button) findViewById(R.id.btn_share);
		

		et_Search = (AutoCompleteTextView) findViewById(R.id.search);
		


		mDrawerlayout.setDrawerListener(mDrawerToggle);
		

		// ============== Define a Custom Header for Navigation drawer================= //

		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.header, null);

		imgLeftMenu = (ImageButton) v.findViewById(R.id.imgLeftMenu);
		imgRightMenu = (ImageButton) v.findViewById(R.id.imgRightMenu);

		et_Search = (AutoCompleteTextView) v.findViewById(R.id.search);

		
		//et_Search = (EditText) v.findViewById(R.id.search);
		
	///////---------------- Test Code  ----------------------///////////////////
		et_Search.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.drawer_list_item));
		et_Search.setOnItemClickListener(this);
		
 ////////// ---------------------------- ///////////////////////////


		// Setting the custom action bar settings... Setting the actionbar color
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#424242")));

		// Setting the custom Action Bar View
		getSupportActionBar().setCustomView(v);
		

		
		// Handling clicks for the left button on the Action Bar
		imgLeftMenu.setOnClickListener(new OnClickListener() {
			
			// When left icon on the Action Bar is clicked, open or close the drawer
			@Override
			public void onClick(View arg0) {
				if (mDrawerlayout.isDrawerVisible(mDrawerList_Right)) {
					mDrawerlayout.closeDrawer(mDrawerList_Right);
				}
				

				if (mDrawerlayout.isDrawerVisible(mDrawerList_Left)) {
					mDrawerlayout.closeDrawer(mDrawerList_Left);
				}
				
				else{
				mDrawerlayout.openDrawer(mDrawerList_Left);				
				
				
				// When you click on the icon, it shows the navigation slider below... Need to make it so when you click a route
				// that you want to follow it will show this slider
				}
			}
		});
		
		
		
		// When Fav icon on the Action Bar is clicked, open or close the drawer
				imgRightMenu.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mDrawerlayout.isDrawerVisible(mDrawerList_Left)) {
							mDrawerlayout.closeDrawer(mDrawerList_Left);
							

						}
						
						if (mDrawerlayout.isDrawerVisible(mDrawerList_Right)) {
							mDrawerlayout.closeDrawer(mDrawerList_Right);

						}
						
						else{
						mDrawerlayout.openDrawer(mDrawerList_Right);
						

						}
					}
				});
		
				imgSearchRoutes.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
//						json = new GetJSON(main, googleMap);
//						json.execute();	
//						json.getScreenCornerCoordinates();
//						routeFragment.setRouteData();
//						
						connect = new Connect(main, googleMap, routeFragment);
						connect.execute();	

						}
				
				});
				
				mLayout.setTouchEnabled(false);
		        mLayout.setPanelSlideListener(new PanelSlideListener() {

		        	@Override
		            public void onPanelSlide(View panel, float slideOffset) {
		                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
		                imgSearchRoutes.setVisibility(View.INVISIBLE);

		            }

		            @Override
		            public void onPanelExpanded(View panel) {
		                Log.i(TAG, "onPanelExpanded");
		                imgSearchRoutes.setVisibility(View.INVISIBLE);

		            }

		            @Override
		            public void onPanelCollapsed(View panel) {
		                Log.i(TAG, "onPanelCollapsed");
		                imgSearchRoutes.setVisibility(View.VISIBLE);


		            }

		            @Override
		            public void onPanelAnchored(View panel) {
		                Log.i(TAG, "onPanelAnchored");
		                imgSearchRoutes.setVisibility(View.INVISIBLE);

		            }

		            @Override
		            public void onPanelHidden(View panel) {
		                Log.i(TAG, "onPanelHidden");
		                imgSearchRoutes.setVisibility(View.VISIBLE);

		            }
		        	
		        
		        });
		        
				mLayout.setAnchorPoint(0.30f);

				
				imgClose.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						setStopPosition(0);
						
						mLayout.setPanelState(PanelState.HIDDEN);
		                imgSearchRoutes.setVisibility(View.VISIBLE);

		                CameraPosition cameraPosition = new CameraPosition.Builder()
					    .target(routeFragment.adapter.getNavRoute().getStops().get(getStopPosition()).getLatLng())      // Sets the center of the map to Mountain View
					    .zoom(15)                   // Sets the zoom
					    .bearing(90)                // Sets the orientation of the camera to east
					    .tilt(40)                   
					    .build();                   // Creates a CameraPosition from the builder
					googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						
					}
				});
				
				// Set the listner for the share button
				btnShare.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						// If the panel is already expanded, i.e "Sharing Mode" then put the panel back to anchored
						if(mLayout.getPanelState() == PanelState.EXPANDED){
							mLayout.setPanelState(PanelState.ANCHORED);
			                imgSearchRoutes.setVisibility(View.INVISIBLE);
						}
						
						// Else if the panel is not expanded, expand it to allow for user "sharing"
						else{
							mLayout.setPanelState(PanelState.EXPANDED);
				
							
			                imgSearchRoutes.setVisibility(View.INVISIBLE);
						}
						

					}
				});

				
				imgBtn_nextStop.setOnClickListener(new OnClickListener() {
										
					@Override
					public void onClick(View v) {
						if(stopPosition < routeFragment.adapter.getNavRoute().getStops().size() - 1){
						incrementStopPosition();
						
						CameraPosition cameraPosition = new CameraPosition.Builder()
					    .target(routeFragment.adapter.getNavRoute().getStops().get(getStopPosition()).getLatLng())      // Sets the center of the map to Mountain View
					    .zoom(19)                   // Sets the zoom
					    .bearing(90)                // Sets the orientation of the camera to east
					    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
					    .build();                   // Creates a CameraPosition from the builder
					googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						
						
//						// Zoom out when closing on the navigation tab
//						  googleMap.animateCamera(CameraUpdateFactory
//									.newLatLngZoom(routeFragment.adapter.getNavRoute().getStops().get(getStopPosition()).getLatLng(),
//											16));
					
					
					
			               routeFragment.adapter.getNavRoute().getStops().get(getStopPosition()).getMarker().showInfoWindow();
						}
					}
				});
				
				imgBtn_previousStop.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(stopPosition > 0){

						decrementStopPosition();
						
						CameraPosition cameraPosition = new CameraPosition.Builder()
					    .target(routeFragment.adapter.getNavRoute().getStops().get(getStopPosition()).getLatLng())      // Sets the center of the map to Mountain View
					    .zoom(19)                   // Sets the zoom
					    .bearing(90)                // Sets the orientation of the camera to east
					    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
					    .build();                   // Creates a CameraPosition from the builder
					googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						
						
//						// Zoom out when closing on the navigation tab
//						  googleMap.animateCamera(CameraUpdateFactory
//									.newLatLngZoom(routeFragment.adapter.getNavRoute().getStops().get(getStopPosition()).getLatLng(),
//											16));
					
					
					
			               routeFragment.adapter.getNavRoute().getStops().get(getStopPosition()).getMarker().showInfoWindow();
						}
					}
				});
				

				
		 lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		Location mostRecentLocation = lm.getLastKnownLocation(provider);
		
		
		if(mostRecentLocation != null) {
			
		    latitude = mostRecentLocation.getLatitude();
		    longitude = mostRecentLocation.getLongitude();
		    
		    lat_Lng = new LatLng(latitude, longitude);
		    
		    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_Lng, 13));
			userLocMarker(lat_Lng);
		    
		}
		
		lm.requestLocationUpdates(provider, 1, 0, this);	
		
		
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
		
//	    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_Lng, 13));
		myMarker.setPosition(latlng);
		
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
	
	
	public void getRoutes(){
		for (int i = 0; i < routes.size(); i++) {
			System.out.println(routes.get(i).getRouteID());	
			}
}
	
	public void drawRoute(Route r){
//		System.out.println("Testing Size: " + r.routePoints.size());
		r.printRoute();
		//for(int i = 0; i < r.getPolyLine().getPoints().size(); i++){
			//System.out.println("PolyPoint: " + r.getPolyLine().getPoints().get(i));

		//}
		r.setPolyLine(googleMap.addPolyline(r.getPolyLineOptions()));
		//Polyline pl = googleMap.addPolylineOptions(r.getPolyLine());
		//googleMap.addPolyline(r.getPolyLine());
		
	}
	
	
	public void hideRoute(Route r){
		System.out.println("Hiding Route: " + r.getRouteTitle());

		r.getPolyLine().remove();
		
		r.getPolyLineOptions().visible(false);
		System.out.println("IS POLY VISIBLE: " + r.getPolyLine().isVisible());
		
		

	}
	
	public void showRoute(Route r){
		r.getPolyLine().setVisible(true);
		System.out.println("Showing Route: " + r.getRouteTitle());

	}
	

	public void showStops(Route r){
		r.showStops(googleMap);
		
	}
	
	public void hideStops(Route r){
		r.hideStops(googleMap);

	}
	
	
	public Schedule getSched() {
		return sched;
	}
	
	
	//Temp
	public void findLocationOfAddress(String addr){
	Geocoder geocoder = new Geocoder(this);  
	List<Address> addresses = null;
	try {
		addresses = geocoder.getFromLocationName(addr, 1);
	} catch (IOException e) {
		e.printStackTrace();
	}
	Address address = addresses.get(0);
	double longitude = address.getLongitude();
	double latitude = address.getLatitude();
	}
	
	
	///////////////// ---------------- Test Code -------------------- ////////////////////
	
	
	public void onItemClick(AdapterView adapterView, View view, int position, long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	
	
	
	public static ArrayList autocomplete(String input) {
		ArrayList resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append("&components=country:gr");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {			
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
				System.out.println("============================================================");
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
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
	
	
	public int getStopPosition() {
		return stopPosition;
	}
	
	public void incrementStopPosition() {
		stopPosition++;
	}

	public void decrementStopPosition() {
		stopPosition--;
	}
	
	public void setStopPosition(int stopPosition) {
		this.stopPosition = stopPosition;
	}
	

	class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
		private ArrayList resultList;
	
	public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public String getItem(int index) {
		return (String) resultList.get(index);
	}
	

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the autocomplete results.
					resultList = autocomplete(constraint.toString());

					// Assign the data to the FilterResults
					filterResults.values = resultList;
					filterResults.count = resultList.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		return filter;
	}
}
	
	
}