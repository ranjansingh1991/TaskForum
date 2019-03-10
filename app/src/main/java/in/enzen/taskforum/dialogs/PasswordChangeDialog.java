package in.enzen.taskforum.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.utils.ConnectionDetector;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.rest.BaseUrl.sProfileUpdate;

/**
 * Created by Rupesh on 04-12-2017.
 */
@SuppressWarnings("ALL")
public class PasswordChangeDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private EditText etPwdOld;
    private EditText etNewPwd;
    private Activity activity;

    public PasswordChangeDialog(Activity activity, int themeResId) {
        super(activity, themeResId);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_change_password);
        Typeface appFontRegular = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");
        final TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        etPwdOld = (EditText) findViewById(R.id.etPwdOld);
        etNewPwd = (EditText) findViewById(R.id.etNewPwd);
        final Button btnCancel = (Button) findViewById(R.id.btnCancel);
        final Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        tvTitle.setTypeface(appFontRegular);
        etPwdOld.setTypeface(appFontRegular, Typeface.NORMAL);
        etNewPwd.setTypeface(appFontRegular, Typeface.NORMAL);
        btnCancel.setTypeface(appFontRegular);
        btnSubmit.setTypeface(appFontRegular);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        tvTitle.setMinWidth(displayMetrics.widthPixels - 16);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancel:
                dismiss();
                break;

            case R.id.btnSubmit:
                // 1. Here we create a method to check Internet avalale or not.
                if (new ConnectionDetector(getContext()).isConnectingToInternet()) {
                    changePassword();
                } else {
                    final NoConnectionDialog mNoConnectionDialog = new NoConnectionDialog(getContext(), R.style.DialogSlideAnim);
                    mNoConnectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mNoConnectionDialog.setCancelable(false);
                    mNoConnectionDialog.setCanceledOnTouchOutside(false);
                    mNoConnectionDialog.getWindow().setGravity(Gravity.BOTTOM);
                    mNoConnectionDialog.show();
                    dismiss();
                    break;
                }
        }
    }


    private void changePassword() {
        final String currPassword = etPwdOld.getText().toString();
        final String newPassword = etNewPwd.getText().toString();
        if (etPwdOld.getText().toString().trim().length() < 1) {
            etPwdOld.setError("Please enter current password");
        } else if (etPwdOld.getText().toString().equals(etNewPwd.getText().toString())) {
            etNewPwd.setError("New password woun't be the same as current password");
        } else if (etNewPwd.getText().toString().trim().length() < 4) {
            etNewPwd.setError("Password must be minimum of 4 characters");
        } else {
            final ProgressDialog mProgressDialog = new ProgressDialog(activity, activity.getApplicationContext().getResources().getString(R.string.please_wait));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, sProfileUpdate,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String sResult) {
                            try {
                                JSONObject jsonObject = new JSONObject(sResult);
                                if (jsonObject.getBoolean("status")) {
                                    Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mProgressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    //Dismissing the progress dialog
                    mProgressDialog.dismiss();
                    Log.e("status Response", String.valueOf(volleyError));
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("password", newPassword);
                    params.put("token", new PreferencesManager(activity).getString("token"));
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            requestQueue.add(stringRequest);
        }
    }

}
