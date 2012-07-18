/*
 * Copyright (c) 2011, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.ChatterSentimentsApp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.app.ForceApp;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.ClientManager.LoginOptions;
import com.salesforce.androidsdk.rest.ClientManager.RestClientCallback;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.util.EventsObservable;
import com.salesforce.androidsdk.util.EventsObservable.EventType;

/**
 * Main activity
 */
public class MainActivity extends Activity {
	private static final int None = 0;
	private static final String PREFS_NAME = "OAuthPrefs";
	RestClient authenticatedClient;
	Location lastGoodLocation;
	Map<String, Sanchit__Sentimental_Topic__c> sentimentMap = new HashMap<String, Sanchit__Sentimental_Topic__c>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view
		setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * MenuInflater inflater = getMenuInflater();
		 * inflater.inflate(R.menu.game_menu, menu);
		 */

		menu.add(None, 1, None, "Settings");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case 1:
			// setContentView(R.layout.options_menu);
			Intent myIntent = new Intent(this, MenuActivity.class);
			this.startActivity(myIntent);
			startActivity(myIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);

		// Bring up passcode screen if needed
		ForceApp.APP.getPasscodeManager().lockIfNeeded(this, true);

		// Do nothing - when the app gets unlocked we will be back here
		if (ForceApp.APP.getPasscodeManager().isLocked()) {
			return;
		}

