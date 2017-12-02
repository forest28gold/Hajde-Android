package com.azizinetwork.hajde.settings;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private Handler timerLocationHandler = new Handler();

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; //    1 minute
    protected LocationManager locationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerLocationHandler.removeCallbacks(timerLocationRunnable);
        Log.d(TAG, "LocationService Stopped!!!");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "LocationService create!!!");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Log.d(TAG, "Get Start Location of Phone!");

        getLocation();

        timerLocationHandler.postDelayed(timerLocationRunnable, 1000 * 30);
    }

    public void getAddress(double lat, double lng) {

//        lat = 41.0569239; // albania
//        lng = 19.9020164;
//
//        lat = 44.5896595; // Bosnia and Herzegovina
//        lng = 17.5182841;
//
//        lat = 42.5918186; // Kosovo =============
//        lng = 20.8399809;
//
//        lat = 41.59501; // Macedonia (FYROM)
//        lng = 21.6374677;
//
//        lat = 42.9977434; // Montenegro
//        lng = 18.7607756;
//
//        lat = 44.2494354; // Serbia
//        lng = 20.7459236;
//
//        lat = 46.8095954; // Switzerland
//        lng = 7.1032335;
//
//        lat = 38.8798727; // Turkey
//        lng = 30.752753;
//
//        lat = 24.85816637664805;
//        lng = 67.07836005631462; // Pakistan
//
//        lat = 51.7138268;
//        lng = 39.1429834; // Russia
//
//        lat = 34.0674;
//        lng = -118.2423; // Los Angelse
//
//        lat = 43.649052;
//        lng = -79.4486917;  // Toronto Canada

        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                Address obj = addresses.get(0);
//            String add = obj.getAddressLine(0);
//            add = add + "\n" + obj.getCountryName();
//            add = add + "\n" + obj.getCountryCode();
//            add = add + "\n" + obj.getAdminArea();
//            add = add + "\n" + obj.getPostalCode();
//            add = add + "\n" + obj.getSubAdminArea();
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getSubThoroughfare();

                Global.g_userInfo.setCountry(obj.getCountryName());
            } else {
                Global.g_userInfo.setCountry(Global.COUNTRY_OTHERS);
            }

        } catch (IOException e) {
            Global.g_userInfo.setCountry(Global.COUNTRY_OTHERS);
        }

        Log.v(TAG, "Address = " + Global.g_userInfo.getCountry());
    }

    private Runnable timerLocationRunnable = new Runnable() {
        @Override
        public void run() {

//            Log.i(TAG, "***************** Location Timer ***********************");

            if (Global.g_userInfo != null) {

                int count = Global.g_userInfo.getSpentTime();
                count += 60;
                Global.g_userInfo.setSpentTime(count);
                GlobalSharedData.getSpentTimeStamp(count);

                if (Global.g_userInfo.getUserID() != null) {
//                Log.i(TAG, "***************** Location Timer : update database ***********************");
                    GlobalSharedData.updateUserDBData();
                }

                timerLocationHandler.postDelayed(this, 1000 * 60);
            }
        }
    };


    public Location getLocation() {
        try {
            locationManager = (LocationManager) getApplication().getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission required");
                return null;
            }

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.e(TAG, "Network and GPS Disabled");
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                    Log.d(TAG, "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Global.g_userInfo.setLatitude((float) latitude);
                            Global.g_userInfo.setLongitude((float) longitude);
                            getAddress(latitude, longitude);
                            Log.d(TAG, "Network My Location : " + "Lat = " + Global.g_userInfo.getLatitude() + ", Lon = " + Global.g_userInfo.getLongitude());
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                        Log.d(TAG, "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                Global.g_userInfo.setLatitude((float) latitude);
                                Global.g_userInfo.setLongitude((float) longitude);
                                getAddress(latitude, longitude);
                                Log.d(TAG, "GPS My Location : " + "Lat = " + Global.g_userInfo.getLatitude() + ", Lon = " + Global.g_userInfo.getLongitude());
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {

        }

        return location;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            if (location != null && Global.g_userInfo != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Global.g_userInfo.setLatitude((float) latitude);
                Global.g_userInfo.setLongitude((float) longitude);

                getAddress(latitude, longitude);

                Log.d(TAG, "My Location : " + "Lat = " + Global.g_userInfo.getLatitude() + ", Lon = " + Global.g_userInfo.getLongitude());
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

}
