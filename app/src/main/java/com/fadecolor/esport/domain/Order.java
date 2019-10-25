package com.fadecolor.esport.domain;

import java.util.Date;

public class Order {

    private String userTel;

    private Date date;

    private int period;

    private int subGymId;

    private int orderId;

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getSubGymId() {
        return subGymId;
    }

    public void setSubGymId(int subGymId) {
        this.subGymId = subGymId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
