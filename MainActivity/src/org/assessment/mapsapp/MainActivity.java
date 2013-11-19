package org.assessment.mapsapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final double SEARCH_RADIUS_KM = 2;
	private static final GeoPoint BERLIN = new GeoPoint(52.51, 13.40);

	private IMapController controller;
	private MapView map;

	Double latitude;
	Double longitude;
	LocationManager lm;
	LocationListener ll;
	private EditText et_latitude;
	private EditText et_longitude;
	private Button btn_gpsLocate;
	private Button btn_go;
	private EditText et;
	String provider;
	Location location;

	protected static StringBuilder jsonstr;
	String firstname, lastname;
	String[] array;
	long c_id;
	String message;
	ArrayList<Car> carList;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String str = bundle.getString("mykey");
			message = str;
			String dbReturned = message;

			System.out.println(dbReturned);
			
			 
			ArrayList<String> results = parseJson(dbReturned);
			Log.d("HANDLER", String.valueOf(results.size()));
			/*
			array = new String[results.size()];
			for (int i = 0; i < results.size(); i++) {
				array[i] = results.get(i);
			}

			// ArrayAdapter<String> adapter = new
			// ArrayAdapter<String>(FoundByNameActivity.this,
			// android.R.layout.simple_list_item_1, array);

			// setListAdapter(adapter);
			
			*/
				
		}
	};
	
	//private void parseJson(String dbReturned) {
	private ArrayList<String> parseJson(String dbReturned) {
		ArrayList<String> liste = new ArrayList<String>();
		carList = new ArrayList<Car>();

		Log.d("Anfang von Parsen", "ArrayList erstellt");

		//dbReturned = et.getText().toString();
		System.out.println("Temporaerer workaround: " + dbReturned);
//		File f = new File("school.ser");
//		if (f.exists()) {
//			FileInputStream fis = new FileInputStream("school.ser");
//			dbReturned = fis.;
//			ois.close();
//		}
//		else {
//			wind = new Location("Windscheidstrasse", rooms, modules, students, instructors);
		GeoPoint position = null;
		JSONArray pos = null;
		JSONArray pos2 = null;
//		String name = null;
//		String address = null;
		 
//		try {
//		    // Getting Array of Contacts
//		    contacts = json.getJSONArray(TAG_CONTACTS);
//		     
//		    // looping through All Contacts
//		    for(int i = 0; i < contacts.length(); i++){
//		        JSONObject c = contacts.getJSONObject(i);
//		         
//		        // Storing each json item in variable
//		        String id = c.getString(TAG_ID);
//		        String name = c.getString(TAG_NAME);
//		        String email = c.getString(TAG_EMAIL);
//		        String address = c.getString(TAG_ADDRESS);
//		        String gender = c.getString(TAG_GENDER);
//		         
//		        // Phone number is agin JSON Object
//		        JSONObject phone = c.getJSONObject(TAG_PHONE);
//		        String mobile = phone.getString(TAG_PHONE_MOBILE);
//		        String home = phone.getString(TAG_PHONE_HOME);
//		        String office = phone.getString(TAG_PHONE_OFFICE);
//		         
//		    }
//		} catch (JSONException e) {
//		    e.printStackTrace();
//		}

		Log.d("DEBUG JSON", "Do we get to the point before JSONArray?");
		JSONObject json = null;
		
		try {
			json = new JSONObject(dbReturned);
			Log.d("DEBUG JSON", "Do we get to after JSONOBJECT???");
			System.out.println(json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray jsonarray = null;
		
		
		try {
			jsonarray = json.getJSONArray("rec");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			jsonarray = new JSONArray("vehicles");
//			System.out.println("DO WE GET TO AFTER JSONARRAY?");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		
		
			/////////////////////////////////////////////////////////////
			/*
			pos = json.getJSONArray("err");
			System.out.println("err");
			pos = json.getJSONArray("msg");
			System.out.println("msg");
			//pos = json.getJSONArray("state");
			System.out.println("state");
			
			//pos = json.getJSONArray("rec");
			JSONObject obj = pos.getJSONObject(0);
			System.out.println("rec");
			pos = json.getJSONArray("vehicles");
			System.out.println("vehicles");
			
			
			
			//pos = json.getJSONArray("vehicles");
			//pos = json.getJSONArray("position");
			//pos = json.getJSONArray("latitude");
			
			
			for(int i = 0; i < pos.length(); i++) {
				System.out.println("parseJsonIn FOR Look: " + pos.length());
				JSONObject geopos = pos.getJSONObject(i);
				
				String longi = geopos.getString("longitude");
				String lati = geopos.getString("latitude");
				String address = geopos.getString("address");
			}
			
		*/
		
			
//			for (int i = 0; i < jsonArray.length(); i++) {
//
//				JSONObject jason = jsonArray.getJSONObject(i);
//				String lat = jason.getString("latitude").toString();
//				String lng = jason.getString("longitude").toString();
//				name = jason.getString("carName").toString();
//
//				Car car = new Car(position, name, address);
//				System.out.println("Position: " + position + ", Name: " + name + ", Address: " + address);
//				carList.add(car);
//				Log.d("carList", "Car aufgenommen");
//			}

			Log.d("CARLIST ", "Size: " + carList.size());



		return liste;
	}

	private static class Car {

		public GeoPoint position;
		public String name;
		public String address;

		public Car(GeoPoint position, String name, String address) {
			this.position = position;
			this.name = name;
			this.address = address;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		et = (EditText) findViewById(R.id.editText1);
		
		btn_go = (Button) findViewById(R.id.btn_go);
		btn_go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("BUTTON GO WAS CLICKED!");
				updateLocation(BERLIN);
			}
		});

		et_latitude = (EditText) findViewById(R.id.et_latitude);
		et_longitude = (EditText) findViewById(R.id.et_longitude);

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (lm != null) {

			ll = new MyLocationListener(this);

			if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 5,
						ll);
				// Toast.makeText(getApplicationContext(), "GPS an",
				// Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "GPS aus",
						Toast.LENGTH_SHORT).show();
			}

		}
		btn_gpsLocate = (Button) findViewById(R.id.btn_gpsLocate);
		btn_gpsLocate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("BUTTON GPS LOCATE WAS CLICKED!");
				provider = LocationManager.GPS_PROVIDER;

				if (provider != null && !provider.equals("")) {
					// Toast.makeText(getApplicationContext(), "Provider: " +
					// provider, Toast.LENGTH_SHORT).show();

					// damit man bei Start des Handys nicht total ohne Location
					// dasteht
					location = lm.getLastKnownLocation(provider);

					if (location != null) {
						latitude = location.getLatitude();
						longitude = location.getLongitude();

						Log.d("---LOCATION---", "longitude: " + latitude
								+ " latitude: " + longitude);

						et_latitude.setText(String.valueOf(latitude));
						et_longitude.setText(String.valueOf(longitude));
					}
				}

			}
		});

		initLocation();
	}

	private void initLocation() {
		this.map = (MapView) this.findViewById(R.id.mapview);
		this.map.setBuiltInZoomControls(true);
		this.controller = map.getController();

		//updateLocation(BERLIN);
	}

	private void updateLocation(GeoPoint location) {
		this.controller.setZoom(12);
		this.controller.setCenter(location);

		List<Car> carsInCloseRadius = filterCarsByDistance(getCars(), location,
				SEARCH_RADIUS_KM);

		// ###################################### TO IMPLEMENT
		// ###########################################

		// Show cars on map

	}
	
	private List<Car> getCars() {

		// ###################################### TO IMPLEMENT
		// ###########################################

		// Read the JSON data from
		// https://www.drive-now.com/php/metropolis/json.vehicle_filter?cit=6099
		// Parse the data and fill a list of Car objects

		Runnable r = new Runnable() {

			@Override
			public void run() {
				Log.d("SEARCH BY NAME, LISTACT", "Am Anfang von run");

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"https://www.drive-now.com/php/metropolis/json.vehicle_filter?cit=6099");

				try {
					
//					 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//					 nameValuePairs.add(new BasicNameValuePair("firstname", firstname));
//					 nameValuePairs.add(new BasicNameValuePair("lastname", lastname));
//					 nameValuePairs.add(new BasicNameValuePair("c_id", String.valueOf(c_id)));
//					 httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					 
					 
					HttpResponse response = httpclient.execute(httppost);

					InputStream input = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(input));
					MainActivity.jsonstr = new StringBuilder();

					String line = null;
					try {

						while ((line = reader.readLine()) != null) {
							System.out.println("ReadLine Ausgabe" + line + "\n");
							jsonstr.append((line + "\n"));
						}

						Bundle bundle = new Bundle();
						bundle.putString("mykey", jsonstr.toString());
						Message msg = handler.obtainMessage();
						msg.setData(bundle);
						Log.d("RUNNABLE", "send message to handler");
						handler.sendMessage(msg);

					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						input.close();
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		};

		(new Thread(r)).start();

		return Collections.emptyList();
	}

	private List<Car> filterCarsByDistance(List<Car> cars, GeoPoint location,
			double searchRadiusKm) {

		// ###################################### TO IMPLEMENT
		// ###########################################

		// Filter the list of cars by a location within the specified radius

		return Collections.emptyList();
	}

	private void showCarsOnMap(List<Car> cars) {
		List<OverlayItem> items = new ArrayList<OverlayItem>();

		for (Car car : cars) {
			items.add(new OverlayItem(car.name, car.address, car.position));
		}

		OnItemGestureListener<OverlayItem> listener = new OnItemGestureListener<OverlayItem>() {
			@Override
			public boolean onItemLongPress(int arg0, OverlayItem item) {
				Toast toast = Toast.makeText(getApplicationContext(),
						item.getTitle() + ", " + item.getSnippet(),
						Toast.LENGTH_LONG);
				toast.show();
				return false;
			}

			@Override
			public boolean onItemSingleTapUp(int arg0, OverlayItem item) {
				Toast toast = Toast.makeText(getApplicationContext(),
						item.getTitle() + ", " + item.getSnippet(),
						Toast.LENGTH_LONG);
				toast.show();
				return false;
			}
		};
		ItemizedIconOverlay<OverlayItem> overlay = new ItemizedIconOverlay<OverlayItem>(
				getApplicationContext(), items, listener);

		this.map.getOverlays().clear();
		this.map.getOverlays().add(overlay);
		this.map.invalidate();
	}

	private class MyLocationListener implements LocationListener {

		MainActivity me;

		public MyLocationListener(MainActivity me) {
			this.me = me;
		}

		@Override
		public void onLocationChanged(Location location) {

			latitude = location.getLatitude();
			longitude = location.getLongitude();

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

			if (!provider.equals("")) {
				Toast.makeText(getApplicationContext(), "DISABLED " + provider,
						Toast.LENGTH_SHORT).show();
			}
			Toast.makeText(getApplicationContext(), "kein Provider",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

}
