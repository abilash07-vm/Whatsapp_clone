package com.example.whatsappclone.Model;

public class PostStatus {
    private long postcount, deletedCount;


    public PostStatus() {
    }

    public PostStatus(long postcount, long deletedCount) {
        this.postcount = postcount;
        this.deletedCount = deletedCount;
    }

    public long getPostcount() {
        return postcount;
    }


    public void setPostcount(long postcount) {
        this.postcount = postcount;
    }

    public long getDeletedCount() {
        return deletedCount;
    }

    public void setDeletedCount(long deletedCount) {
        this.deletedCount = deletedCount;
    }

    @Override
    public String toString() {
        return "PostStatus{" +
                "postcount=" + postcount +
                ", deletedCount=" + deletedCount +
                '}';
    }
}
