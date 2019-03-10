package in.enzen.taskforum.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.enzen.taskforum.R;

/**
 * Created by Rupesh on 2/9/2018.
 */
@SuppressWarnings("ALL")
public class ContactUsFragment extends Fragment {

    private TextView tvTitle;
    private TextView tvCompanyNo;
    private TextView tvCompanyNoText;
    private TextView tvCompanyNoWA;
    private TextView tvCompanyEmail;
    private TextView tvCompanyAddress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        Typeface appFontMontserrat = Typeface.createFromAsset(getActivity().getAssets(), "fonts/madras_regular.ttf");
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvCompanyNo = (TextView) rootView.findViewById(R.id.tvCompanyNo);
        tvCompanyNoText = (TextView) rootView.findViewById(R.id.tvCompanyNoText);
        tvCompanyNoWA = (TextView) rootView.findViewById(R.id.tvCompanyNoWA);
        tvCompanyEmail = (TextView) rootView.findViewById(R.id.tvCompanyEmail);
        tvCompanyAddress = (TextView) rootView.findViewById(R.id.tvCompanyAddress);
        tvTitle.setTypeface(appFontMontserrat);
        tvCompanyNo.setTypeface(appFontMontserrat);
        tvCompanyNoText.setTypeface(appFontMontserrat);
        tvCompanyNoWA.setTypeface(appFontMontserrat);
        tvCompanyEmail.setTypeface(appFontMontserrat);
        tvCompanyAddress.setTypeface(appFontMontserrat);
    }

}
