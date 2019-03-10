package in.enzen.taskforum.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.enzen.taskforum.fragments.SelfAttendanceFragment;
import in.enzen.taskforum.fragments.TeamAttendanceFragment;

/**
 * Created by Rupesh on 08-12-2017.
 */
@SuppressWarnings("ALL")
public class AttendancePagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] sTitle = {"Self Attendance", "Team Attendance"};

    public AttendancePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new SelfAttendanceFragment();
                break;
            case 1:
                fragment = new TeamAttendanceFragment();
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
