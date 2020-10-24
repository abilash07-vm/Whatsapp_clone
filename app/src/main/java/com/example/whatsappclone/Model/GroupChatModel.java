package com.example.whatsappclone.Model;

public class GroupChatModel {
    private String type;
    private long timestamp;
    private String imglink;
    private String name;
    private int msgcount;

    public GroupChatModel(String type, long timestamp, String imglink, String name, int msgcount) {
        this.type = type;
        this.timestamp = timestamp;
        this.imglink = imglink;
        this.name = name;
        this.msgcount = msgcount;
    }

    public GroupChatModel(String type) {
        this.type = type;
    }

    public GroupChatModel() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImglink() {
        return imglink;
    }

    public void setImglink(String imglink) {
        this.imglink = imglink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMsgcount() {
        return msgcount;
    }

    public void setMsgcount(int msgcount) {
        this.msgcount = msgcount;
    }

    @Override
    public String toString() {
        return "GroupChatModel{" +
                "type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", imglink='" + imglink + '\'' +
                ", name='" + name + '\'' +
                ", msgcount=" + msgcount +
                '}';
    }
}
