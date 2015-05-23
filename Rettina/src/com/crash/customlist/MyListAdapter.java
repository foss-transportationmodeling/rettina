package com.crash.customlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crash.customlist.CustomAdapter.ViewHolder;
import com.crash.rettina.MainActivity;
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
	
	private Schedule mContext;
	private static LayoutInflater inflater = null;
	private Activity activity;
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
		//this.mContext = context;
		activity = a;
		
		/*********** Layout inflator to call external xml layout () ***********/
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		tempValue = null;

	}

	public ArrayList<Stop> getData() {
		return mData;
	}

	public void setRoutes(ArrayList<Stop> routes, String routeTitle) {

		mData = routes;
		this.routeTitle = routeTitle;
		setRouteTitle();
		
		// Adding a route title at the start of each routes stops
		
		if(numberOfRoutes < 1){
		mData.add(0, new Stop(mData.get(0).getRouteID(), mData.get(0).getStopID(), mData.get(0).getRouteTitle(), mData.get(0).getLatLng()));
		}
		else{
			mData.add(sizeHolder, new Stop(mData.get(mData.size() - 1).getRouteID(), mData.get((mData.size() - 1)).getStopID(), mData.get((mData.size() - 1)).getRouteTitle(), mData.get((mData.size() - 1)).getLatLng()));

		}
		numberOfRoutes++;
	}
	
	public void setRouteTitle() {
		for(int i = 0; i < mData.size(); i++){
			mData.get(i).setRouteTitle(routeTitle);
		}
	}

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
	public View getView(final int position, View convertView, final ViewGroup parent) {
		View vi = convertView;
		final ViewHolder holder;

		//if (convertView == null) {

			/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
			vi = inflater.inflate(R.layout.custom_schedule, null);

			/****** View Holder Object to contain tabitem.xml file elements ******/

			holder = new ViewHolder();

			holder.tv_stopName = (TextView) vi.findViewById(R.id.tv_stopname);
			holder.tv_stopTime = (TextView) vi.findViewById(R.id.tv_stoptime1);
			holder.tv_stopTime2 = (TextView) vi.findViewById(R.id.tv_stoptime2);

			System.out.println("Here!");
			

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
//		} 
//		else{
//			System.out.println("This Guy!");
//			holder = (ViewHolder) vi.getTag();
//		}

		if (mData.size() <= 0) {
			ViewHolder tempHolder = new ViewHolder();
			vi = new View(activity);
			
			vi.setTag(tempHolder);
		} 
		
		
		else {


			tempValue = (Stop) mData.get(position);


			/************ Set Model values in Holder elements ***********/
			
			System.out.println("Stop Descrip: " + tempValue.getStopDescription());
			
			
//			if(numberOfRoutes == 1 && position == 0){
//			holder.tv_stopTime.setText(tempValue.getStopDescription());
//			holder.tv_stopTime.setTextSize(18);
//			
////			holder.tv_stopTime.setTextColor(holder.tv_stopTime.getResources().getColor(R.color.gray));
//			
//
//			
//			holder.tv_stopName.setText("");
//			holder.tv_stopTime2.setText("");
//
//			}
			
			for(int i = 0; i < routeTitles.size(); i++){
				
			if(position == routeTitles.get(i)){
				holder.tv_stopTime.setText(tempValue.getStopDescription());
				holder.tv_stopTime.setTextSize(18);
				
//				holder.tv_stopTime.setTextColor(holder.tv_stopTime.getResources().getColor(R.color.gray));

				holder.tv_stopName.setText("");
				holder.tv_stopTime2.setText("");

				}
//			else if(numberOfRoutes > 1){
//			
//			}
			else{
				holder.tv_stopName.setText(tempValue.getStopDescription());
			}
			}
			
			//System.out.println("Stop: " + holder.tv_stopName.getText());

			// holder.text1.setText( tempValues.getUrl() );
			// holder.image.setImageResource(
			// res.getIdentifier(
			// "com.androidexample.customlistview:drawable/"+tempValues.getImage()
			// ,null,null));

			/******** Set Item Click Listener for LayoutInflater for each row *******/

//			vi.setOnClickListener(new OnItemClickListener(position));
			//vi.setOnClickListener(this);
			
			}
		
		
		// Work on setting the clicks and stuff later
		
		vi.setOnClickListener(new OnClickListener() {
			
			private MainActivity mainAct;

			@Override
			public void onClick(View v) {
				
				if(mData.size() > 0){
					
					System.out.println("Clicked: " + mData.get(position).getStopDescription());
					 mainAct = (MainActivity) activity;
					 
					 mainAct.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mData.get(position).getLatLng(), 16));	
					 mData.get(position).getMarker().showInfoWindow();
				 
				}
			}
		});
		routeTitles.add(mData.size());

		return vi;
	
	}

	public void removeStops(ArrayList<Stop> tempStops) {
		mData.remove(0);
		mData.removeAll(tempStops);
		numberOfRoutes--;
		sizeHolder = mData.size();
	}

	// implement all abstract methods here
}
