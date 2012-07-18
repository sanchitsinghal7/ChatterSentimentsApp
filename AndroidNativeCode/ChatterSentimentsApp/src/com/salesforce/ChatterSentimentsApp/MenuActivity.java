package com.salesforce.ChatterSentimentsApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MenuActivity extends Activity {

	private static final String PREFS_NAME = "OAuthPrefs";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view
		setContentView(R.layout.options_menu);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		
		// Restore oAuth preferences
        String clientId = "";
        String callbackURL = "";
        try{
	        clientId = settings.getString("clientId", null);
	        callbackURL = settings.getString("callbackURL", null);
        }
        catch(Exception ex){}
        if(clientId != null){
        	((EditText)findViewById(R.id.clientId)).setText(clientId);
        	((EditText)findViewById(R.id.callbackURL)).setText(callbackURL);
        }
	}
	
	
	/**
	 * Called when "Save" button is clicked in settings menu view. 
	 * 
	 * @param v
	 */
	public void onSettingsClick(View v) {
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		
		String clientId = "";
        String callbackURL = "";
        
			SharedPreferences.Editor editor = settings.edit();
			clientId = ((EditText)findViewById(R.id.clientId)).getText().toString();
			editor.putString("clientId", clientId);
			callbackURL = ((EditText)findViewById(R.id.callbackURL)).getText().toString();
			editor.putString("callbackURL", callbackURL);
	
		      // Commit the edits!
		      editor.commit();
        
        
		Intent mainIntent = new Intent(this, MainActivity.class);
    	this.startActivity(mainIntent);
    	startActivity(mainIntent);
	}
	
}
