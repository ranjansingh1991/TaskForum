package in.enzen.taskforum.calendar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
public class BetterViewPager extends ViewPager {

    public BetterViewPager(Context context) {
        super(context);
    }

    public BetterViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //@Override
    public void setChildrenDrawingOrderEnabledCompat(boolean enable) {
        setChildrenDrawingOrderEnabled(enable);
    }
}

