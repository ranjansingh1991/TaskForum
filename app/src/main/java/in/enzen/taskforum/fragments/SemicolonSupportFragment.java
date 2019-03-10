package in.enzen.taskforum.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.enzen.taskforum.R;

/**
 * Created by Rupesh on 2/9/2018.
 */
@SuppressWarnings("ALL")
public class SemicolonSupportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_support, container, false);
        return rootView;
    }
}
