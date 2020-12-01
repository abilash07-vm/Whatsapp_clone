package com.example.whatsappclone.Model;

public class PostModel {
    private String userid, postlink, likedby, caption, postid;
    private boolean isdeleted;
    private long timestamp;
    private int likesCount;

    public PostModel(String userid, String postlink, String likedby, String caption, String postid, boolean isdeleted, long timestamp, int likesCount) {
        this.userid = userid;
        this.postlink = postlink;
        this.likedby = likedby;
        this.caption = caption;
        this.postid = postid;
        this.isdeleted = isdeleted;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
    }

    public PostModel() {
    }

    public boolean isIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(boolean isdeleted) {
        this.isdeleted = isdeleted;
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

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
}
