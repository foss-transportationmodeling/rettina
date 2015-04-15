package com.crash.rettina;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.crash.customlist.CustomAdapter;
import com.crash.customlist.ListModel;
import com.crash.routeinfo.Route;

public class RouteMenu extends Fragment {
	
	ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<Route> selectedFavs = new ArrayList<Route>();


	MainActivity a;
	
    CustomAdapter adapter;
    public  RouteMenu fav = null;
    private  ArrayList<Route> CustomListViewValuesArr = new ArrayList<Route>();
    
    ListView lv;
    GetJSON json;

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
       json = new GetJSON(a, a.googleMap,this);
	    json.execute();

       
	    final ListView lv_favs = (ListView) view.findViewById(R.id.listview_favs);	
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
		         Route clickedRoute = (Route)a.routes.get(arg2);

				 //String selectedRoute = data;
				 				 	
//				 selectedFavs.add(Integer.toString(clickedRoute.getRouteID()));
		         selectedFavs.add(clickedRoute);
		         
		         
				// CustomListViewValuesArr.add(clickedRoute);
				 
				 ((BaseAdapter)lv_favs.getAdapter()).notifyDataSetChanged();
				 
				 
//				 a.drawRoute(a.);
			}
		});

		
        a.getRoutes();

	    	      
	    return view;
	    
	  }
	
	public void drawRoute(Route r){
		a.drawRoute(r);
	}
    
    public void setRouteData()
    {
    	System.out.println("Setting Data!!!");
    	System.out.println("Route Size: " + a.routes.size());

    	listItems.clear();
        for(int i = 0; i < a.routes.size(); i++){
            
        	listItems.add(Integer.toString(a.routes.get(i).getRouteID()));
        	
        }
        
		 ((BaseAdapter)lv.getAdapter()).notifyDataSetChanged();
         
    }
	
}
