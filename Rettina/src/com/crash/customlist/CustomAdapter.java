/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * CustomAdapter is used to create the custom listview design for the "Favorites" listview shown on the left fragment
 * It contains a checkbox which is used to show whether the route is toggled or not. Each
 * element of the listview also contains two image buttons. The further left icon is used to start following along the
 * route basically indicating that the user is on that current route. This triggers 'Navigation mode' and the app responds
 * as if it is following along with the route. The icon further to the right is used to remove the route from the favorites
 */

package com.crash.customlist;

import java.util.ArrayList;

import com.crash.rettina.Favorites;
//import com.crash.rettina.MainActivity;
import com.crash.rettina.Main;
import com.crash.rettina.R;
//import com.crash.rettina.RouteMenu;
import com.crash.routeinfo.Route;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter implements OnClickListener {

	private Activity activity;
	private Main maintile_activity;				// The Main activity which 'everything run's through' and is connected to

	private ArrayList<Route> data;				// The set of Routes
	private Favorites routeActivity_favorites;	// The 'Favorites' Fragment
	private Route navRoute;						// Determines which route is currently in 'Navigation Mode'

	private static LayoutInflater inflater = null;
	
	private Resources res;
	private Route tempValues = null;		// Used to hold a route


	// Constructor
	public CustomAdapter(Activity a, ArrayList<Route> d, Resources resLocal,
			Favorites r) {
		
		activity = a;						// Passing in Main activity
		data = d;							// Passing in the 'favorite' routes
		res = resLocal;
		routeActivity_favorites = r;		// Passing in the 'Favorites' fragment
		maintile_activity = (Main) a;		// Saving the Main activity

		
		// Inflates 'Favorites' listview through external XML resource
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	// Gets the size of data
	public int getCount() {
		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	// Gets the object at the given position
	public Object getItem(int position) {
		return position;
	}

	// Get's the object's ID at the given position
	public long getItemId(int position) {
		return position;
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
	public static class ViewHolder {

		public CheckBox cb_showRoute;
		public ImageButton imgbtn_removeRoute;
		public ImageButton imgbtn_followRoute;
		public ImageButton imgbtn_schedule;
		public TextView tv_routeName;

	}

	// Allows for customizing the 'Favorites' listview
	// Depends upon data size called for each row , Create each ListView row
	public View getView(final int position, View convertView, final ViewGroup parent) {

		View vi = convertView;
		final ViewHolder holder;

			vi = inflater.inflate(R.layout.tabitem, null);	// Inflates the view using tabitem, a custom XML 

			holder = new ViewHolder();
			holder.cb_showRoute = (CheckBox) vi.findViewById(R.id.cb_showroute);// Holder for the Checkbox

			// Remove route button
			holder.imgbtn_removeRoute = (ImageButton) vi.findViewById(R.id.imgbtn_removeroute);
			
			// Navigation mode button
			holder.imgbtn_followRoute = (ImageButton) vi.findViewById(R.id.imgbtn_followroute);
			
			// Schedule view button
			holder.imgbtn_schedule = (ImageButton) vi.findViewById(R.id.imgbtn_schedule);
			
			// TextView that holds the route title
			holder.tv_routeName = (TextView) vi.findViewById(R.id.tv_routename);
		
			vi.setTag(holder);	// Setting the tag for this view as the holder

			
			// If the data size is less than 1, then set the view to the activity
		if (data.size() <= 0) {
			ViewHolder tempHolder = new ViewHolder();
			vi = new View(activity);
			vi.setTag(tempHolder);
		}
			// Otherwise, set the route title
		else {
			tempValues = null;
			tempValues = (Route) data.get(position);
			holder.tv_routeName.setText(tempValues.getRouteTitle());
		}
		
		// If the data size is bigger than 0, handle whether the checkbox is clicked or not
		if(data.size() > 0){
			// If the route is set as 'clicked' then set the checkbox to checked
			if(data.get(position).isClicked()){
				holder.cb_showRoute.setChecked(true);
			}
			// Otherwise, the route is not 'checked' and the checkbox should be empty
			else{
				holder.cb_showRoute.setChecked(false);
			}
		}
		
		// Each row in the listview can listen for clicks...
		// When a row is clicked, it will toggle the checkbox and display the route
		// On the map when the box is checked, and hide the route when unchecked
		vi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// If the number of 'favorite' routes is greater than 0
				if(data.size() > 0){
				
					// The first time this element of the listview is clicked, call this to make the call
					// To get the shape and set the checkbox.
					if(data.get(position).getPolyLine() == null){	
						
						data.get(position).setClicked(true);	// Setting that the route is currently clicked
				        notifyDataSetChanged();					// Update the adapter to show that the route has been clicked
					}
					
				// After this element has been clicked, then this segment of the if/else will be called	
					else{
						
						// If the route is currently set as clicked, then uncheck the route and checkbox
						if(data.get(position).isClicked()){				
							
							holder.cb_showRoute.setChecked(false);	// Uncheck the checkbox
							data.get(position).setClicked(false);	// Set route to 'not clicked'
							data.get(position).hidePolyLine();		// Hide the polyline for that selected route

						
							routeActivity_favorites.hideStops(data.get(position));	// Hide the stops of that given route
							routeActivity_favorites.hideRoute(data.get(position));	// Clean up everything else and call 'hide route' method
						
							// If the clicked route has the 'Navigation Mode' set as true
						if(data.get(position).isNavMode() == true){
							 
							 CameraPosition cameraPosition = new CameraPosition.Builder()
							    .target(data.get(position).getStops().get(0).getLatLng())      // Moves to the first stop
							    .zoom(15)                   // Sets the zoom
							    .bearing(90)                // Sets the orientation of the camera to east
							    .tilt(40)                   
							    .build();                   // Creates a CameraPosition from the builder
							 
							 // Animate the camera according to the above specifications
							 maintile_activity.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
							
							data.get(position).setNavMode(false);	// Set the route to no longer be in 'Navigation Mode'
							}

						}
						
						// Otherwise, the route is not currently clicked
						else {	
							data.get(position).setClicked(true);	// Set the route to 'Clicked'
							holder.cb_showRoute.setChecked(true);	// Set the checkbox to 'Checked'
						
							// Display the route and stops on the Google Map
							routeActivity_favorites.showRoute(data.get(position));
							routeActivity_favorites.showStops(data.get(position));
						}
						
					}
					
				}
			}
		});


		// Remove route listener... This is the 'X' that the user can click to remove the route from the favorites tab
	    holder.imgbtn_removeRoute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// If there are 1 or more routes
				if(data.size() >= 1){
					
					// If the polyline have been found for this route, then remove the routes					
					if(data.get(position).getPolyLine() != null){

						maintile_activity.hideRoute(data.get(position));		// Hide the route on the Map
						routeActivity_favorites.hideStops(data.get(position));	// Hide the stops from the 'Favorites'
						routeActivity_favorites.hideRoute(data.get(position));	// Hide the Route from the 'Favorites'
					
						// If the Route is currently in 'Navigation Mode'
						if(data.get(position).isNavMode() == true){
							 
							// Zoom the camera out once the route has been removed
							 CameraPosition cameraPosition = new CameraPosition.Builder()
							    .target(data.get(position).getStops().get(0).getLatLng())      // Sets the center of the map to Mountain View
							    .zoom(15)                   // Sets the zoom
							    .bearing(90)                // Sets the orientation of the camera to east
							    .tilt(40)                   
							    .build();                   // Creates a CameraPosition from the builder
							 
							 // Animate the camera according to the above specifications
							 maintile_activity.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
							
							 
							data.get(position).setNavMode(false);	// Set the route to no longer be in 'Navigation Mode'
							data.get(position).setClicked(false);	// Set the route to 'Not Clicked'
							}
						
					
						// If the Fragment 'Schedule' is currently added
						if(maintile_activity.fragment_Schedule.isAdded()){
							
							// Then remove the route from the fragment 'Schedule'
							if(maintile_activity.fragment_Schedule.adapter.routeHolder.contains(data.get(position))){
								maintile_activity.fragment_Schedule.removeRoutes(data.get(position));
							}
						}
						
						
						// Remove the route from the Fragment 'Favorites'
						routeActivity_favorites.removeRouteFromFav(data.get(position));
	
						// Update the adapter to reflect the removed route
						notifyDataSetChanged();	
						}
						
					// Otherwise, the polyline is null and remove the route from the fragment 'Favorites'
					else{
						routeActivity_favorites.removeRouteFromFav(data.get(position));
					}		
				}
				
			}
		});
	    
	    
	    // When the user clicks the location marker icon, it will trigger a gps view where 
	    // it animates the camera to follow along route
	    holder.imgbtn_followRoute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// If the number of routes is greater than 0
				if(data.size() >= 1){
					
					// Loop through all the routes
					for(int i = 0; i < data.size(); i++){
						
						// If iterator is not equal to the clicked route position,
						// then set all the rest of the routes unchecked, and remove nav mode
						if(i != position){					
							if(data.get(i).isClicked() && data.get(i).getPolyLine() != null){
								
							data.get(i).hideStops(maintile_activity.googleMap);
							data.get(i).hidePolyLine();
							holder.cb_showRoute.setChecked(false);
							data.get(i).setClicked(false);
							data.get(i).setNavMode(false);
							}
						}
						// Otherwise, set the route as 'clicked', 'Navigation mode true'
						else{
							holder.cb_showRoute.setChecked(true);
							data.get(position).setClicked(true);
							
							// Boolean NavMode is used to set the camera for navigation mode by placing the camera
							data.get(position).setNavMode(true);
							setNavRoute(data.get(position));
							
					        notifyDataSetChanged();	// Update the adapter
					    
				
					    	// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
							CameraPosition cameraPosition = new CameraPosition.Builder()
							    .target(data.get(i).getStops().get(0).getLatLng())      // Sets the center of the map to Mountain View
							    .zoom(19)                   // Sets the zoom
							    .bearing(90)                // Sets the orientation of the camera to east
							    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
							    .build();                   // Creates a CameraPosition from the builder
							maintile_activity.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
							
							// Show the first stop's info on the map
							data.get(i).getStops().get(0).getMarker().showInfoWindow();
					        
						}
					}
						
					}
					
					// If the pressed route has more than 0 stops, then set the camera to follow that route... Used to prevent null pointer errors
					if( data.get(position).getPolyLine() != null){
						
						routeActivity_favorites.showRoute(data.get(position));

						routeActivity_favorites.showStops(data.get(position));	
					
					// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
					CameraPosition cameraPosition = new CameraPosition.Builder()
					    .target(data.get(position).getStops().get(0).getLatLng())      // Sets the center of the map to Mountain View
					    .zoom(19)                   // Sets the zoom
					    .bearing(90)                // Sets the orientation of the camera to east
					    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
					    .build();                   // Creates a CameraPosition from the builder
					
					// Animate the camera according to the above specifications
					maintile_activity.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						
					// Set the route for the Navigation Fragment
					maintile_activity.fragment_Navigator.setRoute(data.get(position));
					
					// Display the first stop's info on the map
					data.get(position).getStops().get(0).getMarker().showInfoWindow();

					// Hide the Favorites Fragment, so it does not overlap the Navigation fragment
					 maintile_activity.hideFavorites(); 
				}
					// Show the Navigation Fragment at the bottom of the screen
					maintile_activity.showNavigation();
				
				}
			
		});
	    
	    // Handles when the user clicks the 'Schedule' button
	    holder.imgbtn_schedule.setOnClickListener(new OnClickListener() {
				    	
			@Override
			public void onClick(View v) {
				// Handles Fragment's moving around
				FragmentTransaction ft = maintile_activity.manager.beginTransaction();
				
				// Remove the 'Favorites' fragment, and displays the 'Schedule' fragment
				ft.remove(routeActivity_favorites);	// Remove the 'Favorites' Fragment
				ft.add(R.id.fragment_group_tile, maintile_activity.fragment_Schedule);	// Adds 'Schedule' Fragment
				ft.addToBackStack("Schedule");	// Schedule Fragment is now on the stack, so the back button will remove it
				ft.commit();
				
				// Set the 'Schedule' Fragment's list view information size
				maintile_activity.fragment_Schedule.lv_arr = new String[maintile_activity.fragment_Routes.clickedRoute.getStops().size()];
				
				// Set the stop descriptions for each Stop on the 'Schedule'
				for(int i = 0; i < maintile_activity.fragment_Routes.clickedRoute.getStops().size(); i++){
					maintile_activity.fragment_Schedule.lv_arr[i] = maintile_activity.fragment_Routes.clickedRoute.getStops().get(i).getStopDescription();
				}
				
				// Set the clicked route
				maintile_activity.fragment_Schedule.tempRoute = maintile_activity.clickedRoute;
				
				// Hide the image button that toggles the 'Schedule' fragment
				maintile_activity.imgbtn_SchedulePopup.setVisibility(View.GONE);
			}
		});

		return vi;
		
	}

	// Handles clicking the view
	@Override
	public void onClick(View v) {

	}

	// Returns the route that is currently in 'Navigation Mode'
	public Route getNavRoute() {
		return navRoute;
	}

	// Set the route that is in 'Navigation Mode'
	public void setNavRoute(Route navRoute) {
		this.navRoute = navRoute;
	}

}