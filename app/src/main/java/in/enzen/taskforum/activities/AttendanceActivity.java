package in.enzen.taskforum.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.adapters.AttendancePagerAdapter;
import in.enzen.taskforum.sliding.SlidingTabLayout;
import in.enzen.taskforum.utils.PreferencesManager;

/**
 * Created by Rupesh on 08-12-2017.
 */
@SuppressWarnings("ALL")
public class AttendanceActivity extends AppCompatActivity {

    private SlidingTabLayout stAttendance;
    private ViewPager vpAttendance;
    private Toolbar mToolbar;
    private AttendancePagerAdapter mAttendancePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.fragment_attendance);

        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);

        Typeface appFontRegular = Typeface.createFromAsset(getAssets(), "fonts/madras_regular.ttf");

        TextView tvToolbarTitle = (TextView)findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView)findViewById(R.id.imgBack);

        tvToolbarTitle.setText("attendance");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getSupportFragmentManager().popBackStackImmediate();
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

        tvToolbarTitle.setTypeface(appFontRegular);

        stAttendance = (SlidingTabLayout)findViewById(R.id.stAttendance);
        vpAttendance = (ViewPager)findViewById(R.id.vpAttendance);

        mAttendancePagerAdapter = new AttendancePagerAdapter(getSupportFragmentManager(), AttendanceActivity.this);
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
        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AttendanceActivity.this);

        // set title
        alertDialogBuilder.setTitle("Confirmation");

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(AttendanceActivity.this, SplashActivity.class));
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
