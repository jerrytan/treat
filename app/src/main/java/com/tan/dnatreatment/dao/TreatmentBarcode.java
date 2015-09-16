package com.tan.dnatreatment.dao;

/**
 * Created by tanzhongyi on 2015/8/31.
 */
public class TreatmentBarcode {
    private String treat_id;
    private String customer_barcode;
    private String step1_barcode;
    private String step2_barcode;
    private String step3_barcode;
    private String step4_barcode;
    private String step5_barcode;
    private String step6_barcode;
    private String blood_barcode;
    private String drug1_barcode;
    private String drug2_barcode;
    private String drug3_barcode;
    private String drug4_barcode;
    private String drug5_barcode;

    public String getStep4Barcode() {
        return step4_barcode;
    }

    public void setStep4Barcode(String step4_barcode) {
        this.step4_barcode = step4_barcode;
    }

    public String getStep5Barcode() {
        return step5_barcode;
    }

    public void setStep5Barcode(String step5_barcode) {
        this.step5_barcode = step5_barcode;
    }

    public String getStep6Barcode() {
        return step6_barcode;
    }

    public void setStep6Barcode(String step6_barcode) {
        this.step6_barcode = step6_barcode;
    }


    public String getDrug4Barcode() {
        return drug4_barcode;
    }

    public void setDrug4Barcode(String drug4_barcode) {
        this.drug4_barcode = drug4_barcode;
    }

    public String getDrug5Barcode() {
        return drug5_barcode;
    }

    public void setDrug5Barcode(String drug5_barcode) {
        this.drug5_barcode = drug5_barcode;
    }

    public String getDrug6Barcode() {
        return drug6_barcode;
    }

    public void setDrug6Barcode(String drug6_barcode) {
        this.drug6_barcode = drug6_barcode;
    }

    private String drug6_barcode;


    public String getCustomer_barcode() {
        return customer_barcode;
    }




    public String getBloodBarcode() {
        return blood_barcode;
    }

    public void setBloodBarcode(String blood_barcode) {
        this.blood_barcode = blood_barcode;
    }

    public String getDrug1Barcode() {
        return drug1_barcode;
    }

    public void setDrug1Barcode(String drug1_barcode) {
        this.drug1_barcode = drug1_barcode;
    }

    public String getDrug2Barcode() {
        return drug2_barcode;
    }

    public void setDrug2Barcode(String drug2_barcode) {
        this.drug2_barcode = drug2_barcode;
    }

    public String getDrug3Barcode() {
        return drug3_barcode;
    }

    public void setDrug3Barcode(String drug3_barcode) {
        this.drug3_barcode = drug3_barcode;
    }

    public TreatmentBarcode(String treat_id, String customer_barcode,
                            String step1_barcode, String step2_barcode, String step3_barcode,
                            String step4_barcode, String step5_barcode, String step6_barcode,
                            String blood_barcode,
                            String drug1_barcode, String drug2_barcode, String drug3_barcode,
                            String drug4_barcode, String drug5_barcode, String drug6_barcode) {
        this.treat_id = treat_id;
        this.customer_barcode = customer_barcode;
        this.step1_barcode = step1_barcode;
        this.step2_barcode = step2_barcode;
        this.step3_barcode = step3_barcode;
        this.step4_barcode = step4_barcode;
        this.step5_barcode = step5_barcode;
        this.step6_barcode = step6_barcode;
        this.blood_barcode = blood_barcode;
        this.drug1_barcode = drug1_barcode;
        this.drug2_barcode = drug2_barcode;
        this.drug3_barcode = drug3_barcode;
        this.drug4_barcode = drug4_barcode;
        this.drug5_barcode = drug5_barcode;
        this.drug6_barcode = drug6_barcode;
    }


    public String getTreatId() {
        return treat_id;
    }

    public void setTreatId(String treat_id) {
        this.treat_id = treat_id;
    }

    public String getCustomerBarcode() {
        return customer_barcode;
    }

    public void setCustomerBarcode(String customer_barcode) {
        this.customer_barcode = customer_barcode;
    }

    public String getStep1Barcode() {
        return step1_barcode;
    }

    public void setStep1Barcode(String step1_barcode) {
        this.step1_barcode = step1_barcode;
    }

    public String getStep2Barcode() {
        return step2_barcode;
    }

    public void setStep2Barcode(String step2_barcode) {
        this.step2_barcode = step2_barcode;
    }

    public String getStep3Barcode() {
        return step3_barcode;
    }

    public void setStep3Barcode(String step3_barcode) {
        this.step3_barcode = step3_barcode;
    }
}
