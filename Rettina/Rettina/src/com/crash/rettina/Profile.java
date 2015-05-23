package com.crash.rettina;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class Profile extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.profile,
	        container, false);
	    
	    return view;
	    
	  }
	
	
}
