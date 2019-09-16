package com.fadecolor.esport.domain;

public class User {

    private int userId;

    private int tel;

    private String password;

    private String username;

    private String sex;

    private String headPath;

    public User() {
    }

    public User(int userId, int tel, String password, String username, String sex, String headPath) {
        this.userId = userId;
        this.tel = tel;
        this.password = password;
        this.username = username;
        this.sex = sex;
        this.headPath = headPath;
    }

    public User(int tel, String password, String username, String sex, String headPath) {
        this.tel = tel;
        this.password = password;
        this.username = username;
        this.sex = sex;
        this.headPath = headPath;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }
}
