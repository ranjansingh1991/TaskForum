package in.enzen.taskforum.calendar.formats;


import in.enzen.taskforum.calendar.CalendarDay;

/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
public interface TitleFormatter {

    /**
     * Converts the supplied day to a suitable month/year title
     *
     * @param day the day containing relevant month and year information
     * @return a label to display for the given month/year
     */
    CharSequence format(CalendarDay day);
}
