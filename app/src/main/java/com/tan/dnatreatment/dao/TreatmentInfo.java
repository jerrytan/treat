package com.tan.dnatreatment.dao;

/**
 * Created by tanzhongyi on 2015/8/26.
 */
public class TreatmentInfo {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public String getEmployeeName() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getEmployeePhone() {
        return employee_phone;
    }

    public void setEmployee_phone(String employee_phone) {
        this.employee_phone = employee_phone;
    }

    public String getStartDate() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEndDate() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomerName() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomerPhone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    private String id;

    private String employee_id;
    private String employee_name;
    private String employee_phone;

    private String start_date;
    private String end_date;

    private String customer_id;
    private String customer_name;
    private String customer_phone;
    private String hospital;

    public String getCustomerSex() {
        return customer_sex;
    }

    public void setCustomer_sex(String customer_sex) {
        this.customer_sex = customer_sex;
    }

    private String customer_sex;

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }


    public TreatmentInfo(String id, String employee_id, String employee_name, String employee_phone,
                         String start_date, String end_date,
                         String customer_id,String customer_name, String customer_phone,String customer_sex, String hospital) {
        this.id = id;
        this.employee_id = employee_id;
        this.employee_name = employee_name;
        this.employee_phone = employee_phone;
        this.start_date = start_date;
        this.end_date = end_date;
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_sex = customer_sex;
        this.hospital = hospital;
    }
}
