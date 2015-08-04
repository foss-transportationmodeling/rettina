/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * The sharing fragment is used to handle when the user is currently navigating a route
 * and selects to share information.  Once the user elects to share information, this
 * fragment appears.  It allows the user to share information such as 'ride experience,'
 * 'seats available,' and location
 */


package com.crash.rettina;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.crash.connection.Connect_Sharing;
import com.crash.routeinfo.Route;


public class Sharing extends Fragment {
	
	public Main main_tile;			// Used to establish communication to the Main activity
	private RatingBar rb_driver;	// Rating bar used for the user to rank the driver
	private RatingBar rb_seats;		// Rating bar used for the user to show how many open seats
	private RadioGroup rg_location;	// Radio group for deciding how the user wants to share the location information
	private Spinner spin_ping;		// Spinner for deciding the location ping time
	private EditText et_comment;	// User can write comments about the trip
	private Route selectedRoute;	// The selected route the user is currently on
	
	public long ping;				// Holds the ping time
		
	
	Sharing sharing;				// Holds the sharing Fragment

	
	// Constructor
	public Sharing(Main mt){
		// Assign the Main activity
		main_tile = mt;
		
		// Set the selected route
		setSelectedRoute(mt.clickedRoute);
		sharing = this;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Used to hold the ping selections time
		ArrayList<String> pingSelections = new ArrayList<String>();
		
		// The ping selection times
		pingSelections.add("5 seconds");
		pingSelections.add("20 seconds");
		pingSelections.add("30 seconds");
		pingSelections.add("45 seconds");
		pingSelections.add("1 minute");
		pingSelections.add("5 minutes");

		// Set the layout as sharing.xml
		final View view = inflater.inflate(R.layout.sharing, container, false);
		
		// Assign the views there resources
		Button btn_share = (Button) view.findViewById(R.id.btn_usershare);
		rb_driver = (RatingBar) view.findViewById(R.id.ratingbar_driver);
		rb_seats = (RatingBar) view.findViewById(R.id.ratingbar_space);
		rg_location = (RadioGroup) view.findViewById(R.id.radio_location);
		spin_ping = (Spinner) view.findViewById(R.id.spinner_locationping);
		et_comment = (EditText) view.findViewById(R.id.et_comment);
		
		// Assign the spinner and ping adapters
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.custom_spinner_item, pingSelections);
		spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
		spin_ping.setAdapter(spinnerAdapter);
		
		// Set the listener for the 'sharing' button
		btn_share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int selected = rg_location.getCheckedRadioButtonId();
				RadioButton b = (RadioButton) view.findViewById(selected);
				
				Editable comment = et_comment.getText();
				
				
				CharSequence radioSelection = b.getText();
				
				float driverRating = rb_driver.getRating();
				float seatingSpace = rb_seats.getRating();			

				// Connects to the server and shares the user's information
				Connect_Sharing jsonsharing = new Connect_Sharing(sharing.getActivity(), sharing);
				jsonsharing.execute();
				
				// These two methods set the Ping Selection time and Location Provider that the user prefers when
				// Clicking share... This will allow the Main_Tile to handle how it will send the location data to the database
				main_tile.setPingTime_Selection(ping);
				main_tile.setLocationProvider_Selection(radioSelection.toString());
				
				
				// Setting ping equal to the user's choice for ping time.... Ping is in miliseconds
				if(spin_ping.getSelectedItemPosition() == 0){
					ping = 5000;
				}
				else if(spin_ping.getSelectedItemPosition() == 1){
					ping = 20000;
				}
				else if(spin_ping.getSelectedItemPosition() == 2){
					ping = 30000;
				}
				else if(spin_ping.getSelectedItemPosition() == 3){
					ping = 45000;
				}
				else if(spin_ping.getSelectedItemPosition() == 4){
					ping = 60000;
				}
				else if(spin_ping.getSelectedItemPosition() == 5){
					ping = 300000;
				}
				
				main_tile.collectUsersLocation(ping);

				main_tile.removeSharing();
					
				main_tile.tabhost.setCurrentTab(3);		
				
			}
		});

		return view;

	}

	public float getRb_driverRating() {
		return rb_driver.getRating();
	}

	public void setRb_driverRating(float f) {
		this.rb_driver.setRating(f);;
	}

	public float getRb_seatsRating() {
		return rb_seats.getRating();

	}

	public void setRb_seatsRating(float f) {
		this.rb_seats.setRating(f);
	}


	
	public String getSpin_PingSelection() {
		return spin_ping.getSelectedItem().toString();
	}


	public String getEt_commentText() {
		return et_comment.getText().toString();
	}

	public void setEt_comment(String comment) {
		this.et_comment.setText(comment);
	}

	public Route getSelectedRoute() {
		return selectedRoute;
	}

	public void setSelectedRoute(Route selectedRoute) {
		this.selectedRoute = selectedRoute;
	}


}
