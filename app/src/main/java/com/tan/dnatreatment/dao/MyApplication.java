package com.tan.dnatreatment.dao;

import android.app.Application;

/**
 * Created by tanzhongyi on 2015/8/27.
 */
public class MyApplication extends Application {
    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public EmployeeInfo getEmployeeInfo() {
        return employeeInfo;
    }

    public void setEmployeeInfo(EmployeeInfo employeeInfo) {
        this.employeeInfo = employeeInfo;
    }

    public TreatmentInfo getTreatmentInfo() {
        return treatmentInfo;
    }

    public void setTreatmentInfo(TreatmentInfo treatmentInfo) {
        this.treatmentInfo = treatmentInfo;
    }

    private CustomerInfo customerInfo;
    private EmployeeInfo employeeInfo;
    private TreatmentInfo treatmentInfo;

    public TreatmentBarcode getBarcode() {
        return barcode;
    }

    public void setBarcode(TreatmentBarcode barcode) {
        this.barcode = barcode;
    }

    private TreatmentBarcode barcode;


}
