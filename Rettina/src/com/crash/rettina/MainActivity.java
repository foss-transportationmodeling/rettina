package com.crash.rettina;


// Make sure when drawing routes if the stops overlap, only draw one stop location

// Search Icon is pixelated!!


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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.crash.connection.Connect;
import com.crash.connection.Connect_Shape;
import com.crash.connection.Connect_Stops;
import com.crash.routeinfo.Route;
import com.google.android.gms.appindexing.AndroidAppUri;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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
	private GetJSON json;
	
	
	public Connect connect;
	
	public Schedule sched;
	
	DrawerLayout mDrawerlayout;
	ViewGroup mDrawerList_Left;
	ViewGroup mDrawerList_Favorites;
	ViewGroup mDrawerList_Right;
	
    public SlidingUpPanelLayout mLayout;


	@SuppressWarnings("deprecation")
	ActionBarDrawerToggle mDrawerToggle;
	ImageButton imgLeftMenu, imgFavoriteMenu, imgRightMenu, imgSearchRoutes, imgClose;
	
	
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

		
		
		et_Search = (AutoCompleteTextView) findViewById(R.id.search);



		mDrawerlayout.setDrawerListener(mDrawerToggle);
		

		// ============== Define a Custom Header for Navigation
		// drawer=================//

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
//		        mLayout.setPanelSlideListener(new PanelSlideListener() {
//
//		        	@Override
//		            public void onPanelSlide(View panel, float slideOffset) {
//		                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
//		            }
//
//		            @Override
//		            public void onPanelExpanded(View panel) {
//		                Log.i(TAG, "onPanelExpanded");
//
//		            }
//
//		            @Override
//		            public void onPanelCollapsed(View panel) {
//		                Log.i(TAG, "onPanelCollapsed");
//
//		            }
//
//		            @Override
//		            public void onPanelAnchored(View panel) {
//		                Log.i(TAG, "onPanelAnchored");
//		            }
//
//		            @Override
//		            public void onPanelHidden(View panel) {
//		                Log.i(TAG, "onPanelHidden");
//		            }
//		        	
//		        
//		        });
		        
				mLayout.setAnchorPoint(0.25f);

				
				imgClose.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mLayout.setPanelState(PanelState.HIDDEN);
						
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
		r.getPolyLine().setVisible(false);
		System.out.println("Hiding Route: " + r.getRouteTitle());

	}
	
	
	public void showStops(Route r){
		r.showStops(googleMap);
		
	}
	
	public void hideStops(Route r){
		r.hideStops(googleMap);

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
		Connect_Stops tempConnect = new Connect_Stops(r);
		tempConnect.execute();
	}
	
	public void getShape(Route r) {
		Connect_Shape tempConnect = new Connect_Shape(r, googleMap, main);
		tempConnect.execute();	
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