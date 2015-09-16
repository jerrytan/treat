package com.tan.dnatreatment.dao;

import java.io.Serializable;

/**
 * Created by tanzhongyi on 2015/8/26.
 */
public class EmployeeInfo implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String id;
    private String name;
    private String phone;

    public EmployeeInfo(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }
}
