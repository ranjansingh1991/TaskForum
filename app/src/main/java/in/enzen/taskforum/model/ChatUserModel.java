package in.enzen.taskforum.model;

/**
 * Created by Rupesh on 4/20/2018.
 */
@SuppressWarnings("ALL")
public class ChatUserModel {

    private String userID;
    private String fullName;
    private String mobile;
    private int msgCount;
    private String type;
    private String avatar;

    public ChatUserModel(String userID, String fullName, String mobile, int msgCount, String type, String avatar) {
        this.userID = userID;
        this.fullName = fullName;
        this.mobile = mobile;
        this.msgCount = msgCount;
        this.type = type;
        this.avatar = avatar;
    }

    public String getUserID() {
        return userID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public String getType() {
        return type;
    }

    public String getAvatar() {
        return avatar;
    }
}