/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 * LoginScreen is used as the login screen for the app. User logins in with a stored username and password
 * This has not been fully incorporated into the app and is left out for this current build.  
 * Eventually, would like to have Usernames/Passwords stored into a data base, have user profiles based
 * on accounts, and also link up Facebook & Google+ for auto logins. 
 * 
 * Would be smart to start trying to get the user's location from here in order to achieve better performance
 * But has not been programmed yet
 */

package com.crash.rettina;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class LoginScreen extends Activity implements LocationListener {

	private TextView tv_CreateAccount;			// TextView that displays to create an account
	private TextView tv_ForgotPassword;			// TextView that displays 'Forgot Password'

	
	// When the Activity is created
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Declare the elements
		final EditText txt_userName = (EditText) findViewById(R.id.userName);	// Holds the username the user enters
		final EditText txt_password = (EditText) findViewById(R.id.password);	// Holds the password the user enters
		ImageButton skip = (ImageButton) findViewById(R.id.skip);

		// Initialize textviews
		tv_CreateAccount = (TextView) findViewById(R.id.tv_createaccount);
		tv_ForgotPassword = (TextView) findViewById(R.id.tv_forgotpassword);

		
		// Skip button to launch the Main Activity... Note: This used to be the Main activity but is no longer being used for now
		// When this is implemented again, this will have to become the 'Main' activity/Launch activity and it will Launch into 
		// The current activity called 'Main'
		skip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				startActivity(new Intent("com.crash.rettina.MAIN"));	// Launches into Main Activity
			}
		});

		// Handle when "Create Account" is clicked
		tv_CreateAccount.setOnClickListener(new View.OnClickListener() {

			// Right now it will default to the MainActivity class
			@Override
			public void onClick(View v) {
				startActivity(new Intent("com.crash.rettina.CREATEACCOUNT"));
			}
		});

		// Handle when "Forgot Password" is clicked
		tv_ForgotPassword.setOnClickListener(new View.OnClickListener() {

			// Right now it will default to the MainActivity class
			@Override
			public void onClick(View v) {
				startActivity(new Intent("com.crash.rettina.MAINACTIVITY"));
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	// Potential for finding user's location.. Not used though
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

}
