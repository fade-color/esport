package com.fadecolor.esport.domain;

import java.util.Date;

public class Comment {

    private Date time;

    private String content;

    private String headPath;

    private String userName;

    private String userTel;

    public Comment() {
    }

    public Comment(Date time, String content, String headPath, String userName, String userTel) {
        this.time = time;
        this.content = content;
        this.headPath = headPath;
        this.userName = userName;
        this.userTel = userTel;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }
}
