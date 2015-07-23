/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * RotueMenu makes up the left fragment and is tied to the MainActivity Class. It uses a regular listview adapter to draw
 * the route listview.  A Tabhost is used to toggle between "Favorites" and "Routes" listviews.  The "Favorites" listview
 * uses the CustomAdapter class in order to draw the listview....
 */

/*
 * To Do
 * 1. Lots of bugs when selecting and removing routes
 */

package com.crash.rettina;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.crash.connection.Connect;
import com.crash.customlist.CustomAdapter;
import com.crash.customlist.ListModel;
import com.crash.routeinfo.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

public class RouteMenu extends Fragment {
	
	ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<Route> selectedFavs = new ArrayList<Route>();
	ListView lv_favs;


	MainActivity a;
	
    CustomAdapter adapter;
    public  RouteMenu fav = null;
    private  ArrayList<Route> CustomListViewValuesArr = new ArrayList<Route>();
    
    ListView lv;

	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.routemenu,
	        container, false);
	    
	    
	    
	    TabHost tabhost = (TabHost) view.findViewById(android.R.id.tabhost);
	    tabhost.setup();
	    TabSpec ts = tabhost.newTabSpec("tag1"); 
	    ts.setContent(R.id.tab1);
	    ts.setIndicator("Search");
	    tabhost.addTab(ts);

	    ts = tabhost.newTabSpec("tag2"); 
	    ts.setContent(R.id.tab2);
	    ts.setIndicator("Favorites");  
	    tabhost.addTab(ts);
	    
	    lv  = (ListView) view.findViewById(R.id.listview_routefilter);	  
	    
	    fav = this;
	    
        Resources res = getResources();
        
       a = (MainActivity) getActivity();
       
   		listItems.add("My Location");
       
//       json = new GetJSON(a, a.googleMap,this);
//	   json.execute();

       
	    lv_favs = (ListView) view.findViewById(R.id.listview_favs);	
	    /**************** Create Custom Adapter *********/
        adapter=new CustomAdapter(fav.getActivity(), selectedFavs,res, fav);
        lv_favs.setAdapter( adapter );

    	
    	lv.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
	    	    android.R.layout.simple_list_item_1 , listItems));
    	
    	lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				 //TextView c = (TextView) arg1.findViewById(R.id.listview_routefilter);
				
				// Take the clicked route from the route search and add it to the favorites.. +1 is used because
				// "My location" is added to spot 0 of the listview
				
				// If "My Location" is clicked, zoom to the user's location as long as it is not null
				if(arg2 < 1){
		         		 if(a.lat_Lng != null){
		        		 a.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(a.lat_Lng, 15));
		         		 }
		         
				}
		         else{
		         Route clickedRoute = (Route)a.routes.get(arg2 - 1);
				
				 // If the clickedRoute is not already in the favorites, then get the stops and add it to the "favorites"
		         if(!selectedFavs.contains(clickedRoute)){

		         
		        	 if(clickedRoute.getStops().size() == 0){
						System.out.println("Getting Stops");
						a.getStops(clickedRoute);
		        	 }
		         
				 				 	
//				 selectedFavs.add(Integer.toString(clickedRoute.getRouteID()));     
		         selectedFavs.add(clickedRoute);
		         
		         }
		         
		         // Else the route is already in the favorites so report the error!
		         else{
		        	 System.out.println(clickedRoute.getRouteTitle() + " Already in Favorites!");
		         }
		         
		         
		         
		         
				// CustomListViewValuesArr.add(clickedRoute);
				 
				 ((BaseAdapter)lv_favs.getAdapter()).notifyDataSetChanged();
				 
				 
//				 a.drawRoute(clickedRoute);
		         }
			}
		});
    	    	
    			
        a.getRoutes();

	    	      
	    return view;
	    
	  }
	
	public void removeRouteFromFav(Route r){
        selectedFavs.remove(r);
		 ((BaseAdapter)lv_favs.getAdapter()).notifyDataSetChanged();
		 System.out.println("Route Removed from favs!");

	}
	
	public void drawRoute(Route r){
		// Will draw the route if it has more than 0 points
		a.drawRoute(r);
		
	}
	
	public void hideRoute(Route r){
		a.hideRoute(r);
	}
	
	public void showRoute(Route r){
		a.showRoute(r);
	}
	
	public void showStops(Route r){
		a.showStops(r);
	}
	
	public void hideStops(Route r){
		a.hideStops(r);
	}
	
    
    public void setRouteData()
    {
    	//System.out.println("Setting Data!!!");
    	//System.out.println("Route Size: " + a.routes.size());

    	listItems.clear();
    	listItems.add("My Location");
        for(int i = 0; i < a.routes.size(); i++){
            
        	listItems.add(a.routes.get(i).getRouteTitle());
        	
        }
        
		 ((BaseAdapter)lv.getAdapter()).notifyDataSetChanged();
         
    }
	
}
