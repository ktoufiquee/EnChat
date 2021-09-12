package com.tsproject.enchat;

public class User {
    String userName;
    String phnNum;
    String contactPermission;

    public User()
    {

    }
    public User(String userName, String phnNum) {
        this.userName = userName;
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
}
