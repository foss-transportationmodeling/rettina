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

import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VerticalSeekBar;

import com.crash.customlist.MyListAdapter;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;

public class Schedule extends Fragment {
	public ArrayList<Stop> stops = new ArrayList<Stop>();
	// public ArrayList<String> stopNames = new ArrayList<String>();

	public Route tempRoute;

	public MyListAdapter adapter;
	// public ListView lv;

//	private TextView tv;
	public VerticalSeekBar seekbar;

	final int N = 10; // total number of textviews to add

	final TextView[] myTextViews = new TextView[N]; // create an empty array;
	public String[] lv_arr;

	Main_Tile main_Tile;
	public ListView lv_stopnames, lv_stoptimes;

	Schedule sched;
	private static final int UNBOUNDED = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);


	public Schedule(Main_Tile mt) {
		main_Tile = mt;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// View view = inflater.inflate(R.layout.schedule, container, false);

		// VerticalSeekBar verticalSeekBar = new VerticalSeekBar(main_Tile);

		View view = inflater.inflate(R.layout.customschedule, container, false);
		sched = this;

		// RelativeLayout rl = (RelativeLayout)
		// view.findViewById(R.id.verticalseek_relativelayout);

		super.onCreate(savedInstanceState);
//		tv = (TextView) view.findViewById(R.id.tv_value);
		
		seekbar = new VerticalSeekBar(sched);
		seekbar = (VerticalSeekBar) view.findViewById(R.id.seekbar);
		// tv.setText(String.valueOf(seekbar.getProgress()) + "/" +
		// String.valueOf(seekbar.getMax()));

		lv_stopnames = (ListView) view.findViewById(R.id.lv_stopnames);
//		lv_stoptimes = (ListView) view.findViewById(R.id.lv_stoptimes);

		seekbar.setMax(100);
//		 seekbar.setDots(new int[] {0, 150, 300, 450});


		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// tv.setText(String.valueOf(seekbar.getProgress()) + "/" +
				// String.valueOf(seekbar.getMax()));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub

			}
		});

		adapter = new MyListAdapter(getActivity(), stops);
		
//		lv_stopnames.setAdapter(new ArrayAdapter<String>(sched.getActivity(),
//				R.layout.schedstopcustomlist, lv_arr));
		
//		lv_stoptimes.setAdapter(adapter);
		
		lv_stopnames.setAdapter(adapter);

		
		// Set the dots
//		seekbar.setDots(getItemHeightofListView(lv_stopnames, lv_arr.length));
//		seekbar.setDotsDrawable(R.drawable.abc_btn_radio_to_on_mtrl_000);
		
				

		// Commenting out to see if this fixes the schedule issue
		if (tempRoute != null) {
			setRoutes(tempRoute);
		}
		

		

		// imgbtn_closesched.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		//
		// // FragmentTransaction ft = main_Tile.manager.beginTransaction();
		// // ft.remove(sched);
		// // ft.commit();
		//
		// // main_Tile.manager.beginTransaction().remove(sched).commit();
		//
		// main_Tile.hideSchedule();
		//
		// }
		// });
		
