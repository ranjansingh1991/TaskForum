package in.enzen.taskforum.model;

/**
 * Created by RANJAN SINGH on 4/23/2018.
 */
@SuppressWarnings("ALL")
public class ReportDataBean {

    float pieValue = 0f;
    String title = "N/A";
    int target = 0;
    int achived = 0;

    public ReportDataBean(float pieValue, String title, int target, int achived) {
        this.pieValue = pieValue;
        this.title = title;
        this.target = target;
        this.achived = achived;
    }

    public float getPieValue() {
        return pieValue;
    }

    public String getTitle() {
        return title;
    }

    public int getTarget() {
        return target;
    }

    public int getAchived() {
        return achived;
    }
}