package com.example.whatsappclone.Model;

public class GroupChatModel {
    private String type;
    private long timestamp;
    private String imglink;
    private String name, lastMessage, grpid;
    private int msgcount;

    public GroupChatModel() {
    }

    public GroupChatModel(String type, long timestamp, String imglink, String name, String lastMessage, String grpid, int msgcount) {
        this.type = type;
        this.timestamp = timestamp;
        this.imglink = imglink;
        this.name = name;
        this.lastMessage = lastMessage;
        this.grpid = grpid;
        this.msgcount = msgcount;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getGrpid() {
        return grpid;
    }

    public void setGrpid(String grpid) {
        this.grpid = grpid;
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
                ", lastMessage='" + lastMessage + '\'' +
                ", grpid='" + grpid + '\'' +
                ", msgcount=" + msgcount +
                '}';
    }
}
