package in.enzen.taskforum.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
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

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.dialogs.ProgressDialog;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.rest.BaseUrl.sSavePreventive;
import static in.enzen.taskforum.utils.Constraints.PROGRESS_DIALOG_BG_COLOR;

@SuppressWarnings("all")


public class OnMPMaintenanceActivity extends AppCompatActivity {

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
    private EditText etFeederName, etDtrName, etDtrLocation, etCapacity, etPoleErection, etRejumpering, etTreeTrimming,
            etDamageCondInMtr, etDamageAbCableMtr, etTransformerEarthing, etDamagePinInsulator, etDamageSickleInsulator;

    private TextView tvImageTitle, tvAttachSnapPath;
    private ImageView imgSnap;

    private Uri mImageCaptureUri = null;
    private String sImageBase64 = "";
    private final int CAMERA = 0;
    private final int CROPING_CODE = 201;
    private final File outPutFile = new File("/sdcard/", "temp.jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_mpmaintenance);
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
        tvToolbarTitle.setText("PREVENTIVE MAINTAINCE");
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
        final ImageView imgLogo = findViewById(R.id.imgLogo);
        final RelativeLayout rlHeadContainer = findViewById(R.id.rlHeadContainer);
        imgLogo.bringToFront();
        rlHeadContainer.invalidate();

        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvSelectDate = findViewById(R.id.tvSelectDate);
        tvSelectTime = findViewById(R.id.tvSelectTime);
        tilSelectDate = findViewById(R.id.tilSelectDate);
        tilSelectTime = findViewById(R.id.tilSelectTime);
        tvImageTitle = findViewById(R.id.tvImageTitle);
        tvAttachSnapPath = findViewById(R.id.tvAttachSnapPath);
        imgSnap = findViewById(R.id.imgSnap);
        etFeederName = findViewById(R.id.etFeederName);
        etDtrName = findViewById(R.id.etDtrName);
        etDtrLocation = findViewById(R.id.etDtrLocation);
        etCapacity = findViewById(R.id.etCapacity);
        etPoleErection = findViewById(R.id.etPoleErection);
        etRejumpering = findViewById(R.id.etRejumpering);
        etDamagePinInsulator = findViewById(R.id.etDamagePinInsulator);
        etDamageAbCableMtr = findViewById(R.id.etDamageAbCableMtr);
        etTransformerEarthing = findViewById(R.id.etTransformerEarthing);
        etDamageCondInMtr = findViewById(R.id.etDamageCondInMtr);
        etDamageSickleInsulator = findViewById(R.id.etDamageSickleInsulator);
        etTreeTrimming = findViewById(R.id.etTreeTrimming);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnReset = findViewById(R.id.btnReset);
        imgSnap = findViewById(R.id.imgSnap);
        tvCurrentDate.setTypeface(appFontRegular2);
        tvCurrentTime.setTypeface(appFontRegular2);
        tvSelectDate.setTypeface(appFontRegular2);
        tvSelectTime.setTypeface(appFontRegular2);
        tvImageTitle.setTypeface(appFontRegular2);
        tvAttachSnapPath.setTypeface(appFontRegular2);
        etFeederName.setTypeface(appFontRegular2);
        etDtrName.setTypeface(appFontRegular2);
        etDtrLocation.setTypeface(appFontRegular2);
        etCapacity.setTypeface(appFontRegular2);
        etTreeTrimming.setTypeface(appFontRegular2);
        etDamageSickleInsulator.setTypeface(appFontRegular2);
        etTransformerEarthing.setTypeface(appFontRegular2);
        etDamageAbCableMtr.setTypeface(appFontRegular2);
        etDamageCondInMtr.setTypeface(appFontRegular2);
        etDamagePinInsulator.setTypeface(appFontRegular2);
        etRejumpering.setTypeface(appFontRegular2);
        etPoleErection.setTypeface(appFontRegular2);


        btnSubmit.setTypeface(appFontRegular2);
        btnReset.setTypeface(appFontRegular2);

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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OnMPMaintenanceActivity.this);
        // set title
        alertDialogBuilder.setTitle("Confirmation");
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(OnMPMaintenanceActivity.this, SplashActivity.class));
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

    private void doSubmit() {
        final ProgressDialog mProgressDialog = new ProgressDialog(OnMPMaintenanceActivity.this, "Please wait...");
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(PROGRESS_DIALOG_BG_COLOR));
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sSavePreventive,
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
                params.put("feeder_name", etFeederName.getText().toString());
                params.put("dtr_name", etDtrName.getText().toString());
                params.put("dtr_location", etDtrLocation.getText().toString());
                params.put("capacity", etCapacity.getText().toString());
                params.put("pole_erection", etPoleErection.getText().toString());
                params.put("rejumpering_span", etRejumpering.getText().toString());
                params.put("tree_trimming", etTreeTrimming.getText().toString());
                params.put("damage_cond_in_mtr", etDamageCondInMtr.getText().toString());
                params.put("damage_ab_cable_mtr", etDamageAbCableMtr.getText().toString());
                params.put("transformer_earthing", etTransformerEarthing.getText().toString());
                params.put("damage_pin_insulator", etDamagePinInsulator.getText().toString());
                params.put("damage_sickle_insulator", etDamageSickleInsulator.getText().toString());
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
        etFeederName.setText("");
        etDtrName.setText("");
        etDtrLocation.setText("");
        etCapacity.setText("");
        etPoleErection.setText("");
        etRejumpering.setText("");
        etDamagePinInsulator.setText("");
        etDamageCondInMtr.setText("");
        etTransformerEarthing.setText("");
        etDamageAbCableMtr.setText("");
        etDamageSickleInsulator.setText("");
        etTreeTrimming.setText("");
    }


}

