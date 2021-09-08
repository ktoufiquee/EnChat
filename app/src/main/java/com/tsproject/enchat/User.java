package com.tsproject.enchat;

public class User {
    String userName;
    String phnNum;
    String userID;

    public User()
    {

    }
    public User(String userName, String phnNum, String userID) {
        this.userName = userName;
        this.phnNum = phnNum;
        this.userID = userID;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
