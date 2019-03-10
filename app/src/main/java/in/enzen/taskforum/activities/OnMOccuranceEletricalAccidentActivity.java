package in.enzen.taskforum.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.enzen.taskforum.R;
import in.enzen.taskforum.SplashActivity;
import in.enzen.taskforum.adapters.CropingOptionAdapter;
import in.enzen.taskforum.utils.CropingOption;
import in.enzen.taskforum.utils.PreferencesManager;

@SuppressWarnings("All")

public class OnMOccuranceEletricalAccidentActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView tvCurrentDate, tvCurrentTime;
    private TextView tvLocation;
    private int dd;
    private int mm;
    private int yy;
    private double dLatitute;
    private double dLongitute;
    private TextInputLayout tilSelectDate, tilSelectTime;
    private TextView tvSelectDate, tvSelectTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Button btnReset, btnSubmit;
    private TextView tvAttachSnapPath;
    private ImageView imgSnap;

    private Uri mImageCaptureUri = null;
    private String sImageBase64 = "";
    private final int CAMERA = 0;
    private final int CROPING_CODE = 201;
    private final File outPutFile = new File("/sdcard/", "temp.jpg");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_moccurance_eletrical_accident);

        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);
        // Typeface appFontRegular = Typeface.createFromAsset(getAssets(), "fonts/app_font_regular.ttf");
        final Typeface appFontRegular2 = Typeface.createFromAsset(getAssets(), "fonts/madras_regular.ttf");
        TextView tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        tvToolbarTitle.setText("ELETRICAL ACCIDENT");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        tvToolbarTitle.setTypeface(appFontRegular2);
        final ImageView imgLogo = findViewById(R.id.imgLogo);
        final RelativeLayout rlHeadContainer = findViewById(R.id.rlHeadContainer);
        imgLogo.bringToFront();
        rlHeadContainer.invalidate();


        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvLocation = findViewById(R.id.tvLocation);
        tilSelectDate = findViewById(R.id.tilSelectDate);
        tilSelectTime = findViewById(R.id.tilSelectTime);
        btnReset = findViewById(R.id.btnReset);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvSelectDate = findViewById(R.id.tvSelectDate);
        tvSelectTime = findViewById(R.id.tvSelectTime);
        imgSnap = (ImageView) findViewById(R.id.imgSnap);

        tvCurrentDate.setTypeface(appFontRegular2);
        tvCurrentTime.setTypeface(appFontRegular2);
        tvLocation.setTypeface(appFontRegular2);
        tilSelectDate.setTypeface(appFontRegular2);
        tilSelectTime.setTypeface(appFontRegular2);
        tvSelectDate.setTypeface(appFontRegular2);
        tvSelectTime.setTypeface(appFontRegular2);


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
        // set current date into textview
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

    }

    private void updateTimeView() {
        Date refreshDate = Calendar.getInstance().getTime();
        String time = "hh:mm a";
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
                tvSelectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, mYear, mMonth, mDay);
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
                String status = "AM";
                if (hourOfDay > 11) {
                    status = "PM";
                }
                int hour_of_12_hour_format;
                if (hourOfDay > 11) {
                    hour_of_12_hour_format = hourOfDay - 12;
                } else {
                    hour_of_12_hour_format = hourOfDay;
                }
                tvSelectTime.setText(hour_of_12_hour_format + " : " + minute + " " + status);
               /* String AM_PM;
                if (hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }*/
                // tvSelectTime.setText(hourOfDay + ":" + minute + " " + AM_PM);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void showLogoutAlertDialog() {
        final SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OnMOccuranceEletricalAccidentActivity.this);
        // set title
        alertDialogBuilder.setTitle("Confirmation");
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PreferencesManager(getApplicationContext()).setLoggedIn(false);
                        startActivity(new Intent(OnMOccuranceEletricalAccidentActivity.this, SplashActivity.class));


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
                CropingOptionAdapter adapter = new CropingOptionAdapter(OnMOccuranceEletricalAccidentActivity.this, cropOptions);
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(OnMOccuranceEletricalAccidentActivity.this);
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
        }}
}



