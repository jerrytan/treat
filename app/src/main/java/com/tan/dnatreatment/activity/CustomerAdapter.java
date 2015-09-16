package com.tan.dnatreatment.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tan.dnatreatment.R;
import com.tan.dnatreatment.dao.CustomerInfo;

import java.util.List;

/**
 * Created by tanzhongyi on 2015/8/27.
 */
public class CustomerAdapter extends ArrayAdapter<CustomerInfo> {
    private int resourceId;

    public CustomerAdapter(Context context, int resource, List<CustomerInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        CustomerInfo customer = getItem(position);

        TextView customer_name  = (TextView) view.findViewById(R.id.treat_customer_name);
        customer_name.setText(customer.getName());
        TextView customer_age  = (TextView) view.findViewById(R.id.customer_age);
        customer_age.setText(customer.getAge());
        TextView customer_sex  = (TextView) view.findViewById(R.id.customer_sex);
        customer_sex.setText(customer.getSex());
        TextView customer_phone  = (TextView) view.findViewById(R.id.customer_phone);
        customer_phone.setText(customer.getPhone());
        TextView customer_address  = (TextView) view.findViewById(R.id.customer_address);
        customer_address.setText(customer.getAddress());
        TextView customer_comment  = (TextView) view.findViewById(R.id.customer_comment);
        customer_comment.setText(customer.getComment());

        return view;
    }
}
