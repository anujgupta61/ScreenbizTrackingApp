package com.screenbiz.screenbiztrackingapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback , AdapterView.OnItemSelectedListener , GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener , LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;               // creating google API client
    private LatLng current ;
    private float total_dist ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1 );
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Spinner spinner = (Spinner) findViewById(R.id.tech_id_spinner) ;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this , R.array.tech_ids , android.R.layout.simple_spinner_item) ;
        adapter . setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner . setAdapter(adapter);
        spinner . setOnItemSelectedListener(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        TextView txtv = (TextView) findViewById(R.id.travelled_dist) ;
        String str = "0 meters" ;
        txtv . setText(str);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        ShowGPSSettings(MapsActivity.this) ;
        mGoogleApiClient.connect();
    }

    @Override
    protected  void onStop()
    {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(500);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest , this);
        } catch (SecurityException e) {
            //
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "on location changed ...", Toast.LENGTH_SHORT).show();
        LatLng old = current ;
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        current = new LatLng(latitude , longitude) ;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current , 19.0f));
        //mMap.addMarker(new MarkerOptions().position(current).title("current marker"));
        Log.v("Track" , "Current : Lat - " + current . latitude + " , Lng - " + current . longitude);
        if(old != null && current != null) {
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            PolylineOptions polyLineOptions = new PolylineOptions();
            points.add(old);
            points.add(current);
            float[] results = new float[1];
            Location.distanceBetween(old.latitude, old.longitude,
                    current.latitude, current.longitude,
                    results);
            polyLineOptions.addAll(points);
            polyLineOptions.width(7);
            polyLineOptions.color(Color.BLUE);
            mMap.addPolyline(polyLineOptions);
            //DrawPath path1 = new DrawPath(mMap);
            //float dist = path1.draw(old, current);
            total_dist += results[0] ;
            TextView txtv = (TextView) findViewById(R.id.travelled_dist) ;
            String str = total_dist + " meters" ;
            txtv . setText(str);
        }
        //Toast.makeText(this, "Total distance : " + total_dist + " meters " , Toast.LENGTH_SHORT).show();
    }

    // Function to take run-time permissions in Android version >= Marshmallow
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ShowGPSSettings(this) ;
                this.recreate() ;
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getApplicationContext(), "You must grant permission to access the gps and use map ...", Toast.LENGTH_LONG).show();
            }
        }
    }

    boolean checkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected ;
    }

    // Function to check GPS connectivity and show alert dialog if there is no GPS
    public void ShowGPSSettings(Activity activity) {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if(! lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // If GPS is disabled
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("NO GPS")
                        .setMessage("Please select High Accuracy Location Mode")
                        .setCancelable(true)
                        .setPositiveButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog . cancel() ;
                            }
                        })
                        .setNegativeButton("GPS Settings",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) ;
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } catch(Exception e) {
                //
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent , View view , int pos , long id) {
        String tech_id = parent . getItemAtPosition(pos) . toString() ;
        Toast . makeText(this , tech_id , Toast.LENGTH_SHORT) . show() ;
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_history) {
            Intent intent = new Intent(this , History.class) ;
            startActivity(intent) ;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap ;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        LatLng sydney1 = new LatLng(-34.09 , 151.0002) ;
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(sydney1).title("Marker1 in Sydney"));
        //DrawPath path1 = new DrawPath(mMap) ;
        //path1 . draw(sydney , sydney1);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
