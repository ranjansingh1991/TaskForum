package in.enzen.taskforum.calendar;

import android.support.annotation.NonNull;

/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
public interface OnDateSelectedListener {

    /**
     * Called when a user clicks on a day.
     * There is no logic to prevent multiple calls for the same date and state.
     *
     * @param widget   the view associated with this listener
     * @param date     the date that was selected or unselected
     * @param selected true if the day is now selected, false otherwise
     */
    void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected);
}
