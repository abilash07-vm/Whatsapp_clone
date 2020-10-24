package com.example.whatsappclone.Model;

public class PrivateMessageModel {
    private String from, message, type, to, date, time;
    private long timestamp;

    public PrivateMessageModel(String from, String message, String type, String to, String date, String time, long timestamp) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.to = to;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
    }

    public PrivateMessageModel(String from, String message, String type, String to, String date, String time) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.to = to;
        this.date = date;
        this.time = time;
    }

    public PrivateMessageModel() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
