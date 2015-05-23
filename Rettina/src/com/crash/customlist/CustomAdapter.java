package com.crash.customlist;

import java.util.ArrayList;

import com.crash.rettina.MainActivity;
import com.crash.rettina.R;
import com.crash.rettina.RouteMenu;
import com.crash.routeinfo.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomAdapter extends BaseAdapter implements OnClickListener {

	/*********** Declare Used Variables *********/
	private Activity activity;
	private ArrayList<Route> data;
	public RouteMenu routeActivity;
	private static LayoutInflater inflater = null;
	public Resources res;
	Route tempValues = null;;
	CheckBox cb_showRoute;
	int i = 0;
	public MainActivity tempA;


	/************* CustomAdapter Constructor *****************/
	public CustomAdapter(Activity a, ArrayList<Route> d, Resources resLocal,
			RouteMenu r) {

		/********** Take passed values **********/
		activity = a;
		data = d;
		res = resLocal;
		routeActivity = r;
		tempA = (MainActivity) activity;

		/*********** Layout inflator to call external xml layout () ***********/
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	/******** What is the size of Passed Arraylist Size ************/
	public int getCount() {

		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
	public static class ViewHolder {

		public CheckBox cb_showRoute;
		public ImageButton imgbtn_removeRoute;
		public ImageButton imgbtn_followRoute;
		public TextView tv_routeName;

	}

	/****** Depends upon data size called for each row , Create each ListView row *****/
	public View getView(final int position, View convertView, final ViewGroup parent) {

		View vi = convertView;
		final ViewHolder holder;

		//if (convertView == null) {

			/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
			vi = inflater.inflate(R.layout.tabitem, null);

			/****** View Holder Object to contain tabitem.xml file elements ******/

			holder = new ViewHolder();
			holder.cb_showRoute = (CheckBox) vi.findViewById(R.id.cb_showroute);

			cb_showRoute = (CheckBox) vi.findViewById(R.id.cb_showroute);

			holder.imgbtn_removeRoute = (ImageButton) vi
					.findViewById(R.id.imgbtn_removeroute);
			
			holder.imgbtn_followRoute = (ImageButton) vi
					.findViewById(R.id.imgbtn_followroute);
			
			holder.tv_routeName = (TextView) vi.findViewById(R.id.tv_routename);
			
			

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
//		} 
//		else{
//			System.out.println("This Guy!");
//			holder = (ViewHolder) vi.getTag();
//		}

		if (data.size() <= 0) {
			ViewHolder tempHolder = new ViewHolder();
			vi = new View(activity);
			vi.setTag(tempHolder);
		} 
		
		
		else {
			//System.out.println("In here!");
			//System.out.println("This Data Size: " + data.size());
			/***** Get each Model object from Arraylist ********/
			tempValues = null;
			
			// Error when removing the last element
			tempValues = (Route) data.get(position);
			//System.out.println(tempValues.getRouteTitle());

			/************ Set Model values in Holder elements ***********/
			
			holder.tv_routeName.setText(tempValues.getRouteTitle());

			// holder.text1.setText( tempValues.getUrl() );
			// holder.image.setImageResource(
			// res.getIdentifier(
			// "com.androidexample.customlistview:drawable/"+tempValues.getImage()
			// ,null,null));

			/******** Set Item Click Listner for LayoutInflater for each row *******/

//			vi.setOnClickListener(new OnItemClickListener(position));
			//vi.setOnClickListener(this);
			
			
		}
		
		vi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(data.size() > 0){
				
					
					if(data.get(position).getPolyLine() == null){
						System.out.println("Getting Shape");
						tempA.getShape(data.get(position));
						//routeActivity.drawRoute(data.get(position));

						
						//data.get(position).printPoly();
						
				       // notifyDataSetChanged();

					}
					else{
					
				 if (holder.cb_showRoute.isChecked()) {
						holder.cb_showRoute.setChecked(false);
						//System.out.println("Hiding Route: " + data.get(position));
						routeActivity.hideRoute(data.get(position));
						
						// Hide the stops for that selected route
						routeActivity.hideStops(data.get(position));
						tempA.sched.removeRoutes(data.get(position));
						


					} else {
						holder.cb_showRoute.setChecked(true);
						routeActivity.drawRoute(data.get(position));
						
						// Show the stops for that route
						routeActivity.showStops(data.get(position));
						
						//MainActivity tempA = (MainActivity) activity;
						tempA.sched.setRoutes(data.get(position));
						

					}
					}
				}
			}
		});

		
		
		// Have to click the view first and then click the 'X' in order to remove the route from favorites
	    // Should be able to just click the 'X' and the route is removed
	    // Also, the bottom route is only being removed no matter which row is clicked
	    
		
		
		// May need to change up the whole click listener thing in order to get this to work...
		// Probably need to find a way to make it so RouteMenu can handle the clicks
		
	    holder.imgbtn_removeRoute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				System.out.println("Data Size: " + data.size());

				if(data.size() >= 1){
					System.out.println("Position: " + position + " Removing: " + data.get(position));						
					//routeActivity.hideRoute(data.get(position));
					//routeActivity.hideRoute(data.get(position));
					
					// Hide the stops for that selected route
					routeActivity.hideStops(data.get(position));
					routeActivity.hideRoute(data.get(position));
					routeActivity.removeRouteFromFav(data.get(position));
					
					
					//MainActivity tempA = (MainActivity) activity;
					tempA.sched.removeRoutes(data.get(position));

					
				//data.remove(position);
				System.out.println("Data Size After Removed: " + data.size());

		        notifyDataSetChanged();
				}
				
				
			}
		});
	    
	    // When the user clicks the location marker icon, it will trigger a gps view where it animates the camera to follow along
	    // The route
	    holder.imgbtn_followRoute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				

				if(data.size() >= 1){
					
					
//					routeActivity.hideStops(data.get(position));
//					routeActivity.hideRoute(data.get(position));
					
					
					//MainActivity tempA = (MainActivity) activity;
//					tempA.sched.removeRoutes(data.get(position));
					
					// Move google maps to the starting route position
					//tempA.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(data.get(position).getStops().get(0).getLatLng(), 16));
					
					// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
					CameraPosition cameraPosition = new CameraPosition.Builder()
					    .target(data.get(position).getStops().get(0).getLatLng())      // Sets the center of the map to Mountain View
					    .zoom(19)                   // Sets the zoom
					    .bearing(90)                // Sets the orientation of the camera to east
					    .tilt(80)                   // Sets the tilt of the camera to 30 degrees
					    .build();                   // Creates a CameraPosition from the builder
					tempA.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					
					//Null Pointer here
					 data.get(position).getStops().get(0).getMarker().showInfoWindow();
					 
					 
					 tempA.mLayout.setPanelState(PanelState.ANCHORED);

					
				//data.remove(position);

		       // notifyDataSetChanged();
				}
				
				
			}
		});

		return vi;
		
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void onClick(View v) {
//		Log.v("CustomAdapter", "=====Row button clicked=====");
//		CheckBox cb = (CheckBox) v.findViewById(R.id.cb_showroute);
//		
////		ImageButton imgbtn_removeRoute = (ImageButton) v
////				.findViewById(R.id.imgbtn_removeroute);
//		
//		
//	     //  int pos = (Integer)v.getTag();
//	       //Log.d("position of clicked item is", ""+pos);
//	       
//	       
//	       if (cb.isChecked()) {
//				cb.setChecked(false);
//				System.out.println("Hiding Route: " + tempValues.getRouteTitle());
//				routeActivity.hideRoute(data.get);
//
//			} else {
//				cb.setChecked(true);
//				routeActivity.drawRoute(tempValues);
//
//			}
//
//
//	}


	
	// Still need to find a way to handle clicking the X
	

//	/********* Called when Item click in ListView ************/
//	private class OnItemClickListener implements OnClickListener {
//		private int mPosition;
//
//		OnItemClickListener(int position) {
//			mPosition = position;
//			
//		}
//
//		@Override
//		public void onClick(View arg0) {
//
//
//			// CustomListview sct = (CustomListview)activity;
//			System.out.println("Clicked!!!");
//			System.out.println("Route clicked: " + tempValues.getRouteTitle());
//			// tempValues.printPoly();
//
//			// Set the checkbox to checked if it is unchecked, otherwise uncheck the box
//			if (cb_showRoute.isChecked()) {
//				cb_showRoute.setChecked(false);
//				routeActivity.hideRoute(tempValues);
//			}
//			// Checks the checkbox and hides the route
//			else {
//				cb_showRoute.setChecked(true);
//				routeActivity.drawRoute(tempValues);
//			}
//
//			
//
//			/****
//			 * Call onItemClick Method inside CustomListViewAndroidExample Class
//			 * ( See Below )
//			 ****/
//
//			// sct.onItemClick(mPosition);
//		}
//	}
}