		// Start Location listener
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				// makeUseOfNewLocation(location);
				lastGoodLocation = location;
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		try {
			// Register the listener with the Location Manager to receive
			// location updates
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListener);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
			}
		}

		// Login options
		String accountType = ForceApp.APP.getAccountType();
		// Restore oAuth preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String clientId = "";
		String callbackURL = "";
		try {
			/*
			 * clientId = settings.getString("clientId", null); callbackURL =
			 * settings.getString("callbackURL", null);
			 */
			clientId = "3MVG9Y6d_Btp4xp4ZWNs88Z5dFOFqEX.OXRkXiGhURf8_5Q1dTCOTram4frE4e7OWrxqTF5PjwfrA13_PIBeP";
			callbackURL = "sfdc://axm/detect/oauth/done";

		} catch (NullPointerException nex) {
			Toast.makeText(getBaseContext(), "Please update app settings!!!",
					Toast.LENGTH_LONG).show();
			this.finish();
			Intent myIntent = new Intent(this, MenuActivity.class);
			this.startActivity(myIntent);
			startActivity(myIntent);
		} catch (ClassCastException cex) {
			Toast.makeText(getBaseContext(), "Please update app settings!!!",
					Toast.LENGTH_LONG).show();
			this.finish();
			Intent myIntent = new Intent(this, MenuActivity.class);
			this.startActivity(myIntent);
			startActivity(myIntent);
		}

		if (clientId == null) {
			Toast.makeText(getBaseContext(), "Please update app settings!!!",
					Toast.LENGTH_LONG).show();

			Intent myIntent = new Intent(this, MenuActivity.class);
			this.startActivity(myIntent);
			startActivity(myIntent);
		} else {
			System.out.println("LoginOptions B");
			LoginOptions loginOptions = new LoginOptions(
					null, // login host is chosen by user through the server
							// picker
					ForceApp.APP.getPasscodeHash(), callbackURL, clientId,
					new String[] { "api" });
			System.out.println("LoginOptions A");
			// Get a rest client
			new ClientManager(this, accountType, loginOptions).getRestClient(
					this, new RestClientCallback() {
						@Override
						public void authenticatedRestClient(RestClient client) {
							if (client == null) {
								ForceApp.APP.logout(MainActivity.this);
								return;
							}

							// Show everything
							findViewById(R.id.root).setVisibility(View.VISIBLE);

							// Show welcome
							// ((TextView)
							// findViewById(R.id.welcome_text)).setText(getString(R.string.welcome,
							// client.getClientInfo().username));

							authenticatedClient = client;

						}
					});
		}
	}

	View.OnClickListener resultClickhandler = new View.OnClickListener() {
		public void onClick(View v) {
			/*
			 * Toast.makeText(getBaseContext(), "Voila: " + v.getTag(),
			 * Toast.LENGTH_LONG).show();
			 */

			if (1 == 1) {
				/*
				 * Toast.makeText(getBaseContext(), "Voila Tagging: " +
				 * ((TextView) findViewById(v.getId())).getText() +
				 * " with latitude: " + lastGoodLocation.getLatitude() +
				 * " and longitude: " + lastGoodLocation.getLongitude(),
				 * Toast.LENGTH_LONG).show();
				 */

				// ListView detail = ((ListView) findViewById(R.id.listView1));
				// DetailActivity act = new DetailActivity(sentimentMap);
				Intent myIntent = new Intent(getApplicationContext(),
						DetailActivity.class);
				finish();

				ArrayList<String> sList = new ArrayList<String>();
				sList.add("Detail");
				sList.add("Topic: " + sentimentMap.get(v.getTag()).getName());
				sList.add("Negative Count: "
						+ String.valueOf(sentimentMap.get(v.getTag())
								.getSanchit__Negatives__c()));
				sList.add("Negative Percent: "
						+ String.valueOf(sentimentMap.get(v.getTag())
								.getSanchit__Negatives_Percent__c()));
				sList.add("Positive Count: "
						+ String.valueOf(sentimentMap.get(v.getTag())
								.getSanchit__Positives__c()));
				sList.add("Positive Percent: "
						+ String.valueOf(sentimentMap.get(v.getTag())
								.getSanchit__Positives_Percent__c()));
				sList.add("Total Count: "
						+ String.valueOf(sentimentMap.get(v.getTag())
								.getSanchit__Total_Sentiments__c()));
				myIntent.putExtra("sList", sList);
				startActivity(myIntent);

				/*
				 * Intent myIntent = new Intent(this, MenuActivity.class);
				 * this.startActivity(myIntent); startActivity(myIntent);
				 */

				// setContentView(R.layout.detail_topic);

				/*
				 * Map<String, Object> fields = new HashMap<String, Object>();
				 * fields.put("Description","latitude: " +
				 * lastGoodLocation.getLatitude() + " longitude: " +
				 * lastGoodLocation.getLongitude());
				 * 
				 * 
				 * RestRequest request = null; try { request =
				 * RestRequest.getRequestForUpdate("v24.0", "Account", (String)
				 * v.getTag(), fields); } catch (UnsupportedEncodingException
				 * e1) { // TODO Auto-generated catch block
				 * e1.printStackTrace(); } catch (IOException e1) { // TODO
				 * Auto-generated catch block e1.printStackTrace(); }
				 * 
				 * 
				 * authenticatedClient.sendAsync(request, new
				 * AsyncRequestCallback() {
				 * 
				 * @Override public void onSuccess(RestResponse response) { try
				 * {
				 * 
				 * Toast.makeText(getBaseContext(), "Voila Tagging Done!!!",
				 * Toast.LENGTH_LONG).show();
				 * 
				 * EventsObservable.get().notifyEvent(EventType.RenditionComplete
				 * ); } catch (Exception e) { e.printStackTrace();
				 * //displayError(e.getMessage()); } }
				 * 
				 * @Override public void onError(Exception exception) {
				 * Toast.makeText(getBaseContext(), "Error while Tagging!!!",
				 * Toast.LENGTH_LONG).show();
				 * EventsObservable.get().notifyEvent(
				 * EventType.RenditionComplete); } });
				 */

			} else {
				Toast.makeText(
						getBaseContext(),
						"Sorry: We could not get a fix on your GPS location. Please check your GPS settings and try again!!!",
						Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	public void onUserInteraction() {
		ForceApp.APP.getPasscodeManager().recordUserInteraction();

	}

	/**
	 * Called when "Logout" button is clicked.
	 * 
	 * @param v
	 */
	public void onLogoutClick(View v) {
		ForceApp.APP.logout(this);
	}

	/**
	 * Called when "My Topics" button is clicked.
	 * 
	 * @param v
	 */
	public void onLaunchOne(View v) {
		try {
			System.out.println("before rest call");
			String searchTerm = "";
			// searchTerm =
			// ((EditText)findViewById(R.id.editText1)).getText().toString();
			String soql;
			((ProgressBar) findViewById(R.id.progressBar1))
					.setIndeterminate(true);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(R.id.progressBar1)).bringToFront();
			if (searchTerm.isEmpty())
				soql = "select id, name, Sanchit__Negatives__c, Sanchit__Negatives_Percent__c,"
						+ " Sanchit__Positives__c, Sanchit__Positives_Percent__c, Sanchit__Total_Sentiments__c"
						+ " from Sanchit__Sentimental_Topic__c";
			else
				soql = "select id, name from Sanchit__Sentimental_Topic__c where name like '"
						+ searchTerm + "'";

			System.out.println("soql: " + soql);
			RestRequest request = RestRequest.getRequestForQuery("v24.0", soql);

			authenticatedClient.sendAsync(request, new AsyncRequestCallback() {

				@Override
				public void onSuccess(RestResponse response) {
					try {
						System.out.println("after rest call");
						/*
						 * REST API call was successful. Add business logic. For
						 * e.g. to process the query results...
						 */

						if (response == null || response.asJSONObject() == null)
							return;
						System.out.println("response received: " + response);
						JSONArray records = response.asJSONObject()
								.getJSONArray("records");

						if (records.length() == 0)
							return;
						setContentView(R.layout.results);
						TableLayout resultsTable = ((TableLayout) findViewById(R.id.results));
						for (int i = 0; i < records.length(); i++) {
							JSONObject senti = (JSONObject) records.get(i);
							String sName = senti.getString("Name");
							String sID = senti.getString("Id");
							System.out.println("response received: " + sName);

							Sanchit__Sentimental_Topic__c sTopic = new Sanchit__Sentimental_Topic__c();
							sTopic.setName(sName);
							sTopic.setSanchit__Negatives__c(Double.valueOf(senti
									.getString("Sanchit__Negatives__c")));
							sTopic.setSanchit__Negatives_Percent__c(Double.valueOf(senti
									.getString("Sanchit__Negatives_Percent__c")));
							sTopic.setSanchit__Positives__c(Double.valueOf(senti
									.getString("Sanchit__Positives__c")));
							sTopic.setSanchit__Positives_Percent__c(Double.valueOf(senti
									.getString("Sanchit__Positives_Percent__c")));
							sTopic.setSanchit__Total_Sentiments__c(Double.valueOf(senti
									.getString("Sanchit__Total_Sentiments__c")));

							sentimentMap.put(sID, sTopic);

							/* Create a new row to be added. */
							TableRow tr = new TableRow(resultsTable
									.getContext());
							tr.setLayoutParams(new LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));
							tr.setPadding(0, 5, 0, 5);
							tr.setClickable(true);
							if (i % 2 == 0)
								tr.setBackgroundColor(Color.BLACK);
							else
								tr.setBackgroundColor(Color.WHITE);
							/* Create a Button to be the row-content. */
							TextView b = new TextView(tr.getContext());
							b.setText(sName);
							b.setLayoutParams(new LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));
							b.setClickable(true);
							b.setTag(sID);
							b.setOnClickListener(resultClickhandler);
							b.setTextSize(21);
							if (i % 2 == 0)
								b.setTextColor(Color.WHITE);
							else
								b.setTextColor(Color.BLACK);

							/* Add Button to row. */
							tr.addView(b);
							/* Add row to TableLayout. */
							resultsTable.addView(tr,
									new TableLayout.LayoutParams(
											LayoutParams.FILL_PARENT,
											LayoutParams.WRAP_CONTENT));
						}

						EventsObservable.get().notifyEvent(
								EventType.RenditionComplete);
					} catch (Exception e) {
						e.printStackTrace();
						// displayError(e.getMessage());
					}
				}

				@Override
				public void onError(Exception exception) {

					EventsObservable.get().notifyEvent(
							EventType.RenditionComplete);
				}
			});

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when "My Topics" button is clicked.
	 * 
	 * @param v
	 */
	public void onLaunchTwo(View v) {
		
		setContentView(R.layout.new_topic);
						
	}
	
	/**
	 * Called when "Create Topic" button is clicked.
	 * 
	 * @param v
	 */
	public void insertNewTopic(View v) {

		Map<String, Object> fields = new HashMap<String, Object>();
		EditText edittext = (EditText) findViewById(R.id.editText1);
		fields.put("Name", edittext.getText());
		RestRequest request = null;
		try {
			request = RestRequest.getRequestForCreate("v24.0", "Sanchit__Sentimental_Topic__c",
					fields);
		} catch (UnsupportedEncodingException e1) { // TODO Auto-generated catch
													// block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO
		}

		authenticatedClient.sendAsync(request, new AsyncRequestCallback() {

			@Override
			public void onSuccess(RestResponse response) {
				try
				{
					Toast.makeText(getBaseContext(),
							"New Sentiment Topic created successfully!!!",
							Toast.LENGTH_LONG).show();

					EventsObservable.get().notifyEvent(
							EventType.RenditionComplete);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Exception exception) {
				Toast.makeText(getBaseContext(), "Error while Saving!!!",
						Toast.LENGTH_LONG).show();
				EventsObservable.get().notifyEvent(EventType.RenditionComplete);
			}
		});

		//setContentView(R.layout.main);

	}


	/**
	 * Called when "Home" button is clicked.
	 * 
	 * @param v
	 */
	public void onHomeClick(View v) {
		setContentView(R.layout.main);
	}

}