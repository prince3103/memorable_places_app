package com.example.memorableplacesupdated;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Intent intent;
    Location geocoderLocation;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults!=null && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    int _intent = intent.getIntExtra("placeNumber",-1);
                    if(_intent==0){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000,0 ,locationListener);
                    }
                }
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                address= "Your Location";
                geocoderLocation = location;
                LocationName();

                addMarker(location, address);
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

        intent = getIntent();
        System.out.println(intent.getIntExtra("placeNumber",-1));
        int _intent = intent.getIntExtra("placeNumber",-1);

        if(_intent==0){
            mMap.setOnMapLongClickListener(this);
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,60000,0,locationListener);

            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        } else {
            address= "Your Location";
            Location _location = new Location(LocationManager.GPS_PROVIDER);
            _location.setLongitude(MainActivity.location.get(_intent).longitude);
            _location.setLatitude(MainActivity.location.get(_intent).latitude);


            address= "Your Location";
            geocoderLocation = _location;
            LocationName();

            addMarker(_location, address);
        }



//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        address="Your Location";
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);



        address= "Your Location";
        geocoderLocation = location;
        LocationName();

        addMarker(location,address);

        MainActivity.location.add(latLng);
        MainActivity.places.add(address);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        Toast.makeText(this,"Location Saved", Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.memorableplacesupdated", Context.MODE_PRIVATE);
        try {
            ArrayList<String> serializeLat= new ArrayList<>();
            ArrayList<String> serializeLon = new ArrayList<>();

            for (LatLng latLng1: MainActivity.location){
                serializeLat.add(Double.toString(latLng1.latitude));
                serializeLon.add(Double.toString(latLng1.longitude));
            }

            sharedPreferences.edit().putString("places5",ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("lat5",ObjectSerializer.serialize(serializeLat)).apply();
            sharedPreferences.edit().putString("lon5",ObjectSerializer.serialize(serializeLon)).apply();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void addMarker(Location location, String address){

        mMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));


    }

    public void LocationName(){
        try {

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(geocoderLocation.getLatitude(), geocoderLocation.getLongitude(), 1);
            System.out.println(addressList);
            address = "Your Location";
            if (addressList != null) {
                if (addressList.get(0).getSubThoroughfare() != null) {
                    address = addressList.get(0).getSubThoroughfare() + " ";
                }
                if (addressList.get(0).getThoroughfare() != null) {
                    if(!address.equals("Your Location"))
                    address += addressList.get(0).getThoroughfare() + " ";
                    else
                        address = addressList.get(0).getThoroughfare() + " ";
                }
                    if (addressList.get(0).getAdminArea() != null) {
                        if(!address.equals("Your Location"))
                        address += addressList.get(0).getAdminArea() ;
                        else
                            address = addressList.get(0).getAdminArea() ;

                    }



            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

//    private class LocationName extends AsyncTask<String,Void,String>{
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                String address="";
//                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//                List<Address> addressList = geocoder.getFromLocation(geocoderLocation.getLatitude(),geocoderLocation.getLongitude(),1);
//                if(addressList!=null){
//                    if(addressList.get(0).getThoroughfare()!=null){
//                        if(addressList.get(0).getSubThoroughfare()!=null){
//                            address+=addressList.get(0).getSubThoroughfare()+" ";
//                        }
//                        address+=addressList.get(0).getThoroughfare();
//                    }
//                }
//                return address;
//            } catch (Exception e){
//                e.printStackTrace();
//                return null;
//            }
//
//
//
//        }
//    }
}
