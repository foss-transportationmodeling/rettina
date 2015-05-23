package com.crash.rettina;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.crash.customlist.MyListAdapter;

public class Navigator extends Fragment {

	  MyListAdapter adapter;
	  ListView lv;
	  MainActivity a;
	  Schedule sched;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.schedule,
	        container, false);
	    
	    
	    
		return view;

}
}
