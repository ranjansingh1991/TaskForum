package in.enzen.taskforum.calendar;

/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
public interface DayViewDecorator {

    /**
     * Determine if a specific day should be decorated
     *
     * @param day {@ linkplain CalendarDay} to possibly decorate
     * @return true if this decorator should be applied to the provided day
     */
    boolean shouldDecorate(CalendarDay day);

    /**
     * Set decoration options onto a facade to be applied to all relevant days
     *
     * @param view View to decorate
     */
    void decorate(DayViewFacade view);

}
