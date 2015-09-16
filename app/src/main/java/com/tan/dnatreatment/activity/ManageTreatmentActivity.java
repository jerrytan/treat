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
import com.tan.dnatreatment.dao.TreatmentInfo;
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

public class ManageTreatmentActivity extends AppCompatActivity {

    private static final int ADD_TREATMENT = 1 ;
    public static final int QUERY_TREATMENT_BY_EID = 2;
    public static final int EDIT_TREATMENT = 3;


    private List<TreatmentInfo> treatmentInfos = new ArrayList<TreatmentInfo>();
    private MyApplication mApplication;
    private TreatmentInfo mSelectedTreat;
    private EmployeeInfo mEmployeeInfo;
    private CustomerInfo mCustomerInfo;
    private TreatmentAdapter mTreatmentAdapter;

    private int listIndex;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_TREATMENT_BY_EID:
                    mTreatmentAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_treatment);

        mApplication = (MyApplication)getApplication();
        mEmployeeInfo = mApplication.getEmployeeInfo();
        mCustomerInfo = mApplication.getCustomerInfo();

        Button btnAddTreatment = (Button)findViewById(R.id.add_treatment);
        btnAddTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditTreatmentActivity.class);
                TreatmentInfo treatmentInfo = mApplication.getTreatmentInfo();
                if (treatmentInfo != null) {
                    mApplication.setTreatmentInfo(null);
                }
                startActivityForResult(intent, ADD_TREATMENT);
            }

        });

        Button btnEditTreatment = (Button)findViewById(R.id.edit_treat_info);
        btnEditTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreatmentInfo treatmentInfo = mApplication.getTreatmentInfo();
                if (treatmentInfo == null) {
                    Toast.makeText(getApplicationContext(), "请选择一个疗程，再点击", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), EditTreatmentActivity.class);
                    startActivityForResult(intent, EDIT_TREATMENT);
                }
            }
        });

        mTreatmentAdapter = new TreatmentAdapter(ManageTreatmentActivity.this,
                R.layout.treatment_list_item,treatmentInfos);
        final ListView listView = (ListView)findViewById(R.id.treat_list);
        listView.setAdapter(mTreatmentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < parent.getCount(); i++) {
                    View v = parent.getChildAt(i);
                    if (position == i) {
                        v.setBackgroundColor(Color.GREEN);
                        mSelectedTreat = treatmentInfos.get(i);
                        listIndex = position;
                        mApplication.setTreatmentInfo(mSelectedTreat);
                    } else {
                        v.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });
        initTreatmentByEid();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listIndex = position;
                mSelectedTreat = treatmentInfos.get(position);
                treatmentInfos.remove(position);
                Toast.makeText(getBaseContext(), mSelectedTreat.getId() + "被删除了",
                        Toast.LENGTH_SHORT).show();

                mTreatmentAdapter.notifyDataSetChanged();
                return true;
            }
        });

        Button btn_select_treat = (Button)findViewById(R.id.select_treat);
        btn_select_treat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedTreat == null) {
                    Toast.makeText(getApplicationContext(),"请选择一个疗程，再点击",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ManageTreatmentActivity.this, "你选择了编号为" + mSelectedTreat.getId() + "的疗程", Toast.LENGTH_SHORT).show();
                    Log.d("ManageTreatmentActivity", mSelectedTreat.toString());

                    Intent fetchIntent = new Intent(getApplicationContext(),FetchBarcodeHistoryActivity.class);
                    fetchIntent.putExtra("tid",  mSelectedTreat.getId());
                    startActivity(fetchIntent);
                    finish();
                }
            }
        });
    }

    private void initTreatmentByEid() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                treatmentInfos.clear();
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(APIConfig.QUERY_TREATMENT_BY_EID_URL);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("eid="+ mEmployeeInfo.getId()+"&cid="+mCustomerInfo.getId());
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!=null) {
                        response.append(line);
                    }
                    String result = response.toString();

                    JSONObject jsonResult = new JSONObject(result);
                    String requestStatus = jsonResult.getString("Status");
                    JSONArray jsonArrayTreat = jsonResult.getJSONArray("Treatment");
                    if (requestStatus.equals("OK")) {
                        for(int i=0;i<jsonArrayTreat.length();i++) {
                            JSONObject jsonObject  = jsonArrayTreat.getJSONObject(i);
                            TreatmentInfo treatmentInfo = new TreatmentInfo(jsonObject.getString("treatment.id"),
                                    jsonObject.getString("employee.id"),jsonObject.getString("employee.name"),jsonObject.getString("employee.phone"),
                                    jsonObject.getString("treatment.start_date"),jsonObject.getString("treatment.end_date"),
                                    jsonObject.getString("customer.id"),jsonObject.getString("customer.name"),jsonObject.getString("customer.phone"));
                            treatmentInfos.add(treatmentInfo);
                        }
                    }
                    Message message = new Message();
                    message.what = QUERY_TREATMENT_BY_EID;
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
        getMenuInflater().inflate(R.menu.menu_manage_treatment, menu);
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

        if (requestCode == ADD_TREATMENT || requestCode == EDIT_TREATMENT) {
            if (resultCode == RESULT_OK) {
                initTreatmentByEid();
            }
        }
    }
}
