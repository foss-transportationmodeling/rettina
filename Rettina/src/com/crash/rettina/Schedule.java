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


package com.crash.rettina;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VerticalSeekBar;

import com.crash.customlist.MyListAdapter;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;

public class Schedule extends Fragment {
	
	Main main_Tile;			// Used to establish communication with the Main activity
	Schedule sched;			// Holds the Schedule Fragment

	final int N = 10; 		// total number of textviews to add
	
	public String[] lv_arr;	// Used to set the dot positions and the height of the seekbar

	public Route tempRoute;	// Used to set the routes for the 'Schedule'

	public MyListAdapter adapter;	// The adapter used for the Listview

//	public VerticalSeekBar seekbar;	// The seekbar that shows the progress of the vehicle
	
	private ImageButton imgbtn_closeSchedule;	// The 'x' button to close the Schedule fragment
	public ListView lv_stopnames, lv_stoptimes;	// The Listviews for the stop names and stop times

	final TextView[] myTextViews = new TextView[N]; // create an empty array;
	public ArrayList<Stop> stops = new ArrayList<Stop>();

	private static final int UNBOUNDED = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	
	@SuppressLint("Instantiatable")
	public Schedule(Main mt) {
		main_Tile = mt;
		
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the Schedule fragment with customschedule.xml
		View view = inflater.inflate(R.layout.customschedule, container, false);
		sched = this;
		
		super.onCreate(savedInstanceState);
			
		stops = main_Tile.clickedRoute.getStops();
		
		
		// Create the seekbar 
//		seekbar = new VerticalSeekBar(sched);
		
		// Set the resource for the seekbar
//		seekbar = (VerticalSeekBar) view.findViewById(R.id.seekbar);
	
		// Set the resources for the listview and the close button
		lv_stopnames = (ListView) view.findViewById(R.id.lv_stopnames);
		imgbtn_closeSchedule = (ImageButton) view.findViewById(R.id.imgbtn_closeschedule);		

//		seekbar.setMinimumHeight(200 * stops.size());
		
		// Set the max for the seekbar as 100
//		seekbar.setMax(100);
//		
//		
//		// Handle the seekbar clicking.. Removed all clicking actions
//		// So the user can not adjust the seekbar
//		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//
//			}
//
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress,
//					boolean fromUser) {
//
//			}
//		});

		// Create the adapter and set the listview adapter
		adapter = new MyListAdapter(getActivity(), stops);
		lv_stopnames.setAdapter(adapter);

		
		// Set the dots
//		seekbar.setDots(getItemHeightofListView(lv_stopnames, lv_arr.length));
//		seekbar.setDotsDrawable(R.drawable.abc_btn_radio_to_on_mtrl_000);
		
				
		// If there is a temporary route, then call the setRoutes() method
		if (tempRoute != null) {
			setRoutes(tempRoute);
		}
		
		// Handles the close schedule button.... If this button is clicked,
		// then hide/remove the Schedule fragment
		imgbtn_closeSchedule.setOnClickListener(new View.OnClickListener() {
		
			 @Override
			 public void onClick(View v) {
			 main_Tile.hideSchedule();
			 }
		 });
		
			// If the Schedule fragment is visible, then handle the tabhost clicking
			if (sched.isVisible()) {
				
				// If the seraching tab is clicked
				main_Tile.tabhost.getTabWidget().getChildAt(0)
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// If the current tab is 0, then set the hidden tab as hidden
							// and remove the Schedule fragment
							if (main_Tile.tabhost.getCurrentTab() == 0
									&& v.getTag().equals(0)) {

								main_Tile.tabhost.setCurrentTab(3);
								
									FragmentTransaction ft = main_Tile.manager
											.beginTransaction();
									ft.remove(sched);
									ft.commit();
							}

						}
					});

				// If the 'Favorites' tab is clicked
			main_Tile.tabhost.getTabWidget().getChildAt(1)
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							
							// If the 'Schedule' is visible, then remove the schedule fragment
							if (sched.isVisible()) {
								FragmentTransaction ft = main_Tile.manager
										.beginTransaction();
								ft.remove(sched);
								ft.commit();
							}
						}
					});

			// If the 'Settings' tab is clicked
			main_Tile.tabhost.getTabWidget().getChildAt(2)
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							
							// If the 'Schedule' fragment is visible, then remove it
							if (sched.isVisible()) {
								FragmentTransaction ft = main_Tile.manager
										.beginTransaction();
								ft.remove(sched);
								ft.commit();
							}
						}
					});
		}
		return view;

	}

	// If the route titles in the adapter do not contain the route
	// given in the parameter, then add that route and update the adapter/listview
	public void setRoutes(Route r) {
		if (!adapter.routeTitles.contains(r.getRouteTitle())) {	
			ArrayList<Stop> tempStops = r.getStops();
			adapter.addRoute(r);
			((BaseAdapter) lv_stopnames.getAdapter()).notifyDataSetChanged();
		}

		else {
			System.out.println("Route is already added to the titles");
		}
	}

	// Remove the route from the listview
	public void removeRoutes(Route r) {
		ArrayList<Stop> tempStops = r.getStops();
		adapter.removeRoute(r);
		
		// Update the listview adapter for the Schedule Fragment
		((BaseAdapter) lv_stopnames.getAdapter()).notifyDataSetChanged();
	}

	// Get the listview view based on the position
	public View getViewByPosition(int pos, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition
				+ listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition) {
			return listView.getAdapter().getView(pos, null, listView);
		} else {
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

	// May be used in the future... Sets the position of the dots
	// Which indicate each stop position
//	public void seekSetDotPosition(ArrayList<Integer> dotArray) {
//		
//		int [] tempIntArray = new int[lv_arr.length];
//		for(int i = 0; i < dotArray.size(); i++){
//			tempIntArray[i] = dotArray.get(i);
//		}
//		
//		// Set the dots on the seekbar and set the resource for the dots
//		seekbar.setDots(tempIntArray);
//		seekbar.setDotsDrawable(R.drawable.abc_btn_switch_to_on_mtrl_00001);
//
//	}
//	
//	// To calculate the total height of all items in ListView call with items = adapter.getCount()
//	public int[] getItemHeightofListView(ListView listView, int items) {
//	    ListAdapter adapter = listView.getAdapter();
//
//	    int grossElementHeight = 0;
//	    int[] dotPositions = new int[lv_arr.length];
//	    
//	    for (int i = 0; i < items; i++) {
//	        View childView = adapter.getView(i, null, listView);
//	        childView.measure(UNBOUNDED, UNBOUNDED);
//	        
//	        grossElementHeight += childView.getMeasuredHeight();
//	        dotPositions[i] = grossElementHeight;
//	    }
//	    return dotPositions;
//	}

}
