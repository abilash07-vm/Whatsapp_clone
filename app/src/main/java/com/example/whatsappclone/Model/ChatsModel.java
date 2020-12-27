package com.example.whatsappclone.Model;

public class ChatsModel {
    private String from, lastmessage;
    private int msgcount;
    private long timestamp;

    public ChatsModel() {
    }

    public ChatsModel(int msgcount, long timestamp, String from) {
        this.msgcount = msgcount;
        this.timestamp = timestamp;
        this.from = from;
    }

    public ChatsModel(String from) {
        this.from = from;
    }

    public ChatsModel(String from, String lastmessage, int msgcount, long timestamp) {
        this.from = from;
        this.lastmessage = lastmessage;
        this.msgcount = msgcount;
        this.timestamp = timestamp;
    }

    public int getMsgcount() {
        return msgcount;
    }

    public void setMsgcount(int msgcount) {
        this.msgcount = msgcount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    @Override
    public String toString() {
        return "ChatsModel{" +
                "from='" + from + '\'' +
                ", lastmessage='" + lastmessage + '\'' +
                ", msgcount=" + msgcount +
                ", timestamp=" + timestamp +
                '}';
    }
}
