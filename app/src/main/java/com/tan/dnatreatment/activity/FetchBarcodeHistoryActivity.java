package com.tan.dnatreatment.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tan.dnatreatment.R;
import com.tan.dnatreatment.dao.EmployeeInfo;
import com.tan.dnatreatment.dao.MyApplication;
import com.tan.dnatreatment.util.APIConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchBarcodeHistoryActivity extends AppCompatActivity {

    private static final int FETCH_BARCODE_HISTORY =1;
    private String mBarcode;
    private String mTreatmentId;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FETCH_BARCODE_HISTORY:
                    TextView textView = (TextView)findViewById(R.id.showBarcodeHistory);
                    try {
                        JSONObject result = (JSONObject) msg.obj;
                        StringBuilder builder = new StringBuilder();

                        builder.append("客户：");
                        builder.append(result.getString("customer.name"));
                        builder.append(" 电话：");
                        builder.append(result.getString("customer.phone"));
                        builder.append("\n");
                        builder.append("所在医院：");
                        builder.append(result.getString("hospital"));
                        builder.append("\n");


                        builder.append("疗程开始时间：");
                        builder.append(result.getString("start_date"));
                        builder.append("\n");
                        builder.append("疗程结束时间：");
                        builder.append(result.getString("end_date"));
                        builder.append("\n");


                        builder.append("销售：");
                        builder.append(result.getString("employee.name"));
                        builder.append(" 电话：");
                        builder.append(result.getString("employee.phone"));
                        builder.append("\n\n");

                        builder.append("以下为治疗过程\n");

                        builder.append("血样入库：");
                        builder.append(convertNullToString(result.getString("blood_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("blood_receive_time")));
                        builder.append("\n\n");

                        builder.append("第一次取药：");
                        builder.append(convertNullToString(result.getString("drug1_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("drug1_distribute_time")));
                        builder.append("\n");
                        builder.append("第一次治疗：");
                        builder.append(convertNullToString(result.getString("step1_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("step1_finish_time")));
                        builder.append("\n\n");


                        builder.append("第二次取药：");
                        builder.append(convertNullToString(result.getString("drug2_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("drug2_distribute_time")));
                        builder.append("\n");
                        builder.append("第二次治疗：");
                        builder.append(convertNullToString(result.getString("step2_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("step2_finish_time")));
                        builder.append("\n\n");

                        builder.append("第三次取药：");
                        builder.append(convertNullToString(result.getString("drug3_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("drug3_distribute_time")));
                        builder.append("\n");
                        builder.append("第三次治疗：");
                        builder.append(convertNullToString(result.getString("step3_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("step3_finish_time")));
                        builder.append("\n\n");

                        builder.append("第四次取药：");
                        builder.append(convertNullToString(result.getString("drug4_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("drug4_distribute_time")));
                        builder.append("\n");
                        builder.append("第四次治疗：");
                        builder.append(convertNullToString(result.getString("step4_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("step4_finish_time")));
                        builder.append("\n\n");

                        builder.append("第五次取药：");
                        builder.append(convertNullToString(result.getString("drug5_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("drug5_distribute_time")));
                        builder.append("\n");
                        builder.append("第五次治疗：");
                        builder.append(convertNullToString(result.getString("step5_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("step5_finish_time")));
                        builder.append("\n\n");

                        builder.append("第六次取药：");
                        builder.append(convertNullToString(result.getString("drug6_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("drug6_distribute_time")));
                        builder.append("\n");
                        builder.append("第六次治疗：");
                        builder.append(convertNullToString(result.getString("step6_employee")));
                        builder.append("  时间：");
                        builder.append(convertNullToString(result.getString("step6_finish_time")));
                        builder.append("\n");


                        textView.setText(builder.toString());
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private String convertNullToString(String nullString) {
        if (nullString.equals("null")) {
            return "暂无";
        }
        else return nullString;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_barcode_history);
        mBarcode = getIntent().getStringExtra("barcode");
        mTreatmentId = getIntent().getStringExtra("tid");

        if (mBarcode!=null) {
            mTreatmentId = mBarcode.substring(6,10);
        }
        fetchBarcodeHistoryById(mTreatmentId);
    }

    private void fetchBarcodeHistoryById(String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(APIConfig.FETCH_BARCODE_HISTORY);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    EmployeeInfo employeeInfo = ((MyApplication)getApplication()).getEmployeeInfo();
                    out.writeBytes("tid=" + mTreatmentId);
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
                    Message message = new Message();
                    message.obj = jsonObject.getJSONObject("History");
                    message.what = FETCH_BARCODE_HISTORY;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fetch_barcode_history, menu);
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
