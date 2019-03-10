package in.enzen.taskforum.utils;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static in.enzen.taskforum.rest.BaseUrl.sLocationUpdate;

/**
 * Created by Rupesh on 3/1/2018.
 */
@SuppressWarnings("ALL")
public class LocationService extends Service implements LocationListener {

    private LocationManager locationManager;
    private static double dLatitute;
    private static double dLongitude;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class LocationServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String sBestProvider = locationManager.getBestProvider(criteria, true);
        Location loc = locationManager.getLastKnownLocation(sBestProvider);
        if (loc != null) {
            onLocationChanged(loc);
        } else {
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null)
                onLocationChanged(loc);
        }

        locationCountDowmTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(LocationService.this, LocationService.class));
    }


    @Override
    public void onLocationChanged(Location location) {
        dLatitute = location.getLatitude();
        dLongitude = location.getLongitude();
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

    private void locationCountDowmTimer() {
        new CountDownTimer(30000, 600000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                sendLocation();
                locationCountDowmTimer();
            }
        }.start();
    }

    private void sendLocation() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sLocationUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String sResult) {
                if (dLatitute == 0.0) {
                    sendLocation();
                }
                if (sResult != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(sResult);

                    } catch (JSONException e) {
                        // mPreferencesManager.setBoolean(LOGIN_STATUS, false);
                        e.printStackTrace();
                        sendLocation();
                    }
                } else {
                    sendLocation();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Dismissing the progress dialog
                Log.e("status Response", String.valueOf(volleyError));
                sendLocation();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(dLatitute));
                params.put("longitude", String.valueOf(dLongitude));
                params.put("token", new PreferencesManager(getApplicationContext()).getString("token"));
                // params.put("user_id", new PreferencesManager(getApplicationContext()).getString(LOGIN_ID));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}