package com.tan.dnatreatment.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tan.dnatreatment.R;
import com.tan.dnatreatment.dao.CustomerInfo;
import com.tan.dnatreatment.dao.EmployeeInfo;
import com.tan.dnatreatment.dao.MyApplication;
import com.tan.dnatreatment.util.APIConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ManageCustomerActivity extends AppCompatActivity {

    public static final int QUERY_CUSTOMER = 1;
    public static final int ADD_CUSTOMER = 2;
    public static final int EDIT_CUSTOMER = 3;

    private List<CustomerInfo> customerList = new ArrayList<CustomerInfo>();
    private MyApplication myApplication;
    private CustomerInfo selectedCustomer;
    private EmployeeInfo employeeInfo;
    private CustomerAdapter customerAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_CUSTOMER:
                    customerAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_customer);

        myApplication = (MyApplication)getApplication();
        employeeInfo = myApplication.getEmployeeInfo();

        Button btnAddCustomer = (Button)findViewById(R.id.add_customer);
        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditCustomerActivity.class);
                CustomerInfo customerInfo = myApplication.getCustomerInfo();
                if (customerInfo != null) {
                    myApplication.setCustomerInfo(null);
                }
                startActivityForResult(intent, ADD_CUSTOMER);
            }
        });

        Button btnEditCustomer = (Button)findViewById(R.id.edit_customer_info);
        btnEditCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerInfo customerInfo = myApplication.getCustomerInfo();
                if (customerInfo == null) {
                    Toast.makeText(getApplicationContext(),"请选择一名客户，再点击",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), EditCustomerActivity.class);
                    intent.putExtra("Customer",customerInfo);
                    startActivityForResult(intent, EDIT_CUSTOMER);
                }
            }
        });

        customerAdapter = new CustomerAdapter(ManageCustomerActivity.this,
                R.layout.customer_list_item,customerList);
        final ListView listView = (ListView)findViewById(R.id.customer_list);
        listView.setAdapter(customerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < parent.getCount(); i++) {
                    View v = parent.getChildAt(i);
                    if (position == i) {
                        v.setBackgroundColor(Color.GREEN);
                        selectedCustomer = customerList.get(i);
                        myApplication.setCustomerInfo(selectedCustomer);
                    } else {
                        v.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });
        initCustomerList();


        Button btn_select_customer = (Button)findViewById(R.id.select_customer);
        btn_select_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedCustomer == null) {
                    Toast.makeText(getApplicationContext(),"请选择一名客户，再点击",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ManageCustomerActivity.this, "你选择了名字为" + selectedCustomer.getName() + "的客户", Toast.LENGTH_SHORT).show();
                    Log.d("ManageCustomerActivity", selectedCustomer.toString());
                    finish();
                }
            }
        });
    }

    private void initCustomerList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                customerList.clear();
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(APIConfig.QUERY_CUSTOMER_INFO_URL);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("eid="+ employeeInfo.getId());
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!=null) {
                        response.append(line);
                    }
                    String result = response.toString();

                    JSONObject jsonObject = new JSONObject(result);
                    String requestStatus = jsonObject.getString("Status");
                    JSONArray employeeArray = jsonObject.getJSONArray("Customer");
                    Log.d("Employee Query Customer","status is " + requestStatus);
                    if (requestStatus.equals("OK")) {
                        for(int i=0;i<employeeArray.length();i++) {
                            JSONObject employeeJson = employeeArray.getJSONObject(i);

                            CustomerInfo customerFromJson = new CustomerInfo(employeeJson.getString("id"),
                                    employeeJson.getString("name"),employeeJson.getString("age"),employeeJson.getString("sex"),
                                    employeeJson.getString("phone"),employeeJson.getString("address"),employeeJson.getString("comment"));
                            customerList.add(customerFromJson);
                        }
                    }

                 
                    Message message = new Message();
                    message.what = QUERY_CUSTOMER;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (connection !=null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_customer, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADD_CUSTOMER || requestCode == EDIT_CUSTOMER) {
            if (resultCode == RESULT_OK) {
                initCustomerList();
                //customerAdapter.notifyDataSetChanged();
            }
        }
    }

}
