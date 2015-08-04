/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * Navigator is a Fragment used to follow along the desired route.  Navigator pops up in
 * the bottom half of the screen and uses a custom gridview to display each stop name and
 * the arrival times for each stop.  The user can click on any stop and the camera will
 * navigate to that stop and display the stop's name on the map
 * 
 * This fragment is toggled when the user selects the 'Navigation' icon on the 'Favorites' fragment
 */

package com.crash.rettina;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.crash.routeinfo.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;

@SuppressLint("Instantiatable")
public class Navigator extends Fragment {
	
	  private Main main_tile;		// Used to hold the Main activity
	  private Route route = null;	// Holds the current route
	  private GridView gridview;	// Gridview is used to display all the stops
	  private MyAdapter adapter;  	// adapter is used as a custom adapter to gridview

	  private ImageButton imgbtn_Close;	// Used to close the Navigator Fragment
	  private Button btn_Share;			// Toggles the 'Sharing' fragment

	  private int stopPosition;		// Used to hold the current stop position

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		
		// Uses the navigator.xml layout
	    View view = inflater.inflate(R.layout.navigator,
	        container, false);
	    
	    // Initializing the views
	    imgbtn_Close = (ImageButton) view.findViewById(R.id.imgbtn_closenav); 
	    btn_Share = (Button) view.findViewById(R.id.btn_share);
		gridview = (GridView) view.findViewById(R.id.gridview_nav);
		
		// Initializing the adapter
		adapter = new MyAdapter(this.getActivity());

		// Setting the adapter and the number of columns in the gridview
		gridview.setAdapter(adapter);
        gridview.setNumColumns(3);
	    
	    
        // Listens for clicks on the gridview
		gridview.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				if(stopPosition < main_tile.fragment_Favorites.adapter.getNavRoute().getStops().size() - 1){
				incrementStopPosition();
				
				// Move the camera to the clicked stop position
				CameraPosition cameraPosition = new CameraPosition.Builder()
			    .target(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getLatLng())      // Sets the center of the map to Mountain View
			    .zoom(19)                   // Sets the zoom
			    .bearing(90)                // Sets the orientation of the camera to east
			    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
			    .build();                   // Creates a CameraPosition from the builder
				
			// Animate the camera to the given specifications
			main_tile.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
			// Display the stop name above the marker on the map
			main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getMarker().showInfoWindow();
				}
			}

		});
		
		// Listens for Long clicks on the gridview items
	    gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
		
	    	@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {			
				if(stopPosition > 0){

				decrementStopPosition();
				
				CameraPosition cameraPosition = new CameraPosition.Builder()
			    .target(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getLatLng())      // Sets the center of the map to Mountain View
			    .zoom(19)                   // Sets the zoom
			    .bearing(90)                // Sets the orientation of the camera to east
			    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
			    .build();                   // Creates a CameraPosition from the builder
				
			// animates the camera to the given specifications
			main_tile.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));				

			// Shows the stop information on the map above the marker
			main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(getStopPosition()).getMarker().showInfoWindow();

				}
				return false;
	    	}	
		});
	    
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
	
	public Navigator(Main mt){
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
	
	// The custom adapter for the gridview
	public class MyAdapter extends BaseAdapter {
		 
        private Context mContext;
 
        public MyAdapter(Context c) {
            mContext = c;
        }
 
        @Override
        public int getCount() {
            return main_tile.fragment_Favorites.adapter.getNavRoute().getStops().size();
        }
 
        @Override
        public Object getItem(int arg0) {
            return main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(arg0);
        }
 
        @Override
        public long getItemId(int arg0) {
            return arg0;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
 
            View grid;

 
            if(convertView==null){
                grid = new View(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                grid=inflater.inflate(R.layout.custom_gridview_nav, parent, false);
            }else{
                grid = (View) convertView;
            }
 
            // Setting the stop names
            TextView tv_stopname = (TextView)grid.findViewById(R.id.tv_gridview_stopname);
            tv_stopname.setText(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(position).getStopDescription());
            
            // Setting the arrival times
            TextView tv_stoptime = (TextView)grid.findViewById(R.id.tv_gridview_stoptime); 
            tv_stoptime.setText(main_tile.fragment_Favorites.adapter.getNavRoute().getStops().get(position).getArrival_time());
 
            return grid;
        }
    }
	
}
