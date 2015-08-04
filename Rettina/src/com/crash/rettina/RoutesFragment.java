/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * RoutesFragment is a Fragment used to hold and search for all the routes that fall within
 * the GPS coordinates. RoutesFragment pops up in the bottom half of the screen and uses a
 * gridview to display all of the found routes.  The routes shown in the gridview can be clicked
 * to display a route preview or pressed to add the route to the 'Favorites' fragment.  When the route
 * is pressed or clicked, it will display the Stops and polyline on the map.  The "RoutesFragment" can
 * be closed by clicking the 'x' shown in the top left of the corner
 */

package com.crash.rettina;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.crash.routeinfo.Route;

public class RoutesFragment extends Fragment {
	
	public Main main_tile;				// Allows for communication to the Main fragment
	public GridView gridview;			// Used to display the found routes in the bottom half of the screen
	
	public MyAdapter adapter;  			// Applied to gridview... Allows for a custom gridview

	public ImageButton imgbtn_closeSearch;	// When clicked, it closes the 'routesfragment'
	public Route clickedRoute;				// Holds the route that was most recently clicked by the user
	public Integer busIcon;					// Holds the bus icon for the gridview
	public TextView tv_searching;			// Used to display the default searching for routes message
	public boolean is_seaching = false;		// Boolean used to check if the user is currently searching for routes or not
	
	public ArrayList<String> routeTitles = new ArrayList<String>();	// Holds all the routetitles for the routes that were found

	// Constructor
	public RoutesFragment(Main mt){
		main_tile = mt;
	}

	// When the Fragment is created
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		
		//	Uses 'routesfragment' as the layout
		    View view = inflater.inflate(R.layout.routesfragment,
		        container, false);

		    // Holds the bus icon for the gridview
		    Integer busIcon = R.drawable.bus;
		    
		    // Holds the gridview, the image button to close the fragment, and the default searching text message
			gridview = (GridView) view.findViewById(R.id.gridView1);
			imgbtn_closeSearch = (ImageButton) view.findViewById(R.id.imgbtn_closeroute);
			tv_searching = (TextView) view.findViewById(R.id.tv_searching);
			
			// Holds the route titles which are saved in the Main fragment
			routeTitles = main_tile.routeTitles;
			
			// Used on the gridview to make a custom gridview
			adapter = new MyAdapter(this.getActivity());
			
			// Setting the adapter and the number of columns
			gridview.setAdapter(adapter);
	        gridview.setNumColumns(3);
	        
	        // If the user is currently seraching, then show the 'routesfragment' fragment otherwise, hide it
	        if(is_seaching == true){
				tv_searching.setVisibility(View.VISIBLE);
	        }
	        else{
	        	tv_searching.setVisibility(View.GONE);
	        }

	        
			// When a tile is pressed, show the polyline, stops, and add the route to the favorites
			gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

				public boolean onItemLongClick(AdapterView<?> parent, View v,
						int position, long id) {
					
					// If the favorites does not already contain this route, then add to the favorites
					if(!main_tile.fragment_Favorites.selectedFavs.contains(main_tile.routes.get(position))){
					
						// If there are not any routes, get the route stops
						if(main_tile.routes.get(position).getStops().size() < 1){
							main_tile.getStops(main_tile.routes.get(position));
						}
						
						// Loop through all the routes 
						for(int i = 0; i < main_tile.routes.size(); i++){
							// Hide all the stops and polylines for all the routes
							if(main_tile.routes.get(i).getStops().size() > 0){
								main_tile.routes.get(i).hideStops(main_tile.googleMap);
								main_tile.routes.get(i).hidePolyLine();
							}
						}
					
					main_tile.showStops(main_tile.routes.get(position)); // Might error out since it will be waiting to get the stops from the previous call first
					main_tile.getShape(main_tile.routes.get(position));
					
					// This loops through all the selected favorites and sets them to unclicked
					// The last added route will be the only clicked route when going to favorites.. If the user wants to display
					// More routes on the map, they will have to go through the favorites and click that way
					for(int i = 0; i < main_tile.fragment_Favorites.selectedFavs.size(); i++){
						main_tile.fragment_Favorites.selectedFavs.get(i).setClicked(false);
					}
					
					// Set the clicked route as 'clicked'
					main_tile.routes.get(position).setClicked(true);
					clickedRoute = main_tile.routes.get(position);
					main_tile.clickedRoute = clickedRoute;
					
					// Set the tripID for the clickedRoute
					clickedRoute.setTripIDs(main_tile.routes.get(position).getTripIDs());
					
					// Add the clickedRoute to the "Favorites"
					main_tile.fragment_Favorites.selectedFavs.add(clickedRoute);
					
					
					// If there are more than 0 favorites, set them as true
					if(main_tile.fragment_Favorites.selectedFavs.size() > 0){
						main_tile.fragment_Favorites.are_there_favorites = true;	
					}
					// Otherwise set that there are no favorites
					else{
						main_tile.fragment_Favorites.are_there_favorites = false;
					}
					
					// Clear the stops from 'Schedule' fragment
					main_tile.fragment_Schedule.stops.clear();
					
					// Make this show only when a route is being previewed on the map or is selected
					main_tile.imgbtn_SchedulePopup.setVisibility(View.VISIBLE);
					
				}
					else{
						System.out.println("Route is already in favorites!");
					}
					
					return true;
				}
			});
			
			// When a tile is clicked, preview the route by showing the polyline and stops on the map
			gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {

					// If there are less than 0 stops, then make the method call to get the Stops
					if(main_tile.routes.get(position).getStops().size() < 1){
						main_tile.getStops(main_tile.routes.get(position));	
					}
					
					// For the entire size of the routes
					for(int i = 0; i < main_tile.routes.size(); i++){
						// If the route currently does not have stops, then get the stops and hide the stops/polyline
						if(main_tile.routes.get(i).getStops().size() > 0){
							main_tile.routes.get(i).hideStops(main_tile.googleMap);
							main_tile.routes.get(i).hidePolyLine();

						}
					}
					
					// Show the stops and get the polyline for the clicked route
					main_tile.showStops(main_tile.routes.get(position)); 
					main_tile.getShape(main_tile.routes.get(position));
				}
			});

			// Click listener for 'x' button ontop of the Search Fragment... Calls the method exitSearch
			// On the Main_Tile fragment since Main_Tile is the Activity that the fragments run off of
			imgbtn_closeSearch.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					main_tile.exitSearch();
				}
			});
		    
			return view;
	}
	
	// If the user is searching, show the searching text and hide the grideview... Otherwise, hide text
	public void searching(boolean search){
		if(search == true){		
			is_seaching = true;		
		}
		else{		
			is_seaching = false;
		}
		
	}
	
	// Custom adapter used to make a custom gridview
	public class MyAdapter extends BaseAdapter {
		 
        private Context mContext;
 
        public MyAdapter(Context c) {
            mContext = c;
        }
 
        @Override
        public int getCount() {
            return routeTitles.size();
        }
 
        @Override
        public Object getItem(int arg0) {
            return routeTitles.get(arg0);
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
                grid=inflater.inflate(R.layout.custom_gridview, parent, false);
            }
            else{
                grid = (View) convertView;
            }
            
            // Assign the views for each Gridview item
            TextView txtView = (TextView)grid.findViewById(R.id.tv_gridviewtitle);
            txtView.setText(routeTitles.get(position));
            
            ImageView imageView = (ImageView)grid.findViewById(R.id.imgview_grid);
            imageView.setImageResource(R.drawable.bus);
            
            return grid;
        }

        }
    }


