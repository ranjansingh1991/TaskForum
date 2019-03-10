package in.enzen.taskforum.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.adapters.AccountListAdapter;
import in.enzen.taskforum.adapters.CropingOptionAdapter;
import in.enzen.taskforum.dialogs.ProgressDialog;
import in.enzen.taskforum.model.AccountBean;
import in.enzen.taskforum.utils.CropingOption;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.rest.BaseUrl.sProfileUpdate;

/**
 * Created by Rupesh on 13-11-2017.
 */
@SuppressWarnings("ALL")
public class ProfileActivity extends AppCompatActivity {

    private static final int CROPING_CODE = 201;
    private static final File outPutFile = new File("/sdcard/", "temp.jpg");
    public static Uri mImageCaptureUri = null;
    private final int CAMERA = 0;
    private final int GALLERY = 1;
    private final int READ_ES = 3;
    private final int WRITE_ES = 2;
    Bitmap myBitmap;
    private RecyclerView rvAccounts;
    AccountBean mAccountBean;
    Toolbar mToolbar;
    ProgressDialog mProgressDialog;

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_ES);
            }
        }
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_ES);
            }
        }
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA);
            }
        }
        setContentView(R.layout.fragment_profile);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) ProfileActivity.this;
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface appFontRegular = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");

        TextView tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        ImageView ivSos = (ImageView) findViewById(R.id.ivSos);

        tvToolbarTitle.setText("account");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ProfileActivity.this.getSupportFragmentManager().popBackStackImmediate();
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

        ivSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGoogleAPIClient();
                startSOS();
            }
        });

        tvToolbarTitle.setTypeface(appFontRegular);

        mAccountBean = new AccountBean();

        rvAccounts = (RecyclerView) findViewById(R.id.rvAccounts);
        final AccountListAdapter mAccountListAdapter = new AccountListAdapter(ProfileActivity.this, mAccountBean);
        rvAccounts.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
        rvAccounts.setAdapter(mAccountListAdapter);
        // rvAccounts.setLayoutManager(new LinearLayoutManager(ProfileActivity()));
        // rvAccounts.setAdapter(new AccountListAdapter(ProfileActivity(), mAccountBean));

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            try {
                File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
                mImageCaptureUri = Uri.fromFile(file);
                CropingIMG();
            } catch (Exception ex) {
                Log.v("OnCameraCallBack", ex.getMessage());
            }
        } else if (requestCode == GALLERY && resultCode == RESULT_OK) {
            try {
                File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
                Uri uri = Uri.fromFile(file);
                mImageCaptureUri = data.getData();
                CropingIMG();
            } catch (Exception ex) {
                Log.v("OnGalleryCallBack", ex.getMessage());
            }
        } else if (requestCode == CROPING_CODE && resultCode == RESULT_OK) {
            if (outPutFile.exists()) {
                Bitmap photo = decodeFile(outPutFile);
                AccountListAdapter.civUserPic.setImageBitmap(photo);
                //TODO: send data to server fro here in Base64 format
                // TODO : Call API for updating image to server
                 uploadImageToServer(encodeImageToBase64(photo));
            } else {
                Toast.makeText(ProfileActivity.this, "Image cannot be crop please choose other image", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadImageToServer(final String sTempImg) {
        mProgressDialog = new ProgressDialog(ProfileActivity.this, "Updating profile image");
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        //sending image to server
        StringRequest request = new StringRequest(Request.Method.POST, sProfileUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("status")) {
                           // Toast.makeText(getApplicationContext(), "updated successfully", Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Toast.makeText(ProfileActivity.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                Log.e("status Response", String.valueOf(volleyError));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("avatar", sTempImg);
                params.put("token", new PreferencesManager(getApplicationContext()).getString("token"));
                return params;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(ProfileActivity.this);
        rQueue.add(request);
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
            Toast.makeText(ProfileActivity.this, "Image size is large", Toast.LENGTH_LONG).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_ES: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ProfileActivity.this.finish();
                }
                return;
            }
            case WRITE_ES: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ProfileActivity.this.finish();
                }
                return;
            }
            case CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ProfileActivity.this.finish();
                }
                return;
            }
        }
    }

    public void CropingIMG() {
        final ArrayList cropOptions = new ArrayList();
        final Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = ProfileActivity.this.getPackageManager().queryIntentActivities(intent, 0);
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
                    mCropingOption.title = ProfileActivity.this.getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    mCropingOption.icon = ProfileActivity.this.getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    mCropingOption.appIntent = new Intent(intent);
                    mCropingOption.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(mCropingOption);
                    listIntent.add(mCropingOption.appIntent);
                }
                CropingOptionAdapter adapter = new CropingOptionAdapter(ProfileActivity.this, cropOptions);
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                            ProfileActivity.this.getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public void showLogoutAlertDialog() {
        final SharedPreferences sp = ProfileActivity.this.getSharedPreferences("Login", MODE_PRIVATE);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ProfileActivity.this);

        // set title
        alertDialogBuilder.setTitle("Confirmation");

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(ProfileActivity.this, SplashActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void startSOS() {
        //Check Permission
        checkPermissions();
        Intent intent = new Intent(ProfileActivity.this, SOSActivity.class);
        startActivity(intent);
    }

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(ProfileActivity.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(ProfileActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(ProfileActivity.this,
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
                        // Toast.makeText(ProfileActivity(), "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(ProfileActivity.this, REQUEST_CHECK_SETTINGS);
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
