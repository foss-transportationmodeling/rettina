package com.crash.rettina;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.crash.customlist.MyListAdapter;
import com.crash.routeinfo.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

@SuppressLint("Instantiatable")
public class Navigator extends Fragment {

	  MyListAdapter adapter;
	  ListView lv;
	  Main_Tile main_tile;
	  Schedule sched;
	  Route route = null;
	  
	  ImageButton imgbtn_Close, imgbtn_PreviousStop, imgbtn_NextStop;
	  Button btn_Share;
	  
	  TextView tv_currentstop_name, tv_nextstop_name, tv_route_name;
	  
	  private int stopPosition;



	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.navigator,
	        container, false);
	    
	    imgbtn_Close = (ImageButton) view.findViewById(R.id.imgbtn_close);
	    
	    imgbtn_Close = (ImageButton) view.findViewById(R.id.imgbtn_close);
	    imgbtn_PreviousStop = (ImageButton) view.findViewById(R.id.imgbtn_previousStop);
	    imgbtn_NextStop = (ImageButton) view.findViewById(R.id.imgbtn_nextStop);
	    btn_Share = (Button) view.findViewById(R.id.btn_share);

	    
	    tv_currentstop_name = (TextView) view.findViewById(R.id.tv_currentstop_name);
	    tv_nextstop_name = (TextView) view.findViewById(R.id.tv_nextstop_name);
	    tv_route_name = (TextView) view.findViewById(R.id.tv_navigation);

	    
	    
	    tv_route_name.setText(main_tile.fragment_Favorites.adapter.getNavRoute().getRouteTitle());
	    
		if(stopPosition < main_tile.fragment_Favorites.adapter.getNavRoute().getStops().size() - 1){
			tv_currentstop_name.setText(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getStopDescription());
			tv_nextstop_name.setText(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition() + 1).getStopDescription());
		}
	    
	    // Clicking the close button (The 'X' in the right corner) will remove the navigation tab
	    // This is done by calling the removeNavigator method in the 'Main_Tile' class
	    imgbtn_Close.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				main_tile.removeNavigator();
				
				setStopPosition(0);
			
                CameraPosition cameraPosition = new CameraPosition.Builder()
			    .target(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getLatLng())      // Sets the center of the map to Mountain View
			    .zoom(14)                   // Sets the zoom
			    .bearing(90)                // Sets the orientation of the camera to east
			    .tilt(40)                   
			    .build();                   // Creates a CameraPosition from the builder
			main_tile.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			
			// When the 'X' is clicked to close Navigation, it will prompt Main_Tile to make a POST request to the server containing
			// A bundle of the user's location data
			main_tile.exitNavigation();
			
			}
		});
	    
	    imgbtn_NextStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(stopPosition < main_tile.fragment_Favorites.adapter.getNavRoute().getStops().size() - 1){
				incrementStopPosition();
				
				CameraPosition cameraPosition = new CameraPosition.Builder()
			    .target(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getLatLng())      // Sets the center of the map to Mountain View
			    .zoom(19)                   // Sets the zoom
			    .bearing(90)                // Sets the orientation of the camera to east
			    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
			    .build();                   // Creates a CameraPosition from the builder
			main_tile.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
			
			main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getMarker().showInfoWindow();
			
			// Set currentStop name when next stop is clicked
			if(stopPosition < main_tile.fragment_Favorites.adapter.getNavRoute().getStops().size() - 2){
				tv_currentstop_name.setText(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getStopDescription());
				tv_nextstop_name.setText(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition() + 1).getStopDescription());
			}
			else{
				tv_currentstop_name.setText(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getStopDescription());
				tv_nextstop_name.setText("");
			}
				}
			}
		});
		
	    imgbtn_PreviousStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(stopPosition > 0){

				decrementStopPosition();
				
				CameraPosition cameraPosition = new CameraPosition.Builder()
			    .target(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getLatLng())      // Sets the center of the map to Mountain View
			    .zoom(19)                   // Sets the zoom
			    .bearing(90)                // Sets the orientation of the camera to east
			    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
			    .build();                   // Creates a CameraPosition from the builder
			main_tile.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));				

			main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getMarker().showInfoWindow();
			
			// Set currentStop and nextStop name when next stop is clicked
				if(stopPosition > 2){
					tv_currentstop_name.setText(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getStopDescription());
					tv_nextstop_name.setText(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition() + 1).getStopDescription());
				}
			
			
				}
			}
		});
	    
	    // On clicking "Share" it will remove the "Navigation" fragment and add the "Sharing" fragment
	    btn_Share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				main_tile.removeNavigator();
				main_tile.addSharing();
			}
	    
	    });

	    
		return view;

}
	
	public Navigator(Main_Tile mt){
		main_tile = mt;
	}
	
	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
	
/*
 * Stop position methods are used to keep track of which stop the camera is focused on. This allows the user to use the arrow keys
 * on the navigation tab to move from stop to stop
 */
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
	
}
