package in.enzen.taskforum.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import in.enzen.taskforum.adapters.TargetAdapter;
import in.enzen.taskforum.utils.PreferencesManager;

@SuppressWarnings("ALL")
public class TargetActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Typeface appFontRegular;
    private String[] sGroupItemsTitle;
    private Drawable[] drawablesGroupItem;
    private TargetAdapter mTargetAdapter;
    private TextView tvEnforcement, tvEnforcementName, tvGRF, tvGrfName;
    ImageView imgEnforcement, imgIconGrf;
    private LinearLayout llEnforcement, llGRF;

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        init();
    }

    @SuppressLint("NewApi")
    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);

        appFontRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/madras_regular.ttf");
        TextView tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);

        tvToolbarTitle.setText("Achivement");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getSupportFragmentManager().popBackStackImmediate();
                finish();
            }
        });

        ImageView ivLogout = findViewById(R.id.ivLogout);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });

        ImageView ivSos = findViewById(R.id.ivSos);
        ivSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGoogleAPIClient();
                startSOS();
            }
        });

        TextView tvCollectionTarget = findViewById(R.id.tvCollectionTarget);
        TextView tvDisconnectionTarget = findViewById(R.id.tvDisconnectionTarget);
        TextView tvDisconnectionSquad = findViewById(R.id.tvDisconnectionSquad);
        RecyclerView rvCollectionTarget = findViewById(R.id.rvCollectionTarget);
        tvEnforcement = findViewById(R.id.tvEnforcement);
        tvEnforcementName = findViewById(R.id.tvEnforcementName);
        tvGRF = findViewById(R.id.tvGRF);
        tvGrfName = findViewById(R.id.tvGrfName);
        llEnforcement = findViewById(R.id.llEnforcement);
        llGRF = findViewById(R.id.llGRF);
        rvCollectionTarget.setHasFixedSize(true);
        RecyclerView rvDisconnectionTarget = findViewById(R.id.rvDisconnectionTarget);
        rvDisconnectionTarget.setHasFixedSize(true);
        RecyclerView rvDisconnectionSquad = findViewById(R.id.rvDisconnectionSquad);
        rvDisconnectionSquad.setHasFixedSize(true);

        tvToolbarTitle.setTypeface(appFontRegular);
        tvCollectionTarget.setTypeface(appFontRegular);
        tvDisconnectionTarget.setTypeface(appFontRegular);
        tvDisconnectionSquad.setTypeface(appFontRegular);
        tvEnforcement.setTypeface(appFontRegular);
        tvEnforcementName.setTypeface(appFontRegular);
        tvGRF.setTypeface(appFontRegular);
        tvGrfName.setTypeface(appFontRegular);

        llEnforcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(TargetActivity.this, EnforcementActivity.class));
            }
        });

        llGRF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(TargetActivity.this, GRFActivity.class));
            }
        });

        rvCollectionTarget.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDisconnectionTarget.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDisconnectionSquad.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        // Collection Target
        sGroupItemsTitle = new String[2];
        sGroupItemsTitle[0] = "LT";
        sGroupItemsTitle[1] = "HT";
        drawablesGroupItem = new Drawable[2];
        drawablesGroupItem[0] = getApplicationContext().getDrawable(R.drawable.ic_collection_target);
        drawablesGroupItem[1] = getApplicationContext().getDrawable(R.drawable.ic_collection_target);
        mTargetAdapter = new TargetAdapter(TargetActivity.this, sGroupItemsTitle, drawablesGroupItem, 1);
        rvCollectionTarget.setAdapter(mTargetAdapter);

        // Disconnection Target
        sGroupItemsTitle = new String[2];
        sGroupItemsTitle[0] = "LT";
        sGroupItemsTitle[1] = "HT";
        drawablesGroupItem = new Drawable[2];
        drawablesGroupItem[0] = getApplicationContext().getDrawable(R.drawable.ic_disconnection_target);
        drawablesGroupItem[1] = getApplicationContext().getDrawable(R.drawable.ic_disconnection_target);
        mTargetAdapter = new TargetAdapter(TargetActivity.this, sGroupItemsTitle, drawablesGroupItem, 2);
        rvDisconnectionTarget.setAdapter(mTargetAdapter);

        // Disconnection Squad
        sGroupItemsTitle = new String[2];
        sGroupItemsTitle[0] = "LT";
        sGroupItemsTitle[1] = "HT";
        drawablesGroupItem = new Drawable[2];
        drawablesGroupItem[0] = getApplicationContext().getDrawable(R.drawable.ic_disconnection_target);
        drawablesGroupItem[1] = getApplicationContext().getDrawable(R.drawable.ic_disconnection_target);
        mTargetAdapter = new TargetAdapter(TargetActivity.this, sGroupItemsTitle, drawablesGroupItem, 3);
        rvDisconnectionSquad.setAdapter(mTargetAdapter);
    }

    public void showLogoutAlertDialog() {
        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TargetActivity.this);

        // set title
        alertDialogBuilder.setTitle("Confirmation");

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(TargetActivity.this, SplashActivity.class));
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
        Intent intent = new Intent(TargetActivity.this, SOSActivity.class);
        startActivity(intent);
    }

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(TargetActivity.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(TargetActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(TargetActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(TargetActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(TargetActivity.this,
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
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(TargetActivity.this, REQUEST_CHECK_SETTINGS);
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
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        break;
                }
                break;
        }
    }
}
