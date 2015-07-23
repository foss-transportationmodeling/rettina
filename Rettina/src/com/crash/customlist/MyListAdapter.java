package com.crash.customlist;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Text;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridLayout.Alignment;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crash.customlist.CustomAdapter.ViewHolder;
import com.crash.rettina.MainActivity;
import com.crash.rettina.Main_Tile;
import com.crash.rettina.R;
import com.crash.rettina.Schedule;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;
import com.google.android.gms.maps.CameraUpdateFactory;

public class MyListAdapter extends BaseAdapter {

	/**
	 * this is our own collection of data, can be anything we want it to be as
	 * long as we get the abstract methods implemented using this data and work
	 * on this data (see getter) you should be fine
	 */
	public ArrayList<Stop> mData;
	public ArrayList<Integer> routeTitles = new ArrayList<Integer>();
	public ArrayList<Route> routeHolder = new ArrayList<Route>();

	private Schedule mContext;
	private static LayoutInflater inflater = null;
	private Activity activity;
	
//	public ArrayList<Integer> dots = new ArrayList<Integer>();
	
	
	public Stop tempValue;
	private String routeTitle;

	public int numberOfRoutes = 0;
	public int sizeHolder = 0;

	/**
	 * our ctor for this adapter, we'll accept all the things we need here
	 *
	 * @param mData
	 */
	public MyListAdapter(Activity a, ArrayList<Stop> mData) {
		this.mData = mData;
		// this.mContext = context;
		activity = a;

		/*********** Layout inflator to call external xml layout () ***********/
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		tempValue = null;	
		
	}

	public ArrayList<Stop> getData() {
		return mData;
	}

	public void addRoute(Route r) {

		// If routeHolder does not already contain the route, add it to
		// routeHolder
		if (!routeHolder.contains(r)) {

			System.out.println("Adding Route: ");

			routeHolder.add(r);

//			 Sets the starting position for the stops on the Schedule listview
			r.setSched_StartPos(mData.size());
			
			routeTitles.add(mData.size()); // Used so the custom adapter can set the correct route title when displaying the stops

			// In order to get the Schedule fragment to display the route title
			// for the start of the list of stops
			// that appear under the route title, it must be added as a Stop
			// first
//			mData.add(mData.size(), new Stop(r.getRouteID(), r.getStops()
//					.get(0).getStopID(), r.getRouteTitle(), null));

			mData.addAll(r.getStops());

			// Sets the ending position for the stops on the Schedule listview
			r.setSched_EndPos(mData.size() - 1);
		}

	}

	public void removeRoute(Route r) {
		// If routeHolder does contain the route, remove it from routeHolder
		if (routeHolder.contains(r)) {
			System.out.println("Removing Route: " + r.getRouteTitle()); // Used so the custom adapter can set the correct route title when displaying the stops


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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
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

		// if (convertView == null) {

		/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
		vi = inflater.inflate(R.layout.custom_schedule, null);

		/****** View Holder Object to contain tabitem.xml file elements ******/

		holder = new ViewHolder();

		holder.tv_stopName = (TextView) vi.findViewById(R.id.tv_stopname);
		holder.tv_stopTime = (TextView) vi.findViewById(R.id.tv_stoptime2);
		holder.tv_stopTime2 = (TextView) vi.findViewById(R.id.tv_stoptime1);

		/************ Set holder with LayoutInflater ************/
		vi.setTag(holder);
		// }
		// else{
		// System.out.println("This Guy!");
		// holder = (ViewHolder) vi.getTag();
		// }

		if (mData.size() <= 0) {
			ViewHolder tempHolder = new ViewHolder();
			vi = new View(activity);

			vi.setTag(tempHolder);
		}

		else {

			tempValue = (Stop) mData.get(position);
			
//			dots.add((int)vi.getY());

			/************ Set Model values in Holder elements ***********/

//			 for (int i = 0; i < routeTitles.size(); i++) {
//			
//			
//			 if (position == routeTitles.get(i)) {
//			if (routeTitles.contains(position)) {
			
			if(position == 0){
				holder.tv_stopName.setText(mData.get(position)
						.getStopDescription());
				holder.tv_stopTime.setText("");
				holder.tv_stopTime2.setText("");
			}
			else{

				//
				//
				//
				System.out.println("Setting Route Title: "
						+ mData.get(position).getStopDescription());
				//
//				holder.tv_stopTime.setText("");
//				holder.tv_stopTime.setText(mData.get(position)
//						.getStopDescription());
				// holder.tv_stopTime.setTextSize(15);
				// holder.tv_stopTime.setFilters( new InputFilter[] { new
				// InputFilter.LengthFilter(10) }

//				holder.tv_stopTime.setTextColor(holder.tv_stopTime
//						.getResources().getColor(R.color.gray));

				holder.tv_stopName.setText(mData.get(position)
						.getStopDescription());
//				
			}
				
	//		}
			//
//			else {
//			
//				System.out.println("Position: " + position + ", Stop: "
//						+ tempValue.getStopDescription());
//
//
//
//			}
	//	}
			 }

		vi.setOnClickListener(new OnClickListener() {

			// Used for MainActivity UI
//			private MainActivity mainAct;
			
			// Used for Tile UI
			private Main_Tile mainAct;


			@Override
			public void onClick(View v) {

				if (mData.size() > 0) {

					// If the Latlng is not null, then go to that Stop. This is
					// because route titles have null latlng
					if (mData.get(position).getLatLng() != null) {
						System.out.println("Clicked: "
								+ mData.get(position).getStopDescription());
						mainAct = (Main_Tile) activity;

						mainAct.googleMap.animateCamera(CameraUpdateFactory
								.newLatLngZoom(mData.get(position).getLatLng(),
										16));
						mData.get(position).getMarker().showInfoWindow();
					} else {
						System.out.println("Clicked a route title!");

					}

				}
			}
		});
		//routeTitles.add(mData.size());
		
		return vi;
		
	}

	public void removeStops(ArrayList<Stop> tempStops, Route r) {
		// Remove the route title so when trying to add Routes, it can
		// accurately
		// tell which routes are already present in the schedule so it will not
		// make duplicate entries

		routeTitles.remove(r.getRouteTitle());
		// mData.remove(0); // This is probably wrong.... Need to find the route
		// to remove
		mData.remove(r.getRouteTitle()); // Try this???? Not working!
		mData.removeAll(tempStops);
		numberOfRoutes--;
		sizeHolder = mData.size();
	}


}
