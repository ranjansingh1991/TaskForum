package in.enzen.taskforum.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.activities.SOSActivity;
import in.enzen.taskforum.adapters.ChatListAdapter;
import in.enzen.taskforum.civ.CircleImageView;
import in.enzen.taskforum.dialogs.ProgressDialog;
import in.enzen.taskforum.model.ChatDetailsModel;
import in.enzen.taskforum.model.Chats;
import in.enzen.taskforum.utils.PreferencesManager;

import static android.content.Context.MODE_PRIVATE;
import static in.enzen.taskforum.rest.BaseUrl.sChatDetails;
import static in.enzen.taskforum.rest.BaseUrl.sSendMessage;
import static in.enzen.taskforum.utils.Constraints.PROGRESS_DIALOG_BG_COLOR;

/**
 * Created by Rupesh on 13-11-2017.
 */
@SuppressWarnings("ALL")
public class ChatFragment extends Fragment implements View.OnClickListener {

    private ListView lvMessages;
    private ChatListAdapter chatArrayAdapter;
    private String sReceiverID;
    private EditText etTxtMsg;
    private ImageView btnSend;
    private boolean side = false;
    Toolbar mToolbar;

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        // Bundle mBundle = getActivity().getIntent().getExtras();

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface appFontRegular = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");

        TextView tvToolbarTitle = (TextView) rootView.findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        ImageView ivSos = (ImageView) rootView.findViewById(R.id.ivSos);

        final CircleImageView civReceiverPic = rootView.findViewById(R.id.civReceiverPic);
        final TextView tvToolbarUserTitle = rootView.findViewById(R.id.tvToolbarUserTitle);

        tvToolbarTitle.setTypeface(appFontRegular);
        tvToolbarUserTitle.setTypeface(appFontRegular);

        Bundle mBundle = this.getArguments();
        sReceiverID = mBundle.getString("sIDs");
        tvToolbarUserTitle.setText(mBundle.getString("sNames"));
        Picasso.with(getActivity()).load(mBundle.getString("sImgURL")).into(civReceiverPic);


        tvToolbarTitle.setText("Chat");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        ImageView ivLogout = (ImageView) rootView.findViewById(R.id.ivLogout);
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

       /* tvToolbarUserTitle.setText(" " + mBundle.getString("sNames"));
        Picasso.with(getActivity()).load(mBundle.getString("sImgURL")).into(civReceiverPic);
        sReceiverID = mBundle.getString("sIDs");*/


        lvMessages = (ListView) rootView.findViewById(R.id.lvMessages);
        etTxtMsg = (EditText) rootView.findViewById(R.id.etTxtMsg);
        btnSend = (ImageView) rootView.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        lvMessages.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatArrayAdapter = new ChatListAdapter(getActivity(), R.layout.outgoing_chat_msg);
        lvMessages.setAdapter(chatArrayAdapter);
        receiveOld();

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                lvMessages.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                if (etTxtMsg.getText().length() < 1) {
                    etTxtMsg.setError("No message!");
                } else {
                    sendMessage();
                }
                break;
        }
    }

    private boolean populateChatMessage(boolean isLeft, String msg, String dateTime) {
        chatArrayAdapter.add(new Chats(isLeft, msg, dateTime));
        if (isLeft) {
            etTxtMsg.setText("");
        }
        return true;
    }

    public void showLogoutAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Confirmation");

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       // getActivity().finishAffinity();
                        new PreferencesManager(getActivity()).setLoggedIn(false);
                        getActivity().startActivity(new Intent(getActivity(), SplashActivity.class));
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

    private void receiveOld() {
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity(), "Please wait...");
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(PROGRESS_DIALOG_BG_COLOR));
        mProgressDialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, sChatDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String sResult) {
                if (sResult != null) {
                    PreferencesManager mPreferencesManager = new PreferencesManager(getActivity());
                    try {
                        JSONObject jsonObject = new JSONObject(sResult);
                        if (jsonObject.getBoolean("status")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("records");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ChatDetailsModel mChatDetailsModel = new ChatDetailsModel(
                                        jsonArray.getJSONObject(i).getString("id"),
                                        jsonArray.getJSONObject(i).getString("from_user_id"),
                                        jsonArray.getJSONObject(i).getString("to_user_id"),
                                        jsonArray.getJSONObject(i).getString("message"),
                                        jsonArray.getJSONObject(i).getString("send_date_time"),
                                        jsonArray.getJSONObject(i).getString("read_status")
                                );
                                populateChatMessage(true, jsonArray.getJSONObject(i).getString("message"),
                                        jsonArray.getJSONObject(i).getString("send_date_time"));
                            }
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mProgressDialog.dismiss();
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
                params.put("from_user_id", sReceiverID);
                params.put("token", new PreferencesManager(getActivity()).getString("token"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void sendMessage() {
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity(), "Please wait...");
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(PROGRESS_DIALOG_BG_COLOR));
        mProgressDialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, sSendMessage, new Response.Listener<String>() {
            @Override
            public void onResponse(String sResult) {
                try {
                    JSONObject jsonObject = new JSONObject(sResult);
                    if (jsonObject.getBoolean("status")) {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressDialog.dismiss();
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
                params.put("from_user_id", sReceiverID);
                params.put("message", etTxtMsg.getText().toString().trim());
                params.put("token", new PreferencesManager(getActivity()).getString("token"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}
