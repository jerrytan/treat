package com.tan.dnatreatment.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tan.dnatreatment.R;
import com.tan.dnatreatment.dao.CustomerInfo;
import com.tan.dnatreatment.dao.EmployeeInfo;
import com.tan.dnatreatment.dao.MyApplication;
import com.tan.dnatreatment.dao.TreatmentInfo;
import com.tan.dnatreatment.util.APIConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class EditTreatmentActivity extends AppCompatActivity {
    public static final int  ADD_TREATMENT = 1;
    private Context mContext;
    private int start_year;
    private int start_month;
    private int start_day;
    private EditText mStartDate;

    private int end_year;
    private int end_month;
    private int end_day;
    private EditText mEndDate;

    private MyApplication mApplication;
    private CustomerInfo mCustomerInfo;
    private EmployeeInfo mEmployeeInfo;
    private TreatmentInfo mTreatmentInfo;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_TREATMENT:
                    Toast.makeText(mContext,"疗程信息已经保存。", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK, null);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_treatment);

        mContext = getApplicationContext();
        mApplication = (MyApplication)getApplication();
        mCustomerInfo = mApplication.getCustomerInfo();
        mEmployeeInfo = mApplication.getEmployeeInfo();
        mTreatmentInfo = mApplication.getTreatmentInfo();

        if (mCustomerInfo != null) {

            TextView customerName = (TextView) findViewById(R.id.add_customer_name);
            customerName.setText(mCustomerInfo.getName());
            TextView customerAge = (TextView) findViewById(R.id.add_customer_age);
            customerAge.setText(mCustomerInfo.getAge());
            TextView customerSex = (TextView) findViewById(R.id.add_customer_sex);
            customerSex.setText(mCustomerInfo.getSex());
            TextView customerPhone = (TextView) findViewById(R.id.add_customer_phone);
            customerPhone.setText(mCustomerInfo.getPhone());
            TextView customerAddress = (TextView) findViewById(R.id.add_customer_address);
            customerAddress.setText(mCustomerInfo.getAddress());
            TextView customerComment = (TextView) findViewById(R.id.add_customer_comment);
            customerComment.setText(mCustomerInfo.getComment());
        }

        if (mTreatmentInfo != null) {
            TextView startDate = (TextView)findViewById(R.id.add_treatment_start_date);
            startDate.setText(mTreatmentInfo.getStartDate());
            TextView endDate = (TextView)findViewById(R.id.add_treatment_end_date);
            endDate.setText(mTreatmentInfo.getEndDate());

        }

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        mStartDate = (EditText)findViewById(R.id.add_treatment_start_date);
        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(EditTreatmentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        start_year = year;
                        start_month = monthOfYear;
                        start_day = dayOfMonth;
                        mStartDate.setText(start_year + "-" + (start_month + 1) + "-" + start_day);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        mEndDate = (EditText)findViewById(R.id.add_treatment_end_date);
        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(EditTreatmentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        end_year = year;
                        end_month = monthOfYear;
                        end_day = dayOfMonth;
                        mEndDate.setText(end_year + "-" + (end_month + 1) + "-" + end_day);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        Button btnSaveTreatmentInfo= (Button)findViewById(R.id.save_treatment_info);
        btnSaveTreatmentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = mStartDate.getText().toString();
                String endDate = mEndDate.getText().toString();
                if (startDate.length()==0 || endDate.length()==0) {
                    Toast.makeText(getApplicationContext(),"必填项，请填入内容", Toast.LENGTH_SHORT).show();
                }
                else {

                    //curl -d "cid=7&eid=55&start=2015-10-10&end=2016-11-10" http://localhost/treat/add_treat.php

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpURLConnection connection = null;
                            try {
                                URL url = new URL(APIConfig.ADD_TREATMENT_INFO_URL);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("POST");
                                connection.setConnectTimeout(5000);
                                connection.setReadTimeout(5000);
                                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                                StringBuilder builder = new StringBuilder();
                                builder.append("cid=");
                                builder.append(mCustomerInfo.getId());
                                builder.append("&eid=");
                                builder.append(mEmployeeInfo.getId());
                                builder.append("&start=");
                                builder.append(mStartDate.getText().toString());
                                builder.append("&end=");
                                builder.append(mEndDate.getText().toString());
                                if(mTreatmentInfo !=null) {
                                    builder.append("&tid=");
                                    builder.append(mTreatmentInfo.getId());
                                }

                                out.writeBytes(builder.toString());

                                InputStream in = connection.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                StringBuilder response = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    response.append(line);
                                }
                                String result = response.toString();

                                JSONObject jsonObject = new JSONObject(result);
                                String requestStatus = jsonObject.getString("Status");
                                String treatMentId = jsonObject.getString("tid");

                                TreatmentInfo treatmentInfo = new TreatmentInfo(treatMentId, mEmployeeInfo.getId(), mEmployeeInfo.getName(), mEmployeeInfo.getPhone(),
                                        mStartDate.getText().toString(), mEndDate.getText().toString(),
                                        mCustomerInfo.getId(), mCustomerInfo.getName(), mCustomerInfo.getPhone());

                                mApplication.setTreatmentInfo(treatmentInfo);

                                Log.d("Employee Add Treatment ", "status is " + requestStatus);

                                Message message = new Message();
                                message.what = ADD_TREATMENT;
                                handler.sendMessage(message);

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (connection != null) {
                                    connection.disconnect();
                                }
                            }
                        }
                    }).start();

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_treatment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
