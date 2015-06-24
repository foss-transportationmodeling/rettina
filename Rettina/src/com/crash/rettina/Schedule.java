/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * Schedule creates the right fragment and is attached to the MainActivity class.  Schedule simply displays all the stops as well
 * as scheduled times.  There are plans to develop real time expected bus arrival times for each stop
 */

/*
 * To Do
 * 1. Fix up bugs... Lots of crashes/things do not update correctly
 * 2. Route names do not display correctly... When routes are removed or new ones are created, they are in the wrong spot etc.
 */


package com.crash.rettina;

import java.util.ArrayList;

import com.crash.customlist.MyListAdapter;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Schedule extends Fragment {
	public ArrayList<Stop> stops = new ArrayList<Stop>();  
	//public ArrayList<String> stopNames = new ArrayList<String>();  

	  MyListAdapter adapter;
	  public ListView lv;
	  MainActivity a;
	  Schedule sched;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.schedule,
	        container, false);

	    lv  = (ListView) view.findViewById(R.id.lv_schedule);	  

	    sched = this;
        
        adapter = new MyListAdapter(sched.getActivity(), stops);    
    	lv.setAdapter(adapter);
    	
    	 
	    return view;
	    
	  }
	
	
	public void setRoutes(Route r){
		
		// If the adapter does not contain the selected route title, add the stops
		if(!adapter.routeTitles.contains(r.getRouteTitle())){
			ArrayList<Stop> tempStops = r.getStops();

			//stops.addAll(tempStops);
			
			//System.out.println("Stop size is: " + stops.size() + ", Route name is " + r.getRouteTitle());

			//adapter.addRoutes(stops, r.getRouteTitle());  // Trying this call instead
			
			adapter.addRoute(r);
		    
		
			 ((BaseAdapter)lv.getAdapter()).notifyDataSetChanged();
 
		    System.out.println("Route: " + adapter.mData.get(0).getStopDescription());
		    System.out.println("Route Data: " + adapter.getData().size());
			}

		else{
		    System.out.println("Route is already added to the titles");

		}
	
		
	}
	public void removeRoutes(Route r){
		ArrayList<Stop> tempStops = r.getStops();
		
	//	adapter.removeStops(tempStops, r);
		
		
		adapter.removeRoute(r);
		
		 ((BaseAdapter)lv.getAdapter()).notifyDataSetChanged();
		 System.out.println("Removing Route, Route Data: " + adapter.getData().size());


	}
	
	public View getViewByPosition(int pos, ListView listView) {
	    final int firstListItemPosition = listView.getFirstVisiblePosition();
	    final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

	    if (pos < firstListItemPosition || pos > lastListItemPosition ) {
	        return listView.getAdapter().getView(pos, null, listView);
	    } else {
	        final int childIndex = pos - firstListItemPosition;
	        return listView.getChildAt(childIndex);
	    }
	}
}

