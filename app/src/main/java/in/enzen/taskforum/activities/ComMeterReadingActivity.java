package in.enzen.taskforum.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.dialogs.ProgressDialog;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.rest.BaseUrl.sCheckMeter;
import static in.enzen.taskforum.utils.Constraints.PROGRESS_DIALOG_BG_COLOR;

@SuppressWarnings("ALL")
public class ComMeterReadingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView tvCurrentDate, tvCurrentTime;
    private TextView tvLocation;
    private int dd;
    private int mm;
    private int yy;
    private double dLatitute;
    private double dLongitute;
    private TextInputLayout tilSelectDate, tilSelectTime;
    private TextInputLayout tilInstallationVerified;
    private TextInputLayout tilInstallationSuppressed;
    private TextInputLayout tilConsumerNumber;
    private TextInputLayout tilUnitSuppressed;
    private TextInputLayout tilActualReading;
    private TextInputLayout tilMeterReader;
    private TextInputLayout tilNOUnauthorised;
    private TextInputLayout tilPVRDone;
    private EditText etInstallationVerified;
    private EditText etInstallationSuppressed;
    private EditText etConsumerNumber;
    private EditText etUnitSuppressed;
    private EditText etActualReading;
    private EditText etMeterReader;
    private EditText etNOUnauthorised;
    private EditText etPVRDone;
    private TextView tvSelectDate, tvSelectTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Button btnReset, btnSubmit;

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_meter_reading);
        init();
    }

    private void init() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);
        // Typeface appFontRegular = Typeface.createFromAsset(getAssets(), "fonts/app_font_regular.ttf");
        final Typeface appFontRegular2 = Typeface.createFromAsset(getAssets(), "fonts/madras_regular.ttf");
        TextView tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        tvToolbarTitle.setText("CHECK METER READING");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        tvToolbarTitle.setTypeface(appFontRegular2);
        final ImageView imgLogo = findViewById(R.id.imgLogo);
        final RelativeLayout rlHeadContainer = findViewById(R.id.rlHeadContainer);
        imgLogo.bringToFront();
        rlHeadContainer.invalidate();

        ImageView ivSos = (ImageView) findViewById(R.id.ivSos);
        ivSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGoogleAPIClient();
                startSOS();
            }
        });



        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvLocation = findViewById(R.id.tvLocation);
        tilSelectDate = findViewById(R.id.tilSelectDate);
        tilSelectTime = findViewById(R.id.tilSelectTime);
        tilInstallationVerified = findViewById(R.id.tilInstallationVerified);
        tilInstallationSuppressed = findViewById(R.id.tilInstallationSuppressed);
        tilConsumerNumber = findViewById(R.id.tilConsumerNumber);
        tilUnitSuppressed = findViewById(R.id.tilUnitSuppressed);
        tilActualReading = findViewById(R.id.tilActualReading);
        tilMeterReader = findViewById(R.id.tilMeterReader);
        tilNOUnauthorised = findViewById(R.id.tilNOUnauthorised);
        tilPVRDone = findViewById(R.id.tilPVRDone);

        btnReset = findViewById(R.id.btnReset);
        btnSubmit = findViewById(R.id.btnSubmit);


        tvSelectDate = findViewById(R.id.tvSelectDate);
        tvSelectTime = findViewById(R.id.tvSelectTime);
        etInstallationVerified = findViewById(R.id.etInstallationVerified);
        etInstallationSuppressed = findViewById(R.id.etInstallationSuppressed);
        etConsumerNumber = findViewById(R.id.etConsumerNumber);
        etUnitSuppressed = findViewById(R.id.etUnitSuppressed);
        etActualReading = findViewById(R.id.etActualReading);
        etMeterReader = findViewById(R.id.etMeterReader);
        etNOUnauthorised = findViewById(R.id.etNOUnauthorised);
        etPVRDone = findViewById(R.id.etPVRDone);

        tvCurrentDate.setTypeface(appFontRegular2);
        tvCurrentTime.setTypeface(appFontRegular2);
        tvLocation.setTypeface(appFontRegular2);
        tilSelectDate.setTypeface(appFontRegular2);
        tilSelectTime.setTypeface(appFontRegular2);
        tilInstallationVerified.setTypeface(appFontRegular2);
        tilInstallationSuppressed.setTypeface(appFontRegular2);
        tilConsumerNumber.setTypeface(appFontRegular2);
        tilUnitSuppressed.setTypeface(appFontRegular2);
        tilActualReading.setTypeface(appFontRegular2);
        tilMeterReader.setTypeface(appFontRegular2);
        tilNOUnauthorised.setTypeface(appFontRegular2);
        tilPVRDone.setTypeface(appFontRegular2);

        btnSubmit.setTypeface(appFontRegular2);
        btnReset.setTypeface(appFontRegular2);

        tvSelectDate.setTypeface(appFontRegular2);
        tvSelectTime.setTypeface(appFontRegular2);
        etInstallationVerified.setTypeface(appFontRegular2);
        etInstallationSuppressed.setTypeface(appFontRegular2);
        etConsumerNumber.setTypeface(appFontRegular2);
        etUnitSuppressed.setTypeface(appFontRegular2);
        etActualReading.setTypeface(appFontRegular2);
        etMeterReader.setTypeface(appFontRegular2);
        etNOUnauthorised.setTypeface(appFontRegular2);
        etPVRDone.setTypeface(appFontRegular2);

        tilSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDateView();
            }
        });

        tilSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTimeView();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReset();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmit();
            }
        });

        // For Header AutoDate
        final Calendar cal = Calendar.getInstance();
        dd = cal.get(Calendar.DAY_OF_MONTH);
        mm = cal.get(Calendar.MONTH);
        yy = cal.get(Calendar.YEAR);
        // set current date into textview
        tvCurrentDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(dd).append("-").append(mm + 1).append("-").append(yy));

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTimeView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

    }

    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(ComMeterReadingActivity.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    private void startSOS() {
        //Check Permission
        checkPermissions();
        Intent intent = new Intent(ComMeterReadingActivity.this, SOSActivity.class);
        startActivity(intent);
    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ComMeterReadingActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }

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
                            status.startResolutionForResult(ComMeterReadingActivity.this, REQUEST_CHECK_SETTINGS);
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



    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ComMeterReadingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(ComMeterReadingActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(ComMeterReadingActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }


    private void updateTimeView() {
        Date refreshDate = Calendar.getInstance().getTime();
        String time = "hh:mm a";
        tvCurrentTime.setText(DateFormat.format(time, refreshDate));
    }

    private void displayDateView() {
        // Form Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tvSelectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void displayTimeView() {
        // Form Time
        final Calendar calendar = Calendar.getInstance();
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String status = "AM";
                if (hourOfDay > 11) {
                    status = "PM";
                }
                int hour_of_12_hour_format;
                if (hourOfDay > 11) {
                    hour_of_12_hour_format = hourOfDay - 12;
                } else {
                    hour_of_12_hour_format = hourOfDay;
                }
                tvSelectTime.setText(hour_of_12_hour_format + " : " + minute + " " + status);
               /* String AM_PM;
                if (hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }*/
                // tvSelectTime.setText(hourOfDay + ":" + minute + " " + AM_PM);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void showLogoutAlertDialog() {
        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ComMeterReadingActivity.this);
        // set title
        alertDialogBuilder.setTitle("Confirmation");
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(ComMeterReadingActivity.this, SplashActivity.class));
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

    private void doReset() {
        tvCurrentDate.setText("");
        tvCurrentTime.setText("");
        etInstallationVerified.setText("");
        etInstallationSuppressed.setText("");
        etConsumerNumber.setText("");
        etUnitSuppressed.setText("");
        etActualReading.setText("");
        etMeterReader.setText("");
        etNOUnauthorised.setText("");
        etPVRDone.setText("");
    }

    private void doSubmit() {
        final ProgressDialog mProgressDialog = new ProgressDialog(ComMeterReadingActivity.this, "Please wait...");
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(PROGRESS_DIALOG_BG_COLOR));
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sCheckMeter,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("status")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mProgressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.e("status Response", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new HashMap<>();
                params.put("date", tvSelectDate.getText().toString());
                params.put("time", tvSelectTime.getText().toString() + ":00");
                params.put("no_of_installation_verified", etInstallationVerified.getText().toString());
                params.put("supressed", etInstallationSuppressed.getText().toString());
                params.put("consumer_no", etConsumerNumber.getText().toString());
                params.put("unit_supressed", etUnitSuppressed.getText().toString());
                params.put("actual_reading", etActualReading.getText().toString());
                params.put("meter_reader_taken", etMeterReader.getText().toString());
                params.put("no_of_unauthorized", etNOUnauthorised.getText().toString());
                params.put("penalty_pvr", etPVRDone.getText().toString());
                params.put("token", new PreferencesManager(getApplicationContext()).getString("token"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
