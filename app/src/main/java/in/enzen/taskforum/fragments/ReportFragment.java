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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.List;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.activities.SOSActivity;
import in.enzen.taskforum.adapters.PieChartRVAdapter;
import in.enzen.taskforum.model.ReportDataBean;
import in.enzen.taskforum.utils.KeyNames;
import in.enzen.taskforum.utils.PreferencesManager;

import static android.content.Context.MODE_PRIVATE;
import static in.enzen.taskforum.rest.BaseUrl.sReportFilterList;
import static in.enzen.taskforum.rest.BaseUrl.sReportPieChart;

/**
 * Created by Rupesh on 13-11-2017.
 */
@SuppressWarnings("ALL")
public class ReportFragment extends Fragment implements KeyNames {

    RecyclerView rvPieChart;
    private Toolbar mToolbar;
    private Spinner spCircle, spDivision, spSubDivision, spSection;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    //private static String[] sCircleId;
    //private static String[] sSubDivisionId;
    //private static String[] sDivisionId;
    //private static String[] sSectionId;
    //private static String[] sName;

    ArrayList<String> sNameCircle;
    ArrayList<String> sNameDivision;
    ArrayList<String> sNameSubDivision;
    ArrayList<String> sNameSection;
    static ArrayList<String> sCircleID;
    static ArrayList<String> sDivisionID;
    static ArrayList<String> sSubDivisionID;
    static ArrayList<String> sSectionID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tvToolbarTitle = (TextView) rootView.findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        ImageView ivSos = (ImageView) rootView.findViewById(R.id.ivSos);
        Typeface appFontRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/madras_regular.ttf");
        tvToolbarTitle.setText("Report");

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

        spCircle = rootView.findViewById(R.id.spCircle);
        sNameCircle = new ArrayList<>();
        getCircleName();

        spCircle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                //  ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
                getCircleData(sCircleID.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spDivision = rootView.findViewById(R.id.spDivision);
        sNameDivision = new ArrayList<>();
        getDivisionName();

        spSubDivision = rootView.findViewById(R.id.spSubDivision);

        spDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                getSubDivisionName(pos);
               /* String s = sCircleID.get(spCircle.getSelectedItemPosition());
                String s1 = sDivisionID.get(pos);*/
                getDivionData(sCircleID.get(spCircle.getSelectedItemPosition()), sDivisionID.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spSection = rootView.findViewById(R.id.spSection);
        sNameSection = new ArrayList<>();

        spSubDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSubDivionData(sCircleID.get(spCircle.getSelectedItemPosition()),
                        sDivisionID.get(spDivision.getSelectedItemPosition()), sSubDivisionID.get(position));
                getSectionName(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSectionData(sCircleID.get(spCircle.getSelectedItemPosition()),
                        sDivisionID.get(spDivision.getSelectedItemPosition()),
                        sSubDivisionID.get(spSubDivision.getSelectedItemPosition()),
                        sSectionID.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rvPieChart = (RecyclerView) rootView.findViewById(R.id.rvPieChart);
        rvPieChart.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
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

    private void getCircleName() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sReportFilterList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("records");
                        JSONArray jsonArray = jsonObject1.getJSONArray("circle_list");
                        sCircleID = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            sCircleID.add(jsonArray.getJSONObject(i).getString("circle_id"));
                            sNameCircle.add(jsonArray.getJSONObject(i).getString("name"));
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sNameCircle);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spCircle.setAdapter(adapter);

                   // spCircle.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sNameCircle));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Log.e("Error on volly", volleyError.getLocalizedMessage());
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

    private void getDivisionName() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sReportFilterList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("records");
                        JSONArray jsonArray = jsonObject1.getJSONArray("division_list");
                        sDivisionID = new ArrayList<>();
                        ;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            sDivisionID.add(jsonArray.getJSONObject(i).getString("division_id"));
                            String name = jsonArray.getJSONObject(i).getString("name");
                            sNameDivision.add(name);
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sNameDivision);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spDivision.setAdapter(adapter);

                   // spDivision.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sNameDivision));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Log.e("Error on volly", volleyError.getLocalizedMessage());
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

    private void getSubDivisionName(final int pos) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sReportFilterList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("records");
                        JSONArray jsonArray = jsonObject1.getJSONArray("sub_division_list");
                        sSubDivisionID = new ArrayList<>();
                        sNameSubDivision = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (jsonArray.getJSONObject(i).getString("division_id").equalsIgnoreCase(sDivisionID.get(pos))) {
                                sSubDivisionID.add(jsonArray.getJSONObject(i).getString("sub_division_id"));
                                sNameSubDivision.add(jsonArray.getJSONObject(i).getString("name"));
                            }
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sNameSubDivision);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spSubDivision.setAdapter(adapter);

                   // spSubDivision.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sNameSubDivision));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Log.e("Error on volly", volleyError.getLocalizedMessage());
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

    private void getSectionName(final int pos) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sReportFilterList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("records");
                        JSONArray jsonArray = jsonObject1.getJSONArray("section_list");
                        sSectionID = new ArrayList<>();
                        sNameSection = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (jsonArray.getJSONObject(i).getString("sub_division_id").equalsIgnoreCase(sSubDivisionID.get(pos))) {
                                sSectionID.add(jsonArray.getJSONObject(i).getString("section_id"));
                                sNameSection.add(jsonArray.getJSONObject(i).getString("name"));
                            }
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sNameSection);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spSection.setAdapter(adapter);

