package com.crash.rettina;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Settings extends Fragment {
	
	public Main_Tile main_Tile;
	
	// Constructor
	public Settings(Main_Tile mt){
		main_Tile = mt;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.settings,
		        container, false);
	
				return view;

	}
			

}
