package in.enzen.taskforum.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import in.enzen.taskforum.R;
import in.enzen.taskforum.model.MonthlyTaskModel;

/**
 * Created by Rupesh on 13-12-2017.
 */
@SuppressWarnings("ALL")
public class MonthlyTaskAdapter extends RecyclerView.Adapter<MonthlyTaskAdapter.ViewHolder> {

    private Context context;
    private List<MonthlyTaskModel> arrayList;
    protected int mLastPosition = -1;

    public MonthlyTaskAdapter(Context context, List<MonthlyTaskModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_task_list_items, parent, false);
        ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvTitle.setText(arrayList.get(position).getTitle());
        holder.tvMonthlyTaskName.setText(arrayList.get(position).getDescription());
        setAnimation(holder.itemView, position);
    }

    protected void setAnimation(View viewToAnimate, int position) {
        if (position > mLastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.INFINITE, 0.5f, Animation.INFINITE, 0.5f);
            anim.setDuration(new Random().nextInt(401));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvMonthlyTaskName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (itemView).findViewById(R.id.tvTitle);
            tvMonthlyTaskName = (itemView).findViewById(R.id.tvTaskDetail);
        }
    }
}
