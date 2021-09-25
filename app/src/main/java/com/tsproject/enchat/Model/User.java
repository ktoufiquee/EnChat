package com.tsproject.enchat.Model;

public class User {
    String userName;
    String phnNum;
    String contactPermission;
    String contactName;
    String uID;
    String chatID;
    String imageURL;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public User()
    {

    }
    public User(String userName, String phnNum) {
        this.userName = userName;
        this.phnNum = phnNum;
    }

    public User(String userName, String phnNum, String contactPermission, String uID) {
        this.userName = userName;
        this.phnNum = phnNum;
        this.contactPermission = contactPermission;
        this.uID = uID;
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
}
