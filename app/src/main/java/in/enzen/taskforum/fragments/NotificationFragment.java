package in.enzen.taskforum.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.activities.SOSActivity;
import in.enzen.taskforum.adapters.NotificationAdapter;
import in.enzen.taskforum.dialogs.ProgressDialog;
import in.enzen.taskforum.model.NotificationModel;
import in.enzen.taskforum.utils.KeyNames;
import in.enzen.taskforum.utils.PreferencesManager;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static in.enzen.taskforum.rest.BaseUrl.sNotification;
import static in.enzen.taskforum.utils.Constraints.PROGRESS_DIALOG_BG_COLOR;

/**
 * Created by Rupesh on 13-11-2017.
 */
@SuppressWarnings("ALL")
public class NotificationFragment extends Fragment implements KeyNames {

    RecyclerView rvNotification;
    ArrayList<NotificationModel> arraylist = new ArrayList<NotificationModel>();
    private String[] sTitle;
    private String[] sDate;
    private String[] sDespcription;
    NotificationAdapter mNotificationAdapter;
    Toolbar mToolbar;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private String title;
    private String date;
    private String des;

//    int nitificationId = 1;
//    NotificationManager notificationManager = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        getNotification();

       // 1.extra to be added....
       /* NotificationManager notif=(NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (getActivity()).setContentTitle(title).setContentText(date).
                setContentTitle(des).setSmallIcon(R.drawable.enzen_logo).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);*/



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
        ImageView imgBack = (ImageView) childRoot.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getActivity().finish();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });


        TextView tvToolbarTitle = (TextView) childRoot.findViewById(R.id.tvToolbarTitle);
        ImageView ivSos = (ImageView) childRoot.findViewById(R.id.ivSos);

        tvToolbarTitle.setText("notification");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        ivSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGoogleAPIClient();
                startSOS();
            }
        });

        ImageView ivLogout = (ImageView) childRoot.findViewById(R.id.ivLogout);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });

        tvToolbarTitle.setTypeface(appFontRegular);

        rvNotification = childRoot.findViewById(R.id.rvNotification);

        /*sTitle = new String[]{
                "High Tension",
                "Low Tension",
                "Sub Station",
                "Street Light",
                "HLT"
        };

        sDate = new String[]{
                "05/12/2017",
                "06/12/2017",
                "07/12/2017",
                "08/12/2017",
                "011/12/2017"
        };

        sDespcription = new String[]{
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
        };

        for (int i = 0; i < sTitle.length; i++) {
            NotificationModel scholarNames = new NotificationModel(sTitle[i], sDate[i], sDespcription[i]);
            arraylist.add(scholarNames);
        }

        rvNotification = (RecyclerView) childRoot.findViewById(R.id.rvNotification);
        mNotificationAdapter = new NotificationAdapter(getActivity(), arraylist);
        rvNotification.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvNotification.setAdapter(mNotificationAdapter);*/
    }

    private void getNotification() {
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity(), "Please wait...");
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(PROGRESS_DIALOG_BG_COLOR));
        mProgressDialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, sNotification, new Response.Listener<String>() {
            @Override
            public void onResponse(String sResult) {
                if (sResult != null) {
                    mProgressDialog.dismiss();
                    PreferencesManager mPreferencesManager = new PreferencesManager(getActivity());
                    try {
                        JSONObject jsonObject = new JSONObject(sResult);
                        if (jsonObject.getBoolean("status")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("records");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                NotificationModel mNotificationModel = new NotificationModel(
                                        jsonArray.getJSONObject(i).getString("title"),
                                        jsonArray.getJSONObject(i).getString("date"),
                                        jsonArray.getJSONObject(i).getString("description")
                                );
                                arraylist.add(mNotificationModel);
                                mNotificationAdapter = new NotificationAdapter(getActivity(), arraylist);
                                rvNotification.setLayoutManager(new LinearLayoutManager(getActivity()));
                                rvNotification.setAdapter(mNotificationAdapter);
                            }
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Dismiss the progress dialog
                mProgressDialog.dismiss();
                Log.e("status Response", String.valueOf(volleyError));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new HashMap<>();
                params.put("token", new PreferencesManager(getActivity()).getString("token"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        // Toast.makeText(getActivity(), "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        // Toast.makeText(getActivity(), "GPS is Disabled in your device", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
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

}

