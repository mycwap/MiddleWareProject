package ie.ucc.middlewareproject;

import java.io.IOException;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Address;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends Activity implements OnItemSelectedListener {
    Spinner spinner;
    Location location = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {


    }

    class getLocationAsyncTask extends AsyncTask<Void, Void, String[]> {

        @SuppressLint("ShowToast")

        @Override
        protected String[] doInBackground(Void... params) {

            String[] locationString = new String[5];

            if (location != null) {
                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
                    try {

                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);

                        if (addresses != null) {

                            for (int i = 0; i < addresses.size(); i++) {
                                Address address = addresses.get(i);
                                StringBuilder strReturnedAddress = new StringBuilder();

                                int n = address.getMaxAddressLineIndex();
                                for (int a = 0; a <= n; a++) {
                                    strReturnedAddress.append(
                                            address.getAddressLine(a)).append(
                                            ",");
                                }
                                ;
                                locationString[i] = strReturnedAddress.toString();
                            }
                            ;
                        } else {

                            showToastInAsync("Can't get address");


                        }
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.v("Add", "ERROR : " + e);
                    e.printStackTrace();
                }
            } else {
                showToastInAsync("Can't get coordinates");
            }

            return locationString;
        }

    }

    private Location bestLastKnownLocation() {
        Boolean isWifiEnable = false;
        Boolean isGPSEnable = false;
        Location location1 = null;
        // Acquire a reference to the system Location Manager
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    //    location1 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        isGPSEnable = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isWifiEnable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        // Define a listener that responds to location updates
        LocationListener locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
               // makeUseOfNewLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (isGPSEnable == false && isWifiEnable == false) {
            // no network provider is enabled
            showToastInAsync("No provider");
        } else {
            if(isWifiEnable) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d("Network", "Network");
            }else {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.d("GPS", "GPS");
            }
        }
        lm.removeUpdates(locationListener);
        return location1;
    }

    public void getAddress(View view) {
        location = bestLastKnownLocation();
        String[] test = {};

        try {
            test = new getLocationAsyncTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        boolean value = false;
        for (int i=0; i<test.length; i++) {
            if (test[i] != null) {
                value = false;
                break;
            }
        }

        if (value) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, test);
            // Apply the adapter to the spinner

            spinner.setAdapter(adapter);
            getNotification();
        }else {
            showToastInAsync("no address");
        }

    }

    private void getNotification() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.mario)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void showToastInAsync(final String toast) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
