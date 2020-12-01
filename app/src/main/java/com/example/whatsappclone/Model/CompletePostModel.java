package com.example.whatsappclone.Model;

public class CompletePostModel {
    private String userid, postlink, likedby, caption, postid;
    private boolean isdeleted;
    private long timestamp;
    private int likesCount;
    private String name;
    private String status;
    private String image;
    private int postCount;

    public CompletePostModel() {
    }

    public CompletePostModel(String userid, String postlink, String likedby, String caption, String postid, boolean isdeleted, long timestamp, int likesCount, String name, String status, String image, int postCount) {
        this.userid = userid;
        this.postlink = postlink;
        this.likedby = likedby;
        this.caption = caption;
        this.postid = postid;
        this.isdeleted = isdeleted;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
        this.name = name;
        this.status = status;
        this.image = image;
        this.postCount = postCount;
    }

    public CompletePostModel(String userid, String postlink, String likedby, String caption, boolean isdeleted, long timestamp, int likesCount) {
        this.userid = userid;
        this.postlink = postlink;
        this.likedby = likedby;
        this.caption = caption;
        this.isdeleted = isdeleted;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
    }

    public void addUserdata(Contact contact) {
        this.name = contact.getName();
        this.status = contact.getStatus();
        this.image = contact.getImage();
        this.postCount = contact.getPostcount();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPostlink() {
        return postlink;
    }

    public void setPostlink(String postlink) {
        this.postlink = postlink;
    }

    public String getLikedby() {
        return likedby;
    }

    public void setLikedby(String likedby) {
        this.likedby = likedby;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(boolean isdeleted) {
        this.isdeleted = isdeleted;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
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

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    @Override
    public String toString() {
        return "CompletePostModel{" +
                "userid='" + userid + '\'' +
                ", postlink='" + postlink + '\'' +
                ", likedby='" + likedby + '\'' +
                ", caption='" + caption + '\'' +
                ", postid='" + postid + '\'' +
                ", isdeleted=" + isdeleted +
                ", timestamp=" + timestamp +
                ", likesCount=" + likesCount +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                ", postCount=" + postCount +
                '}';
    }
}

