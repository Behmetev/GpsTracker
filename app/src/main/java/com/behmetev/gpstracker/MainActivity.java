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
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocListenerInterface {
    private TextView tvDistance;
    private TextView tvVelocity;
    private TextView tvCoordinates;
    private Location lastLocation;
    private int distance;
    private MyLocListener myLocListener;
    private LocationManager locationManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    protected void init() {
        tvVelocity = findViewById(R.id.tvVelocity);
        tvDistance = findViewById(R.id.tvDistance);
        tvCoordinates = findViewById(R.id.tvCoordinates);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(1000);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLocListener();
        myLocListener.setLocListenerInterface(this);
        checkPermissions();
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
        }
        lastLocation = location;
        tvCoordinates.setText(location.getLatitude() + " " + location.getLongitude());
        tvDistance.setText(String.valueOf(distance));
        tvVelocity.setText(String.valueOf(location.getSpeed()));
    }
}