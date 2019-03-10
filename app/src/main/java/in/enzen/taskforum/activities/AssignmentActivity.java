package in.enzen.taskforum.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.adapters.AssignmentPagerAdapter;
import in.enzen.taskforum.sliding.SlidingTabLayout;
import in.enzen.taskforum.utils.PreferencesManager;

/**
 * Created by Rupesh on 08-12-2017.
 */
@SuppressWarnings("ALL")
public class AssignmentActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private SlidingTabLayout stLibrary;
    private ViewPager vpLibrary;

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.fragment_assignment);

        init();
    }

    private void init() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);

        Typeface appFontRegular = Typeface.createFromAsset(getAssets(), "fonts/madras_regular.ttf");

        TextView tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);

        tvToolbarTitle.setText("assignment");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getSupportFragmentManager().popBackStackImmediate();
                finish();
            }
        });

        ImageView ivLogout = (ImageView) findViewById(R.id.ivLogout);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });

        ImageView ivSos = (ImageView) findViewById(R.id.ivSos);
        ivSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGoogleAPIClient();
                startSOS();
            }
        });

        tvToolbarTitle.setTypeface(appFontRegular);

        stLibrary = (SlidingTabLayout) findViewById(R.id.stAssignment);
        vpLibrary = (ViewPager) findViewById(R.id.vpAssignment);

        AssignmentPagerAdapter mAssignmentPagerAdapter = new AssignmentPagerAdapter(getSupportFragmentManager(), AssignmentActivity.this);
        stLibrary.setCustomTabView(R.layout.tab_title_item, R.id.tvTabName);
        stLibrary.setDistributeEvenly(true);
        vpLibrary.setAdapter(mAssignmentPagerAdapter);
        try {
            stLibrary.setViewPager(vpLibrary);
        } catch (Exception e) {
            Log.i("FF", e.getMessage());
        }
    }

    public void showLogoutAlertDialog() {
        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AssignmentActivity.this);

        // set title
        alertDialogBuilder.setTitle("Confirmation");

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(AssignmentActivity.this, SplashActivity.class));
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

    private void startSOS() {
        //Check Permission
        checkPermissions();
        Intent intent = new Intent(AssignmentActivity.this, SOSActivity.class);
        startActivity(intent);
    }

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(AssignmentActivity.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(AssignmentActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AssignmentActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(AssignmentActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(AssignmentActivity.this,
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
                        Toast.makeText(AssignmentActivity.this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(AssignmentActivity.this, REQUEST_CHECK_SETTINGS);
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
                        Toast.makeText(AssignmentActivity.this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        Toast.makeText(AssignmentActivity.this, "GPS is Disabled in your device", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }
}
