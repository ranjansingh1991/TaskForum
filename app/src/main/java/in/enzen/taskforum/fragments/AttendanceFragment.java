package in.enzen.taskforum.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.adapters.AttendancePagerAdapter;
import in.enzen.taskforum.sliding.SlidingTabLayout;
import in.enzen.taskforum.utils.PreferencesManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rupesh on 13-11-2017.
 */
@SuppressWarnings("ALL")
public class AttendanceFragment extends Fragment {

    SlidingTabLayout stAttendance;
    ViewPager vpAttendance;
    Toolbar mToolbar;
    AttendancePagerAdapter mAttendancePagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendance, container, false);

        init(rootView);
        return rootView;
    }

    private void init(View childView) {
        mToolbar = (Toolbar) childView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface appFontRegular = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");

        TextView tvToolbarTitle = (TextView) childView.findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) childView.findViewById(R.id.imgBack);

        tvToolbarTitle.setText("attendance");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        ImageView ivLogout = (ImageView) childView.findViewById(R.id.ivLogout);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });

        tvToolbarTitle.setTypeface(appFontRegular);

        stAttendance = (SlidingTabLayout)childView.findViewById(R.id.stAttendance);
        vpAttendance = (ViewPager)childView.findViewById(R.id.vpAttendance);

        mAttendancePagerAdapter = new AttendancePagerAdapter(getFragmentManager(), getActivity());
        stAttendance.setCustomTabView(R.layout.tab_title_item, R.id.tvTabName);
        stAttendance.setDistributeEvenly(true);
        vpAttendance.setAdapter(mAttendancePagerAdapter);
        try {
            stAttendance.setViewPager(vpAttendance);
        } catch (Exception e) {
            Log.i("FF", e.getMessage());
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
