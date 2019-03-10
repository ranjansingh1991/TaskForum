package in.enzen.taskforum.model;

/**
 * Created by Rupesh on 13-12-2017.
 */

public class MonthlyTaskModel {

    private String title;
    private String description;

    public MonthlyTaskModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
