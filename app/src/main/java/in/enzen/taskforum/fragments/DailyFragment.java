package in.enzen.taskforum.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.enzen.taskforum.R;
import in.enzen.taskforum.adapters.DailyTaskAdapter;
import in.enzen.taskforum.model.DailyTaskModel;
import in.enzen.taskforum.utils.KeyNames;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.rest.BaseUrl.sAssignmentTarget;

/**
 * Created by Rupesh on 06-12-2017.
 */
@SuppressWarnings("ALL")
public class DailyFragment extends Fragment implements KeyNames {

    RecyclerView rvDailyFragment;
    private String[] sTaskName;
    private String[] sDate;
    private String[] sDescription;
    DailyTaskAdapter mDailyTaskAdapter;
    ArrayList<DailyTaskModel> arraylist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View childRoot) {
        rvDailyFragment = childRoot.findViewById(R.id.rvDailyFragment);

        rvDailyFragment.setLayoutManager(new LinearLayoutManager(getActivity()));

        dailyData();


     /*   sTaskName = new String[]{
                "Task 1",
                "Task 2",
                "Task 3",
                "Task 4"
        };
        sDate = new String[]{
                "07/12/2017",
                "07/12/2017",
                "07/12/2017",
                "07/12/2017"
        };
        sDescription = new String[]{
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
        };
        for (int i = 0; i < sTaskName.length; i++) {
            DailyTaskModel mDailyTaskModel = new DailyTaskModel(sTaskName[i], sDate[i], sDescription[i]);
            arraylist.add(mDailyTaskModel);
        }
        rvDailyFragment = childRoot.findViewById(R.id.rvDailyFragment);
        mDailyTaskAdapter = new DailyTaskAdapter(getActivity(), arraylist);
        rvDailyFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDailyFragment.setAdapter(mDailyTaskAdapter);*/

    }
    private void dailyData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sAssignmentTarget, new Response.Listener<String>() {
            @Override
            public void onResponse(String sResult) {

                try {
                    JSONObject jsonObject = new JSONObject(sResult);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject records = jsonObject.getJSONObject("records");
                        JSONArray jsonArray = records.getJSONArray("todays_target");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            DailyTaskModel dailyTaskModel = new DailyTaskModel(
                                    jsonArray.getJSONObject(i).getString("title"),
                                    jsonArray.getJSONObject(i).getString("description"));
                            arraylist.add(dailyTaskModel);
                        }
                        mDailyTaskAdapter = new DailyTaskAdapter(getActivity(), arraylist);
                        rvDailyFragment.setAdapter(mDailyTaskAdapter);

                    } else {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Dismiss the progress dialog
                Log.e("status Response", String.valueOf(volleyError));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new HashMap<>();
                params.put("token", new PreferencesManager(getActivity()).getString("token"));
                params.put("type", "daily");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

}
