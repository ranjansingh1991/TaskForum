package in.enzen.taskforum.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Base64;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.adapters.CropingOptionAdapter;
import in.enzen.taskforum.dialogs.ProgressDialog;
import in.enzen.taskforum.utils.CropingOption;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.rest.BaseUrl.sSquadOperation;
import static in.enzen.taskforum.utils.Constraints.PROGRESS_DIALOG_BG_COLOR;
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


@SuppressWarnings("all")

public class MrtJointSquadOperationActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView tvCurrentDate, tvCurrentTime;
    private TextView tvLocation;
    private int dd;
    private int mm;
    private int yy;
    private TextView tvSelectDate, tvSelectTime;
    private TextInputLayout tilSelectDate, tilSelectTime;

    private TextInputLayout tilNoOfPremisesVerified,tilNoOfByPassCases,tilLoadInKW,tiltNOOfTemperCases,tiltTemperLoadInKw;
    private EditText etNoOfPremisesVerified, etNoOfByPassCases, etByPassLoadInKW, etNOOfTemperCases, etTemperLoadInKw;

    private Button btnSubmit;
    private Button btnReset;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView tvScreenTitle;

    private TextView tvAttachSnapPath;
    private ImageView imgSnap;
    private Uri mImageCaptureUri = null;
    private String sImageBase64 = "";
    private final int CAMERA = 0;
    private final int CROPING_CODE = 201;
    private final File outPutFile = new File("/sdcard/", "temp.jpg");

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrt_joint_squad_operation);
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
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = findViewById(R.id.imgBack);
        tvToolbarTitle.setText("JOINT SQUAD OPERATION");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView ivLogout = findViewById(R.id.ivLogout);

        ImageView ivSos = (ImageView) findViewById(R.id.ivSos);

        ivSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGoogleAPIClient();
                startSOS();
            }
        });


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

        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvLocation = findViewById(R.id.tvLocation);
        tvCurrentDate.setTypeface(appFontRegular2);
        tvCurrentTime.setTypeface(appFontRegular2);
        tvLocation.setTypeface(appFontRegular2);

        tvSelectDate = findViewById(R.id.tvSelectDate);
        tvSelectTime = findViewById(R.id.tvSelectTime);
        tilSelectDate = findViewById(R.id.tilSelectDate);
        tilSelectTime = findViewById(R.id.tilSelectTime);
        tvSelectDate.setTypeface(appFontRegular2);
        tvSelectTime.setTypeface(appFontRegular2);
        tilSelectDate.setTypeface(appFontRegular2);
        tilSelectTime.setTypeface(appFontRegular2);

        tilNoOfPremisesVerified = findViewById(R.id.tilNoOfPremisesVerified);
        tilNoOfByPassCases = findViewById(R.id.tilNoOfByPassCases);
        tilLoadInKW= findViewById(R.id.tilLoadInKW);
        tiltNOOfTemperCases = findViewById(R.id.tiltNOOfTemperCases);
        tiltTemperLoadInKw = findViewById(R.id.tiltTemperLoadInKw);

        tilLoadInKW.setTypeface(appFontRegular2);
        tilNoOfByPassCases.setTypeface(appFontRegular2);
        tiltTemperLoadInKw.setTypeface(appFontRegular2);
        tilNoOfPremisesVerified.setTypeface(appFontRegular2);
        tiltNOOfTemperCases.setTypeface(appFontRegular2);

        etNoOfPremisesVerified= findViewById(R.id.etNoOfPremisesVerified);
        etNoOfByPassCases = findViewById(R.id.etNoOfByPassCases);
        etByPassLoadInKW = findViewById(R.id.etByPassLoadInKW);
        etNOOfTemperCases = findViewById(R.id.etNOOfTemperCases);
        etTemperLoadInKw = findViewById(R.id.etTemperLoadInKw);
        imgSnap = (ImageView) findViewById(R.id.imgSnap);
        tvAttachSnapPath = (TextView) findViewById(R.id.tvAttachSnapPath);
        tvScreenTitle = findViewById(R.id.tvScreenTitle);
        etNOOfTemperCases.setTypeface(appFontRegular2);
        etTemperLoadInKw.setTypeface(appFontRegular2);
        etNoOfPremisesVerified.setTypeface(appFontRegular2);
        etByPassLoadInKW.setTypeface(appFontRegular2);
        etNoOfByPassCases.setTypeface(appFontRegular2);
        tvAttachSnapPath.setTypeface(appFontRegular2);

        imgSnap = (ImageView) findViewById(R.id.imgSnap);
        tvAttachSnapPath = (TextView) findViewById(R.id.tvAttachSnapPath);
        tvScreenTitle = findViewById(R.id.tvScreenTitle);

        btnSubmit= findViewById(R.id.btnSubmit);
        btnReset = findViewById(R.id.btnReset);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmit();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReset();
            }
        });

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

        imgSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageCaptureUri = Uri.parse("file:///sdcard/photo.jpg");
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                startActivityForResult(intent, CAMERA);
            }
        });
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MrtJointSquadOperationActivity.this);
        // set title
        alertDialogBuilder.setTitle("Confirmation");
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(MrtJointSquadOperationActivity.this, SplashActivity.class));


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            try {
                File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
                tvAttachSnapPath.setText(R.string.image_selected);
                mImageCaptureUri = Uri.fromFile(file);
                cropingIMG();
            } catch (Exception ex) {
                Log.v("OnCameraCallBack", ex.getMessage());
            }
        } else if (requestCode == CROPING_CODE && resultCode == RESULT_OK) {
            if (outPutFile.exists()) {
                Bitmap photo = decodeFile(outPutFile);
                imgSnap.setImageBitmap(photo);
                tvAttachSnapPath.setText(R.string.image_selected);
                sImageBase64 = encodeImageToBase64(photo);
            } else {
                Toast.makeText(getApplicationContext(), "Problem while decoding image", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Bitmap decodeFile(File file) {
        try {
            // decode image size
            BitmapFactory.Options mOptions = new BitmapFactory.Options();
            mOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, mOptions);
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = mOptions.outWidth, height_tmp = mOptions.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // decode with inSampleSize
            BitmapFactory.Options mOptions1 = new BitmapFactory.Options();
            mOptions1.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, mOptions1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        System.gc();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, bos);
        byte[] bb = bos.toByteArray();
        String sPick = "";
        try {
            System.gc();
            sPick = Base64.encodeToString(bb, Base64.DEFAULT);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return sPick;
    }

    private void cropingIMG() {
        final ArrayList cropOptions = new ArrayList();
        final Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        final List<Intent> listIntent = new ArrayList<Intent>();
        int size = list.size();
        if (size == 0) {
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));
            if (size == 1) {
                Intent mIntent = new Intent(intent);
                ResolveInfo res = list.get(0);
                mIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                startActivityForResult(mIntent, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption mCropingOption = new CropingOption();
                    mCropingOption.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    mCropingOption.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    mCropingOption.appIntent = new Intent(intent);
                    mCropingOption.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(mCropingOption);
                    listIntent.add(mCropingOption.appIntent);
                }
                CropingOptionAdapter adapter = new CropingOptionAdapter(MrtJointSquadOperationActivity.this, cropOptions);
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MrtJointSquadOperationActivity.this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(listIntent.get(item), CROPING_CODE);
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });
                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void startSOS() {
        //Check Permission
        checkPermissions();
        Intent intent = new Intent(MrtJointSquadOperationActivity.this, SOSActivity.class);
        startActivity(intent);
    }

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(MrtJointSquadOperationActivity.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MrtJointSquadOperationActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MrtJointSquadOperationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MrtJointSquadOperationActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(MrtJointSquadOperationActivity.this,
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
                            status.startResolutionForResult(MrtJointSquadOperationActivity.this, REQUEST_CHECK_SETTINGS);
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








    private void doSubmit() {
        final ProgressDialog mProgressDialog = new ProgressDialog(MrtJointSquadOperationActivity.this, "Please wait...");
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(PROGRESS_DIALOG_BG_COLOR));
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sSquadOperation,
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
                params.put("nos_of_cases_verifed", etNoOfPremisesVerified.getText().toString());
                params.put("nos_of_bypass_cases", etNoOfByPassCases.getText().toString());
                params.put("bypassed_load_in_kw", etByPassLoadInKW.getText().toString());
                params.put("nos_of_temper_cases", etNOOfTemperCases.getText().toString());
                params.put("tempered_load_in_kw", etTemperLoadInKw.getText().toString());
                params.put("photo", sImageBase64);
                params.put("token", new PreferencesManager(getApplicationContext()).getString("token"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void doReset() {
        tvCurrentDate.setText("");
        tvCurrentTime.setText("");
        etNoOfPremisesVerified.setText("");
        etNoOfByPassCases.setText("");
        etByPassLoadInKW.setText("");
        etNOOfTemperCases.setText("");
        etTemperLoadInKw.setText("");
        imgSnap.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_image_add_dark_holo_64));
    }

}
