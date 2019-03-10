package in.enzen.taskforum.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.activities.HomeActivity;
import in.enzen.taskforum.activities.ProfileActivity;
import in.enzen.taskforum.civ.CircleImageView;
import in.enzen.taskforum.dialogs.PasswordChangeDialog;
import in.enzen.taskforum.dialogs.ProgressDialog;
import in.enzen.taskforum.model.AccountBean;
import in.enzen.taskforum.utils.KeyNames;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.rest.BaseUrl.sProfileUpdate;

/**
 * Created by Rupesh on 01-12-2017.
 */
@SuppressWarnings("ALL")
public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder> implements KeyNames {

    public static CircleImageView civUserPic;

    private static EditText etName;
    private static TextView tvEmail;
    private static EditText etPhone;
    private static TextView tvEmployer;
    private static TextView tvEmpID;
    private static TextView tvDesignation;
    private static TextView tvReportingManager;

    private final int CAMERA = 0;
    private final int GALLERY = 1;
    private ProfileActivity activity;
    private Uri mImageCaptureUri;
    private AlertDialog alert;
    private AccountBean mAccountBean;
    ProgressDialog mProgressDialog;

    public Bitmap bitmap;


    public AccountListAdapter(ProfileActivity activity, AccountBean mAccountBean) {
        this.activity = activity;
        this.mAccountBean = mAccountBean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;
        ViewHolder mViewHolder = null;
        switch (viewType) {
            case 0:
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_list_header, parent, false);
                mViewHolder = new ViewHolder(rootView, viewType);
                break;

            case 1:
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_list_body_items, parent, false);
                mViewHolder = new ViewHolder(rootView, viewType);
                break;

            case 2:
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_list_control_items, parent, false);
                mViewHolder = new ViewHolder(rootView, viewType);
                break;
        }
        return mViewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Typeface appFontRegular = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");

        switch (position) {
            case 0:
                // holder.tvUserFullName.setText(mAccountBean.getName());
                holder.tvUserFullName.setText(new PreferencesManager(activity).getString(USER_NAME));
                holder.tvUserFullName.setTypeface(appFontRegular);
                Picasso.with(activity).load(new PreferencesManager(activity).getString(USER_IMAGE)).into(civUserPic);
                // Picasso.with(activity).load(mAccountBean.getImageURL()).into(civUserPic);
                holder.btnEditProfile.setTypeface(appFontRegular);
                holder.btnEditProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PasswordChangeDialog mPasswordChangeDialog = new PasswordChangeDialog(activity, R.style.DialogSlideAnim);
                        mPasswordChangeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        mPasswordChangeDialog.getWindow().setGravity(Gravity.BOTTOM);
                        mPasswordChangeDialog.show();
                    }
                });
                holder.imgUploadNewPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagePopUp();
                    }
                });
                break;

            case 1:
                etName.setText(new PreferencesManager(activity).getString(USER_NAME));
                tvEmail.setText(new PreferencesManager(activity).getString(EMAIL));
                etPhone.setText(new PreferencesManager(activity).getString(MOBILE_NO));
                tvEmployer.setText(new PreferencesManager(activity).getString(EMPLOYER));
                tvEmpID.setText(new PreferencesManager(activity).getString(EMP_ID));
                tvDesignation.setText(new PreferencesManager(activity).getString(DESIGNATION));
                tvReportingManager.setText(new PreferencesManager(activity).getString(REPORTING_MANAGER));

                etName.setTypeface(appFontRegular);
                tvEmail.setTypeface(appFontRegular);
                etPhone.setTypeface(appFontRegular);
                tvEmployer.setTypeface(appFontRegular);
                tvEmpID.setTypeface(appFontRegular);
                tvDesignation.setTypeface(appFontRegular);
                tvReportingManager.setTypeface(appFontRegular);
                break;

            case 2:
                holder.btnCancel.setTypeface(appFontRegular);
                holder.btnSubmit.setTypeface(appFontRegular);
                holder.btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadUserInfo();
                    }
                });
                holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivity(new Intent(activity, HomeActivity.class));
                        activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                        activity.finish();
                    }
                });
                break;
        }
    }

    private void uploadUserInfo() {
        mProgressDialog = new ProgressDialog(activity, "Updating details...");
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        //sending image to server
        StringRequest request = new StringRequest(Request.Method.POST, sProfileUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
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
                mProgressDialog.dismiss();
                Log.e("status Response", String.valueOf(volleyError));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fullname", etName.getText().toString());
                params.put("phone", etPhone.getText().toString());
                params.put("token", new PreferencesManager(activity).getString("token"));
                return params;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(activity);
        rQueue.add(request);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    private void imagePopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.profile_pic_change_dialog, null);
        builder.setView(dialogView);
        final TextView tvCameraPPCD = (TextView) dialogView.findViewById(R.id.tvCameraPPCD);
        final TextView tvGalleryPPCD = (TextView) dialogView.findViewById(R.id.tvGalleryPPCD);
        tvCameraPPCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageCaptureUri = Uri.parse("file:///sdcard/photo.jpg");
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                activity.startActivityForResult(intent, CAMERA);
                alert.dismiss();
            }
        });

        tvGalleryPPCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(i, GALLERY);
                alert.dismiss();
            }
        });
        alert = builder.create();
        alert.show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUploadNewPic;
        TextView tvUserFullName;
        Button btnEditProfile;
        Button btnCancel;
        Button btnSubmit;

        public ViewHolder(View itemView, int nPosition) {
            super(itemView);
            switch (nPosition) {
                case 0:
                    civUserPic = (CircleImageView) itemView.findViewById(R.id.civUserPic);
                    imgUploadNewPic = itemView.findViewById(R.id.imgUploadNewPic);
                    tvUserFullName = itemView.findViewById(R.id.tvUserFullName);
                    btnEditProfile = itemView.findViewById(R.id.btnEditProfile);
                    break;
                case 1:
                    etName = itemView.findViewById(R.id.etName);
                    tvEmail = itemView.findViewById(R.id.tvEmail);
                    etPhone = itemView.findViewById(R.id.etPhone);
                    tvEmployer = itemView.findViewById(R.id.tvEmployer);
                    tvEmpID = itemView.findViewById(R.id.tvEmpID);
                    tvReportingManager = itemView.findViewById(R.id.tvReportingManager);
                    tvDesignation = itemView.findViewById(R.id.tvDesignation);
                    break;
                case 2:
                    btnCancel = itemView.findViewById(R.id.btnCancel);
                    btnSubmit = itemView.findViewById(R.id.btnSubmit);
                    break;
            }
        }
    }

    // update image extra
    private void uploadToServer() {
        //Showing the progress dialog
        mProgressDialog = new ProgressDialog(activity, "Uploading please wait...");
        // final ProgressDialog loading = ProgressDialog.show(activity,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sProfileUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //Disimissing the progress dialog
                mProgressDialog.dismiss();
                JSONObject jsonObject = new JSONObject();
                try {
                    if (jsonObject.getString("update_status").equalsIgnoreCase("success")) {
                        Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Showing toast message of the response

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        mProgressDialog.dismiss();

                        //Showing toast
                        Toast.makeText(activity, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                params.put("avatar", image);
                params.put("token", new PreferencesManager(activity).getString("token"));
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}
