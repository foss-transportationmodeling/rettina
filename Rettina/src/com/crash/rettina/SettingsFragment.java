/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * Settings is a fragment used to allow the user to set their desired settings for the app
 * This Fragment has not yet been completed and will be completed in the future. This is
 * simply a placeholder right now and can only display the layout
 */


package com.crash.rettina;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {
	
	public Main main_Tile;
	
	// Constructor
	public SettingsFragment(Main mt){
		main_Tile = mt;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.settings,
		        container, false);
	
				return view;

	}
			

}
