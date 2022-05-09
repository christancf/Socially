package com.example.socially.models;

public class ModelComment {
    String cid, comment, timestamp, uid, uDp, uFName, uLName;

    public ModelComment() {
    }

    public ModelComment(String cid, String comment, String timestamp, String uid, String uDp, String uFName, String uLName) {
        this.cid = cid;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uid = uid;
        this.uDp = uDp;
        this.uFName = uFName;
        this.uLName = uLName;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuFName() {
        return uFName;
    }

    public void setuFName(String uFName) {
        this.uFName = uFName;
    }

    public String getuLName() {
        return uLName;
    }

    public void setuLName(String uLName) {
        this.uLName = uLName;
    }
}
