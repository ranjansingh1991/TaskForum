package in.enzen.taskforum.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.enzen.taskforum.R;
import in.enzen.taskforum.activities.FirstPhaseActivity;
import in.enzen.taskforum.activities.FirstPhaseDisconnectionActivity;
import in.enzen.taskforum.activities.SquadFirstPhaseActivity;
import in.enzen.taskforum.activities.SquadThirdPhaseActivity;
import in.enzen.taskforum.activities.ThirdPhaseActivity;
import in.enzen.taskforum.activities.ThirdPhaseDisconnectionActivity;

/**
 * Created by Rupesh on 2/26/2018.
 */
@SuppressWarnings("ALL")
public class TargetAdapter extends RecyclerView.Adapter<TargetAdapter.TargetHolder> {
    private String[] sTitle;
    private Drawable[] drawables;
    private int nGroupPosition;
    private Activity activity;
    private final static int FADE_DURATION = 1000;

    public TargetAdapter(Activity activity, String[] sTitle, Drawable[] drawables, int nGroupPosition) {
        this.activity = activity;
        this.sTitle = sTitle;
        this.drawables = drawables;
        this.nGroupPosition = nGroupPosition;
    }

    @Override
    public TargetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.target_list_items, parent, false);
        return new TargetHolder(view);
    }

    @Override
    public void onBindViewHolder(TargetHolder holder, final int position) {
        holder.tvItemChild.setText(sTitle[position]);
        holder.imgIcon.setImageDrawable(drawables[position]);
        holder.llRVItemParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performNext(position);
            }
        });
        setFadeAnimation(holder.itemView);
        final Typeface appFontRegular = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular2.otf");
        holder.tvItemChild.setTypeface(appFontRegular);
    }

    private void performNext(int position) {
        switch (nGroupPosition) {
            // Collection Target
            case 1:
                switch (position) {
                    case 0:
                        activity.startActivity(new Intent(activity, FirstPhaseActivity.class));
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                    case 1:
                        activity.startActivity(new Intent(activity, ThirdPhaseActivity.class));
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                }
                break;

            // Disconnection Target
            case 2:
                switch (position) {
                    case 0:
                        activity.startActivity(new Intent(activity, FirstPhaseDisconnectionActivity.class));
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                    case 1:
                        activity.startActivity(new Intent(activity, ThirdPhaseDisconnectionActivity.class));
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                }
                break;

            // Disconnection Squad
            case 3:
                switch (position) {
                    case 0:
                        activity.startActivity(new Intent(activity, SquadFirstPhaseActivity.class));
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                    case 1:
                        activity.startActivity(new Intent(activity, SquadThirdPhaseActivity.class));
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        break;
                }
                break;
        }
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return sTitle.length;
    }

    public class TargetHolder extends RecyclerView.ViewHolder {
        LinearLayout llRVItemParent;
        ImageView imgIcon;
        TextView tvItemChild;

        public TargetHolder(View itemView) {
            super(itemView);
            llRVItemParent = (LinearLayout) itemView.findViewById(R.id.llRVItemParent);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
            tvItemChild = (TextView) itemView.findViewById(R.id.tvItemChild);
        }
    }
}
