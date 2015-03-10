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
    Boolean isWifiEnable ;
    Boolean isGPSEnable ;
    LocationService locationService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(this);
        locationService=new LocationService(MainActivity.this);

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


    public void getAddress(View view) {

        location=locationService.getLocation();
        String[] test = {};

        try {
            test = new getLocationAsyncTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        if (test.length>0) {
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

    public void showToastInAsync(final String toast) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
