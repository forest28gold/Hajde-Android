package com.azizinetwork.hajde.model.parse;


import java.util.ArrayList;

public class PostData {

    private String objectId;
    private String content;
    private String backColor;
    private String type;
    private String filePath;
    private String userID;
    private String latitude;
    private String longitude;
    private String time;
    private String period;

    private int imgHeight;
    private int commentCount;
    private int likeCount;
    private int reportCount;
    private String commentType;
    private String likeType;
    private String reportType;

    private ArrayList<String> commentTypeArray = new ArrayList<>();
    private ArrayList<String> likeTypeArray = new ArrayList<>();
    private ArrayList<String> reportTypeArray = new ArrayList<>();

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
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

    public ArrayList<String> getCommentTypeArray() {
        return commentTypeArray;
    }

    public void setCommentTypeArray(ArrayList<String> commentTypeArray) {
        this.commentTypeArray = commentTypeArray;
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
