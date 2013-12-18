package com.skynet.nearyby;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skynet.nearyby.async.AsyncPlaceListener;
import com.skynet.nearyby.async.GooglePlacesAsyncTask;
import com.skynet.nearyby.model.Place;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener, OnInfoWindowClickListener, AsyncPlaceListener, LocationListener {
	
	private static final long MIN_TIME = 400;
	private static final float MIN_DISTANCE = 1000;
	
	private GoogleMap googleMap;
	private UiSettings uiSettings;
	private LocationManager locationManager;
	
	private ImageButton button;
	private EditText editText;

	private List<Marker> markerList;
	private List<Place> placeList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		init();
	}
	
	private void init() {
		setUpMapIfNeeded();
		button = (ImageButton) findViewById(R.id.btn_search);
		button.setOnClickListener(this);
		editText = (EditText) findViewById(R.id.edit_text_search);
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					performSearch();
					return true;
				}
				return false;
			}
		});
						
		markerList = new ArrayList<Marker>();
		placeList = new ArrayList<Place>();
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE,
				(LocationListener) this);
	}
	
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
        	googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }
    
    private void setUpMap() {
    	googleMap.setMyLocationEnabled(true);
    	
    	Location myLocation = googleMap.getMyLocation();
    	if(myLocation != null)
    		onLocationChanged(myLocation);
    	
    	googleMap.setOnInfoWindowClickListener(this);
    	
    	uiSettings = googleMap.getUiSettings();
    	uiSettings.setCompassEnabled(true);
    	uiSettings.setMyLocationButtonEnabled(true);
    	uiSettings.setZoomControlsEnabled(true);
    	uiSettings.setZoomGesturesEnabled(true);
    	uiSettings.setTiltGesturesEnabled(false);
    }

    private void performSearch() {
    	hideSoftKeyBoard();
		Location myLoaction = googleMap.getMyLocation();
		
		if(editText.getText().toString().length() != 0)
			if(myLoaction != null)
				new GooglePlacesAsyncTask(this).execute(new String[]{Double.toString(myLoaction.getLatitude()), Double.toString(myLoaction.getLongitude()), editText.getText().toString()});
    }
    
	@Override
	public void doStuff(List<Place> placeList) {

		this.placeList = placeList;
		
		if(markerList.size() != 0) {
			for(Marker marker : markerList) {
				marker.remove();
			}
			markerList.clear();
		}
		
		// Draw place icons on the map
		if(placeList.size() != 0) {
			for(Place place : placeList) {
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(new LatLng(place.getLat(), place.getLng()));
				markerOptions.title(place.getName());
				markerOptions.snippet(place.getAddr());
				
				markerList.add(googleMap.addMarker(markerOptions));
			}
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
	    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
	    googleMap.animateCamera(cameraUpdate);
	    locationManager.removeUpdates(this);
	    
		Toast.makeText(this, "Location : " + Double.toString(latLng.latitude) + ", " + Double.toString(latLng.longitude), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v) {
		performSearch();
	}
	
	@Override
	public void onInfoWindowClick(Marker marker) {
		String addr = marker.getSnippet();
		for(Place place : placeList) {
			if(place.getAddr().contains(addr)) {
				AlertDialog.Builder ab = null;
				ab = new AlertDialog.Builder(this);
				ab.setMessage(place.getJson());
				ab.setPositiveButton(android.R.string.ok, null);
				ab.setTitle("Json Result");
				ab.show(); 
			}
		}
	}
	
	private void hideSoftKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm.isAcceptingText())// verify if the soft keyboard is open
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
