package com.tan.dnatreatment.dao;

import android.app.Application;

import java.io.Serializable;

/**
 * Created by tanzhongyi on 2015/8/26.
 */
public class CustomerInfo implements Serializable{
    public CustomerInfo(String id, String name, String age, String sex, String phone, String address, String comment) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.phone = phone;
        this.address = address;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String name;
    private String age;
    private String sex;
    private String phone;
    private String address;
    private String comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("name:");
        buffer.append(name);
        buffer.append("\nsex:");
        buffer.append(sex);
        buffer.append("\nage:");
        buffer.append(age);
        buffer.append("\nphone:");
        buffer.append(phone);
        buffer.append("\naddress:");
        buffer.append(address);
        buffer.append("\ncomment:");
        buffer.append(comment);

        return buffer.toString();
    }




}
