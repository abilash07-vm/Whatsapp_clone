package com.example.whatsappclone.Model;

public class MessageModel {
    private String reciever;
    private String sender;
    private String message;
    private String date;
    private String time;

    public MessageModel(String reciever, String sender, String message, String date, String time) {
        this.reciever = reciever;
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public MessageModel() {
    }

    public MessageModel(String sender, String message, String date, String time) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
