package in.enzen.taskforum.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import in.enzen.taskforum.R;

/**
 * Created by Rupesh on 2/7/2018.
 */

public class NoInternetDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private LinearLayout llParentNoInternetDialog;
    private RelativeLayout rlDialogContainer;
    private ImageView imgLogo;
    private Button btnOK;

    public NoInternetDialog(Activity activity, int themeResId) {
        super(activity, themeResId);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_no_internet);

        init();
    }

    private void init() {
        Typeface typefaceApp = Typeface.createFromAsset(activity.getAssets(), "fonts/madras_regular.ttf");
        llParentNoInternetDialog = (LinearLayout) findViewById(R.id.llParentNoInternetDialog);
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        llParentNoInternetDialog.setMinimumWidth(displayMetrics.widthPixels);
        rlDialogContainer = (RelativeLayout) findViewById(R.id.rlDialogContainer);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imgLogo.bringToFront();
        rlDialogContainer.invalidate();
        btnOK = (Button) findViewById(R.id.btnOK);

        btnOK.setTypeface(typefaceApp);

        btnOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOK:
                dismiss();
                break;
        }
    }
}
