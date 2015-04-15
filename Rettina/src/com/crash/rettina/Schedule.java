package com.crash.rettina;

import java.util.ArrayList;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Schedule extends Fragment {
	ArrayList<String> listItems = new ArrayList<String>();
    

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.schedule,
	        container, false);
	    
	    
	    ListView lv = (ListView) view.findViewById(R.id.lv_schedule);	  
	    
	    listItems.add("Student Union");
    	listItems.add("Engineering Westbound");
    	listItems.add("Phillip E Austin");
    	listItems.add("Husky Village");
    	listItems.add("Towers");
    	listItems.add("North");
    	listItems.add("North Parking Garage");
    	
    	lv.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
	    	    android.R.layout.simple_list_item_1 , listItems));
	    	        
	    
	    return view;
	    
	  }
	


}

