package in.enzen.taskforum.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.enzen.taskforum.fragments.DailyFragment;
import in.enzen.taskforum.fragments.MonthlyFragment;

/**
 * Created by Rupesh on 06-12-2017.
 */
@SuppressWarnings("ALL")
public class AssignmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] sTitle = {"Daily Task", "Monthly Task"};

    public AssignmentPagerAdapter(FragmentManager fm, Context contex) {
        super(fm);
        this.context=contex;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new DailyFragment();
                break;
            case 1:
                fragment = new MonthlyFragment();
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
