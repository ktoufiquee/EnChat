package com.tsproject.enchat;

public class User {
    String userName;
    String phnNum;
    String contactPermission;
    String contactName;
    String uID;

    public User()
    {

    }
    public User(String contactName, String phnNum) {
        this.contactName = contactName;
        this.phnNum = phnNum;
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
