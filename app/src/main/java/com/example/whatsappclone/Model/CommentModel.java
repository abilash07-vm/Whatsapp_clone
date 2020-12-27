package com.example.whatsappclone.Model;

public class CommentModel {
    private String userid, comment, commentid;
    private long timestamp;

    public CommentModel() {
    }

    public CommentModel(String userid, String comment, String commentid, long timestamp) {
        this.userid = userid;
        this.comment = comment;
        this.commentid = commentid;
        this.timestamp = timestamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CommentModel{" +
                "userid='" + userid + '\'' +
                ", comment='" + comment + '\'' +
                ", commentid='" + commentid + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
