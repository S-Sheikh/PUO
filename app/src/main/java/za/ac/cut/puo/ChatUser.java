package za.ac.cut.puo;

public class ChatUser {
    private static ChatUser currentUser = new ChatUser();
    private String objectId;
    private String nickname;
    private String deviceId;

    public static ChatUser currentUser() {
        return currentUser;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

                                            