//		SeekSetDotPosition(lv_arr.length);
		
		if (sched.isVisible()) {

			main_Tile.tabhost.getTabWidget().getChildAt(0)
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							System.out.println("Clicking search!");

							if (main_Tile.tabhost.getCurrentTab() == 0
									&& v.getTag().equals(0)) {

								// Set for new Nav Fragment
								// main_Tile.mLayout.setPanelState(PanelState.COLLAPSED);

								main_Tile.tabhost.setCurrentTab(3);

								// if(clickedRoute != null &&
								// clickedRoute.getPolyLine().isVisible()){
								// hideStops(clickedRoute);
								// hideRoute(clickedRoute);
								// }
								//
//								if (sched.isVisible()) {
									FragmentTransaction ft = main_Tile.manager
											.beginTransaction();
									ft.remove(sched);
									ft.commit();
									
									
//								}

							}
							// else{
							//
							// if(main_Tile.tabhost.getCurrentTab() == 1 &&
							// main_Tile.fragment_Favorites.isVisible()){
							// // Need to make it so it only connects when
							// unique routes will be shown, otherwise, it will
							// just be
							// // Doing more work by getting the same routes
							// over and over whenver search is clicked
							//
							// FragmentTransaction ft =
							// manager.beginTransaction();
							// // ft.replace(R.id.main_fragmentgroup,
							// (Fragment)fm);
							// ft.remove(fragment_Favorites);
							// ft.commit();
							// }

							// main_Tile.mLayout.setPanelState(PanelState.ANCHORED);
							// connect = new Connect(main, googleMap);
							// connect.execute();
							// main_Tile.tabhost.setCurrentTab(0);
							// }

						}
					});

			main_Tile.tabhost.getTabWidget().getChildAt(1)
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							// if(main_Tile.tabhost.getCurrentTab() == 1 &&
							// v.getTag().equals(1)){
							// main_Tile.mLayout.setPanelState(PanelState.COLLAPSED);
							// FragmentTransaction ft =
							// manager.beginTransaction();
							// // ft.replace(R.id.main_fragmentgroup,
							// (Fragment)fm);
							// ft.remove(fragment_Favorites);
							// ft.commit();
							// main_Tile.tabhost.setCurrentTab(3);
							// }
							// else{
							// FragmentTransaction ft =
							// manager.beginTransaction();
							// ft.add(R.id.main_fragmentgroup,
							// fragment_Favorites);
							// ft.commit();
							// main_Tile.tabhost.setCurrentTab(1);
							//
							// }
							//
							// }
							if (sched.isVisible()) {
								FragmentTransaction ft = main_Tile.manager
										.beginTransaction();
								ft.remove(sched);
								ft.commit();
							}
						}
					});

			main_Tile.tabhost.getTabWidget().getChildAt(2)
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							System.out.println("Clicked Settings!");

							// if(main_Tile.tabhost.getCurrentTab() == 1 &&
							// fragment_Favorites.isVisible()){
							// FragmentTransaction ft =
							// manager.beginTransaction();
							// ft.remove(fragment_Favorites);
							// ft.commit();
							//
							// main_Tile.tabhost.setCurrentTab(2);
							// }
							//
							// else if(main_Tile.tabhost.getCurrentTab() == 2){
							//
							// main_Tile.mLayout.setPanelState(PanelState.COLLAPSED);
							// main_Tile.tabhost.setCurrentTab(3);

							if (sched.isVisible()) {
								FragmentTransaction ft = main_Tile.manager
										.beginTransaction();
								ft.remove(sched);
								ft.commit();
							}

						}
						// else{
						// main_Tile.mLayout.setPanelState(PanelState.COLLAPSED);
						// // FragmentTransaction ft =
						// manager.beginTransaction();
						// // ft.add(R.id.main_fragmentgroup,
						// fragment_Schedule);
						// // ft.commit();
						// //
						// main_Tile.tabhost.setCurrentTab(2);
						//
						// }
						//
						// }
					});
		}
		return view;

	}

	public void setRoutes(Route r) {

		// If the adapter does not contain the selected route title, add the
		// stops

		System.out.println("route title null : " + r.getRouteTitle());

		// System.out.println("Adapter route titles: " +
		// adapter.routeTitles.size());
//		adapter.routeTitles.add(0);

		if (!adapter.routeTitles.contains(r.getRouteTitle())) {
			ArrayList<Stop> tempStops = r.getStops();

			// stops.addAll(tempStops);

			// System.out.println("Stop size is: " + stops.size() +
			// ", Route name is " + r.getRouteTitle());

			// adapter.addRoutes(stops, r.getRouteTitle()); // Trying this call
			// instead

			adapter.addRoute(r);

			((BaseAdapter) lv_stopnames.getAdapter()).notifyDataSetChanged();
//			((BaseAdapter) lv_stoptimes.getAdapter()).notifyDataSetChanged();

			System.out.println("Route: "
					+ adapter.mData.get(0).getStopDescription());
			System.out.println("Route Data: " + adapter.getData().size());
		}

		else {
			System.out.println("Route is already added to the titles");

		}

	}

	public void removeRoutes(Route r) {
		ArrayList<Stop> tempStops = r.getStops();

		adapter.removeRoute(r);

		((BaseAdapter) lv_stopnames.getAdapter()).notifyDataSetChanged();
		System.out.println("Removing Route, Route Data: "
				+ adapter.getData().size());

	}

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

	// Fills lv_arr with the stop titles, so that way a custom listview does not
	// need to be created and instead can be populated
	// by lv_arr
	public void seekSetDotPosition(ArrayList<Integer> dotArray) {
		int [] tempIntArray = new int[lv_arr.length];
//		int step = 85;
//		
//		for(int i = 0; i < numstops; i++){
//			tempIntArray[i] = (int) lv_stopnames.getChildAt(i).getY();
//
//			
//			System.out.println("child position: " + (int) lv_stopnames.getChildAt(i).getY());
//		}
		for(int i = 0; i < dotArray.size(); i++){
			tempIntArray[i] = dotArray.get(i);
		}
		seekbar.setDots(tempIntArray);
		seekbar.setDotsDrawable(R.drawable.abc_btn_switch_to_on_mtrl_00001);

	}
	
	// To calculate the total height of all items in ListView call with items = adapter.getCount()
	public int[] getItemHeightofListView(ListView listView, int items) {
	    ListAdapter adapter = listView.getAdapter();

	    int grossElementHeight = 0;
	    int[] dotPositions = new int[lv_arr.length];
	    
	    for (int i = 0; i < items; i++) {
	        View childView = adapter.getView(i, null, listView);
	        childView.measure(UNBOUNDED, UNBOUNDED);
	        
	        grossElementHeight += childView.getMeasuredHeight();
	        dotPositions[i] = grossElementHeight;
	    }
	    return dotPositions;
	}

}
