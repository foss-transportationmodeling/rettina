package com.crash.rettina;

import java.util.ArrayList;

import com.crash.connection.Connect;
import com.crash.connection.Connect_Shape;
import com.crash.connection.Connect_Stops;
import com.crash.rettina.R.drawable;
import com.crash.routeinfo.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

public class Main_Tile extends Activity implements LocationListener{

	public GoogleMap googleMap;
	public SlidingUpPanelLayout mLayout;
	private Location location;
	private LocationManager lm;
	private Marker myMarker;
	public LatLng lat_Lng;
	private double latitude;
	private double longitude;

	public ArrayList<Route> routes = new ArrayList<Route>();

	public ArrayList<String> routeTitles = new ArrayList<String>();
	
	
	public TabHost tabhost;

	public Connect connect;
	public GridView gridview;
	public ArrayAdapter<String> adapter;
	
	public Favorites fragment_Favorites;
	public Schedule fragment_Schedule;

	public FragmentManager manager;

	Main_Tile main;
	
	private Route clickedRoute = null;

	private static final String TAG = "Test";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tile);

		manager = getFragmentManager();

		final MapFragment fm = (MapFragment) manager
				.findFragmentById(R.id.map_tile);
		
		fragment_Favorites = new Favorites();
		fragment_Schedule = new Schedule();


		googleMap = fm.getMap();

		main = this;

		// Remove the compass and the zoom options that come stock on the map
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.getUiSettings().setCompassEnabled(false);
		googleMap.getUiSettings().setMapToolbarEnabled(false);

		tabhost = (TabHost) findViewById(android.R.id.tabhost);

		// GridView gridview = (GridView) findViewById(R.id.gridView1);

		gridview = (GridView) findViewById(R.id.gridView1);


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
		

		mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_tile);

		mLayout.setTouchEnabled(false);
		mLayout.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				Log.i(TAG, "onPanelSlide, offset " + slideOffset);
				// imgSearchRoutes.setVisibility(View.INVISIBLE);

			}

			@Override
			public void onPanelExpanded(View panel) {
				Log.i(TAG, "onPanelExpanded");
				// imgSearchRoutes.setVisibility(View.INVISIBLE);

			}

			@Override
			public void onPanelCollapsed(View panel) {
				Log.i(TAG, "onPanelCollapsed");
				// imgSearchRoutes.setVisibility(View.VISIBLE);

			}

			@Override
			public void onPanelAnchored(View panel) {
				Log.i(TAG, "onPanelAnchored");
				// imgSearchRoutes.setVisibility(View.INVISIBLE);

			}

			@Override
			public void onPanelHidden(View panel) {
				Log.i(TAG, "onPanelHidden");
				// imgSearchRoutes.setVisibility(View.VISIBLE);

			}

		});

		mLayout.setAnchorPoint(0.30f);
	

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, routeTitles);

		gridview.setAdapter(adapter);

		// When a tile is clicked, show the route preview and stops
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				
				// If the favorites does not already contain this route, then add to the favorites
				if(!fragment_Favorites.selectedFavs.contains(routes.get(position))){
				
				if(routes.get(position).getStops().size() < 1){
					getStops(routes.get(position));
				}
				
				
				// Work on hiding the polyline when another item is clicked!!!
				for(int i = 0; i < routes.size(); i++){
					if(routes.get(i).getStops() != null){
					routes.get(i).hideStops(googleMap);
					
						if(routes.get(i).getPolyLine() != null){
							routes.get(i).hidePolyLine();
						}
					}
				}
				
				showStops(routes.get(position)); // Might error out since it will be waiting to get the stops from the previous call first
				getShape(routes.get(position));
				
				// This loops through all the selected favorites and sets them to unclicked
				// The last added route will be the only clicked route when going to favorites.. If the user wants to display
				// More routes on the map, they will have to go through the favorites and click that way
				for(int i = 0; i < fragment_Favorites.selectedFavs.size(); i++){
					fragment_Favorites.selectedFavs.get(i).setClicked(false);
				}
				
				routes.get(position).setClicked(true);
				clickedRoute = routes.get(position);
				fragment_Favorites.selectedFavs.add(clickedRoute);
				
			}
				else{
					System.out.println("Route is already in favorites!");
				}
			}
		});
		
		// When a tile is held, add the route to the favorites
		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			// Need to make this so it does not interfere with the clicking of a route...
			// If the route has already been clicked, it should reuse that route information and simply add it to the favorites
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {

				Toast.makeText(getApplicationContext(),
						((TextView) v).getText(), Toast.LENGTH_SHORT).show();
				
				if(routes.get(position).getStops().size() < 1){
					getStops(routes.get(position));
				}
				
				for(int i = 0; i < routes.size(); i++){
					if(routes.get(i).getStops().size() > 0){
					routes.get(i).hideStops(googleMap);
					routes.get(i).hidePolyLine();

					}
				}
				
				showStops(routes.get(position)); // Might error out since it will be waiting to get the stops from the previous call first
				getShape(routes.get(position));
				
				// For now clear the stops so only the last clicked route will be displayed... Will need to change this so
				// Multiple routes can be supported when Favorites is used
				fragment_Schedule.stops.clear();
				
				fragment_Favorites.selectedFavs.add(routes.get(position));
				fragment_Schedule.setRoutes(routes.get(position));

				return false;
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
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_Lng, 13));
			userLocMarker(lat_Lng);

		}
		
				// This sets an ontouch listener for each of the tabs... This is used to detect when an already focused
				// Tab is clicked again.  This way, the tab can be unselected and go back to the Map main view
				for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
				    View v = tabhost.getTabWidget().getChildAt(i);
				    v.setTag(i);
				}

		lm.requestLocationUpdates(provider, 1, 0, this);
		
		tabhost.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tabhost.getCurrentTab() == 0 && v.getTag().equals(0)){
					mLayout.setPanelState(PanelState.COLLAPSED);
					tabhost.setCurrentTab(3);
					
					if(clickedRoute != null && clickedRoute.getPolyLine().isVisible()){
					hideStops(clickedRoute);
					hideRoute(clickedRoute);
					}
				}
				else{
					
					if(tabhost.getCurrentTab() == 1 && fragment_Favorites.isVisible()){
					// Need to make it so it only connects when unique routes will be shown, otherwise, it will just be
					// Doing more work by getting the same routes over and over whenver search is clicked
						
						FragmentTransaction ft = manager.beginTransaction();
//						ft.replace(R.id.main_fragmentgroup, (Fragment)fm);
						ft.remove(fragment_Favorites);
						ft.commit();
					}
					
					
					mLayout.setPanelState(PanelState.ANCHORED);
					connect = new Connect(main, googleMap);
					connect.execute();
					tabhost.setCurrentTab(0);
				}
				
			}
		});

	
		
		tabhost.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(tabhost.getCurrentTab() == 1 && v.getTag().equals(1)){
					mLayout.setPanelState(PanelState.COLLAPSED);
					FragmentTransaction ft = manager.beginTransaction();
//					ft.replace(R.id.main_fragmentgroup, (Fragment)fm);
					ft.remove(fragment_Favorites);
					ft.commit();
					tabhost.setCurrentTab(3);
				}
				else{
					FragmentTransaction ft = manager.beginTransaction();
					ft.add(R.id.main_fragmentgroup, fragment_Favorites);
					ft.commit();
					tabhost.setCurrentTab(1);

				}
				
			}
		});
	
	
	tabhost.getTabWidget().getChildAt(2).setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			System.out.println("Clicked Settings!");
			
			if(tabhost.getCurrentTab() == 1 && fragment_Favorites.isVisible()){
					FragmentTransaction ft = manager.beginTransaction();
					ft.remove(fragment_Favorites);
					ft.commit();
					
					tabhost.setCurrentTab(2);
				}
			
			else if(tabhost.getCurrentTab() == 2){
				mLayout.setPanelState(PanelState.COLLAPSED);
				tabhost.setCurrentTab(3);
			}
			else{
				mLayout.setPanelState(PanelState.COLLAPSED);
				FragmentTransaction ft = manager.beginTransaction();
				ft.add(R.id.main_fragmentgroup, fragment_Schedule);
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

		//r.getPolyLine().remove();
		r.getPolyLine().setVisible(false);
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

	public void updateRoutes() {
		if (routeTitles.size() < routes.size()) {
			routeTitles.clear();
			for (int i = 0; i < routes.size(); i++) {
				routeTitles.add(routes.get(i).getRouteTitle());
			}
			adapter.notifyDataSetChanged();
			gridview.setAdapter(adapter);
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
	
	public void hideFavorites(){
		FragmentTransaction ft = manager.beginTransaction();
//		ft.replace(R.id.main_fragmentgroup, (Fragment)fm);
		ft.remove(fragment_Favorites);
		ft.commit();
		tabhost.setCurrentTab(3);

	}
	
}