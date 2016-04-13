package com.tan.dnatreatment.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tan.dnatreatment.R;
import com.tan.dnatreatment.dao.CustomerInfo;
import com.tan.dnatreatment.dao.EmployeeInfo;
import com.tan.dnatreatment.dao.MyApplication;
import com.tan.dnatreatment.util.APIConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EditCustomerActivity extends AppCompatActivity {

    public static final int ADD_CUSTOMER =1;
    public static final int EDIT_CUSTOMER =2;

    private EmployeeInfo mEmployeeInfo;
    private MyApplication mApplication;
    private CustomerInfo mCustomerInfo;
    private String mCustomerSex;

    private EditText mCustomerName;
    private EditText mCustomerAge;
    private RadioGroup mCustomerSexGroup;
    private EditText mCustomerPhone;
    private EditText mCustomerAddress;
    private EditText mCustomerComment;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_CUSTOMER:
                    Toast.makeText(EditCustomerActivity.this,"客户资料已经更新",Toast.LENGTH_LONG);
                    setResult(RESULT_OK,null);
                    finish();
                    break;
                case EDIT_CUSTOMER:
                    break;

                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        mApplication = (MyApplication)getApplication();
        mEmployeeInfo = mApplication.getEmployeeInfo();
        mCustomerInfo = mApplication.getCustomerInfo();

        mCustomerName = (EditText)findViewById(R.id.add_customer_name);
        mCustomerAge = (EditText)findViewById(R.id.add_customer_age);
        mCustomerPhone = (EditText)findViewById(R.id.add_customer_phone);
        mCustomerAddress = (EditText)findViewById(R.id.add_customer_address);
        mCustomerComment= (EditText)findViewById(R.id.add_customer_comment);

        CustomerInfo customerInfo = (CustomerInfo)getIntent().getSerializableExtra("Customer");
        mCustomerSex = "男";

        mCustomerSexGroup = (RadioGroup)findViewById(R.id.add_customer_sex);
        mCustomerSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonId);
                mCustomerSex = rb.getText().toString();
            }
        });

        //edit customerinfo
        if (customerInfo!=null) {
            mCustomerName.setText(mCustomerInfo.getName());
            mCustomerAge.setText(mCustomerInfo.getAge());
            mCustomerPhone.setText(mCustomerInfo.getPhone());
            mCustomerAddress.setText(mCustomerInfo.getAddress());
            mCustomerComment.setText(mCustomerInfo.getComment());
            if(mCustomerInfo.getSex().equals("男")) {
                ((RadioButton)mCustomerSexGroup.getChildAt(0)).setChecked(true);
                ((RadioButton)mCustomerSexGroup.getChildAt(1)).setChecked(false);
            }
            else {
                ((RadioButton)mCustomerSexGroup.getChildAt(0)).setChecked(false);
                ((RadioButton)mCustomerSexGroup.getChildAt(1)).setChecked(true);
            }
        }

        Button btnAddCustomer = (Button)findViewById(R.id.upload_customer_info);
        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String customerName = mCustomerName.getText().toString();
                final String customerAge = mCustomerAge.getText().toString();
                final String customerPhone = mCustomerPhone.getText().toString();
                final String customerAddress = mCustomerAddress.getText().toString();
                final String customerComment = mCustomerComment.getText().toString();


                if (customerName.length()==0 || customerAge.length() ==0 || customerPhone.length()==0
                        || customerAddress.length() ==0) {
                    Toast.makeText(getApplicationContext(),"必填项，请填入内容", Toast.LENGTH_SHORT).show();
                }

                else {
                    //curl -d "name=tan&phone=186&sex=male&age=40&address=beijing&employee_id=55&comment='good'" http://localhost/treat/add_customer.php
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpURLConnection connection = null;
                            try {
                                URL url = new URL(APIConfig.ADD_CUSTOMER_INFO_URL);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("POST");
                                connection.setConnectTimeout(5000);
                                connection.setReadTimeout(5000);
                                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                                StringBuilder builder = new StringBuilder();
                                builder.append("name=");
                                builder.append(URLEncoder.encode(customerName, "UTF-8"));
                                builder.append("&phone=");
                                builder.append(customerPhone);
                                builder.append("&age=");
                                builder.append(customerAge);
                                builder.append("&sex=");
                                builder.append(URLEncoder.encode(mCustomerSex, "UTF-8"));
                                builder.append("&address=");
                                builder.append(URLEncoder.encode(customerAddress, "UTF-8"));
                                builder.append("&eid=");
                                builder.append( mEmployeeInfo.getId());
                                builder.append("&comment=");
                                builder.append(URLEncoder.encode(customerComment, "UTF-8"));
                                if (mCustomerInfo != null) {
                                    builder.append("&cid=");
                                    builder.append(mCustomerInfo.getId());
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
                                String customerId = jsonObject.getString("cid");
                                CustomerInfo customerInfo = new CustomerInfo(customerId, customerName,
                                        customerAge, mCustomerSex, customerPhone, customerAddress, customerComment);
                                mApplication.setCustomerInfo(customerInfo);

                                Log.d("Employee Add Customer", "status is " + requestStatus);

                                Message message = new Message();
                                message.what = ADD_CUSTOMER;
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
        getMenuInflater().inflate(R.menu.menu_add_customer, menu);
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
