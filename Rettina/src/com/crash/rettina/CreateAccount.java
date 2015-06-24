package com.crash.rettina;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class CreateAccount extends Activity {
	
	// Create the variables to hold the Username, Password, Password Confirmation, and the "OK" button
	private EditText userName, password, password_Confirmation;
	private ImageButton imgbtn_Ok;
	private String username_Entered, password_Entered, password_Confirmation_Entered;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_accnt);
		
		
		// Initialize the EditTexts and ImageButton
		userName = (EditText) findViewById(R.id.userName);
		password = (EditText) findViewById(R.id.password);
		password_Confirmation = (EditText) findViewById(R.id.passwordConfirmation);
		imgbtn_Ok = (ImageButton) findViewById(R.id.imgbtn_ok_Create);

		
		// When the "OK" button is clicked, check to see if the Username is available as well as if the passwords match up
		// If they do, save the Username and password combo. If not available or no match, then post the error and do not save
		imgbtn_Ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// If the username is greater than 5 characters save it, otherwise post the error
				if(userName.getText().toString().length() > 5){
				username_Entered = userName.getText().toString();
				}
				else{
					// Temporary error post, need to be able to alert the user
					System.out.println("Username must be at least 5 characters!");
				}
					// See if the passwords are long enough
				if(password.getText().toString().length() > 5 && password_Confirmation.getText().toString().length() > 5){
					System.out.println("Passwords Long Enough!");
					
					// Check to see if the two passwords match
					if(password.getText().toString() == password_Confirmation.getText().toString()){
						System.out.println("Passwords Match!");

					password_Entered = password.getText().toString();
					password_Confirmation_Entered = password_Confirmation.getText().toString();
					}
					else{
						System.out.println("Passwords Do Not Match!");
					}
					
				}
				else{
					System.out.println("Passwords Not Long Enough!");

				}
	
			}
		});
		
		
		
		
		
	}
}
