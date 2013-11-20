package org.assessment.mapsapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final double SEARCH_RADIUS_KM = 2;
	//private static final GeoPoint BERLIN = new GeoPoint(52.51, 13.40);

	private IMapController controller;
	private MapView map;

	private Double latitude = 52.51;   //BERLIN
	private Double longitude = 13.40;
	private LocationManager lm;
	private LocationListener ll;
	private EditText et_latitude;
	private EditText et_longitude;
	private Button btn_gpsLocate;
	private Button btn_go;
	private String provider;
	private Location location;

	protected static StringBuilder jsonstr;
	private String message;
	private List<Car> carList = new ArrayList<Car>();

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String str = bundle.getString("mykey");
			message = str;
			String jsonResponse = message;
			carList = parseJson(jsonResponse);
			List<Car> filteredCarList= filterCarsByDistance(carList, new GeoPoint(latitude, longitude), SEARCH_RADIUS_KM);
			showCarsOnMap(filteredCarList);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn_go = (Button) findViewById(R.id.btn_go);
		btn_go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateLocation(new GeoPoint(latitude,longitude));
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
				//Toast.makeText(getApplicationContext(), "GPS an",
				//Toast.LENGTH_SHORT).show();
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
					
					// make sure we have some location data
					location = lm.getLastKnownLocation(provider);

					if (location != null) {
						latitude = location.getLatitude();
						longitude = location.getLongitude();
						et_latitude.setText(String.valueOf(latitude));
						et_longitude.setText(String.valueOf(longitude));
					}
				}
			}
		});

		initLocation();
	}
	
	private List<Car> parseJson(String dbReturned) {
	
		// Parse JSON and retrieve relevant data

		try {
			JSONObject mainObj = new JSONObject(dbReturned);
			JSONObject vehiclesObj = mainObj.getJSONObject("rec");
			JSONObject vehiclesObj2 = vehiclesObj.getJSONObject("vehicles");
			JSONArray vehicles = vehiclesObj2.getJSONArray("vehicles");
			
			for (int i = 0; i < vehicles.length(); i++) {
				JSONObject jsonVehicle = vehicles.getJSONObject(i);
				JSONObject jsonPosition = jsonVehicle.getJSONObject("position");
				double lat = jsonPosition.getDouble("latitude");
				double lng = jsonPosition.getDouble("longitude");
				String address = jsonPosition.getString("address").toString();
				String name = jsonVehicle.getString("carName").toString();

				Car car = new Car(new GeoPoint(lat, lng), name, address);
				carList.add(car);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return carList;
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

	private void initLocation() {
		this.map = (MapView) this.findViewById(R.id.mapview);
		this.map.setBuiltInZoomControls(true);
		this.controller = map.getController();
		this.controller.setZoom(12);
		this.controller.setCenter(new GeoPoint(latitude,longitude));
		
	}

	private void updateLocation(GeoPoint location) {
		this.controller.setZoom(12);
		this.controller.setCenter(location);

		List<Car> carsInCloseRadius = filterCarsByDistance(getCars(), location,
				SEARCH_RADIUS_KM);

		// ###################################### TO IMPLEMENT

		// Show cars on map

	}

	private List<Car> getCars() {

		// ###################################### TO IMPLEMENT

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

					HttpResponse response = httpclient.execute(httppost);

					InputStream input = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(input));
					MainActivity.jsonstr = new StringBuilder();

					String line = null;
					try {

						while ((line = reader.readLine()) != null) {
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
		// Filter the list of cars by a location within the specified radius
		
		List<Car> filteredCarList = new ArrayList<Car>();
		

		for(Car car : cars) {
			if (((car.position).distanceTo(location) / 1000) < searchRadiusKm) {
				filteredCarList.add(car);
			}

		}
		return filteredCarList;
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
