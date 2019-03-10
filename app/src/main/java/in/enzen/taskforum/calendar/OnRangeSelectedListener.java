package in.enzen.taskforum.calendar;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
public interface OnRangeSelectedListener {

    /**
     * Called when a user selects a range of days.
     * There is no logic to prevent multiple calls for the same date and state.
     *
     * @param widget   the view associated with this listener
     * @param dates     the dates in the range, in ascending order
     */
    void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates);
}
