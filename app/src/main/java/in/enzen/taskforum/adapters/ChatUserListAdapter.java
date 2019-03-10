package in.enzen.taskforum.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.enzen.taskforum.R;
import in.enzen.taskforum.civ.CircleImageView;
import in.enzen.taskforum.model.ChatUserModel;

/**
 * Created by Rupesh on 4/20/2018.
 */
@SuppressWarnings("ALL")
public class ChatUserListAdapter extends RecyclerView.Adapter<ChatUserListAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<ChatUserModel> arrayList;
    private List<ChatUserModel> assignmentNamesList = null;
    LayoutInflater inflater;

    public ChatUserListAdapter(Activity activity, List<ChatUserModel> assignmentNamesList) {
        this.activity = activity;

        this.assignmentNamesList = assignmentNamesList;
        inflater = LayoutInflater.from(activity);
        this.arrayList = new ArrayList<ChatUserModel>();
        this.arrayList.addAll(assignmentNamesList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_list_items, parent, false);
        ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Typeface appBold = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");
        holder.tvUserChatName.setTypeface(appBold);

        holder.tvUserChatName.setText(arrayList.get(position).getFullName());
        Picasso.with(activity).load(arrayList.get(position).getAvatar()).into(holder.civChatUserImage);
        // animate(holder);
        // Picasso.with(context).load(new PreferencesManager(context).getString(arrayList.get(position).getAvatar())).into(holder.civChatUserImage);
    }

   /* public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(activity, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }*/

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civChatUserImage;
        TextView tvUserChatName;

        public ViewHolder(View itemView) {
            super(itemView);
            civChatUserImage = itemView.findViewById(R.id.civChatUserImage);
            tvUserChatName = itemView.findViewById(R.id.tvUserChatName);
        }
    }
}
