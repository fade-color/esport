package com.fadecolor.esport.domain;

import java.util.Date;

public class Activity {

    private Integer activityId;

    private Date time;

    private String imageSrc;

    private String location;

    private Integer commentNum;

    private String userTel;

    private String content;

    private String userName;

    private String headPath;

    public Integer getActivityId() {
        return activityId;
    }

    public Activity() {
    }

    public Activity(Integer activityId, Date time, String imageSrc, String location, Integer commentNum, String userTel, String content, String userName, String headPath) {
        this.activityId = activityId;
        this.time = time;
        this.imageSrc = imageSrc;
        this.location = location;
        this.commentNum = commentNum;
        this.userTel = userTel;
        this.content = content;
        this.userName = userName;
        this.headPath = headPath;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }
}
