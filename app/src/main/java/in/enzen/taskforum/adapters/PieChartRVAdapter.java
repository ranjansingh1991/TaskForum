package in.enzen.taskforum.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import in.enzen.taskforum.R;
import in.enzen.taskforum.model.ReportDataBean;
import in.enzen.taskforum.utils.KeyNames;


/**
 * Created by Rupesh on 05-04-2018.
 */

@SuppressWarnings("ALL")
public class PieChartRVAdapter extends RecyclerView.Adapter<PieChartRVAdapter.ViewHolder> implements KeyNames {

    private Activity activity;
    private List<ReportDataBean> listItem;

    public PieChartRVAdapter(Activity activity, List<ReportDataBean> listItem) {
        this.activity = activity;
        this.listItem = listItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pie_chart_item, parent, false);
        return (new PieChartRVAdapter.ViewHolder(view));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvTitle.setText(listItem.get(position).getTitle());
        holder.tvTargetData.setText(String.valueOf(listItem.get(position).getTarget()));
        holder.tvAchievementData.setText(String.valueOf(listItem.get(position).getAchived()));

        ArrayList<Entry> yvalues = new ArrayList<Entry>();

        yvalues.add(new Entry(1 - (listItem.get(position).getPieValue()), 1));
        yvalues.add(new Entry(listItem.get(position).getPieValue(), 0));

        PieDataSet dataSet = new PieDataSet(yvalues, "");
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("Target");
        xVals.add("Achiement");
        // create pie data set
        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);


        PieData data = new PieData(xVals, dataSet);
        // In Percentage
        data.setValueFormatter(new PercentFormatter());
        // dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.WHITE);
        holder.piechart.setData(data);

       // animate(holder);
    }

   /* public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(activity, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }*/


    @Override
    public int getItemCount() {
        return listItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        PieChart piechart;
        TextView tvTitle, tvTargetData, tvAchievementData;

        public ViewHolder(View itemView) {
            super(itemView);
            piechart = (PieChart) itemView.findViewById(R.id.piechart);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvTargetData = (TextView) itemView.findViewById(R.id.tvTargetData);
            tvAchievementData = (TextView) itemView.findViewById(R.id.tvAchievementData);
            piechart.setDrawHoleEnabled(true);
            piechart.setTransparentCircleRadius(30f);
            piechart.setHoleRadius(30f);
            piechart.setRotationEnabled(true);
            // customize legends
            Legend l = piechart.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
            l.setXEntrySpace(7);
            l.setYEntrySpace(5);
        }
    }

}
