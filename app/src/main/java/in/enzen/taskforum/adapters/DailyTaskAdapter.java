package in.enzen.taskforum.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import in.enzen.taskforum.R;
import in.enzen.taskforum.model.DailyTaskModel;

/**
 * Created by Rupesh on 06-12-2017.
 */
@SuppressWarnings("ALL")
public class DailyTaskAdapter extends RecyclerView.Adapter<DailyTaskAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DailyTaskModel> arrayList;
    protected int mLastPosition = -1;

    int mYear, mMonth,mDay;


    public DailyTaskAdapter(Context context, ArrayList<DailyTaskModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_task_list_item, parent, false);
        ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_assignmentTitle.setText(arrayList.get(position).getTaskTitle());
        holder.tv_assignmentDescription.setText(arrayList.get(position).getTaskDescription());

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        holder.tv_assignmentDate.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(mMonth + 1).append("-")
                .append(mDay).append("-")
                .append(mYear).append(" "));

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

        TextView tv_assignmentTitle;
        TextView tv_assignmentDate;
        TextView tv_assignmentDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_assignmentTitle = itemView.findViewById(R.id.tv_assignmentTitle);
            tv_assignmentDate = itemView.findViewById(R.id.tv_assignmentDate);
            tv_assignmentDescription = itemView.findViewById(R.id.tv_assignmentDescription);
        }
    }
}
