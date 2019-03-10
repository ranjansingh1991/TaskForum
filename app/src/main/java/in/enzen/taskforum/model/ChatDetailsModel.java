package in.enzen.taskforum.model;

/**
 * Created by Rupesh on 4/20/2018.
 */
@SuppressWarnings("ALL")
public class ChatDetailsModel {

    private String ID;
    private String fromUserID;
    private String toUserID;
    private String message;
    private String sendDateTime;
    private String readStatus;

    public ChatDetailsModel(String ID, String fromUserID, String toUserID, String message, String sendDateTime, String readStatus) {
        this.ID = ID;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.message = message;
        this.sendDateTime = sendDateTime;
        this.readStatus = readStatus;
    }

    public String getID() {
        return ID;
    }

    public String getFromUserID() {
        return fromUserID;
    }

    public String getToUserID() {
        return toUserID;
    }

    public String getMessage() {
        return message;
    }

    public String getSendDateTime() {
        return sendDateTime;
    }

    public String getReadStatus() {
        return readStatus;
    }
}
