package in.enzen.taskforum.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.enzen.taskforum.R;
import in.enzen.taskforum.indicator.LoadingIndicatorView;

/**
 * Created by Rupesh on 31-10-2017.
 */
@SuppressWarnings("ALL")
public class ProgressDialog extends Dialog {

    private Context context;
    private String sMsg;

    public ProgressDialog(@NonNull Context context, String sMsg) {
        super(context);
        this.context = context;
        this.sMsg = sMsg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
        final LinearLayout llPDialogParent = (LinearLayout) findViewById(R.id.llPDialogParent);
        final DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        llPDialogParent.setMinimumHeight(displayMetrics.heightPixels);
        llPDialogParent.setMinimumWidth(displayMetrics.widthPixels);
        final LoadingIndicatorView loadingIndicator = (LoadingIndicatorView) findViewById(R.id.loadingIndicator);
        loadingIndicator.setIndicator("LineScaleIndicator");
        loadingIndicator.show();
        final TextView tvDialogMsg = (TextView) findViewById(R.id.tvDialogMsg);
        Typeface typefaceApp = Typeface.createFromAsset(context.getAssets(), "fonts/madras_regular.ttf");
        tvDialogMsg.setTypeface(typefaceApp);
        tvDialogMsg.setText(sMsg);
    }
}
