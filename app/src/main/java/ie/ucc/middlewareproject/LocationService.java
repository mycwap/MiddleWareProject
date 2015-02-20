package ie.ucc.middlewareproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service implements LocationListener {
    Boolean isWifiEnable;
    Boolean isGPSEnable;
    Context context;
    Location location;
    private static final long MIN_DISTANCE = 10;
    private static final long MIN_TIME = 1000 * 60;

    public LocationService(Context context) {

        this.context = context;
        bestLastKnownLocation();
    }

    public Location getLocation() {
        return location;
    }

    public Location bestLastKnownLocation() {

        location = null;
        String provider = null;
        // Acquire a reference to the system Location Manager
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
          // location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        isGPSEnable = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isWifiEnable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        // Define a listener that responds to location updates

        if (isGPSEnable == false && isWifiEnable == false) {
            // no network provider is enabled
            //showToastInAsync("No provider");
        } else {
//            if (isWifiEnable) {
//                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME, MIN_DISTANCE, this);
//                provider = LocationManager.NETWORK_PROVIDER;
//                Log.d("Network", "Network");
//            }
            if (isGPSEnable) {
                //if (lm == null) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                    provider = LocationManager.GPS_PROVIDER;

                    Log.d("GPS", "GPS");
              //  }
            }

            if (lm != null) {
                location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            }
        }

        return location;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {

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
}
