package in.enzen.taskforum.model;

/**
 * Created by Rupesh on 06-12-2017.
 */
@SuppressWarnings("ALL")
public class DailyTaskModel {

    private String taskTitle;
    //private String taskDate;
    private String taskDescription;

    public DailyTaskModel(String taskTitle, String taskDescription){
        this.taskTitle = taskTitle;
        //this.taskDate = taskDate;
        this.taskDescription = taskDescription;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

   /* //public String getTaskDate() {
        return taskDate;
    }*/

    public String getTaskDescription() {
        return taskDescription;
    }
}
