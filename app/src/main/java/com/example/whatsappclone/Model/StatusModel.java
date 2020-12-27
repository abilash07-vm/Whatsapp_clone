package com.example.whatsappclone.Model;

public class StatusModel {
    private String userid, statuslink, seenBy, caption, statusid;
    private long timestamp;
    private int viewCount;

    public StatusModel() {
    }

    public StatusModel(String userid, String statuslink, String seenBy, String caption, String statusid, long timestamp, int viewCount) {
        this.userid = userid;
        this.statuslink = statuslink;
        this.seenBy = seenBy;
        this.caption = caption;
        this.statusid = statusid;
        this.timestamp = timestamp;
        this.viewCount = viewCount;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getStatuslink() {
        return statuslink;
    }

    public void setStatuslink(String statuslink) {
        this.statuslink = statuslink;
    }

    public String getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(String seenBy) {
        this.seenBy = seenBy;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getStatusid() {
        return statusid;
    }

    public void setStatusid(String statusid) {
        this.statusid = statusid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    @Override
    public String toString() {
        return "StatusModel{" +
                "userid='" + userid + '\'' +
                ", statuslink='" + statuslink + '\'' +
                ", likedby='" + seenBy + '\'' +
                ", caption='" + caption + '\'' +
                ", statusid='" + statusid + '\'' +
                ", timestamp=" + timestamp +
                ", viewCount=" + viewCount +
                '}';
    }
}
