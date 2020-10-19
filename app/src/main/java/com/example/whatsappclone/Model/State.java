package com.example.whatsappclone.Model;

public class State {
    private String date,time,state;

    public State(String date, String time, String state) {
        this.date = date;
        this.time = time;
        this.state = state;
    }

    public State() {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
