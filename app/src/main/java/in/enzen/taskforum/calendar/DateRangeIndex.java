package in.enzen.taskforum.calendar;

/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
public interface DateRangeIndex {

    int getCount();

    int indexOf(CalendarDay day);

    CalendarDay getItem(int position);

}
