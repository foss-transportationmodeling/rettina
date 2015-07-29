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
	
	public Main_Tile main_tile;
	public GridView gridview;
	
	//public ArrayAdapter<String> adapter; // Used for old style
	public MyAdapter adapter;  

	public ImageButton imgbtn_closeSearch;	
	public Route clickedRoute;
	public ArrayList<String> routeTitles = new ArrayList<String>();
	public Integer busIcon;
	public TextView tv_searching;	// Used to display the default searching for routes message
	public boolean is_seaching = false;
	
		
	// Constructor
	public RoutesFragment(Main_Tile mt){
		main_tile = mt;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		    View view = inflater.inflate(R.layout.routesfragment,
		        container, false);

		    Integer busIcon = R.drawable.bus;
		    
			gridview = (GridView) view.findViewById(R.id.gridView1);
			imgbtn_closeSearch = (ImageButton) view.findViewById(R.id.imgbtn_closeroute);
			
			tv_searching = (TextView) view.findViewById(R.id.tv_searching);
			
			
			routeTitles = main_tile.routeTitles;
			
			adapter = new MyAdapter(this.getActivity());
			
			gridview.setAdapter(adapter);
	        gridview.setNumColumns(3);
	        
	        if(is_seaching == true){

				tv_searching.setVisibility(View.VISIBLE);
//				gridview.setVisibility(View.GONE);
	        }
	        else{
	        	tv_searching.setVisibility(View.GONE);
//				gridview.setVisibility(View.VISIBLE);
	        }

	        
			// When a tile is clicked, show the route preview and stops
			
			gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

				public boolean onItemLongClick(AdapterView<?> parent, View v,
						int position, long id) {
					
					// If the favorites does not already contain this route, then add to the favorites
					if(!main_tile.fragment_Favorites.selectedFavs.contains(main_tile.routes.get(position))){
					
						// New attempt at hiding the polyline
						if(main_tile.routes.get(position).getStops().size() < 1){
							main_tile.getStops(main_tile.routes.get(position));
							
						}
						
						for(int i = 0; i < main_tile.routes.size(); i++){
							if(main_tile.routes.get(i).getStops().size() > 0){
								main_tile.routes.get(i).hideStops(main_tile.googleMap);
								main_tile.routes.get(i).hidePolyLine();
								//main_tile.hideRoute(main_tile.routes.get(i));

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
					
					main_tile.routes.get(position).setClicked(true);
					clickedRoute = main_tile.routes.get(position);
					
					main_tile.clickedRoute = clickedRoute;
					
					// Try this to fix the Null pointer on the Sharing Activity
					clickedRoute.setTripIDs(main_tile.routes.get(position).getTripIDs());
					
					main_tile.fragment_Favorites.selectedFavs.add(clickedRoute);
					
					
					
					// Since there are favorites, display the actual favorites instead of the default empty message
					if(main_tile.fragment_Favorites.selectedFavs.size() > 0){
						main_tile.fragment_Favorites.are_there_favorites = true;	
//					main_tile.fragment_Favorites.lv_favs.setVisibility(View.VISIBLE);
//					main_tile.fragment_Favorites.tv_emptyfavs.setVisibility(View.GONE);
					}
					else{
						main_tile.fragment_Favorites.are_there_favorites = false;
					}
					
					main_tile.fragment_Schedule.stops.clear();
					
					
					// Not route stops yet
					
//					System.out.println("Error Checking: Route Stops are: ");
//					clickedRoute.printRoute();

					
					// Commenting to test if this fixes the schedule problem
				 if(clickedRoute.getStops().size() > 0){
						//System.out.println("Trying to set schedule!");
					//main_tile.fragment_Schedule.tempRoute = clickedRoute;
					
					
						// I think this is the problem line... the stops are being found in the background during the shape connect so the clickedroute
						// Is actually null when this is called.... Then the postexecute finishes 
				//	main_tile.fragment_Schedule.setRoutes(clickedRoute);

					
					}
				
					
					// Make this show only when a route is being previewed on the map or is selected
					main_tile.imgbtn_SchedulePopup.setVisibility(View.VISIBLE);
					
				}
					else{
						System.out.println("Route is already in favorites!");
					}
					return true;
				}
			});
			
			// When a tile is held, add the route to the favorites
			gridview.setOnItemClickListener(new OnItemClickListener() {

				// Need to make this so it does not interfere with the clicking of a route...
				// If the route has already been clicked, it should reuse that route information and simply add it to the favorites
				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {

					
					if(main_tile.routes.get(position).getStops().size() < 1){
						main_tile.getStops(main_tile.routes.get(position));
						
					}
					
					for(int i = 0; i < main_tile.routes.size(); i++){
						if(main_tile.routes.get(i).getStops().size() > 0){
							main_tile.routes.get(i).hideStops(main_tile.googleMap);
							main_tile.routes.get(i).hidePolyLine();

						}
					}
					
					main_tile.showStops(main_tile.routes.get(position)); // Might error out since it will be waiting to get the stops from the previous call first
					main_tile.getShape(main_tile.routes.get(position));
							
					// For now clear the stops so only the last clicked route will be displayed... Will need to change this so
					// Multiple routes can be supported when Favorites is used
					
					//fragment_Favorites.selectedFavs.add(routes.get(position));
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
            // This may cause an error because if there aren't routes, then it won't initialize the resources behind the views
            TextView txtView = (TextView)grid.findViewById(R.id.tv_gridviewtitle);
            txtView.setText(routeTitles.get(position));
            
            ImageView imageView = (ImageView)grid.findViewById(R.id.imgview_grid);
            imageView.setImageResource(R.drawable.bus);
            
            return grid;
        }

        }
    }


