package in.enzen.taskforum.model;

/**
 * Created by Rupesh on 07-12-2017.
 */
@SuppressWarnings("ALL")
public class Chats {

    public boolean left;
    public String message;
    public String dateTime;

    public Chats(boolean left, String msg, String dateTime) {
        super();
        this.left = left;
        this.message = msg;
        this.dateTime = dateTime;
    }

}