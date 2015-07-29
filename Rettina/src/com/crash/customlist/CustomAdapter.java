/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * CustomAdapter is used to create the custom listview design for the "Favorites" listview shown on the left fragment
 * It contains a checkbox on the left side of the view which is used to show whether the route is toggled or not. Each
 * element of the listview also contains two image buttons. The further left icon is used to start following along the
 * route basically indicating that the user is on that current route. The icon further to the right is used to remove
 * the route from the favorites
 */

/*

 */

package com.crash.customlist;

import java.util.ArrayList;

import com.crash.rettina.Favorites;
import com.crash.rettina.MainActivity;
import com.crash.rettina.Main_Tile;
import com.crash.rettina.R;
import com.crash.rettina.RouteMenu;
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

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomAdapter extends BaseAdapter implements OnClickListener {

	/*********** Declare Used Variables *********/
	private Activity activity;
	private ArrayList<Route> data;
	public RouteMenu routeActivity;

	public Favorites routeActivity_favorites;

	private static LayoutInflater inflater = null;
	public Resources res;
	Route tempValues = null;;
	CheckBox cb_showRoute;
	int i = 0;
	public MainActivity tempA;

	public Main_Tile maintile_activity;

	private Route navRoute;

	/************* CustomAdapter Constructor *****************/
	public CustomAdapter(Activity a, ArrayList<Route> d, Resources resLocal,
			RouteMenu r) {

		/********** Take passed values **********/
		activity = a;
		data = d;
		res = resLocal;
		routeActivity = r;
		tempA = (MainActivity) activity;

		/*********** Layout inflator to call external xml layout () ***********/
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	// Used for Tile UI
	/************* CustomAdapter Constructor *****************/
	public CustomAdapter(Activity a, ArrayList<Route> d, Resources resLocal,
			Favorites r) {

		/********** Take passed values **********/
		activity = a;
		data = d;
		res = resLocal;
		routeActivity_favorites = r;
		maintile_activity = (Main_Tile) activity;

		/*********** Layout inflator to call external xml layout () ***********/
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	/******** What is the size of Passed Arraylist Size ************/
	public int getCount() {

		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

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

	/****** Depends upon data size called for each row , Create each ListView row *****/
	public View getView(final int position, View convertView, final ViewGroup parent) {

		View vi = convertView;
		final ViewHolder holder;

		//if (convertView == null) {

			/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
			vi = inflater.inflate(R.layout.tabitem, null);

			/****** View Holder Object to contain tabitem.xml file elements ******/

			holder = new ViewHolder();
			holder.cb_showRoute = (CheckBox) vi.findViewById(R.id.cb_showroute);

			cb_showRoute = (CheckBox) vi.findViewById(R.id.cb_showroute);

			holder.imgbtn_removeRoute = (ImageButton) vi
					.findViewById(R.id.imgbtn_removeroute);
			
			holder.imgbtn_followRoute = (ImageButton) vi
					.findViewById(R.id.imgbtn_followroute);
			
			holder.imgbtn_schedule = (ImageButton) vi
					.findViewById(R.id.imgbtn_schedule);
			
			holder.tv_routeName = (TextView) vi.findViewById(R.id.tv_routename);
			
			

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
//		} 
//		else{
//			System.out.println("This Guy!");
//			holder = (ViewHolder) vi.getTag();
//		}

		if (data.size() <= 0) {
			ViewHolder tempHolder = new ViewHolder();
			vi = new View(activity);
			vi.setTag(tempHolder);
		} 
		
		
		else {
	
			/***** Get each Model object from Arraylist ********/
			tempValues = null;
			
			// Error when removing the last element
			tempValues = (Route) data.get(position);
			//System.out.println(tempValues.getRouteTitle());

			/************ Set Model values in Holder elements ***********/
			
			holder.tv_routeName.setText(tempValues.getRouteTitle());

			// holder.text1.setText( tempValues.getUrl() );
			// holder.image.setImageResource(
			// res.getIdentifier(
			// "com.androidexample.customlistview:drawable/"+tempValues.getImage()
			// ,null,null));

			/******** Set Item Click Listner for LayoutInflater for each row *******/

//			vi.setOnClickListener(new OnItemClickListener(position));
			//vi.setOnClickListener(this);
			
			
		}
		
		// Trying to set the checkbox based on whether the route is clicked or not
		if(data.size() > 0){
			if(data.get(position).isClicked()){
				holder.cb_showRoute.setChecked(true);
			}
			else{
				holder.cb_showRoute.setChecked(false);
			}
		}
		
		vi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(data.size() > 0){
				
					// The first time this element of the listview is clicked, call this to make the call
					// To get the shape and set the checkbox... This will populate the right fragment
					if(data.get(position).getPolyLine() == null){
						data.get(position).printRoute();
						
						System.out.println("Setting Schedule stops for the first time!!!!");

						// Setting that the route is currently clicked
						data.get(position).setClicked(true);

						//holder.cb_showRoute.setChecked(true);

						// Used for MainActivity UI
						//tempA.getShape(data.get(position));
						
						// Used for Tile UI
						//maintile_activity.getShape(data.get(position));
						
	
				        notifyDataSetChanged();


					}
					
				// After this element has been clicked, then this segment of the if/else will be called	
					else{
						
						if(data.get(position).isClicked()){
							
							System.out.println("UnClicking!");
					
				// if (holder.cb_showRoute.isChecked()) {
						holder.cb_showRoute.setChecked(false);
						
						data.get(position).setClicked(false);

						//System.out.println("Hiding Route: " + data.get(position));
					//routeActivity.hideRoute(data.get(position));
						
						
						//data.get(position).getPolyLine().remove();
						//data.get(position).getPolyLine().setVisible(false);
						
						// Hide the stops for that selected route
						
						data.get(position).hidePolyLine();
						
						// Used for MainActivity UI		
						//routeActivity.hideStops(data.get(position));
						
						// Used for Tile UI
						routeActivity_favorites.hideStops(data.get(position));
						routeActivity_favorites.hideRoute(data.get(position));
						

						
						if(data.get(position).isNavMode() == true){
//							 tempA.mLayout.setPanelState(PanelState.COLLAPSED);
							 
							 CameraPosition cameraPosition = new CameraPosition.Builder()
							    .target(data.get(position).getStops().get(0).getLatLng())      // Sets the center of the map to Mountain View
							    .zoom(15)                   // Sets the zoom
							    .bearing(90)                // Sets the orientation of the camera to east
							    .tilt(40)                   
							    .build();                   // Creates a CameraPosition from the builder
							tempA.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
							

							data.get(position).setNavMode(false);
							}
						
						// Used for MainActivity UI
						//tempA.sched.removeRoutes(data.get(position));	// Remove the routes from the schedule fragment
						
						
						// Need to implement this when I get the schedule up for Tile UI

					} else {
						System.out.println("Clicking!");

						data.get(position).setClicked(true);

						holder.cb_showRoute.setChecked(true);
						
						// Used for MainActivity UI
						//routeActivity.showRoute(data.get(position));
						
						// Used for Tile UI
						routeActivity_favorites.showRoute(data.get(position));
						

						
						// Show the stops for that route
						// Used for MainActivity UI
						// routeActivity.showStops(data.get(position));
						
						// Used for Tile UI
						routeActivity_favorites.showStops(data.get(position));

						
						// Used for MainActivity UI
						// tempA.sched.setRoutes(data.get(position));	// Set the stops for the schedule fragment
						
						
						
						// DO NOT HAVE SCHEDULE SET UP YET FOR MAIN_TILE.... WILL NEED TO UNCOMMENT THIS ONCE IMPLEMENTED!!!
						//Main_Tile.sched.setRoutes(data.get(position));	// Set the stops for the schedule fragment
						
						// Places the navigation panel into a collapsed state, which means it is no longer in "navigation mode"
						// Used for MainActivity UI
						//tempA.mLayout.setPanelState(PanelState.COLLAPSED);				

					}
					}
				}
			}
		});


		// Remove route listener... This is the 'X' that the user can click to remove the route from the favorites tab
	    holder.imgbtn_removeRoute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				System.out.println("Data Size: " + data.size());

				if(data.size() >= 1){
					System.out.println("Position: " + position + " Removing: " + data.get(position));						
					//routeActivity.hideRoute(data.get(position));
					//routeActivity.hideRoute(data.get(position));
					
					// If the polyline have been found for this route, then remove the routes					
					if(data.get(position).getPolyLine() != null){
						
						// Hide the stops for that selected route
						// Used for MainActivity UI
//					routeActivity.hideStops(data.get(position));
//					routeActivity.hideRoute(data.get(position));
						
						maintile_activity.hideRoute(data.get(position));

						
						// Used for Tile UI
					routeActivity_favorites.hideStops(data.get(position));
					routeActivity_favorites.hideRoute(data.get(position));
					
					
					if(data.get(position).isNavMode() == true){
						
						 // Used for MainActivity UI
						 //tempA.mLayout.setPanelState(PanelState.COLLAPSED);
						 
						 CameraPosition cameraPosition = new CameraPosition.Builder()
						    .target(data.get(position).getStops().get(0).getLatLng())      // Sets the center of the map to Mountain View
						    .zoom(15)                   // Sets the zoom
						    .bearing(90)                // Sets the orientation of the camera to east
						    .tilt(40)                   
						    .build();                   // Creates a CameraPosition from the builder
						 
						 // Used for MainActivity UI
//						tempA.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						 
						 // Used for Tile UI
						 maintile_activity.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						
						 
						data.get(position).setNavMode(false);
						data.get(position).setClicked(false);
						
						

						}
					
				
					// If the Schedule contains the route and the fragment_schedule is already added,
					// then Remove the route from the schedule fragment
					if(maintile_activity.fragment_Schedule.isAdded()){
						if(maintile_activity.fragment_Schedule.adapter.routeHolder.contains(data.get(position))){
							
							maintile_activity.fragment_Schedule.removeRoutes(data.get(position));
						}
					}
					// Used for Tile UI
					routeActivity_favorites.removeRouteFromFav(data.get(position));
										
					
					
					//MainActivity tempA = (MainActivity) activity;

					
				//data.remove(position);
				System.out.println("Data Size After Removed: " + data.size());

		        notifyDataSetChanged();
					}
					else{
						
						
						// Used for MainActivity UI
//						routeActivity.removeRouteFromFav(data.get(position));
//						 tempA.mLayout.setPanelState(PanelState.COLLAPSED);

						// Used for Tile UI
						routeActivity_favorites.removeRouteFromFav(data.get(position));
						
						System.out.println("Can't remove the stops since the route was never clicked");

					}
				}
				
				
			}
		});
	    
	    // When the user clicks the location marker icon, it will trigger a gps view where it animates the camera to follow along
	    // The route
	    holder.imgbtn_followRoute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				

				if(data.size() >= 1){
					
					for(int i = 0; i < data.size(); i++){
						if(i != position){
							
							System.out.println("Not the clicked route: " + i);
							
							if(data.get(i).isClicked() && data.get(i).getPolyLine() != null){
								
								// Used for MainActivity UI
//							data.get(i).hideStops(tempA.googleMap);
							
								// Used for Tile UI
							data.get(i).hideStops(maintile_activity.googleMap);

							data.get(i).hidePolyLine();
							holder.cb_showRoute.setChecked(false);
							data.get(i).setClicked(false);
							data.get(i).setNavMode(false);
							}
						}
						else{
							System.out.println("Clicked route: " + i);
							holder.cb_showRoute.setChecked(true);
							data.get(position).setClicked(true);
							
							// Boolean NavMode is used to set the camera for navigation mode by placing the camera
							// Calls in connect_shape which is done on postexecute
							data.get(position).setNavMode(true);
					
							// Used for MainActivity UI
							//tempA.getShape(data.get(position));
							
							// Used for Tile UI
							//maintile_activity.getShape(data.get(position));

							
							setNavRoute(data.get(position));
							
					        notifyDataSetChanged();
					        

					        System.out.println("Going to navigation mode!");
					        
					        
					        // Used for MainActivity UI
//					        if (tempA.mDrawerlayout.isDrawerVisible(tempA.mDrawerList_Left)) {
//								tempA.mDrawerlayout.closeDrawer(tempA.mDrawerList_Left);
//							}
							
							//routeActivity.showRoute(data.get(position));

							
							// Show the stops for that route
							//routeActivity.showStops(data.get(position));
							
							//tempA.sched.setRoutes(data.get(position));	
							
				
					    	// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
							CameraPosition cameraPosition = new CameraPosition.Builder()
							    .target(data.get(i).getStops().get(0).getLatLng())      // Sets the center of the map to Mountain View
							    .zoom(19)                   // Sets the zoom
							    .bearing(90)                // Sets the orientation of the camera to east
							    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
							    .build();                   // Creates a CameraPosition from the builder
							maintile_activity.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
							
							//Null Pointer here
							data.get(i).getStops().get(0).getMarker().showInfoWindow();
					        
						 
						 // Sets the bottom panel to pop up into an achored state which is "Navigation Mode"
					     // Used for MainActivity UI
							
						// maintile_activity.mLayout.setPanelState(PanelState.ANCHORED);
							
							

						}
					}
						
					}
					
					// Move google maps to the starting route position
					//tempA.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(data.get(position).getStops().get(0).getLatLng(), 16));
					
					
					// If the pressed route has more than 0 stops, then set the camera to follow that route... Used to prevent null pointer errors
					if( data.get(position).getPolyLine() != null){
						System.out.println("Going to navigation mode!");
						
						
						// Used for MainActivity UI
//						routeActivity.showRoute(data.get(position));
						
						// Used for Tile UI
						routeActivity_favorites.showRoute(data.get(position));

						
						// Show the stops for that route
						// Used for MainActivity UI
//						routeActivity.showStops(data.get(position));
						
						// Used for Tile UI
						routeActivity_favorites.showStops(data.get(position));

						
						
						// Used for MainActivity UI
//						tempA.sched.setRoutes(data.get(position));	
						
						// Used for Tile UI... Schedule needs to be implemented first!
//						maintile_activity.sched.setRoutes(data.get(position));	

						
						
					
					// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
					CameraPosition cameraPosition = new CameraPosition.Builder()
					    .target(data.get(position).getStops().get(0).getLatLng())      // Sets the center of the map to Mountain View
					    .zoom(19)                   // Sets the zoom
					    .bearing(90)                // Sets the orientation of the camera to east
					    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
					    .build();                   // Creates a CameraPosition from the builder
					
					// Used for MainActivity UI
//					tempA.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					
					// Used for Tile UI
					maintile_activity.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					
					
					maintile_activity.fragment_Navigator.setRoute(data.get(position));
					
					//Null Pointer here
					 data.get(position).getStops().get(0).getMarker().showInfoWindow();
					 
					 // Will have to change the panel to a GPS panel.... Possibly use a fragment
//					 maintile_activity.mLayout.setPanelState(PanelState.COLLAPSED);
					 
					 
					 maintile_activity.hideFavorites();
					 
					 // Used for MainActivity UI
//					 tempA.mLayout.setPanelState(PanelState.ANCHORED);
					 

					
				//data.remove(position);

		       // notifyDataSetChanged();
				}
					maintile_activity.showNavigation();
				
				}
			
		});
	    
	    holder.imgbtn_schedule.setOnClickListener(new OnClickListener() {
			
	    	// May need to reference the main_fragmentgroup somehow if I get an error
	    	
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = maintile_activity.manager.beginTransaction();
				
				ft.remove(routeActivity_favorites);
				ft.add(R.id.fragment_group_tile, maintile_activity.fragment_Schedule);
				ft.addToBackStack("Schedule");
				ft.commit();
				
				maintile_activity.fragment_Schedule.lv_arr = new String[maintile_activity.fragment_Routes.clickedRoute.getStops().size()];
				
				for(int i = 0; i < maintile_activity.fragment_Routes.clickedRoute.getStops().size(); i++){
					maintile_activity.fragment_Schedule.lv_arr[i] = maintile_activity.fragment_Routes.clickedRoute.getStops().get(i).getStopDescription();
				}
				
				maintile_activity.fragment_Schedule.tempRoute = maintile_activity.clickedRoute;
				maintile_activity.imgbtn_SchedulePopup.setVisibility(View.GONE);
			}
		});

		return vi;
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public Route getNavRoute() {
		return navRoute;
	}

	public void setNavRoute(Route navRoute) {
		this.navRoute = navRoute;
	}

}