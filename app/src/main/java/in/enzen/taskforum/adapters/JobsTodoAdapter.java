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
import android.widget.Toast;

import in.enzen.taskforum.R;
import in.enzen.taskforum.activities.AmcArrearDriveActivity;
import in.enzen.taskforum.activities.AmcBillRevisionActivity;
import in.enzen.taskforum.activities.AmcInstalltionVerfActivity;
import in.enzen.taskforum.activities.ComArrearCollectionAgainstRCActivity;
import in.enzen.taskforum.activities.ComArrearDriveActivity;
import in.enzen.taskforum.activities.ComBillRevisionActivity;
import in.enzen.taskforum.activities.ComCommercialComplainActivity;
import in.enzen.taskforum.activities.ComCurrentBillCollectionActivity;
import in.enzen.taskforum.activities.ComDaysTotalAmtActivity;
import in.enzen.taskforum.activities.ComDehookingDrivesActivity;
import in.enzen.taskforum.activities.ComFormationVillageCommiteeActivity;
import in.enzen.taskforum.activities.ComIdentificationConsumerActivity;
import in.enzen.taskforum.activities.ComInstalltionVerificationActivity;
import in.enzen.taskforum.activities.ComMeterReadingActivity;
import in.enzen.taskforum.activities.ComNoOfDisconnection1PhActivity;
import in.enzen.taskforum.activities.ComNoOfRCActivity;
import in.enzen.taskforum.activities.ComPhaseMeterReadingActivity;
import in.enzen.taskforum.activities.ComStaffMeetingActivity;
import in.enzen.taskforum.activities.DailyInputReadingActivity;
import in.enzen.taskforum.activities.ElectricAccidentActivity;
import in.enzen.taskforum.activities.MrtByPassIdentificationActivity;
import in.enzen.taskforum.activities.MrtDehookingDrivesActivity;
import in.enzen.taskforum.activities.MrtInstallationVerificationActivity;
import in.enzen.taskforum.activities.MrtJointSquadOperationActivity;
import in.enzen.taskforum.activities.MrtMeterFoundDFActivity;
import in.enzen.taskforum.activities.MrtMeterReadingActivity;
import in.enzen.taskforum.activities.MrtNewChargingActivity;
import in.enzen.taskforum.activities.MrtPhaseMeterReading;
import in.enzen.taskforum.activities.MrtStaffMeetingActivity;
import in.enzen.taskforum.activities.OnMCheckMeterReading;
import in.enzen.taskforum.activities.OnMDTFencingActivity;
import in.enzen.taskforum.activities.OnMDTSenitisationActivity;
import in.enzen.taskforum.activities.OnMDehookingDrivesActivity;
import in.enzen.taskforum.activities.OnMDtrEnergyAuditActivity;
import in.enzen.taskforum.activities.OnMFormationVillageCommiteeActivity;
import in.enzen.taskforum.activities.OnMIdentificationConsumerActivity;
import in.enzen.taskforum.activities.OnMLBalancingActivity;
import in.enzen.taskforum.activities.OnMLineSpacerActivity;
import in.enzen.taskforum.activities.OnMNewConnectionVerification;
import in.enzen.taskforum.activities.OnMPMaintenanceActivity;
import in.enzen.taskforum.activities.OnMPhaseMeterReadingActivity;
import in.enzen.taskforum.activities.OnMPoleReplacementActivity;
import in.enzen.taskforum.activities.OnMSafetyWorkActivity;
import in.enzen.taskforum.activities.OnMStaffMeetingActivity;
import in.enzen.taskforum.activities.OnMTemporaryConnectionVerification;
import in.enzen.taskforum.activities.OnMTreeTrimmingActivity;
import in.enzen.taskforum.activities.SdoAmcConsumerMellaActivity;
import in.enzen.taskforum.activities.SdoAmcPhaseMeterReadingActivity;
import in.enzen.taskforum.activities.SdoAmcStaffMeetingActivity;
import in.enzen.taskforum.activities.SdoAmcVillageMeetingActivity;
import in.enzen.taskforum.activities.SdoAssessmentActivity;
import in.enzen.taskforum.activities.SdoDehookingDrivesActivity;
import in.enzen.taskforum.activities.SdoEnergyReportingActivity;
import in.enzen.taskforum.activities.SdoRecoveryAssessmentActivity;
import in.enzen.taskforum.utils.PreferencesManager;

import static in.enzen.taskforum.utils.KeyNames.DESIGNATION;


/**
 * Created by Rupesh on 21-12-2017.
 */
@SuppressWarnings("ALL")
public class JobsTodoAdapter extends RecyclerView.Adapter<JobsTodoAdapter.JobsTodoHolder> {

    private String[] sTitle;
    private Drawable[] drawables;
    private int nGroupPosition;
    private Activity activity;
    private final static int FADE_DURATION = 1000;

    public JobsTodoAdapter(Activity activity, String[] sTitle, Drawable[] drawables, int nGroupPosition) {
        this.activity = activity;
        this.sTitle = sTitle;
        this.drawables = drawables;
        this.nGroupPosition = nGroupPosition;
    }

