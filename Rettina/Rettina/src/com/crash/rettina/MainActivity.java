package com.crash.rettina;


// Make sure when drawing routes if the stops overlap, only draw one stop location

// Add a get routes button and a search field....

// Get the bounding box lat/lng from the current zoom value



import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.crash.routeinfo.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends ActionBarActivity implements
		LocationListener{

	ArrayList<String> dataArray_right = new ArrayList<String>();
	ArrayList<Object> objectArray_right = new ArrayList<Object>();
	ArrayList<String> dataArray_left = new ArrayList<String>();
	ArrayList<Object> objectArray_left = new ArrayList<Object>();
	ArrayList<Route> routes = new ArrayList<Route>();

	public Map<String, LatLng> vehicles = new LinkedHashMap<String, LatLng>();
	public GoogleMap googleMap;
	private Location location;
	private LocationManager lm;
	private Marker myMarker;
	private LatLng lat_Lng;
	private double latitude;
	private double longitude;
	private GetJSON json;
	
	private Schedule f1;
	
	
	DrawerLayout mDrawerlayout;
	ViewGroup mDrawerList_Left;
	ViewGroup mDrawerList_Favorites;
	ViewGroup mDrawerList_Right;
	@SuppressWarnings("deprecation")
	ActionBarDrawerToggle mDrawerToggle;
	ImageButton imgLeftMenu, imgFavoriteMenu, imgRightMenu;
	Switch switch_Btn;
	EditText et_Search;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
	
		
		final FragmentManager manager = getFragmentManager(); 
		
		final MapFragment fm = (MapFragment) manager
				.findFragmentById(R.id.map3);
		
				
		googleMap = fm.getMap();
	
		
//		json = new GetJSON(googleMap, this);
//		json.execute();

		
		// Remove the compass and the zoom options that come stock on the map
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.getUiSettings().setCompassEnabled(false);

		
		// ===============Initialization of Variables=========================//

		mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList_Left = (ViewGroup) findViewById(R.id.drawer_list_left);		
		mDrawerList_Right = (ViewGroup) findViewById(R.id.drawer_list_right);
			
		
		
		imgLeftMenu = (ImageButton) findViewById(R.id.imgLeftMenu);
		imgRightMenu = (ImageButton) findViewById(R.id.imgRightMenu);
		switch_Btn = (Switch) findViewById(R.id.map_switch);
		et_Search = (EditText) findViewById(R.id.search);

		
		


		mDrawerlayout.setDrawerListener(mDrawerToggle);
		

		// ============== Define a Custom Header for Navigation
		// drawer=================//

		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.header, null);

		imgLeftMenu = (ImageButton) v.findViewById(R.id.imgLeftMenu);
		imgRightMenu = (ImageButton) v.findViewById(R.id.imgRightMenu);
		
		et_Search = (EditText) v.findViewById(R.id.search);


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
		
		
		switch_Btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(buttonView.isChecked()){
					System.out.println("Checked");

					f1 = new Schedule();
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.add(R.id.fragment_group, f1);
					transaction.commit();
				}
				else if(buttonView.isChecked() == false){
					
					System.out.println("not Checked");
					if(fm != null){
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.remove(f1);
					transaction.commit();
					}
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
		System.out.println("Printin!");
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
		googleMap.addPolyline(r.getPolyLine());
		
	}
	
	public void hideRoute(Route r){
		r.getPolyLine().visible(false);

	}
	
	
}