package com.example.whatsappclone.Model;

public class Contact {
    private String name;
    private String status;
    private String image;


    public Contact(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
