package in.enzen.taskforum.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import in.enzen.taskforum.activities.SOSActivity;
import in.enzen.taskforum.adapters.AssignmentPagerAdapter;
import in.enzen.taskforum.sliding.SlidingTabLayout;
import in.enzen.taskforum.utils.PreferencesManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rupesh on 13-11-2017.
 */
@SuppressWarnings("ALL")
public class AssignmentFragment extends Fragment {

    SlidingTabLayout stLibrary;
    ViewPager vpLibrary;
    Toolbar mToolbar;

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_assignment, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View childRoot) {

        mToolbar = (Toolbar) childRoot.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface appFontRegular = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");

        TextView tvToolbarTitle = (TextView) childRoot.findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) childRoot.findViewById(R.id.imgBack);
        ImageView ivSos = (ImageView) childRoot.findViewById(R.id.ivSos);

        tvToolbarTitle.setText("assignment");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        ImageView ivLogout = (ImageView) childRoot.findViewById(R.id.ivLogout);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });

        ivSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGoogleAPIClient();
                startSOS();
            }
        });

        tvToolbarTitle.setTypeface(appFontRegular);

        stLibrary = (SlidingTabLayout)childRoot.findViewById(R.id.stAssignment);
        vpLibrary = (ViewPager)childRoot.findViewById(R.id.vpAssignment);

        AssignmentPagerAdapter mAssignmentPagerAdapter = new AssignmentPagerAdapter(((FragmentActivity)
                getActivity()).getSupportFragmentManager(), getActivity());
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
        final SharedPreferences sp = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Confirmation");

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getActivity()).setLoggedIn(false);
                        startActivity(new Intent(getActivity(), SplashActivity.class));

                       // getActivity().finishAffinity();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void startSOS() {
        //Check Permission
        checkPermissions();
        Intent intent = new Intent(getActivity(), SOSActivity.class);
        startActivity(intent);
    }

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
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
                        // Toast.makeText(getActivity(), "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
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
}
