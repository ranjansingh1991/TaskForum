package in.enzen.taskforum.activities;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import in.enzen.taskforum.adapters.JobsTodoAdapter;
import in.enzen.taskforum.utils.PreferencesManager;

/**
 * Created by Rupesh on 21-12-2017.
 */
@SuppressWarnings("ALL")
public class JobsTodoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView ivJM_Notification;
    private Typeface appFontRegular;
    private TextView tvGroupOneTitle;
    private TextView tvGroupTwoTitle;
    private TextView tvSDO;
    private TextView tvGroupAMC;
    private TextView tvGroupThird;
    private RecyclerView rvGroupOne;
    private RecyclerView rvGroupTwo;
    private RecyclerView rvSDO;
    private RecyclerView rvAMC;
    private RecyclerView rvGroupThird;
    private JobsTodoAdapter mJobsTodoAdapter;

    private String[] sGroupItemsTitle;
    private Drawable[] drawablesGroupItem;

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_todo);
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);

        appFontRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/madras_regular.ttf");
        // appFontRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/app_font_regular.ttf");
        // appFontRegular = Typeface.createFromAsset(getAssets(), "fonts/app_font_secondary_regular.ttf");
//         appFontRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/app_font_roboto_regular.ttf");

        TextView tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);

        tvToolbarTitle.setText("Jobs");
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

        ivJM_Notification = (ImageView) findViewById(R.id.ivJM_Notification);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        ivJM_Notification.startAnimation(shake);

        tvGroupOneTitle = (TextView) findViewById(R.id.tvGroupOneTitle);
        tvGroupTwoTitle = (TextView) findViewById(R.id.tvGroupTwoTitle);
        tvSDO = (TextView) findViewById(R.id.tvSDO);
        tvGroupAMC = (TextView) findViewById(R.id.tvGroupAMC);
        tvGroupThird = (TextView) findViewById(R.id.tvGroupThird);

        rvGroupOne = (RecyclerView) findViewById(R.id.rvGroupOne);
        rvGroupOne.setHasFixedSize(true);
        rvGroupTwo = (RecyclerView) findViewById(R.id.rvGroupTwo);
        rvGroupTwo.setHasFixedSize(true);
        rvSDO = (RecyclerView) findViewById(R.id.rvSDO);
        rvSDO.setHasFixedSize(true);
        rvAMC = (RecyclerView) findViewById(R.id.rvAMC);
        rvAMC.setHasFixedSize(true);
        rvGroupThird = (RecyclerView) findViewById(R.id.rvGroupThird);
        rvGroupThird.setHasFixedSize(true);

        tvGroupOneTitle.setTypeface(appFontRegular);
        tvGroupTwoTitle.setTypeface(appFontRegular);
        tvSDO.setTypeface(appFontRegular);
        tvGroupAMC.setTypeface(appFontRegular);
        tvGroupThird.setTypeface(appFontRegular);

        rvGroupOne.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rvGroupTwo.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSDO.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rvAMC.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rvGroupThird.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        // JM(COM)
        sGroupItemsTitle = new String[18];
        sGroupItemsTitle[0] = "CHECK METER READING";
        sGroupItemsTitle[1] = "Daily Input Reading";
        sGroupItemsTitle[2] = "CURRENT BILL COLLECTION PROGRESS";
        sGroupItemsTitle[3] = "ARREAR DRIVE";
        sGroupItemsTitle[4] = "INSTALLATION VERIFICATION";
        sGroupItemsTitle[5] = "DEHOOKING DRIVES";
        sGroupItemsTitle[6] = getApplicationContext().getResources().getString(R.string._3_ph_meter_reading);
        sGroupItemsTitle[7] = "STAFF MEETING";
        sGroupItemsTitle[8] = "No of Disconnection 1 PH com";
        sGroupItemsTitle[9] = "No of Disconnection 1 PH dom";
        sGroupItemsTitle[10] = "No of RC";
        sGroupItemsTitle[11] = "Arrear Collection against RC";
        sGroupItemsTitle[12] = "Identification of Unauthorise Consumer";
        sGroupItemsTitle[13] = "Formation of Village Committe";
        sGroupItemsTitle[14] = "Days Total Collection AMt";
        sGroupItemsTitle[15] = "Days Total Collection MR";
        sGroupItemsTitle[16] = "No of Commercial Complain Received";
        sGroupItemsTitle[17] = "Recovery from Bill Revision Cases";
        drawablesGroupItem = new Drawable[19];
        drawablesGroupItem[0] = getApplicationContext().getDrawable(R.drawable.ic_meter_reading);
        drawablesGroupItem[1] = getApplicationContext().getDrawable(R.drawable.ic_daily_input_reading);
        drawablesGroupItem[2] = getApplicationContext().getDrawable(R.drawable.ic_bill_revision);
        drawablesGroupItem[3] = getApplicationContext().getDrawable(R.drawable.ic_arrear_drive);
        drawablesGroupItem[4] = getApplicationContext().getDrawable(R.drawable.ic_installation_verification);
        drawablesGroupItem[5] = getApplicationContext().getDrawable(R.drawable.ic_dehooking_drives);
        drawablesGroupItem[6] = getApplicationContext().getDrawable(R.drawable.ic_3ph_meter_reading);
        drawablesGroupItem[7] = getApplicationContext().getDrawable(R.drawable.ic_staff_meeting);
        drawablesGroupItem[8] = getApplicationContext().getDrawable(R.drawable.ic_disconnection);
        drawablesGroupItem[9] = getApplicationContext().getDrawable(R.drawable.ic_disconnection);
        drawablesGroupItem[10] = getApplicationContext().getDrawable(R.drawable.ic_no_of_rc);
        drawablesGroupItem[11] = getApplicationContext().getDrawable(R.drawable.ic_arrear_drive);
        drawablesGroupItem[12] = getApplicationContext().getDrawable(R.drawable.ic_unauthorize_consumer);
        drawablesGroupItem[13] = getApplicationContext().getDrawable(R.drawable.ic_village_meeting);
        drawablesGroupItem[14] = getApplicationContext().getDrawable(R.drawable.ic_total_collection);
        drawablesGroupItem[15] = getApplicationContext().getDrawable(R.drawable.ic_total_collection);
        drawablesGroupItem[16] = getApplicationContext().getDrawable(R.drawable.ic_complain_received);
        drawablesGroupItem[17] = getApplicationContext().getDrawable(R.drawable.ic_bill_recovery_revision);
        mJobsTodoAdapter = new JobsTodoAdapter(JobsTodoActivity.this, sGroupItemsTitle, drawablesGroupItem, 1);
        rvGroupThird.setAdapter(mJobsTodoAdapter);
        // rvGroupThird.setItemAnimator(new DefaultItemAnimator());

        // JM (O&M)
        sGroupItemsTitle = new String[19];
        sGroupItemsTitle[0] = "DT SANITISATION";
        sGroupItemsTitle[1] = "LOAD BALANCING";
        sGroupItemsTitle[2] = "TREE TREEMING";//PREVENTIVE MAINTENANACE
        sGroupItemsTitle[3] = "NEW CONNECTION VERIFICATION";
        sGroupItemsTitle[4] = "TEMPORARY CONNECTION VERIFICATION";
        sGroupItemsTitle[5] = "PREVENTIVE MAINTENANACE";
        sGroupItemsTitle[6] = getApplicationContext().getResources().getString(R.string._3_ph_meter_reading);
        sGroupItemsTitle[7] = "DEHOOKING DRIVES";
        sGroupItemsTitle[8] = "STAFF MEETING";
        sGroupItemsTitle[9] = "CHECK METER READING";
        sGroupItemsTitle[10] = "Pole Replacement/Interposing";
        sGroupItemsTitle[11] = "Identification of Unauthorise Consumer";
        sGroupItemsTitle[12] = "Formation of Village Committe";
        sGroupItemsTitle[13] = "Line Spacer";
        sGroupItemsTitle[14] = "Safety Work";
        sGroupItemsTitle[15] = "DTR Energy Audit";
        sGroupItemsTitle[16] = "DT Fencing";
        sGroupItemsTitle[17] = "Daily Input Reading";
        sGroupItemsTitle[18] = "Occurence of electrical Accident";
        drawablesGroupItem = new Drawable[19];
        drawablesGroupItem[0] = getApplicationContext().getDrawable(R.drawable.ic_dt_sanitisation);
        drawablesGroupItem[1] = getApplicationContext().getDrawable(R.drawable.ic_load_balancing);
        drawablesGroupItem[2] = getApplicationContext().getDrawable(R.drawable.ic_tree_treeming);
        drawablesGroupItem[3] = getApplicationContext().getDrawable(R.drawable.ic_new_connection_verification);
        drawablesGroupItem[4] = getApplicationContext().getDrawable(R.drawable.ic_temporary_verification);
        drawablesGroupItem[5] = getApplicationContext().getDrawable(R.drawable.ic_preventive_maintenanace);
        drawablesGroupItem[6] = getApplicationContext().getDrawable(R.drawable.ic_3ph_meter_reading);
        drawablesGroupItem[7] = getApplicationContext().getDrawable(R.drawable.ic_dehooking_drives);
        drawablesGroupItem[8] = getApplicationContext().getDrawable(R.drawable.ic_staff_meeting);
        drawablesGroupItem[9] = getApplicationContext().getDrawable(R.drawable.ic_meter_reading);
        drawablesGroupItem[10] = getApplicationContext().getDrawable(R.drawable.ic_pole_replacement);
        drawablesGroupItem[11] = getApplicationContext().getDrawable(R.drawable.ic_unauthorize_consumer);
        drawablesGroupItem[12] = getApplicationContext().getDrawable(R.drawable.ic_village_meeting);
        drawablesGroupItem[13] = getApplicationContext().getDrawable(R.drawable.ic_line_spacer);
        drawablesGroupItem[14] = getApplicationContext().getDrawable(R.drawable.ic_safety_work);
        drawablesGroupItem[15] = getApplicationContext().getDrawable(R.drawable.ic_energy_audit);
        drawablesGroupItem[16] = getApplicationContext().getDrawable(R.drawable.ic_fencing);
        drawablesGroupItem[17] = getApplicationContext().getDrawable(R.drawable.ic_daily_input_reading);
        drawablesGroupItem[18] = getApplicationContext().getDrawable(R.drawable.ic_electrical_accidents);
        mJobsTodoAdapter = new JobsTodoAdapter(JobsTodoActivity.this, sGroupItemsTitle, drawablesGroupItem, 2);
        rvGroupOne.setAdapter(mJobsTodoAdapter);
        // rvGroupOne.setItemAnimator(new DefaultItemAnimator());

        // JM(MRT)
        sGroupItemsTitle = new String[9];
        sGroupItemsTitle[0] = "CHECK METER READING";
        sGroupItemsTitle[1] = getApplicationContext().getResources().getString(R.string._3_ph_meter_reading);
        sGroupItemsTitle[2] = "DEHOOKING DRIVES";
        sGroupItemsTitle[3] = "INSTALLATION VERIFICATION";
        sGroupItemsTitle[4] = "No of Meter found DF";
        sGroupItemsTitle[5] = "No of Bypass/Temper cases identification";
        sGroupItemsTitle[6] = "NEW CHARGING";
        sGroupItemsTitle[7] = "STAFF MEETING";
        sGroupItemsTitle[8] = "JOINT SQUAD OPERATION";
        drawablesGroupItem = new Drawable[9];
        drawablesGroupItem[0] = getApplicationContext().getDrawable(R.drawable.ic_meter_reading);
        drawablesGroupItem[1] = getApplicationContext().getDrawable(R.drawable.ic_3ph_meter_reading);
        drawablesGroupItem[2] = getApplicationContext().getDrawable(R.drawable.ic_dehooking_drives);
        drawablesGroupItem[3] = getApplicationContext().getDrawable(R.drawable.ic_installation_verification);
        drawablesGroupItem[4] = getApplicationContext().getDrawable(R.drawable.ic_defective_meter_found);
        drawablesGroupItem[5] = getApplicationContext().getDrawable(R.drawable.ic_bypass_identification);
        drawablesGroupItem[6] = getApplicationContext().getDrawable(R.drawable.ic_new_charging);
        drawablesGroupItem[7] = getApplicationContext().getDrawable(R.drawable.ic_staff_meeting);
        drawablesGroupItem[8] = getApplicationContext().getDrawable(R.drawable.ic_joint_squad_operation);
        mJobsTodoAdapter = new JobsTodoAdapter(JobsTodoActivity.this, sGroupItemsTitle, drawablesGroupItem, 3);
        rvGroupTwo.setAdapter(mJobsTodoAdapter);
        // rvGroupTwo.setItemAnimator(new DefaultItemAnimator());

        // SDO & AMC
        sGroupItemsTitle = new String[11];
        sGroupItemsTitle[0] = getApplicationContext().getResources().getString(R.string._3_ph_meter_reading);
        sGroupItemsTitle[1] = "DEHOOKING DRIVES";
        sGroupItemsTitle[2] = "ARREAR DRIVE";
        sGroupItemsTitle[3] = "BILL REVISION";
        sGroupItemsTitle[4] = "CONSUMER MELLA";
        sGroupItemsTitle[5] = "VILLAGE MEETING";
        sGroupItemsTitle[6] = "STAFF MEETING";
        sGroupItemsTitle[7] = "Energy Audit & Reporting";
        sGroupItemsTitle[8] = "INSTALLATION VERIFICATION";
        sGroupItemsTitle[9] = "Assessment";
        sGroupItemsTitle[10] = "Recovery from Assesment";

        drawablesGroupItem = new Drawable[11];
        drawablesGroupItem[0] = getApplicationContext().getDrawable(R.drawable.ic_3ph_meter_reading);
        drawablesGroupItem[1] = getApplicationContext().getDrawable(R.drawable.ic_dehooking_drives);
        drawablesGroupItem[2] = getApplicationContext().getDrawable(R.drawable.ic_arrear_drive);
        drawablesGroupItem[3] = getApplicationContext().getDrawable(R.drawable.ic_bill_revision);
        drawablesGroupItem[4] = getApplicationContext().getDrawable(R.drawable.ic_consumer_mella);
        drawablesGroupItem[5] = getApplicationContext().getDrawable(R.drawable.ic_village_meeting);
        drawablesGroupItem[6] = getApplicationContext().getDrawable(R.drawable.ic_staff_meeting);
        drawablesGroupItem[7] = getApplicationContext().getDrawable(R.drawable.ic_energy_audit);
        drawablesGroupItem[8] = getApplicationContext().getDrawable(R.drawable.ic_installation_verification);
        drawablesGroupItem[9] = getApplicationContext().getDrawable(R.drawable.ic_assessment);
        drawablesGroupItem[10] = getApplicationContext().getDrawable(R.drawable.ic_recovery_assessment);

        mJobsTodoAdapter = new JobsTodoAdapter(JobsTodoActivity.this, sGroupItemsTitle, drawablesGroupItem, 4);
        rvSDO.setAdapter(mJobsTodoAdapter);
        // rvSDO.setItemAnimator(new DefaultItemAnimator());

        // AMC
        sGroupItemsTitle = new String[9];
        sGroupItemsTitle[0] = getApplicationContext().getResources().getString(R.string._3_ph_meter_reading);
        sGroupItemsTitle[1] = "BILL REVISION";
        sGroupItemsTitle[2] = "ARREAR DRIVE";
        sGroupItemsTitle[3] = "INSTALLATION VERIFICATION";
        sGroupItemsTitle[4] = "CONSUMER MELLA";
        sGroupItemsTitle[5] = "VILLAGE MEETING";
        sGroupItemsTitle[6] = "STAFF MEETING";
        sGroupItemsTitle[7] = "Assessment";
        sGroupItemsTitle[8] = "Recovery from Assesment";
        drawablesGroupItem = new Drawable[9];
        drawablesGroupItem[0] = getApplicationContext().getDrawable(R.drawable.ic_3ph_meter_reading);
        drawablesGroupItem[1] = getApplicationContext().getDrawable(R.drawable.ic_bill_revision);
        drawablesGroupItem[2] = getApplicationContext().getDrawable(R.drawable.ic_arrear_drive);
        drawablesGroupItem[3] = getApplicationContext().getDrawable(R.drawable.ic_installation_verification);
        drawablesGroupItem[4] = getApplicationContext().getDrawable(R.drawable.ic_consumer_mella);
        drawablesGroupItem[5] = getApplicationContext().getDrawable(R.drawable.ic_village_meeting);
        drawablesGroupItem[6] = getApplicationContext().getDrawable(R.drawable.ic_staff_meeting);
        drawablesGroupItem[7] = getApplicationContext().getDrawable(R.drawable.ic_assessment);
        drawablesGroupItem[8] = getApplicationContext().getDrawable(R.drawable.ic_recovery_assessment);
        mJobsTodoAdapter = new JobsTodoAdapter(JobsTodoActivity.this, sGroupItemsTitle, drawablesGroupItem, 5);
        rvAMC.setAdapter(mJobsTodoAdapter);
        // rvAMC.setItemAnimator(new DefaultItemAnimator());


    }

    public void showLogoutAlertDialog() {
        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(JobsTodoActivity.this);

        // set title
        alertDialogBuilder.setTitle("Confirmation");

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(JobsTodoActivity.this, SplashActivity.class));


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
        Intent intent = new Intent(JobsTodoActivity.this, SOSActivity.class);
        startActivity(intent);
    }

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(JobsTodoActivity.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(JobsTodoActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(JobsTodoActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(JobsTodoActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(JobsTodoActivity.this,
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
                            status.startResolutionForResult(JobsTodoActivity.this, REQUEST_CHECK_SETTINGS);
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
