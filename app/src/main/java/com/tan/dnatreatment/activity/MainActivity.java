package com.tan.dnatreatment.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tan.dnatreatment.R;
import com.tan.dnatreatment.dao.MyApplication;
import com.tan.dnatreatment.dao.UpdateVersionInfo;
import com.tan.dnatreatment.util.APIConfig;
import com.tan.dnatreatment.util.DownLoadManager;
import com.tan.dnatreatment.util.UpdateInfoParser;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{


    private  MyApplication mApplication = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = (MyApplication) getApplication();

        Button btnLogin = (Button)findViewById(R.id.main_employee_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EmployeeProfileActivity.class);
                startActivity(intent);
            }
        });

        Button btnManageCustomer = (Button)findViewById(R.id.main_manage_customer);
        btnManageCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mApplication.getEmployeeInfo() == null) {
                    promptLogin();
                } else {
                    Intent intent = new Intent(MainActivity.this, ManageCustomerActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button btnManageTreatment = (Button)findViewById(R.id.main_manage_treatment);
        btnManageTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mApplication.getEmployeeInfo() == null) {
                    promptLogin();
                }
                else if(mApplication.getCustomerInfo() == null ) {
                    promptSelectCustomer();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, ManageTreatmentActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button btnPrintBarcode  = (Button)findViewById(R.id.main_print_barcode);
        btnPrintBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mApplication.getEmployeeInfo() == null) {
                    promptLogin();
                }
                else if(mApplication.getCustomerInfo() == null ) {
                    promptSelectCustomer();
                }
                else if (mApplication.getTreatmentInfo() == null) {
                    promptSelectTreatment();
                }
                else {
                    Intent intent = new Intent(MainActivity.this,com.gprinter.sample.PrintBarcodeActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button btnTreatment  = (Button)findViewById(R.id.main_treatment);
        btnTreatment.setOnClickListener(this);

        Button btnSetting  = (Button)findViewById(R.id.main_printer_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(MainActivity.this,com.gprinter.sample.MainActivity.class);
                startActivity(intent);
            }
        });

        Button btnAbout  = (Button)findViewById(R.id.main_about);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ImageView imageView = new ImageView(MainActivity.this);
//                imageView.setImageResource(R.drawable.company);
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setIcon(R.drawable.logo);
//                builder.setTitle("关于公司");
//                builder.setView(imageView);
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                Dialog alertDialog = builder.create();
//                alertDialog.show();
                Intent intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });

        Button btnSaveBlood = (Button)findViewById(R.id.main_save_blood);
        btnSaveBlood.setOnClickListener(this);

        Button btnGetDrug = (Button)findViewById(R.id.main_get_drug);
        btnGetDrug.setOnClickListener(this);

    }



    private void promptLogin() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("请登录");
        dialog.setMessage("亲爱的员工，请登录后使用.");
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this,EmployeeProfileActivity.class);
                startActivity(intent);                        }
        });
        dialog.show();
    }

    private void promptSelectCustomer() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("请选择客户");
        dialog.setMessage("亲爱的员工，请选择一个客户后使用.");
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this,ManageCustomerActivity.class);
                startActivity(intent);                        }
        });
        dialog.show();
    }

    private void promptSelectTreatment() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("请选择疗程");
        dialog.setMessage("亲爱的员工，请选择一个疗程后使用.");
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this,ManageTreatmentActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (mApplication.getEmployeeInfo() == null) {
            promptLogin();
        }
        else {
            startScanBarcode();
        }
    }

    private void startScanBarcode() {
        Intent intent = new Intent(getApplicationContext(), cn.hugo.android.scanner.CaptureActivity.class);
        startActivity(intent);
    }
}
