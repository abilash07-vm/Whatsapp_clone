package com.example.whatsappclone.Model;

public class ChatsModel {
    private String from;
    private int msgcount;
    private long timestamp;

    public ChatsModel() {
    }

    public ChatsModel(int msgcount, long timestamp, String from) {
        this.msgcount = msgcount;
        this.timestamp = timestamp;
        this.from = from;
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

    @Override
    public String toString() {
        return "ChatsModel{" +
                "sender='" + from + '\'' +
                ", msgcount=" + msgcount +
                ", timestamp=" + timestamp +
                '}';
    }
}
