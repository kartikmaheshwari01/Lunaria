package com.example.lunarphase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bn_view;
    private ViewPager2 viewPager;
    private ViewPagerAdapter adapter;
    private double lat = 0.0;
    private double lng = 0.0;
    private static final int LOCATION_PERMISSION_REQUEST = 999;
    private LocationManager locationManager;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isWorkScheduled = prefs.getBoolean("isWorkScheduled", false);
        if (!isWorkScheduled) {
            PeriodicWorkRequest notificationWork =
                    new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.HOURS)
                            .build();

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "daily_moon_notification",
                    ExistingPeriodicWorkPolicy.KEEP,
                    notificationWork
            );
            prefs.edit().putBoolean("isWorkScheduled", true).apply();
        }



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check location permission
        int fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (fine != PackageManager.PERMISSION_GRANTED || coarse != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST);
        } else {
            fetchAndDisplayLocation();
        }

        // Setup ViewPager and BottomNavigation
        bn_view = findViewById(R.id.bn_view);
        viewPager = findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);


        bn_view.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (id == R.id.nav_calendar) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (id == R.id.nav_facts) {
                    viewPager.setCurrentItem(2);
                    return true;
                } else if (id == R.id.nav_explore) {
                    viewPager.setCurrentItem(3);
                    return true;
                } else if (id == R.id.nav_more) {
                    viewPager.setCurrentItem(4);
                    return true;
                }
                return false;
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bn_view.getMenu().getItem(position).setChecked(true);
            }
        });
        viewPager.setCurrentItem(0);
    }


    @SuppressLint("MissingPermission")
    private void fetchAndDisplayLocation() {
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled && !networkEnabled) {
            Toast.makeText(this, "Please enable location services (GPS or Network)", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        Location loc = null;
        if (networkEnabled) {
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (loc == null && gpsEnabled) {
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (loc != null) {
            updateLocationUI(loc);
        } else {
            String providerToUse = gpsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
            locationManager.requestSingleUpdate(providerToUse, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    updateLocationUI(location);
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    Toast.makeText(MainActivity.this, "Location provider disabled. Please enable it.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {}

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
            }, null);

            Toast.makeText(this, "Waiting for location update...", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocationUI(Location loc) {
        lat = loc.getLatitude();
        lng = loc.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                String country = address.getCountryName();

                TextView tvLocation = findViewById(R.id.tvLocation);
                tvLocation.setText("Location: " + city + ", " + country);
            } else {
                Toast.makeText(this, "Address not found for this location", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error fetching address", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fetchAndDisplayLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public double getLatitude() {
        return lat;
    }
    public double getLongitude() {
        return lng;
    }

}
