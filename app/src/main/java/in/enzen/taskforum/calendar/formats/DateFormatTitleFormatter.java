package in.enzen.taskforum.calendar.formats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import in.enzen.taskforum.calendar.CalendarDay;


/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
public class DateFormatTitleFormatter implements TitleFormatter {

    private final DateFormat dateFormat;

    /**
     * Format using "LLLL yyyy" for formatting
     */
    public DateFormatTitleFormatter() {
        this.dateFormat = new SimpleDateFormat(
                "LLLL yyyy", Locale.getDefault()
        );
    }

    /**
     * Format using a specified {@linkplain DateFormat}
     *
     * @param format the format to use
     */
    public DateFormatTitleFormatter(DateFormat format) {
        this.dateFormat = format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence format(CalendarDay day) {
        return dateFormat.format(day.getDate());
    }
}
