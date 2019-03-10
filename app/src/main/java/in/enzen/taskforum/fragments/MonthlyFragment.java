package in.enzen.taskforum.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import in.enzen.taskforum.adapters.MonthlyTaskAdapter;
import in.enzen.taskforum.model.MonthlyTaskModel;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.rest.BaseUrl.sAssignmentTarget;

/**
 * Created by Rupesh on 06-12-2017.
 */
@SuppressWarnings("ALL")
public class MonthlyFragment extends Fragment {

    private RecyclerView rvMonthlyFragment;
    private MonthlyTaskAdapter mMonthlyTaskAdapter;

    ArrayList<MonthlyTaskModel> arraylist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_monthly, container, false);

        init(rootView);
        return rootView;
    }

    private void init(View childView) {
        // List<MonthlyTaskModel> monthlyListItem = getAllMonthlyList();
        // layoutManager = new LinearLayoutManager(getActivity());
        //  mMonthlyTaskAdapter = new MonthlyTaskAdapter(getActivity(), monthlyListItem);

        rvMonthlyFragment = (RecyclerView) childView.findViewById(R.id.rvMonthlyFragment);
        rvMonthlyFragment.setHasFixedSize(true);
        rvMonthlyFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMonthlyFragment.setItemAnimator(new DefaultItemAnimator());

        monthlyData();
    }

    /*private List<MonthlyTaskModel> getAllMonthlyList() {
        List<MonthlyTaskModel> items = new ArrayList<>();

        items.add(new MonthlyTaskModel("01-12-2017", "Friday", "4 HT Task"));
        items.add(new MonthlyTaskModel("02-12-2017", "Saturday", "3 LT Task"));
        items.add(new MonthlyTaskModel("03-12-2017", "Sunday", "-NA-"));
        items.add(new MonthlyTaskModel("04-12-2017", "Monday", "2 SLD Task"));
        items.add(new MonthlyTaskModel("05-12-2017", "Tuesday", "7 Street Light Work"));
        items.add(new MonthlyTaskModel("06-12-2017", "Wednesday", "2 Ht & 3 LT Work"));
        items.add(new MonthlyTaskModel("07-12-2017", "Thursday", "Field Work"));
        items.add(new MonthlyTaskModel("08-12-2017", "Friday", "Collection"));
        items.add(new MonthlyTaskModel("09-12-2017", "Saturday", "Holiday"));
        items.add(new MonthlyTaskModel("10-12-2017", "Sunday", "-NA-"));
        items.add(new MonthlyTaskModel("11-12-2017", "Monday", "2 SLD Task"));
        items.add(new MonthlyTaskModel("12-12-2017", "Tuesday", "7 Street Light Work"));
        items.add(new MonthlyTaskModel("13-12-2017", "Wednesday", "2 Ht & 3 LT Work"));
        items.add(new MonthlyTaskModel("14-12-2017", "Thursday", "Field Work"));
        items.add(new MonthlyTaskModel("15-12-2017", "Friday", "Collection"));
        items.add(new MonthlyTaskModel("16-12-2017", "Saturday", "7 LT work"));
        items.add(new MonthlyTaskModel("17-12-2017", "Sunday", "-NA-"));
        items.add(new MonthlyTaskModel("18-12-2017", "Monday", "2 SLD Task"));
        items.add(new MonthlyTaskModel("19-12-2017", "Tuesday", "7 Street Light Work"));
        items.add(new MonthlyTaskModel("20-12-2017", "Wednesday", "2 Ht & 3 LT Work"));
        items.add(new MonthlyTaskModel("21-12-2017", "Thursday", "Field Work"));
        items.add(new MonthlyTaskModel("22-12-2017", "Friday", "Collection"));
        items.add(new MonthlyTaskModel("23-12-2017", "Saturday", "Collection"));
        items.add(new MonthlyTaskModel("24-12-2017", "Sunday", "-NA-"));
        items.add(new MonthlyTaskModel("25-12-2017", "Monday", "2 SLD Task"));
        items.add(new MonthlyTaskModel("26-12-2017", "Tuesday", "7 Street Light Work"));
        items.add(new MonthlyTaskModel("27-12-2017", "Wednesday", "2 Ht & 3 LT Work"));
        items.add(new MonthlyTaskModel("28-12-2017", "Thursday", "Field Work"));
        items.add(new MonthlyTaskModel("29-12-2017", "Friday", "Collection"));
        items.add(new MonthlyTaskModel("30-12-2017", "Saturday", "5 LT Task"));
        items.add(new MonthlyTaskModel("31-12-2017", "Sunday", "-NA-"));

        return items;
    }*/

    private void monthlyData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sAssignmentTarget, new Response.Listener<String>() {
            @Override
            public void onResponse(String sResult) {

                try {
                    JSONObject jsonObject = new JSONObject(sResult);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject records = jsonObject.getJSONObject("records");
                        JSONArray jsonArray = records.getJSONArray("month_target");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            MonthlyTaskModel monthlyTaskModel = new MonthlyTaskModel(
                                    jsonArray.getJSONObject(i).getString("title"),
                                    jsonArray.getJSONObject(i).getString("description"));
                            arraylist.add(monthlyTaskModel);
                        }
                        mMonthlyTaskAdapter = new MonthlyTaskAdapter(getActivity(), arraylist);
                        rvMonthlyFragment.setAdapter(mMonthlyTaskAdapter);

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
                params.put("type", "monthly");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

}
