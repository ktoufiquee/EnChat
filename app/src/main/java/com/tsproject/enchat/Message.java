package com.tsproject.enchat;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Message {
    private long timestamp;
    private int react = -1;
    private String messageID;
    private String senderId;
    private String message;
    private ArrayList<String> mediaUrlList;
    private String mediaUrl;

    public Message(String messageID, String senderId, String message, ArrayList<String> mediaUrlList) {
        this.messageID = messageID;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
    }

    public Message() {

    }

    public Message(String senderId, String message, String messageID, long timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.messageID = messageID;
        this.timestamp = timestamp;
    }

    public Message(long timestamp, int react, String messageID, String senderId, String message, String mediaUrl) {
        this.timestamp = timestamp;
        this.react = react;
        this.messageID = messageID;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrl = mediaUrl;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getReact() {
        return react;
    }

    public void setReact(int react) {
        this.react = react;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }

    public void setMediaUrlList(ArrayList<String> mediaUrlList) {
        this.mediaUrlList = mediaUrlList;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
