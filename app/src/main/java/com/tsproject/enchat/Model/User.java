package com.tsproject.enchat.Model;

public class User {
    String userName;
    String phnNum;
    String contactPermission;
    String contactName;
    String uID;
    String chatID;
    String imageURL;
    String about;
    String activeStatus;

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public User(String userName, String phnNum, String contactPermission, String uID, String about, String activeStatus) {
        this.userName = userName;
        this.phnNum = phnNum;
        this.contactPermission = contactPermission;
        this.uID = uID;
        this.about = about;
        this.activeStatus = activeStatus;
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
