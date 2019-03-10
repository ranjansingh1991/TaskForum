package in.enzen.taskforum.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.List;

import in.enzen.taskforum.R;
import in.enzen.taskforum.model.PlacesListData;

/**
 * Created by Rupesh on 2/10/2018.
 */
@SuppressWarnings("ALL")
public class SOSRVAdapter extends RecyclerView.Adapter<SOSRVAdapter.ViewHolder> {

    private Activity activity;
    private List<PlacesListData> listData;
    private double dLat;
    private double dLng;

    public SOSRVAdapter(Activity activity, List<PlacesListData> listData, double dLat, double dLng) {
        this.activity = activity;
        this.listData = listData;
        this.dLat = dLat;
        this.dLng = dLng;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvName.setText(listData.get(position).getName());
        holder.tvVicinity.setText(listData.get(position).getVicinity());
        holder.btnDirectionSOS.setText("~" + calculationByDistance((new LatLng(listData.get(position).getLat(),
                listData.get(position).getLng())), new LatLng(dLat, dLng)) + " KM");
        holder.btnCallSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = listData.get(position).getPhone();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNo));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });
        holder.btnDirectionSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + listData.get(position).getLat() + "," +
                        listData.get(position).getLng() + " (" + activity.getResources().getString(R.string.app_name) + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public String calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        double distance = Radius * c;
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        return numberFormat.format(distance);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvVicinity;
        Button btnCallSOS;
        Button btnDirectionSOS;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvVicinity = (TextView) itemView.findViewById(R.id.tvVicinity);
            btnCallSOS = (Button) itemView.findViewById(R.id.btnCallSOS);
            btnDirectionSOS = (Button) itemView.findViewById(R.id.btnDirectionSOS);
        }
    }
}
