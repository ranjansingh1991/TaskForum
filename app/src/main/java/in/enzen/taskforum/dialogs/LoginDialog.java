package in.enzen.taskforum.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.activities.HomeActivity;
import in.enzen.taskforum.utils.ConnectionDetector;
import in.enzen.taskforum.utils.KeyNames;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.rest.BaseUrl.sLoginURL;
import static in.enzen.taskforum.utils.Constraints.PROGRESS_DIALOG_BG_COLOR;

/**
 * Created by Rupesh on 31-10-2017.
 */
@SuppressWarnings("ALL")
public class  LoginDialog extends Dialog implements View.OnClickListener, KeyNames {

    TextInputLayout tilUserId;
    TextInputLayout tilPassword;
    EditText etUserId;
    EditText etPassword;
    Button btnLogin;
    private LinearLayout llParentSignInDialog;
    private RelativeLayout rlDialogContainer;
    private ImageView imgLogo;
    private ImageView imgPwdControl;
    private Activity activity;
    private boolean isPasswordVisible = false;
    private ProgressDialog mProgressDialog;

    public LoginDialog(Activity activity, int themeResId) {
        super(activity, themeResId);
        this.activity = activity;
        this.mProgressDialog = new ProgressDialog(activity,
                activity.getApplicationContext().getResources().getString(R.string.please_wait));
        this.mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(PROGRESS_DIALOG_BG_COLOR));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_sign_in);

        init();
    }

    private void init() {
        Typeface appFontRegular = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");
        // Typeface appFontLight = Typeface.createFromAsset(activity.getAssets(), "fonts/montserrat_light.ttf");
        llParentSignInDialog = (LinearLayout) findViewById(R.id.llParentSignInDialog);
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        llParentSignInDialog.setMinimumWidth(displayMetrics.widthPixels);
        rlDialogContainer = (RelativeLayout) findViewById(R.id.rlDialogContainer);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imgPwdControl = (ImageView) findViewById(R.id.imgPwdControl);
        imgLogo.bringToFront();
        rlDialogContainer.invalidate();
        tilUserId = (TextInputLayout) findViewById(R.id.tilUserId);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        etUserId = (EditText) findViewById(R.id.etUserId);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        tilUserId.setTypeface(appFontRegular);
        tilPassword.setTypeface(appFontRegular);
        etUserId.setTypeface(appFontRegular);
        etPassword.setTypeface(appFontRegular);
        btnLogin.setTypeface(appFontRegular);

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etPassword.getText().length() < 1) {
                    imgPwdControl.setImageDrawable(activity.getDrawable(R.drawable.ic_pwd_eye_disabled_36));
                } else if (isPasswordVisible) {
                    imgPwdControl.setImageDrawable(activity.getDrawable(R.drawable.ic_pwd_eye_open_36));
                } else if (!isPasswordVisible) {
                    imgPwdControl.setImageDrawable(activity.getDrawable(R.drawable.ic_pwd_eye_closed_36));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnLogin.setOnClickListener(this);
        imgPwdControl.setOnClickListener(this);
    }


    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
               /* Calendar calendar = Calendar.getInstance();
                int iLoginHours = calendar.get(Calendar.HOUR_OF_DAY);
                if (iLoginHours >= 9 && iLoginHours < 20) {
                    Toast.makeText(activity, "Login Success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, "Working Hours is Over Please Login Tomorrow After 9 AM", Toast.LENGTH_SHORT).show();
                    break;
                }*/
                mProgressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (view == btnLogin) {
                            if (new ConnectionDetector(activity).isConnectingToInternet()) {
                                // loginTask();
                                newLogin(etUserId.getText().toString(), etPassword.getText().toString());
                            } else {
                                showNoInternetDialog();
                            }
                        }
                    }
                }, 2000);
                break;

            case R.id.imgPwdControl:
                if (etPassword.getText().length() < 1) {
                    break;
                } else {
                    if (isPasswordVisible) {
                        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etPassword.setSelection(etPassword.getText().length());
                        isPasswordVisible = false;
                    } else {
                        etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        etPassword.setSelection(etPassword.getText().length());
                        isPasswordVisible = true;
                    }
                }
                break;
        }
    }

    private void showNoInternetDialog() {
        NoInternetDialog mNoInternetDialog = new NoInternetDialog(activity, R.style.DialogSlideAnim);
        mNoInternetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mNoInternetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mNoInternetDialog.setCancelable(false);
        mNoInternetDialog.setCanceledOnTouchOutside(false);
        mNoInternetDialog.show();
    }

    private void newLogin(final String sEmail, final String sPassword) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sLoginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String sResult) {
                if (sResult != null) {
                    PreferencesManager mPreferencesManager = new PreferencesManager(activity);
                    try {
                        JSONObject jsonObject = new JSONObject(sResult);
                        if (jsonObject.getBoolean("authentication")) {
                            if (jsonObject.getBoolean("status")) {
                                new PreferencesManager(activity).setEmail(sEmail);
                                String s = new PreferencesManager(activity).getEmail();
                                mPreferencesManager.setString(TOKEN, jsonObject.getString("token"));
                                jsonObject = jsonObject.getJSONObject("records").getJSONObject("user");
                                mPreferencesManager.setString(ATTENDANCE_ID, jsonObject.getString("attendance_id"));
                                mPreferencesManager.setString(LOGIN_ID, jsonObject.getString("login_user_id"));
                                mPreferencesManager.setString(USER_NAME, jsonObject.getString("fullname"));
                                mPreferencesManager.setString(EMPLOYER, jsonObject.getString("employer"));
                                mPreferencesManager.setString(EMP_ID, jsonObject.getString("employment_id"));
                                mPreferencesManager.setString(DEPARTMENT, jsonObject.getString("department"));
                                mPreferencesManager.setString(DESIGNATION, jsonObject.getString("designation"));
                                mPreferencesManager.setString(CIRCLE_ID, jsonObject.getString("circle_id"));
                                mPreferencesManager.setString(DIVISION_ID, jsonObject.getString("division_id"));
                                mPreferencesManager.setString(SUB_DIVISION_ID, jsonObject.getString("sub_division_id"));
                                mPreferencesManager.setString(SECTION_ID, jsonObject.getString("section_id"));
                                mPreferencesManager.setString(MOBILE_NO, jsonObject.getString("phone"));
                                mPreferencesManager.setString(USER_IMAGE, jsonObject.getString("img_url"));
                                mPreferencesManager.setString(EMAIL, jsonObject.getString("email"));
                                mPreferencesManager.setString(REPORTING_MANAGER, jsonObject.getString("reporting_manager"));
                                new PreferencesManager(activity).setLoggedIn(true);
                                activity.startActivity(new Intent(activity, HomeActivity.class));
                                activity.finish();
                            } else {
                                Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Redirect to login if authentication FALSE
                            LoginDialog mLoginDialog = new LoginDialog(activity, R.style.DialogSlideAnim);
                            mLoginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            mLoginDialog.getWindow().setGravity(Gravity.BOTTOM);
                            mLoginDialog.setCancelable(false);
                            mLoginDialog.setCanceledOnTouchOutside(false);
                            mLoginDialog.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(activity, "Oops! Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
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
                params.put("email", sEmail);
                params.put("password", sPassword);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

}