                   // spSection.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sNameSection));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Log.e("Error on volly", volleyError.getLocalizedMessage());
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

    private void getCircleData(final String circle_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sReportPieChart, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    List<ReportDataBean> listItem = new ArrayList<>();
                    ReportDataBean mReportDataBean = null;
                    int nData = 0;
                    //Connection
                    int target = jsonObject.getJSONObject("records").getJSONObject("collection").getInt("target");
                    int achived = jsonObject.getJSONObject("records").getJSONObject("collection").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("collection").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("collection").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("collection").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Disconnection
                    target = jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Realisation
                    target = jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Arrear
                    target = jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Sanitisation
                    target = jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Enforcement
                    target = jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //MR
                    target = jsonObject.getJSONObject("records").getJSONObject("mr").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("mr").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("mr").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("mr").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("mr").getInt("collection"));
                    listItem.add(mReportDataBean);
                    ;
                    //GRF
                    target = jsonObject.getJSONObject("records").getJSONObject("grf").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("grf").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("grf").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("grf").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("grf").getInt("collection"));
                    listItem.add(mReportDataBean);
                    ;
                    rvPieChart.setAdapter(new PieChartRVAdapter(getActivity(), listItem));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Error on volly", volleyError.getLocalizedMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new HashMap<>();
                params.put("token", new PreferencesManager(getActivity()).getString("token"));
                params.put("circle_id", circle_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void getDivionData(final String circle_id, final String division_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sReportPieChart, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    List<ReportDataBean> listItem = new ArrayList<>();
                    ReportDataBean mReportDataBean = null;
                    int nData = 0;
                    //Connection
                    int target = jsonObject.getJSONObject("records").getJSONObject("collection").getInt("target");
                    int achived = jsonObject.getJSONObject("records").getJSONObject("collection").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("collection").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("collection").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("collection").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Disconnection
                    target = jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Realisation
                    target = jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Arrear
                    target = jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Sanitisation
                    target = jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Enforcement
                    target = jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //MR
                    target = jsonObject.getJSONObject("records").getJSONObject("mr").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("mr").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("mr").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("mr").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("mr").getInt("collection"));
                    listItem.add(mReportDataBean);
                    ;
                    //GRF
                    target = jsonObject.getJSONObject("records").getJSONObject("grf").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("grf").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("grf").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("grf").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("grf").getInt("collection"));
                    listItem.add(mReportDataBean);
                    ;
                    rvPieChart.setAdapter(new PieChartRVAdapter(getActivity(), listItem));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Error on volly", volleyError.getLocalizedMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new HashMap<>();
                params.put("token", new PreferencesManager(getActivity()).getString("token"));
                params.put("circle_id", circle_id);
                params.put("division_id", division_id);
                params.put("filter", "1");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void getSubDivionData(final String circle_id, final String division_id, final String subDivion_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sReportPieChart, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    List<ReportDataBean> listItem = new ArrayList<>();
                    ReportDataBean mReportDataBean = null;
                    int nData = 0;
                    //Connection
                    int target = jsonObject.getJSONObject("records").getJSONObject("collection").getInt("target");
                    int achived = jsonObject.getJSONObject("records").getJSONObject("collection").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("collection").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("collection").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("collection").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Disconnection
                    target = jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Realisation
                    target = jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Arrear
                    target = jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Sanitisation
                    target = jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Enforcement
                    target = jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //MR
                    target = jsonObject.getJSONObject("records").getJSONObject("mr").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("mr").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("mr").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("mr").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("mr").getInt("collection"));
                    listItem.add(mReportDataBean);
                    ;
                    //GRF
                    target = jsonObject.getJSONObject("records").getJSONObject("grf").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("grf").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("grf").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("grf").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("grf").getInt("collection"));
                    listItem.add(mReportDataBean);
                    ;
                    rvPieChart.setAdapter(new PieChartRVAdapter(getActivity(), listItem));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Error on volly", volleyError.getLocalizedMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new HashMap<>();
                params.put("token", new PreferencesManager(getActivity()).getString("token"));
                params.put("circle_id", circle_id);
                params.put("division_id", division_id);
                params.put("sub_division_id", subDivion_id);
                params.put("filter", "1");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void getSectionData(final String circle_id, final String division_id, final String subDivion_id, final String section_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sReportPieChart, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    List<ReportDataBean> listItem = new ArrayList<>();
                    ReportDataBean mReportDataBean = null;
                    int nData = 0;
                    //Connection
                    int target = jsonObject.getJSONObject("records").getJSONObject("collection").getInt("target");
                    int achived = jsonObject.getJSONObject("records").getJSONObject("collection").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("collection").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("collection").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("collection").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Disconnection
                    target = jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("disconnection").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Realisation
                    target = jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("realisation").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Arrear
                    target = jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("arrear").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Sanitisation
                    target = jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("sanitisation").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //Enforcement
                    target = jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("enforcement").getInt("collection"));
                    listItem.add(mReportDataBean);
                    //MR
                    target = jsonObject.getJSONObject("records").getJSONObject("mr").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("mr").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("mr").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("mr").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("mr").getInt("collection"));
                    listItem.add(mReportDataBean);
                    ;
                    //GRF
                    target = jsonObject.getJSONObject("records").getJSONObject("grf").getInt("target");
                    achived = jsonObject.getJSONObject("records").getJSONObject("grf").getInt("collection");
                    if (target == 0) {
                        nData = achived;
                    } else if (achived > 0)
                        nData = achived / target;
                    else
                        nData = 0;
                    mReportDataBean = new ReportDataBean((float) nData,
                            jsonObject.getJSONObject("records").getJSONObject("grf").getString("title"),
                            jsonObject.getJSONObject("records").getJSONObject("grf").getInt("target"),
                            jsonObject.getJSONObject("records").getJSONObject("grf").getInt("collection"));
                    listItem.add(mReportDataBean);
                    ;
                    rvPieChart.setAdapter(new PieChartRVAdapter(getActivity(), listItem));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Error on volly", volleyError.getLocalizedMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new HashMap<>();
                params.put("token", new PreferencesManager(getActivity()).getString("token"));
                params.put("circle_id", circle_id);
                params.put("division_id", division_id);
                params.put("sub_division_id", subDivion_id);
                params.put("section_id", section_id);
                params.put("filter", "1");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

}





