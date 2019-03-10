package in.enzen.taskforum.calendar;

/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
public interface OnMonthChangedListener {

    /**
     * Called upon change of the selected day
     *
     * @param widget the view associated with this listener
     * @param date   the month picked, as the first day of the month
     */
    void onMonthChanged(MaterialCalendarView widget, CalendarDay date);
}
