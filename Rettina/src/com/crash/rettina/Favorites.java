package com.crash.rettina;

import java.util.ArrayList;

import com.crash.customlist.CustomAdapter;
import com.crash.routeinfo.Route;
import com.crash.routeinfo.Stop;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class Favorites extends Fragment{
	
	ListView lv_favs;
    CustomAdapter adapter;
	public ArrayList<Route> selectedFavs = new ArrayList<Route>();
	
	Main_Tile a;
    public Favorites fav = null;
	public TextView tv_emptyfavs;
	public boolean are_there_favorites = false; // This boolean controls whether their are favorites displayed, or if the empty favorites message should be displayed

    ImageButton imgbtn_listviewbutton;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
  	      Bundle savedInstanceState) {
    	
    	fav = this;
        Resources res = getResources();

    	View view = inflater.inflate(R.layout.favorites,
    	        container, false);
    	
    	
		tv_emptyfavs = (TextView) view.findViewById(R.id.tv_emptyfavs);
		
        a = (Main_Tile) getActivity();
        
        if(selectedFavs.size() > 0){
			are_there_favorites = true;
        }
       
	 lv_favs = (ListView) view.findViewById(R.id.lv_favorites);	
	    /**************** Create Custom Adapter *********/
     adapter = new CustomAdapter(fav.getActivity(), selectedFavs, res, fav);
     lv_favs.setAdapter( adapter );
     
     
  // If favorites is empty, set the favorites to display the default empty favs message and hide the favs
		if(are_there_favorites == false){
			System.out.println("There aren't any favorites");
			
			tv_emptyfavs.setVisibility(View.VISIBLE);
//			lv_favs.setVisibility(View.GONE);
		}
		// Otherwise, display the favorites and hide the default empty message
		else{
			
			System.out.println("There are favorites");

//			lv_favs.setVisibility(View.VISIBLE);
			tv_emptyfavs.setVisibility(View.GONE);
		}
     
     
     imgbtn_listviewbutton = (ImageButton) view.findViewById(R.id.imgbtn_closefav);
     
     imgbtn_listviewbutton.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			a.hideFavorites();
		}
	});
     
     
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
}
