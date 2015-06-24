/*
 * Rettina - 2015
 * Mitchell Thornton
 * Professor Konduri
 * University of Connecticut
 */

/*
 Main is used as the login screen for the app. User logins in with a stored username and password
 */

/*
 * To Do
 * 1. Add Facebook and Google+ connectivity for auto acount password login
 * 2. Add saved password/username states or make the user always logged in?
 * 3. Actually save username/password to a database and start an user system
 * 4. Add a "Create a new account" option
 * 5. Add a "Forgot password" option

 */

package com.crash.rettina;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Main extends Activity implements LocationListener {

	private String userName;
	private String password;
	private TextView tv_CreateAccount;
	private TextView tv_ForgotPassword;

	// Used for JSon

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Declare the elements
		final EditText txt_userName = (EditText) findViewById(R.id.userName);
		final EditText txt_password = (EditText) findViewById(R.id.password);
		// Button btn_login = (Button) findViewById(R.id.btn_login);
		// ImageButton btn_skip = (ImageButton) findViewById(R.id.btn_arrow);
		// ImageButton btn_faceBook = (ImageButton)
		// findViewById(R.id.imgbtn_fb);
		ImageButton skip = (ImageButton) findViewById(R.id.skip);

		// Initialize textviews
		tv_CreateAccount = (TextView) findViewById(R.id.tv_createaccount);
		tv_ForgotPassword = (TextView) findViewById(R.id.tv_forgotpassword);

		// Calling async task to get json

		skip.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				startActivity(new Intent("com.crash.rettina.MAINACTIVITY"));
				
				startActivity(new Intent("com.crash.rettina.MAIN_TILE"));

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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
