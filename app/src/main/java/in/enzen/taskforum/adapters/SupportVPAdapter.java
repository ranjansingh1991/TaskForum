package in.enzen.taskforum.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import in.enzen.taskforum.fragments.ContactUsFragment;
import in.enzen.taskforum.fragments.SemicolonSupportFragment;

/**
 * Created by Rupesh on 2/9/2018.
 */
@SuppressWarnings("ALL")
public class SupportVPAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private String[] sTitle = {"CONTACT US", "APP SUPPORT"};

    public SupportVPAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ContactUsFragment();
                break;
            case 1:
                fragment = new SemicolonSupportFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return sTitle.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sTitle[position];
    }

}
