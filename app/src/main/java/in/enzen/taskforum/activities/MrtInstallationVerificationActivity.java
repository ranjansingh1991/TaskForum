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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import static in.enzen.taskforum.rest.BaseUrl.sInstallVerification;
import static in.enzen.taskforum.utils.Constraints.PROGRESS_DIALOG_BG_COLOR;

/**
 * Created by Rupesh on 29-03-2018.
 */
@SuppressWarnings("ALL")
public class MrtInstallationVerificationActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView tvCurrentDate, tvCurrentTime;
    private int dd;
    private int mm;
    private int yy;
    private TextView tvSelectDate, tvSelectTime;
    private TextInputLayout tilSelectDate, tilSelectTime;
    private Button btnSubmit;
    private Button btnReset;
    private int mYear, mMonth, mDay, mHour, mMinute;

    private RadioGroup rgHtCategory;
    private RadioButton rbLT, rbHT, rbHtSIN, rbHtMIN, rbHtLIN;
    private LinearLayout llHtCategory, llEntryFields;
    private EditText etInstallationVerified, etNoOfUnauthorisedFound, etUnauthorisedPVRMade, etCaseLoadEnhanced,
            etMeterDeclaredDefective, etSpotPenaltyCollected;

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrt_installtion_verification);
        init();
    }

    private void init() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);
        final Typeface appFontRegular2 = Typeface.createFromAsset(getAssets(), "fonts/madras_regular.ttf");
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        tvSelectDate = findViewById(R.id.tvSelectDate);
        tvSelectTime = findViewById(R.id.tvSelectTime);
        ImageView imgBack = findViewById(R.id.imgBack);
        tvToolbarTitle.setText("INSTALLATION VERIFICATION");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView ivLogout = findViewById(R.id.ivLogout);
        ImageView ivSos = (ImageView) findViewById(R.id.ivSos);
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

        ivSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGoogleAPIClient();
                startSOS();
            }
        });

        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tilSelectDate = findViewById(R.id.tilSelectDate);
        tilSelectTime = findViewById(R.id.tilSelectTime);
        rgHtCategory = findViewById(R.id.rgHtCategory);
        llHtCategory = findViewById(R.id.llHtCategory);
        llEntryFields = findViewById(R.id.llEntryFields);
        rbLT = findViewById(R.id.rbLT);
        rbHT = findViewById(R.id.rbHT);
        rbHtSIN = findViewById(R.id.rbHtSIN);
        rbHtMIN = findViewById(R.id.rbHtMIN);
        rbHtLIN = findViewById(R.id.rbHtLIN);
        etInstallationVerified = findViewById(R.id.etInstallationVerified);
        etNoOfUnauthorisedFound = findViewById(R.id.etNoOfUnauthorisedFound);
        etUnauthorisedPVRMade = findViewById(R.id.etUnauthorisedPVRMade);
        etCaseLoadEnhanced = findViewById(R.id.etCaseLoadEnhanced);
        etMeterDeclaredDefective = findViewById(R.id.etMeterDeclaredDefective);
        etSpotPenaltyCollected = findViewById(R.id.etSpotPenaltyCollected);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnReset = findViewById(R.id.btnReset);
        tvCurrentDate.setTypeface(appFontRegular2);
        tvCurrentTime.setTypeface(appFontRegular2);
        btnSubmit.setTypeface(appFontRegular2);
        btnReset.setTypeface(appFontRegular2);
        rbLT.setTypeface(appFontRegular2);
        rbHT.setTypeface(appFontRegular2);
        rbHtSIN.setTypeface(appFontRegular2);
        rbHtMIN.setTypeface(appFontRegular2);
        rbHtLIN.setTypeface(appFontRegular2);
        etInstallationVerified.setTypeface(appFontRegular2);
        etNoOfUnauthorisedFound.setTypeface(appFontRegular2);
        etUnauthorisedPVRMade.setTypeface(appFontRegular2);
        etCaseLoadEnhanced.setTypeface(appFontRegular2);
        etMeterDeclaredDefective.setTypeface(appFontRegular2);
        etSpotPenaltyCollected.setTypeface(appFontRegular2);

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
        // set current date & time into TextView
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rbLT:
                if (checked)
                    // Display LT entry fields
                    Toast.makeText(MrtInstallationVerificationActivity.this, "You selected LT", Toast.LENGTH_SHORT).show();
                llEntryFields.setVisibility(View.VISIBLE);
                llHtCategory.setVisibility(View.GONE);
                break;
            case R.id.rbHT:
                if (checked)
                    // First open HT category option then in Option selected display the entry fields.
                    Toast.makeText(MrtInstallationVerificationActivity.this, "You selected HT", Toast.LENGTH_SHORT).show();
                llEntryFields.setVisibility(View.GONE);
                llHtCategory.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void onHtCategoryClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbHtSIN:
                if (checked)
                Toast.makeText(MrtInstallationVerificationActivity.this, "You selected SIN", Toast.LENGTH_SHORT).show();
                llEntryFields.setVisibility(View.VISIBLE);
                break;
            case R.id.rbHtMIN:
                if (checked)
                Toast.makeText(MrtInstallationVerificationActivity.this, "You selected MIN", Toast.LENGTH_SHORT).show();
                llEntryFields.setVisibility(View.VISIBLE);
                break;
            case R.id.rbHtLIN:
                if (checked)
                Toast.makeText(MrtInstallationVerificationActivity.this, "You selected LIN", Toast.LENGTH_SHORT).show();
                llEntryFields.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateTimeView() {
        Date refreshDate = Calendar.getInstance().getTime();
        String time = "hh:mm:ss a";
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
                tvSelectDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, mYear, mMonth, mDay);
        /*DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tvSelectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, mYear, mMonth, mDay);*/
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
                tvSelectTime.setText(hourOfDay + ":" + minute);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void showLogoutAlertDialog() {
        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MrtInstallationVerificationActivity.this);
        // set title
        alertDialogBuilder.setTitle("Confirmation");
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(MrtInstallationVerificationActivity.this, SplashActivity.class));


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
        Intent intent = new Intent(MrtInstallationVerificationActivity.this, SOSActivity.class);
        startActivity(intent);
    }

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(MrtInstallationVerificationActivity.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MrtInstallationVerificationActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MrtInstallationVerificationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MrtInstallationVerificationActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(MrtInstallationVerificationActivity.this,
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
                            status.startResolutionForResult(MrtInstallationVerificationActivity.this, REQUEST_CHECK_SETTINGS);
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

    private void doReset() {
        tvCurrentDate.setText("");
        tvCurrentTime.setText("");
        rbLT.setChecked(false);
        rbHT.setChecked(false);
        rbHtSIN.setChecked(false);
        rbHtMIN.setChecked(false);
        rbHtLIN.setChecked(false);
        etInstallationVerified.setText("");
        etNoOfUnauthorisedFound.setText("");
        etUnauthorisedPVRMade.setText("");
        etCaseLoadEnhanced.setText("");
        etMeterDeclaredDefective.setText("");
        etSpotPenaltyCollected.setText("");
    }

    private void doSubmit() {
        final ProgressDialog mProgressDialog = new ProgressDialog(MrtInstallationVerificationActivity.this, "Please wait...");
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(PROGRESS_DIALOG_BG_COLOR));
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sInstallVerification,
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
                if (rbLT.isChecked()){
                    params.put("phase_type", rbLT.getText().toString());
                } else {
                    params.put("phase_type", rbHT.getText().toString());
                }
                if (rbHtSIN.isChecked()) {
                    params.put("type", rbHtSIN.getText().toString());
                } else if (rbHtMIN.isChecked()) {
                    params.put("type", rbHtMIN.getText().toString());
                } else {
                    params.put("type", rbHtSIN.getText().toString());
                }
                params.put("no_installation_verified", etInstallationVerified.getText().toString());
                params.put("no_unauthorised_found", etNoOfUnauthorisedFound.getText().toString());
                params.put("no_unauthorised_pvr", etUnauthorisedPVRMade.getText().toString());
                params.put("no_case_enhance", etCaseLoadEnhanced.getText().toString());
                params.put("no_meter_defective", etMeterDeclaredDefective.getText().toString());
                params.put("amount_charged", etSpotPenaltyCollected.getText().toString());
                params.put("token", new PreferencesManager(getApplicationContext()).getString("token"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
