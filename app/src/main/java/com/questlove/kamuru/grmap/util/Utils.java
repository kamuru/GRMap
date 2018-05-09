package com.questlove.kamuru.grmap.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.questlove.kamuru.grmap.R;

public class Utils {
    public static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 1;

    public static void initMyLocationButton(FragmentActivity activity, GoogleMap map) {
        map.getUiSettings().setMyLocationButtonEnabled(false);

        SupportMapFragment mapFragment = (SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map);
        View mapView = mapFragment.getView();

        View location_button = mapView.findViewWithTag("GoogleMapMyLocationButton");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(location_button.getLayoutParams().width, location_button.getLayoutParams().height); // size of button in dp
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.setMargins(0, 0, 20, 20);
        location_button.setLayoutParams(params);
    }

    public static void initCompass(FragmentActivity activity) {
        SupportMapFragment mapFragment = (SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map);
        View mapView = mapFragment.getView();

        View compass = mapView.findViewWithTag("GoogleMapCompass");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(compass.getLayoutParams().width, compass.getLayoutParams().height); // size of button in dp
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.setMargins(20, 260, 0, 0);
        compass.setLayoutParams(params);
    }

    public static boolean getLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(
                activity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        ActivityCompat.requestPermissions(
                activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_ACCESS_FINE_LOCATION);

        return false;
    }

    public static void setPlaceAutocomplete(Activity activity, final GoogleMap map) {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                activity.getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setFilter(new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("KR").build());
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                map.clear();

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                map.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(null, "An error occurred: " + status);
            }
        });

        final DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);
        LinearLayout view = (LinearLayout) autocompleteFragment.getView();
        ImageButton menuButton = new ImageButton(activity);
        menuButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_menu_black_24dp));
        menuButton.setBackgroundColor(Color.TRANSPARENT);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        view.removeView(view.getChildAt(0));
        view.addView(menuButton, 0);
    }
}
