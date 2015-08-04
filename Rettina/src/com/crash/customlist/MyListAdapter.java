/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * MyListAdapter is used to create a custom ListView for the 'Schedule' Fragment
 * It holds the Route Title, Stop Description, Arrival, and Departure times
 */

package com.crash.customlist;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.crash.rettina.R;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;

public class MyListAdapter extends BaseAdapter {

	public ArrayList<Stop> mData;
	public ArrayList<Integer> routeTitles = new ArrayList<Integer>();
	public ArrayList<Route> routeHolder = new ArrayList<Route>();
	
	private static LayoutInflater inflater = null;
	private Activity activity;
			
	private String routeTitle;

	public int numberOfRoutes = 0;
	public int sizeHolder = 0;


	// Constructor
	public MyListAdapter(Activity a, ArrayList<Stop> mData) {
		this.mData = mData;
		activity = a;

		// Layout inflater which uses external XML
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// Returns the ArrayList that holds the Stops
	public ArrayList<Stop> getData() {
		return mData;
	}

	// Add a Route
	public void addRoute(Route r) {

		// If routeHolder does not already contain the route, add it to routeHolder
		if (!routeHolder.contains(r)) {

			routeHolder.add(r);		// Add route to routeHolder

			
//			 Sets the starting position for the stops on the Schedule ListView
			r.setSched_StartPos(mData.size());
			
			// Used so the custom adapter can set the correct route title when displaying the stops
			routeTitles.add(mData.size()); 

			// In order to get the Schedule fragment to display the route title
			// for the start of the list of stops that appear under the route title, 
			// it must be added as a Stop first
			
			// Adding the route title first the first position
			if(mData.size() < 1){
			mData.add(new Stop(r.getRouteID(), r.getStops().get(0).getStopID(), r.getRouteTitle(), r.getStops().get(0).getLatLng()));
			}

			// Add all the Stops that correspond to that Route to mData
			mData.addAll(r.getStops());

			// Sets the ending position for the stops on the Schedule listview
			r.setSched_EndPos(mData.size() - 1);
		}

	}

	// Remove a route
	public void removeRoute(Route r) {
		
		// If routeHolder does contain the route, remove it from routeHolder
		if (routeHolder.contains(r)) {

			routeHolder.remove(r);
			routeTitles.remove(routeTitles.indexOf(r.getSched_StartPos()));  

			// Remove the route title from mData by using the starting position,
			// and remove all the stops
			mData.remove(r.getSched_StartPos());
			mData.removeAll(r.getStops());

		}
	}

	// Method to set the route title, will place the route title at the end of
	// the mData.. So if there are 0 routes in mData,
	// It will place the routetitle at position 0. If more than 1 route, it will
	// place the route title at the end of the previous route
	// Denoting a new set of stops
	public void setRouteTitle() {
		for (int i = 0; i < mData.size(); i++) {
			mData.get(i).setRouteTitle(routeTitle);
		}
	}

	// Gets the size of stops that are currently loaded into mData
	@Override
	public int getCount() {
		if (mData.size() <= 0)
			return 1;
		return mData.size();

	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	// Create a holder Class to contain inflated xml file elements
	public static class ViewHolder {
		public TextView tv_stopName;
		public TextView tv_stopTime;
		public TextView tv_stopTime2;

	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		View vi = convertView;
		final ViewHolder holder;

		// Inflate custom_schedule.xml file for each row ( Defined below )
		vi = inflater.inflate(R.layout.custom_schedule, null);

		// View Holder Object to contain tabitem.xml file elements

		holder = new ViewHolder();

		holder.tv_stopName = (TextView) vi.findViewById(R.id.tv_stopname);	// Holds the Stop name
		holder.tv_stopTime = (TextView) vi.findViewById(R.id.tv_stoptime2);	// Holds the departure time
		holder.tv_stopTime2 = (TextView) vi.findViewById(R.id.tv_stoptime1);// Holds the arrival time

		//Set holder with LayoutInflater
		vi.setTag(holder);

		// If size of Stops is less than 1, then set the View to the activity
		if (mData.size() <= 0) {
			ViewHolder tempHolder = new ViewHolder();
			vi = new View(activity);
			vi.setTag(tempHolder);
		}
		// Otherwise, populate the views
		else {

			// If it's the first position, then set that equal to the RouteTitle (It's saved as a Stop
			// with the Stop Description equal to the Route Title... (Only way I could get it to show the route title easily)
			if(position == 0){
				holder.tv_stopName.setText(mData.get(position)	// Set the Route Title
						.getStopDescription());	
				holder.tv_stopTime.setText("");					// Set Departure time as ''
				holder.tv_stopTime2.setText("");				// Set arrival time as ''
			}
			// Otherwise, set all the other views as normal stops
			else{
				
				holder.tv_stopTime.setTextColor(holder.tv_stopTime					// Set the StopTime color to Gray
						.getResources().getColor(R.color.gray));
				holder.tv_stopTime2.setText(mData.get(position).getArrival_time());	// Set the Arrival Time	
				holder.tv_stopTime.setText(mData.get(position).getDeparture_time());// Set the Departure Time
				holder.tv_stopName.setText(mData.get(position)						// Set the Stop Name
						.getStopDescription());				
			}
				
		}
	
		return vi;
	}
	
	// Remove the Stops
	public void removeStops(ArrayList<Stop> tempStops, Route r) {
		// Remove the route title so when trying to add Routes, it can
		// accurately tell which routes are already present in the schedule
		// so it will not make duplicate entries

		routeTitles.remove(r.getRouteTitle());		// Remove the Route Title from 'routeTitles'
		mData.remove(r.getRouteTitle());			// Remove the Route Title from 'mData'
		mData.removeAll(tempStops);					// Remove all the Stops from 'mData'
		numberOfRoutes--;							// Decrement the number of routes
		sizeHolder = mData.size();					// Set the size equal to the new size
	}


}
