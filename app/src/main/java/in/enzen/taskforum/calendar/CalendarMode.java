package in.enzen.taskforum.calendar;

/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
@Experimental
public enum CalendarMode {

    MONTHS(6),
    WEEKS(1);

    public final int visibleWeeksCount;

    CalendarMode(int visibleWeeksCount) {
        this.visibleWeeksCount = visibleWeeksCount;
    }
}
