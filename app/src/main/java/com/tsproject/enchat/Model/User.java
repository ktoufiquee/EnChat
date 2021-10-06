package com.tsproject.enchat.Model;

public class User implements Comparable<User> {
    String userName;
    String phnNum;
    String contactPermission;
    String contactName;
    String uID;
    String chatID;
    String imageURL;
    int type;
    String about;
    String activeStatus;
    String typing;
    Long lastTime;

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public User() {

    }

    public User(String uID) {
        this.uID = uID;
    }

    public User(String userName, String phnNum) {
        this.userName = userName;
        this.phnNum = phnNum;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }

    public User(String userName, String phnNum, String contactPermission, String uID, String about, String activeStatus, String typing) {
        this.userName = userName;
        this.phnNum = phnNum;
        this.contactPermission = contactPermission;
        this.uID = uID;
        this.about = about;
        this.activeStatus = activeStatus;
        this.typing = typing;
    }

    public User(String userName, String phnNum, String contactPermission, String contactName, String uID, String chatID, String imageURL, int type, String about, String activeStatus, String typing, Long lastTime) {
        this.userName = userName;
        this.phnNum = phnNum;
        this.contactPermission = contactPermission;
        this.contactName = contactName;
        this.uID = uID;
        this.chatID = chatID;
        this.imageURL = imageURL;
        this.type = type;
        this.about = about;
        this.activeStatus = activeStatus;
        this.typing = typing;
        this.lastTime = lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public Long getLastTime() {
        return lastTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhnNum() {
        return phnNum;
    }

    public void setPhnNum(String phnNum) {
        this.phnNum = phnNum;
    }

    public String getContactPermission() {
        return contactPermission;
    }

    public void setContactPermission(String contactPermission) {
        this.contactPermission = contactPermission;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }


    @Override
    public int compareTo(User user) {
        return user.getLastTime().compareTo(this.getLastTime());
    }
}
