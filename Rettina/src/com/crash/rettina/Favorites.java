/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * Favorites is a Fragment used to hold the Routes that the user deems as their favorite
 * The Favorites Fragment appears in the bottom half of the screen when the user presses
 * The "Favorites' Tab located in the top center of the menu bar
 * This Fragment uses a custom ListView 'CustomAdapter' to display all the favorite routes
 * Each row of the Favorites ListView includes the following: 
 * 
 * 		The route title - The name of the route
 * 		Checkbox - Used to tell if the route is currently being shown on the map or is hidden
 * 		Schedule icon - Can access that Route's schedule
 * 		Navigation icon - Can enter a 'Navigation Mode' that animates the camera to follow along the route
 * 		'X' icon - Used to remove the Route from the favorites
 * 
 * As stated before, the ListView is custom, so the view's functionality is handled by 'CustomAdapter'
 */

package com.crash.rettina;

import java.util.ArrayList;

import com.crash.customlist.CustomAdapter;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class Favorites extends Fragment{
	
	private ListView lv_favs;		// Displays the Favorite Routes... Uses a CustomAdapter to populate it
	
    public CustomAdapter adapter;	// Custom Adapter to create the custom ListView to display the 'Favorite Routes'
	public ArrayList<Route> selectedFavs = new ArrayList<Route>();	// Holds the Favorite Routes
	
	private Main activity;			// Establishes the link to the Main activity
	
    public Favorites fav = null;	// Holds the Favorites Fragment
    
	public TextView tv_emptyfavs;	// Used to display a default message that states if there currently are no favorites 
	public boolean are_there_favorites = false; // This boolean controls whether their are favorites displayed, or if the empty favorites message should be displayed

    private ImageButton imgbtn_closeFavorites;	// Used to close the 'Favorites' fragment


    // On creating the Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
  	      Bundle savedInstanceState) {
    	
    	// Setting the variable to hold the "Favorites" fragment
    	fav = this;		
    	
        Resources res = getResources();
        
        // Inflate Favorites fragment with favorites.xml
    	View view = inflater.inflate(R.layout.favorites,	
    	        container, false);
    	
    	// Setting the tv_emptyfavs resource as tv_emptyfavs
		tv_emptyfavs = (TextView) view.findViewById(R.id.tv_emptyfavs);	
		
		// Setting the Main activity
        activity = (Main) getActivity();	
        
        // If the amount of selected favorites is greater than 0, then there are favorites
        // So set 'are_there_favorites' = true
        if(selectedFavs.size() > 0){
			are_there_favorites = true;
        }
       
       // Setting the ListView 'lv_favs'  and ImageButton 'close_favorites' xml resources
	 lv_favs = (ListView) view.findViewById(R.id.lv_favorites);	
	 imgbtn_closeFavorites = (ImageButton) view.findViewById(R.id.imgbtn_closefav);

	 
	 
	  // Create Custom Adapter
     adapter = new CustomAdapter(fav.getActivity(), selectedFavs, res, fav);
     
     // Set the adapter to the ListView
     lv_favs.setAdapter( adapter );
     
     
     	// If favorites is empty, set the favorites to display the default empty favs message and hide the favs
		if(are_there_favorites == false){			
			tv_emptyfavs.setVisibility(View.VISIBLE);
		}
		// Otherwise, display the favorites and hide the default empty message
		else{
			tv_emptyfavs.setVisibility(View.GONE);
		}
     
     
     // Handles when imgbtn_closeFavorites (The 'x' in the corner) is clicked
	imgbtn_closeFavorites.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			// Remove the Favorites fragment and sets the current tab to a tab that the user can not see
			activity.hideFavorites();
		}
	});
     
     
	return view;
	
}	// End of OnCreate
    
    
    // Removes the route from the 'Favorites'
    public void removeRouteFromFav(Route r){
        selectedFavs.remove(r);
		 ((BaseAdapter)lv_favs.getAdapter()).notifyDataSetChanged();	// Updates the adapter
	}
	
    // Draws the route ontop the Google Map
	public void drawRoute(Route r){
		// Will draw the route if it has more than 0 points
		activity.drawRoute(r);
	}
	
	// Hides the route on the Google Map
	public void hideRoute(Route r){
		activity.hideRoute(r);
	}
	
	// Shows the Route on the Gogle Map
	public void showRoute(Route r){
		activity.showRoute(r);
	}
	
	// Shows the Stops on the Google Map
	public void showStops(Route r){
		activity.showStops(r);
	}
	
	// Hides the stops on the Google Map
	public void hideStops(Route r){
		activity.hideStops(r);
	}
}
