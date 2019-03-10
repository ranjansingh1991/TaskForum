package in.enzen.taskforum.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.adapters.SOSRVAdapter;
import in.enzen.taskforum.dialogs.ProgressDialog;
import in.enzen.taskforum.model.PlacesListData;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.utils.Constraints.PROGRESS_DIALOG_BG_COLOR;

@SuppressWarnings("ALL")
public class SOSActivity extends AppCompatActivity implements LocationListener {

    private final String API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private final String API_KEY = "AIzaSyC1GSLUEeKa-0O00squII0NC-ujEbG6Svg";//"AIzaSyAlephJ0hrtumqgyu67AoCkKGWIlZbMc-U";
    private RecyclerView rvSOS;
    private SOSRVAdapter mSosrvAdapter;
    private double dLatitude;
    private double dLongitude;
    private LocationManager locationManager;
    private List<PlacesListData> listData = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    private Toolbar mToolbar;
    private static final int CALL_PHONE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            init();
        } else {
            requestCallPermission();
        }
    }

    private void init() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);
        final Typeface appFontRegular2 = Typeface.createFromAsset(getAssets(), "fonts/madras_regular.ttf");
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = findViewById(R.id.imgBack);
        ImageView ivSos = findViewById(R.id.ivSos);
        ivSos.setVisibility(View.GONE);
        tvToolbarTitle.setText("SOS");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        tvToolbarTitle.setTypeface(appFontRegular2);

        mProgressDialog = new ProgressDialog(SOSActivity.this, "Getting information ...");
        this.mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(PROGRESS_DIALOG_BG_COLOR));
        mProgressDialog.show();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String sBestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location loc = locationManager.getLastKnownLocation(sBestProvider);
        if (loc != null) {
            onLocationChanged(loc);
        } else {
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null)
                onLocationChanged(loc);
        }
        rvSOS = (RecyclerView) findViewById(R.id.rvSOS);
        StringBuilder mStringBuilder = new StringBuilder(API);
        mStringBuilder.append("location=").append(dLatitude).append(",").append(dLongitude);
        mStringBuilder.append("&radius=3000");
        mStringBuilder.append("&types=" + "police");
        mStringBuilder.append("&key=");
        mStringBuilder.append(API_KEY);
        // Creating a new non-ui thread task to download Google place json data
        PlacesTask mPlacesTask = new PlacesTask();
        // Invokes the "doInBackground()" method of the class PlaceTask
        mPlacesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mStringBuilder.toString());
    }

    /*  Show Popup to access User Permission  */
    private void requestCallPermission() {
        ActivityCompat.requestPermissions(SOSActivity.this,
                new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
    }

    //On Request permission method to check the permisison is granted or not for Marshmallow+ Devices
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If permission granted show location dialog if APIClient is not null
                    init();
                } else {
                    Log.e("About Call", "Call Permission denied.");
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        dLatitude = location.getLatitude();
        dLongitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /* A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        // URL define here..
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            // Checking connection..
            urlConnection = (HttpURLConnection) url.openConnection();
            // Optional function for time reading from the google
            urlConnection.setReadTimeout(10000  /*Time IN Milliseconds i.e 10sec */);
            urlConnection.setConnectTimeout(15000 /*Time IN Milliseconds i.e 30sec */);
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    private String downloadUrl1(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;        // URL define here..
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();        // Checking connection..
            // Optional function for time reading from the google
            urlConnection.setReadTimeout(10000  /*Time IN Milliseconds i.e 10sec */);
            urlConnection.setConnectTimeout(15000 /*Time IN Milliseconds i.e 30sec */);
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    /* Defining a class to fetch Google Places & display in list view according to user choice..*/
    class PlacesTask extends AsyncTask<String, Integer, String> {
        String data = null;

        public PlacesTask() {
        }

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {

            try {
                data = downloadUrl(url[0]);
                //url from which to fetch data
            } catch (Exception e) {
                int stTemp = e.toString().indexOf(":");
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(final String result) {
            if (data != null && data.length() > 1) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (i < 5) {
                            JSONObject jsObject = jsonArray.getJSONObject(i);
                            String sReference = jsObject.getString("reference");
                            String sName = jsObject.getString("name");
                            String sVicinity = jsObject.getString("vicinity");
                            JSONObject jsGeometry = jsObject.getJSONObject("geometry");
                            double Lat = jsGeometry.getJSONObject("location").getDouble("lat");
                            double Lng = jsGeometry.getJSONObject("location").getDouble("lng");
                            StringBuilder mStringBuilder;
                            mStringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?reference=");
                            mStringBuilder.append(sReference).append("&key=").append(API_KEY);
                            ReferrenceTask mReferrenceTask = new ReferrenceTask(i);
                            mReferrenceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mStringBuilder.toString());
                            PlacesListData mPlacesListData = new PlacesListData(sName, sVicinity, null, Lat, Lng);
                            listData.add(mPlacesListData);
                        }
                    }
                    mSosrvAdapter = new SOSRVAdapter(SOSActivity.this, listData, dLatitude, dLongitude);
                    rvSOS.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvSOS.setAdapter(mSosrvAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ReferrenceTask extends AsyncTask<String, Integer, String> {

        String data = null;
        int pos;

        ReferrenceTask(int pos) {
            this.pos = pos;
        }

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl1(url[0]);
                //url from which to fetch data
            } catch (Exception e) {
                int stTemp = e.toString().indexOf(":");
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (data != null && data.length() > 1) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    jsonObject = jsonObject.getJSONObject("result");
                    String sPhone = jsonObject.getString("formatted_phone_number");
                    PlacesListData mPlacesListData = listData.get(pos);
                    mPlacesListData = new PlacesListData(mPlacesListData.getName(),
                            mPlacesListData.getVicinity(),
                            sPhone, mPlacesListData.getLat(), mPlacesListData.getLng());
                    listData.set(pos, mPlacesListData);
                    mSosrvAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        }
    }

    public void showLogoutAlertDialog() {
        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SOSActivity.this);
        // set title
        alertDialogBuilder.setTitle("Confirmation");
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(SOSActivity.this, SplashActivity.class));


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
