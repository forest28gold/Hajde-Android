package com.azizinetwork.hajde.model.parse;

import java.util.ArrayList;

public class ActivityPostData {

    private String objectId;
    private String postId;
    private String fromUser;
    private String backColor;
    private String toUser;
    private String comment;
    private String latitude;
    private String longitude;
    private String time;
    private int likeCount;
    private int reportCount;
    private String likeType;
    private String reportType;

    private ArrayList<String> likeTypeArray = new ArrayList<>();
    private ArrayList<String> reportTypeArray = new ArrayList<>();

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public String getLikeType() {
        return likeType;
    }

    public void setLikeType(String likeType) {
        this.likeType = likeType;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public ArrayList<String> getLikeTypeArray() {
        return likeTypeArray;
    }

    public void setLikeTypeArray(ArrayList<String> likeTypeArray) {
        this.likeTypeArray = likeTypeArray;
    }

    public ArrayList<String> getReportTypeArray() {
        return reportTypeArray;
    }

    public void setReportTypeArray(ArrayList<String> reportTypeArray) {
        this.reportTypeArray = reportTypeArray;
    }
}
