package in.enzen.taskforum.model;

/**
 * Created by Rupesh on 06-12-2017.
 */
@SuppressWarnings("ALL")
public class NotificationModel {

    private String title;
    private String date;
    private String description;

    public NotificationModel(String title, String date, String description) {
        this.title = title;
        this.date = date;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
