package com.behmetev.gpstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocListenerInterface {
    private TextView tvDistance;
    private TextView tvVelocity;
    private TextView tvCoordinates;
    private Location lastLocation;
    private GeoPoint startLocation;
    private int distance;
    private List<GeoPoint> geoPoints;
    private MyLocListener myLocListener;
    private LocationManager locationManager;
    private MapView map;
    private IMapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);
        init();
    }

    protected void init() {
        map = findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        geoPoints = new ArrayList<>();

        mapController = map.getController();
        mapController.setZoom(17);
        mapController.setCenter(new GeoPoint(51.7125, 39.1604));


        tvVelocity = findViewById(R.id.tvVelocity);
        tvDistance = findViewById(R.id.tvDistance);
        tvCoordinates = findViewById(R.id.tvCoordinates);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLocListener();
        myLocListener.setLocListenerInterface(this);
        checkPermissions();
    }

    public void onResume() {
        super.onResume();
        map.onResume();
    }

    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == RESULT_OK) {
            checkPermissions();
        }/* else {
            Toast.makeText(this, "No GPS permissions", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, myLocListener);
        }
    }

    @Override
    public void onLocationChanged(Location location) {


        if (location.hasSpeed() && lastLocation != null) {
            distance += lastLocation.distanceTo(location);
            geoPoints.add(new GeoPoint(location.getLatitude(),location.getLongitude()));
            mapController.setCenter(new GeoPoint(location.getLatitude(),location.getLongitude()));
            //items.add(new OverlayItem("Title", "Description", new GeoPoint(location.getLatitude(),location.getLongitude()))); // Lat/Lon decimal degrees
        }

        Polyline line = new Polyline();   //see note below!
        line.setPoints(geoPoints);
        map.getOverlayManager().add(line);
/*
        Marker startMarker = new Marker(map);
        startMarker.setPosition(new GeoPoint(51.7125, 39.1604));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(startMarker);
*/


        lastLocation = location;
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());




        tvCoordinates.setText(location.getLatitude() + " " + location.getLongitude());
        tvDistance.setText(String.valueOf(distance));
        tvVelocity.setText(String.valueOf(location.getSpeed()));
    }
}