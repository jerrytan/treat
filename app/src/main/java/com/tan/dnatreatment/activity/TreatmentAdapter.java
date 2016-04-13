package com.tan.dnatreatment.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tan.dnatreatment.R;
import com.tan.dnatreatment.dao.TreatmentInfo;

import java.util.List;

/**
 * Created by tanzhongyi on 2015/8/27.
 */
public class TreatmentAdapter extends ArrayAdapter<TreatmentInfo> {
    private int resourceId;

    public TreatmentAdapter(Context context, int resource, List<TreatmentInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TreatmentInfo treatmentInfo = getItem(position);

        TextView customerName  = (TextView) view.findViewById(R.id.treat_customer_name);
        customerName.setText(treatmentInfo.getCustomerName());
        TextView customerPhone  = (TextView) view.findViewById(R.id.treat_customer_phone);
        customerPhone.setText(treatmentInfo.getCustomerPhone());

        TextView emplyeeName  = (TextView) view.findViewById(R.id.treat_employee_name);
        emplyeeName.setText(treatmentInfo.getEmployeeName());
        TextView employeePhone  = (TextView) view.findViewById(R.id.treat_employee_phone);
        employeePhone.setText(treatmentInfo.getEmployeePhone());

        TextView treatStart  = (TextView) view.findViewById(R.id.treat_start_date);
        treatStart.setText(treatmentInfo.getStartDate());
        TextView treatEnd  = (TextView) view.findViewById(R.id.treat_end_date);
        treatEnd.setText(treatmentInfo.getEndDate());

        TextView treatHospital  = (TextView) view.findViewById(R.id.treat_hospital);
        treatHospital.setText(treatmentInfo.getHospital());

        return view;
    }
}
