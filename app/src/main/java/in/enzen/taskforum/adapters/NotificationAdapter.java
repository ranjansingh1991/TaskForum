package in.enzen.taskforum.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.enzen.taskforum.R;
import in.enzen.taskforum.model.NotificationModel;

/**
 * Created by Rupesh on 06-12-2017.
 */
@SuppressWarnings("ALL")
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NotificationModel> arrayList;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item, parent, false);
        ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_notificationTitle.setText(arrayList.get(position).getTitle());
        holder.tv_date.setText(arrayList.get(position).getDate());
        holder.tv_description.setText(arrayList.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_notificationTitle;
        TextView tv_date;
        TextView tv_description;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_notificationTitle = itemView.findViewById(R.id.tv_notificationTitle);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_description = itemView.findViewById(R.id.tv_description);
        }
    }
}
