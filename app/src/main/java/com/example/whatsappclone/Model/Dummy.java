package com.example.whatsappclone.Model;

public class Dummy {
    private String type;
    private  long timestamp;
    private String imglink;
    private String name;

    public Dummy(String type, long timestamp, String imglink, String name) {
        this.type = type;
        this.timestamp = timestamp;
        this.imglink = imglink;
        this.name=name;
    }


    public Dummy(String type) {
        this.type = type;
    }

    public Dummy() {
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
}
