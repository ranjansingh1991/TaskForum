package in.enzen.taskforum.calendar;

/**
 * Created by Rupesh on 08-08-2017.
 */
@SuppressWarnings("ALL")
public class DecoratorResult {

    public final DayViewDecorator decorator;
    public final DayViewFacade result;

    DecoratorResult(DayViewDecorator decorator, DayViewFacade result) {
        this.decorator = decorator;
        this.result = result;
    }
}
