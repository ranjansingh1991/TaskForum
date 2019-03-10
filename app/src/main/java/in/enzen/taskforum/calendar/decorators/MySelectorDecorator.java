package in.enzen.taskforum.calendar.decorators;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import in.enzen.taskforum.R;
import in.enzen.taskforum.calendar.CalendarDay;
import in.enzen.taskforum.calendar.DayViewDecorator;
import in.enzen.taskforum.calendar.DayViewFacade;


/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
public class MySelectorDecorator implements DayViewDecorator {

    private final Drawable drawable;

    public MySelectorDecorator(Activity context) {
        // drawable = context.getResources().getDrawable(R.drawable.my_selector);
        drawable = context.getResources().getDrawable(R.drawable.sun_selector);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
