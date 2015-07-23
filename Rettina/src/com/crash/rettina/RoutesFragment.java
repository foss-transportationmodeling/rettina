package com.crash.rettina;

import java.util.ArrayList;

import com.crash.routeinfo.Route;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class RoutesFragment extends Fragment {
	
	public Main_Tile main_tile;
	public GridView gridview;
	public ArrayAdapter<String> adapter;
	public Route clickedRoute;

	public ArrayList<String> routeTitles = new ArrayList<String>();
	
	// Constructor
	public RoutesFragment(Main_Tile mt){
		main_tile = mt;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		    View view = inflater.inflate(R.layout.routesfragment,
		        container, false);

		    
			gridview = (GridView) view.findViewById(R.id.gridView1);
			
			routeTitles = main_tile.routeTitles;
			
			
			adapter = new ArrayAdapter<String>(this.getActivity(),
					android.R.layout.simple_list_item_1, routeTitles);

			gridview.setAdapter(adapter);

			// When a tile is clicked, show the route preview and stops
			
			gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

				public boolean onItemLongClick(AdapterView<?> parent, View v,
						int position, long id) {
					
					// If the favorites does not already contain this route, then add to the favorites
					if(!main_tile.fragment_Favorites.selectedFavs.contains(main_tile.routes.get(position))){
					
//					if(main_tile.routes.get(position).getStops().size() < 1){
//						main_tile.getStops(main_tile.routes.get(position));
//					}
//					
//					
//					// Work on hiding the polyline when another item is clicked!!!
//					for(int i = 0; i < main_tile.routes.size(); i++){
//						if(main_tile.routes.get(i).getStops() != null){
//							main_tile.routes.get(i).hideStops(main_tile.googleMap);
//						
//						//	if(main_tile.routes.get(i).getPolyLine() != null){
//								main_tile.routes.get(i).hidePolyLine();
//						//	}
//						}
//					}
					
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

		    
			return view;

	}

}
