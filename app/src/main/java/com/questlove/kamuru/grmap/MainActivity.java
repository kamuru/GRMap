package com.questlove.kamuru.grmap;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.questlove.kamuru.grmap.util.Utils;

import static com.questlove.kamuru.grmap.util.Utils.REQUEST_CODE_ACCESS_FINE_LOCATION;

@SuppressLint("MissingPermission")
public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private GoogleMap map;
    private Location lastLocation;
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean locationPermissionGranted = false;
    private boolean requestingLocationUpdates = false;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            lastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Log.d(null, "location updated");
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastLocation);
            super.onSaveInstanceState(outState);
        }
    }

    public void zoomIn(View view) {
        map.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void zoomOut(View view) {
        map.animateCamera(CameraUpdateFactory.zoomOut());
    }

    public void myLocationClicked(View view) {
        requestingLocationUpdates = true;
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lastLocation = location;
                map.setMyLocationEnabled(true);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        locationPermissionGranted = Utils.getLocationPermission(this);
        if (locationPermissionGranted) {
            map.setMyLocationEnabled(true);
        }

        Utils.initMyLocationButton(this, map);
        Utils.initCompass(this);
        Utils.setPlaceAutocomplete(this, map);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    map.setMyLocationEnabled(true);
                }
            }
        }
    }
}
