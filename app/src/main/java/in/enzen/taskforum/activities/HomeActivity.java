package in.enzen.taskforum.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.adapters.HomeFragmentAdapter;
import in.enzen.taskforum.civ.CircleImageView;
import in.enzen.taskforum.model.HomeFragmentBean;
import in.enzen.taskforum.services.MessageService;
import in.enzen.taskforum.utils.KeyNames;
import in.enzen.taskforum.utils.LocationService;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.rest.BaseUrl.sLocationUpdate;

@SuppressWarnings("ALL")
public class HomeActivity extends AppCompatActivity implements KeyNames, LocationListener {

    Toolbar mToolbar;
    GridLayoutManager layoutManager;
    RecyclerView rv_home_fragment;
    PreferencesManager mPreferencesManager;

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    double lattitude;
    double longitude;
    TextView tvCurrentLatLng;

    final String TAG = "GPS";
    private final int ALL_PERMISSIONS_RESULT = 101;
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    LocationManager locationManager;
    private static double dLatitute;
    private static double dLongitute;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);
        startService(new Intent(getApplicationContext(), MessageService.class));
        initialise();
    }

    private void initialise() {
        tvCurrentLatLng = findViewById(R.id.tvCurrentLatLng);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);

        Typeface appFontRegular = Typeface.createFromAsset(getAssets(), "fonts/madras_regular.ttf");

        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        TextView tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        TextView tvCompany = (TextView) findViewById(R.id.tvCompany);

        tvUserName.setTypeface(appFontRegular);
        tvWelcome.setTypeface(appFontRegular);
        tvCompany.setTypeface(appFontRegular);
        ImageView ivLogout = (ImageView) findViewById(R.id.ivLogout);

        tvUserName.setText("HI" + " " + new PreferencesManager(getApplicationContext()).getString(USER_NAME));
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });

        //Init Google API Client
        initGoogleAPIClient();
        //Check Permission
        checkPermissions();

        List<HomeFragmentBean> rowListItem = getAllItemList();
        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        rv_home_fragment = (RecyclerView) findViewById(R.id.rv_home_fragment);

        rv_home_fragment.setHasFixedSize(true);
        rv_home_fragment.setLayoutManager(layoutManager);
        HomeFragmentAdapter adapter = new HomeFragmentAdapter(HomeActivity.this, rowListItem);
        rv_home_fragment.setItemAnimator(new DefaultItemAnimator());
        rv_home_fragment.setAdapter(adapter);

        CircleImageView civUserPic = (CircleImageView) findViewById(R.id.civUserPic);
        Picasso.with(this).load(new PreferencesManager(getApplicationContext()).getString(USER_IMAGE)).into(civUserPic);

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (!isGPS && !isNetwork) {
            Log.d(TAG, "Connection off");
            getLastLocation();
        } else {
            Log.d(TAG, "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d(TAG, "Permission requests");
                    canGetLocation = false;
                }
            }
            // get location
            //getLocation();
        }

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
        locationUpdate();
    }

    public void showLogoutAlertDialog() {
        final SharedPreferences sp = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);

        // set title
        alertDialogBuilder.setTitle("Confirmation");

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(HomeActivity.this, SplashActivity.class));


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private List<HomeFragmentBean> getAllItemList() {
        List<HomeFragmentBean> allItems = new ArrayList<>();

        allItems.add(new HomeFragmentBean(R.drawable.ic_notifications_24px, "Notification"));
        allItems.add(new HomeFragmentBean(R.drawable.ic_calendar_24px, "Calendar"));
        allItems.add(new HomeFragmentBean(R.drawable.ic_assignment_24px, "Assignment"));
        allItems.add(new HomeFragmentBean(R.drawable.ic_task_todo_list_24px, "Jobs"));
        allItems.add(new HomeFragmentBean(R.drawable.ic_collection_target, "Achievement"));
        allItems.add(new HomeFragmentBean(R.drawable.ic_report_24px, "Report"));
        allItems.add(new HomeFragmentBean(R.drawable.ic_chat_24px, "Chat"));
        allItems.add(new HomeFragmentBean(R.drawable.ic_profile_24px, "Profile"));
        allItems.add(new HomeFragmentBean(R.drawable.ic_sos_home, "SOS"));
        allItems.add(new HomeFragmentBean(R.drawable.ic_support_holo_96, "Support"));
        return allItems;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!HomeActivity.this.isDestroyed()) {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            {
                if (count > 1) {
                    getSupportFragmentManager().popBackStack();
                }
            }
        }
    }

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(HomeActivity.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    // Show Location Access Dialog
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        //Setting priotity of Location request to high
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        // This is the key ingredient to show dialog always when GPS is off
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here.
                        // Toast.makeText(HomeActivity.this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        // Toast.makeText(HomeActivity.this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        // Toast.makeText(HomeActivity.this, "GPS is Disabled in your device", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    // On Request permission method to check the permisison is granted or not for Marshmallow+ Devices
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If permission granted show location dialog if APIClient is not null
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();
                } else {
                    Log.e("Settings", "Result Cancel");
                    // Toast.makeText(HomeActivity.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        dLatitute = location.getLatitude();
        dLongitute = location.getLongitude();
        tvCurrentLatLng.setText("" + dLatitute + ", " + dLongitute);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            onLocationChanged(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            onLocationChanged(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    onLocationChanged(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO Last Location" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void locationUpdate() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sLocationUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String sResult) {
                if (dLatitute == 0.0) {
                    locationUpdate();
                }
                if (sResult != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(sResult);

                    } catch (JSONException e) {
                        // mPreferencesManager.setBoolean(LOGIN_STATUS, false);
                        e.printStackTrace();
                        locationUpdate();
                    }
                } else {
                    locationUpdate();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Dismissing the progress dialog
                Log.e("status Response", String.valueOf(volleyError));
                locationUpdate();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(dLatitute));
                params.put("longitude", String.valueOf(dLongitute));
                params.put("token", new PreferencesManager(getApplicationContext()).getString("token"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        startService(new Intent(HomeActivity.this, LocationService.class));
        startService(new Intent(getApplicationContext(), MessageService.class));
    }

}
