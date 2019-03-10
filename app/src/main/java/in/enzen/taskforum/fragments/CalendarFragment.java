package in.enzen.taskforum.fragments;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.ForegroundColorSpan;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.activities.SOSActivity;
import in.enzen.taskforum.calendar.CalendarDay;
import in.enzen.taskforum.calendar.DayViewDecorator;
import in.enzen.taskforum.calendar.DayViewFacade;
import in.enzen.taskforum.calendar.MaterialCalendarView;
import in.enzen.taskforum.calendar.OnDateSelectedListener;
import in.enzen.taskforum.calendar.OnMonthChangedListener;
import in.enzen.taskforum.calendar.decorators.HighlightWeekendsDecorator;
import in.enzen.taskforum.calendar.decorators.MySelectorDecorator;
import in.enzen.taskforum.calendar.decorators.OneDayDecorator;
import in.enzen.taskforum.calendar.spans.DotSpan;
import in.enzen.taskforum.utils.PreferencesManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rupesh on 13-11-2017.
 */
@SuppressWarnings("ALL")
public class CalendarFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener {

    MaterialCalendarView mCV_teacher_activity;
    TextView vT_selected_date;
    Toolbar mToolbar;

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calender, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface appFontRegular = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");

        TextView tvToolbarTitle = (TextView) rootView.findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        ImageView ivSos = (ImageView) rootView.findViewById(R.id.ivSos);

        tvToolbarTitle.setText("calendar");
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

        mCV_teacher_activity = (MaterialCalendarView) rootView.findViewById(R.id.mCV_teacher_activity);
        vT_selected_date = (TextView) rootView.findViewById(R.id.vT_selected_date);

        mCV_teacher_activity = (MaterialCalendarView) rootView.findViewById(R.id.mCV_teacher_activity);
        vT_selected_date = (TextView) rootView.findViewById(R.id.vT_selected_date);

        mCV_teacher_activity.setOnDateChangedListener(this);
        mCV_teacher_activity.setOnMonthChangedListener(this);

        mCV_teacher_activity.addDecorator(oneDayDecorator);

        mCV_teacher_activity.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);

        // mCV_teacher_activity.setDateSelected(CalendarDay.from(2017, 6, 29), true);
        // mCV_teacher_activity.setDateSelected(CalendarDay.from(2017, 7, 2), true);

        mCV_teacher_activity.addDecorator(new EventDecorator());

        mCV_teacher_activity.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.header_back)));
                view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.header_back)));
            }
        });

        vT_selected_date.setText(getSelectedDatesString());

        mCV_teacher_activity.addDecorators(
                new MySelectorDecorator(getActivity()),
                new HighlightWeekendsDecorator(),
                oneDayDecorator
        );

        return rootView;
    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        vT_selected_date.setText(getSelectedDatesString());
        //If you change a decorate, you need to invalidate decorators
        oneDayDecorator.setDate(date.getDate());
        widget.invalidateDecorators();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        // noinspection ConstantConditions
        // getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    private String getSelectedDatesString() {
        CalendarDay date = mCV_teacher_activity.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }

    private class EventDecorator implements DayViewDecorator {

        private final CalendarDay today;
        private final Drawable backgroundDrawable;
        private final int color;

        private EventDecorator() {
            today = CalendarDay.today();
            backgroundDrawable = getResources().getDrawable(R.drawable.today_circle_background);
            color = getResources().getColor(R.color.colorAccent);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return today.equals(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(backgroundDrawable);
            view.addSpan(new DotSpan(1, color));
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