    @Override
    public JobsTodoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobs_todo_list_items, parent, false);
        return new JobsTodoHolder(view);
    }

    @Override
    public void onBindViewHolder(JobsTodoHolder holder, final int position) {
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
            // (COM)
            case 1:
                if (new PreferencesManager(activity).getString(DESIGNATION).equalsIgnoreCase("JM (COM)")) {
                    switch (position) {
                        case 0:
                            activity.startActivity(new Intent(activity, ComMeterReadingActivity.class));               // Done +sos
                            break;
                        case 1:
                            //activity.startActivity(new Intent(activity, .class));               // Done +sos

                            Toast.makeText(activity, "Functionality coming soon", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            activity.startActivity(new Intent(activity, ComCurrentBillCollectionActivity.class));      // Done+sos
                            break;
                        case 3:
                            activity.startActivity(new Intent(activity, ComArrearDriveActivity.class));                 // Done + sos
                            break;
                        case 4:
                            activity.startActivity(new Intent(activity, ComInstalltionVerificationActivity.class));    // Done + sos
                            break;
                        case 5:
                            activity.startActivity(new Intent(activity, ComDehookingDrivesActivity.class));             // Done + sos
                            break;
                        case 6:
                            activity.startActivity(new Intent(activity, ComPhaseMeterReadingActivity.class));           // Done + sos
                            break;
                        case 7:
                            activity.startActivity(new Intent(activity, ComStaffMeetingActivity.class));               // Done + sos
                            break;
                        case 8:
                            activity.startActivity(new Intent(activity, ComNoOfDisconnection1PhActivity.class));        // Done + sos
                            break;
                        case 9:
                            Toast.makeText(activity, "Functionality coming soon", Toast.LENGTH_SHORT).show();      // Screen + API NA
                            break;
                        case 10:
                            activity.startActivity(new Intent(activity, ComNoOfRCActivity.class));                      // Done+ sos
                            break;
                        case 11:
                            activity.startActivity(new Intent(activity, ComArrearCollectionAgainstRCActivity.class));   // Done +sos
                            break;
                        case 12:
                            activity.startActivity(new Intent(activity, ComIdentificationConsumerActivity.class));      // Done +sos
                            break;
                        case 13:
                            activity.startActivity(new Intent(activity, ComFormationVillageCommiteeActivity.class));    // Done +sos
                            break;
                        case 14:
                            activity.startActivity(new Intent(activity, ComDaysTotalAmtActivity.class));                // Done +sos
                            break;
                        case 15:
                            Toast.makeText(activity, "Functionality coming soon", Toast.LENGTH_SHORT).show();
                            break;
                        case 16:
                            activity.startActivity(new Intent(activity, ComCommercialComplainActivity.class));  // Done +sos
                            break;
                        case 17:
                            activity.startActivity(new Intent(activity, ComBillRevisionActivity.class));                // Done+sos
                            break;
                    }
                    break;
                } else {
                    Toast.makeText(activity, "You are not assign for this job", Toast.LENGTH_LONG).show();
                }

                // (O & M)
            case 2:
                if (new PreferencesManager(activity).getString(DESIGNATION).equalsIgnoreCase("JM (O&M)")) {
                    switch (position) {
                        case 0:
                            activity.startActivity(new Intent(activity, OnMDTSenitisationActivity.class));                 // Done +sos
                            break;
                        case 1:
                            activity.startActivity(new Intent(activity, OnMLBalancingActivity.class));                    // Done +sos
                            break;
                        case 2:
                            activity.startActivity(new Intent(activity, OnMTreeTrimmingActivity.class));                  // Done + sos
                            break;
                        case 3:
                            activity.startActivity(new Intent(activity, OnMNewConnectionVerification.class));              // Done +sos
                            break;
                        case 4:
                            activity.startActivity(new Intent(activity, OnMTemporaryConnectionVerification.class));        // Done +sos
                            break;
                        case 5:
                            activity.startActivity(new Intent(activity, OnMPMaintenanceActivity.class));                   // Done +sos
                            break;
                        case 6:
                            activity.startActivity(new Intent(activity, OnMPhaseMeterReadingActivity.class));              // Done +sos
                            break;
                        case 7:
                            activity.startActivity(new Intent(activity, OnMDehookingDrivesActivity.class));                // Done +sos
                            break;
                        case 8:
                            activity.startActivity(new Intent(activity, OnMStaffMeetingActivity.class));                  // Done +sos
                            break;
                        case 9:
                            activity.startActivity(new Intent(activity, OnMCheckMeterReading.class));                      // Done +sos
                            break;
                        case 10:
                            activity.startActivity(new Intent(activity, OnMPoleReplacementActivity.class));                // Done +sos
                            break;
                        case 11:
                            activity.startActivity(new Intent(activity, OnMIdentificationConsumerActivity.class));         // Done +sos
                            break;
                        case 12:
                            activity.startActivity(new Intent(activity, OnMFormationVillageCommiteeActivity.class));       // Done +sos
                            break;
                        case 13:
                            activity.startActivity(new Intent(activity, OnMLineSpacerActivity.class));                     // Done +sos
                            break;
                        case 14:
                            activity.startActivity(new Intent(activity, OnMSafetyWorkActivity.class));                     // Done +sos
                            break;
                        case 15:
                            activity.startActivity(new Intent(activity, OnMDtrEnergyAuditActivity.class));                  // Done +sos
                            break;
                        case 16:
                            activity.startActivity(new Intent(activity, OnMDTFencingActivity.class));                       // Done +sos
                            break;
                        case 17:
                            activity.startActivity(new Intent(activity, DailyInputReadingActivity.class));                  // Done + sos
                            break;
                        case 18:
                            activity.startActivity(new Intent(activity, ElectricAccidentActivity.class));                   // Done + SOS
                            break;
                    }
                    break;
                }else {
                    Toast.makeText(activity, "You are not assign for this job", Toast.LENGTH_LONG).show();
                }


            // (MRT)
            case 3:
                if (new PreferencesManager(activity).getString(DESIGNATION).equalsIgnoreCase("JM (MRT)")) {
                    switch (position) {
                        case 0:
                            activity.startActivity(new Intent(activity, MrtMeterReadingActivity.class));               // Done +sos
                            break;
                        case 1:
                            activity.startActivity(new Intent(activity, MrtPhaseMeterReading.class));                  // Done +sos
                            break;
                        case 2:
                            activity.startActivity(new Intent(activity, MrtDehookingDrivesActivity.class));             // Done +sos
                            break;
                        case 3:
                            activity.startActivity(new Intent(activity, MrtInstallationVerificationActivity.class));    // Done + SOS
                            break;
                        case 4:
                            activity.startActivity(new Intent(activity, MrtMeterFoundDFActivity.class));                // Done +sos
                            break;
                        case 5:
                            activity.startActivity(new Intent(activity, MrtByPassIdentificationActivity.class));        // Done +sos
                            break;
                        case 6:
                            activity.startActivity(new Intent(activity, MrtNewChargingActivity.class));              // Done + SOS
                            break;
                        case 7:
                            activity.startActivity(new Intent(activity, MrtStaffMeetingActivity.class));               // Done + sos
                            break;
                        case 8:
                            activity.startActivity(new Intent(activity, MrtJointSquadOperationActivity.class));         // Done +sos
                            break;
                    }
                    break;
                }else {
                    Toast.makeText(activity, "You are not assign for this job", Toast.LENGTH_LONG).show();
                }

            // SDO & AMC
            case 4:
                if (new PreferencesManager(activity).getString(DESIGNATION).equalsIgnoreCase("AM (COM)")){
                switch (position) {
                    case 0:
                        activity.startActivity(new Intent(activity, SdoAmcPhaseMeterReadingActivity.class));           // Done +sos
                        break;
                    case 1:
                        activity.startActivity(new Intent(activity, SdoDehookingDrivesActivity.class));                 // Done +sos
                        break;
                    case 2:
                        activity.startActivity(new Intent(activity, AmcArrearDriveActivity.class));                     // Done +sos
                        break;
                    case 3:
                        activity.startActivity(new Intent(activity, AmcBillRevisionActivity.class));                   // Done +sos
                        break;
                    case 4:
                        activity.startActivity(new Intent(activity, SdoAmcConsumerMellaActivity.class));               // Done +sos
                        break;
                    case 5:
                        activity.startActivity(new Intent(activity, SdoAmcVillageMeetingActivity.class));              // Done +sos
                        break;
                    case 6:
                        activity.startActivity(new Intent(activity, SdoAmcStaffMeetingActivity.class));                // DOne + SOS
                        break;
                    case 7:
                        activity.startActivity(new Intent(activity, SdoEnergyReportingActivity.class));                // Done +sos
                        break;
                    case 8:
                        activity.startActivity(new Intent(activity, AmcInstalltionVerfActivity.class));                // Done +sos
                        break;
                    case 9:
                        activity.startActivity(new Intent(activity, SdoAssessmentActivity.class));                     // Done +sos
                        break;
                    case 10:
                        activity.startActivity(new Intent(activity, SdoRecoveryAssessmentActivity.class));             // Done +sos
                        break;
                }
                break;
            }else{
                Toast.makeText(activity, "You are not assign for this job", Toast.LENGTH_LONG).show();
            }

            // AMC
            case 5:
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
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

    public class JobsTodoHolder extends RecyclerView.ViewHolder {

        LinearLayout llRVItemParent;
        ImageView imgIcon;
        TextView tvItemChild;

        public JobsTodoHolder(View itemView) {
            super(itemView);
            llRVItemParent = (LinearLayout) itemView.findViewById(R.id.llRVItemParent);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
            tvItemChild = (TextView) itemView.findViewById(R.id.tvItemChild);
        }
    }
}
