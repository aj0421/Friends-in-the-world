package com.example.friends_in_the_world;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.friends_in_the_world.Classes.Group;
import com.example.friends_in_the_world.Classes.Member;
import com.example.friends_in_the_world.Classes.Message;
import com.example.friends_in_the_world.Controllers.MainController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    MapView mMapView;
    private MainController controller;
    private Marker userMark;
    private String ID;
    private Member user;
    private Map<String, Marker> markers;
    private LocationListener locationListener;
    private LocationManager locationManager;

    // Updating the view
    Observer<Member> markUser = (Member user) -> {
        if (user != null) {
            this.user = user;
            if (user.getCoordinates() != null) {
                setUserMark();
            }
        }
    };

    Observer<Group> markMembers = (Group group) -> {
        for (Marker m : markers.values()) {
            m.remove();
        }
        markers.clear();
        if (group != null) {
            ID = group.getID();
            // Mark the members
            for (Member member : group.getMembers()) {
                if (member.getCoordinates() != null && !member.getName().equals(user.getName())) {
                    markers.put(member.getName(), mark(member));
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = (MainController) getActivity().getApplication();
        markers = new HashMap<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        controller.getUser().observe(this,markUser);
        controller.getCurrentGroup().observe(this,markMembers);
    }

    private void setUserMark() {
        LatLng coordinates = user.getCoordinates();
        if (userMark == null) {
            userMark = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(user.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            mMap.moveCamera((CameraUpdateFactory.newLatLng(coordinates)));
        } else {
            userMark.setPosition(coordinates);
        }
    }
    private Marker mark(Member member) {
        return mMap.addMarker(new MarkerOptions()
                .position(member.getCoordinates())
                .title(member.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    private final class MapLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            LatLng coordinates = new LatLng(loc.getLatitude(), loc.getLongitude());
            controller.location(null, coordinates);
            if (ID != null) {
                controller.send(Message.location(ID, coordinates));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    }
    public void startLocating() {
        if (locationListener != null) {
            return;
        }
        FragmentActivity act = getActivity();
        if (act == null) {
            return;
        }
        try {
            locationManager = (LocationManager) act
                    .getSystemService(Context.LOCATION_SERVICE);
        } catch (Exception e) {

            return;
        }
        locationListener = new MapLocationListener();

        if (PackageManager.PERMISSION_GRANTED == ActivityCompat
                .checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) ||
                PackageManager.PERMISSION_GRANTED == ActivityCompat
                        .checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 30000, 0, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.location_fragment, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
            locationListener = null;
        }
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}