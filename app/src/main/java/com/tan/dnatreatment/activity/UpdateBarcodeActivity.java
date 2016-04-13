package com.tan.dnatreatment.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class UpdateBarcodeActivity extends AppCompatActivity {

    public static final int QUERY_BARCODE =1;
    public static final int UPDATE_BARCODE=2;

    private String mBarcode;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_BARCODE:
                    TextView textView = (TextView)findViewById(R.id.codeInfo);
                    try {
                        JSONObject result = (JSONObject) msg.obj;
                        StringBuilder builder = new StringBuilder();
                        builder.append("客户姓名：");
                        builder.append(result.getString("CustomerName"));
                        builder.append("\n");

                        builder.append("客户年龄：");
                        builder.append(result.getString("CustomerAge"));
                        builder.append("\n");

                        builder.append("客户性别：");
                        builder.append(result.getString("CustomerSex"));
                        builder.append("\n");

                        builder.append("客户电话：");
                        builder.append(result.getString("CustomerPhone"));
                        builder.append("\n");

                        builder.append("开始时间：");
                        builder.append(result.getString("Start"));
                        builder.append("\n");

                        builder.append("结束时间：");
                        builder.append(result.getString("End"));
                        builder.append("\n");

                        builder.append("条形码类型：");
                        builder.append(result.getString("CodeType"));
                        builder.append("\n");

                        textView.setText(builder.toString());
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case UPDATE_BARCODE:
                    Toast.makeText(UpdateBarcodeActivity.this, msg.obj +" 操作记录已经更新成功.",Toast.LENGTH_SHORT).show();

                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_barcode);
        Intent intent = getIntent();
        mBarcode = intent.getStringExtra("barcode");
        queryBarcodeInfo(mBarcode);

        Button btnFetchBarcodeHistory = (Button)findViewById(R.id.fetchBarcodeHistory);
        btnFetchBarcodeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fetchIntent = new Intent(UpdateBarcodeActivity.this,FetchBarcodeHistoryActivity.class);
                fetchIntent.putExtra("barcode",mBarcode);
                startActivity(fetchIntent);
            }
        });
        Button btnUpdateBarcode = (Button)findViewById(R.id.updateBarcode);
        btnUpdateBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection connection = null;
                        try {
                            URL url = new URL(APIConfig.UPDATE_BARCODE_URL);
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setConnectTimeout(5000);
                            connection.setReadTimeout(5000);
                            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                            EmployeeInfo employeeInfo = ((MyApplication)getApplication()).getEmployeeInfo();
                            out.writeBytes("code=" + mBarcode+"&eid="+employeeInfo.getId());
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
                            message.obj = requestStatus;
                            message.what = UPDATE_BARCODE;
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
        });
    }


    private void queryBarcodeInfo(final String barcode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(APIConfig.QUERY_BARCODE_URL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("code=" + barcode);
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
                    JSONObject barcodeObject  = jsonObject.getJSONObject("barcode");
                    Message message = new Message();
                    message.obj = barcodeObject;
                    message.what = QUERY_BARCODE;
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
        getMenuInflater().inflate(R.menu.menu_update_barcode, menu);
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
