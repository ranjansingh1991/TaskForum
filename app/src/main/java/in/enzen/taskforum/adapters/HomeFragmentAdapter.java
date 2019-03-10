package in.enzen.taskforum.adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.enzen.taskforum.R;
import in.enzen.taskforum.activities.AssignmentActivity;
import in.enzen.taskforum.activities.HomeActivity;
import in.enzen.taskforum.activities.JobsTodoActivity;
import in.enzen.taskforum.activities.NotificationActivity;
import in.enzen.taskforum.activities.ProfileActivity;
import in.enzen.taskforum.activities.SOSActivity;
import in.enzen.taskforum.activities.SupportActivity;
import in.enzen.taskforum.activities.TargetActivity;
import in.enzen.taskforum.fragments.CalendarFragment;
import in.enzen.taskforum.fragments.ChatUserListFragment;
import in.enzen.taskforum.fragments.ReportFragment;
import in.enzen.taskforum.model.HomeFragmentBean;

/**
 * Created by Rupesh on 04-12-2017.
 */
@SuppressWarnings("ALL")
public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder> {

    private List<HomeFragmentBean> itemList;
    // private Context context;
    HomeActivity activity;


    public HomeFragmentAdapter(HomeActivity context, List<HomeFragmentBean> itemList) {
        this.activity = context;
        this.itemList = itemList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_fragment_items, null);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Typeface appFontRegular = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");

        holder.iv_home_fragment.setImageResource(itemList.get(position).getImage());
        holder.tv_name.setText(itemList.get(position).getName());
        holder.tv_name.setTypeface(appFontRegular);

        holder.card_home_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = null;
                FragmentTransaction fragmentTransaction = null;
                switch (position) {
                    case 0:
                        // Notification
                        Intent notiIntent = new Intent(activity, NotificationActivity.class);
                        activity.startActivity(notiIntent);
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        /*fragment = new NotificationFragment();
                        fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.addToBackStack*//*(null);
                        fragmentTransaction.commit();*/
                        break;
                    case 1:
                        // Calendar
                        fragment = new CalendarFragment();
                        fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 2:
                        // Assignment
                        Intent intent = new Intent(activity, AssignmentActivity.class);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                    case 3:
                        // JobsTodo
                        Intent jobsIntent = new Intent(activity, JobsTodoActivity.class);
                        activity.startActivity(jobsIntent);
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                    case 4:
                        // Target of Collection, Disconnection & Squad
                        Intent targetIntent = new Intent(activity, TargetActivity.class);
                        activity.startActivity(targetIntent);
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                    case 5:
                        // Report
                        fragment = new ReportFragment();
                        fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 6:
                        // Chart
                        fragment = new ChatUserListFragment();
                        fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 7:
                        // Profile
                        Intent profileIntent = new Intent(activity, ProfileActivity.class);
                        activity.startActivity(profileIntent);
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                    case 8:
                        // SOS
                        Intent sosIntent = new Intent(activity, SOSActivity.class);
                        activity.startActivity(sosIntent);
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                    case 9:
                        // Support
                        Intent supportIntent = new Intent(activity, SupportActivity.class);
                        activity.startActivity(supportIntent);
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_home_fragment;
        TextView tv_name;
        CardView card_home_list;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_home_fragment = itemView.findViewById(R.id.iv_home_fragment);
            tv_name = itemView.findViewById(R.id.tv_name);
            card_home_list = itemView.findViewById(R.id.card_home_list);
        }
    }
}
