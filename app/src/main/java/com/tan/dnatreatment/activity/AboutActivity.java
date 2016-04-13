package com.tan.dnatreatment.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tan.dnatreatment.R;
import com.tan.dnatreatment.dao.UpdateVersionInfo;
import com.tan.dnatreatment.util.APIConfig;
import com.tan.dnatreatment.util.DownLoadManager;
import com.tan.dnatreatment.util.UpdateInfoParser;

import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AboutActivity extends AppCompatActivity {
    private final int UPDATE_NONEEDED = 0;
    private final int UPDATE_CLIENT = 1;
    private final int GET_UNDATEINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;
    private String TAG ="ABOUTACTIVITY";

    TextView mVersion;
    Button mCheckVersion;
    private int mLocalVersion;


    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_NONEEDED:
                    Toast.makeText(AboutActivity.this, "已经是最新版本，不需要升级.", Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_CLIENT:
                    final UpdateVersionInfo info = (UpdateVersionInfo) msg.obj;
                    AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                    builder.setIcon(R.drawable.logo);
                    builder.setTitle("版本有更新");
                    builder.setMessage(info.getNote());
                    builder.setPositiveButton("下载版本并更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Log.i(TAG, "下载apk,更新");
                            downloadApkForUpdate(info);
                        }
                    });
                    Dialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                case GET_UNDATEINFO_ERROR:
                    Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mVersion = (TextView) findViewById(R.id.about_version);
        mVersion.setText(getVersionName());
        mLocalVersion = getVersionCode();

        mCheckVersion = (Button) findViewById(R.id.about_check);
        mCheckVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
            }
        });
    }

    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    String path = APIConfig.CHECK_VERSION_URL;
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        is = conn.getInputStream();
                    }
                    UpdateVersionInfo info = UpdateInfoParser.getUpdataInfo(is);
                    if (info.getVersionCode() <= mLocalVersion) {
                        Log.i(TAG, "服务器没有更新的版本");
                        Message msg = new Message();
                        msg.what = UPDATE_NONEEDED;
                        handler.sendMessage(msg);
                    } else if(info.getVersionCode() > mLocalVersion)  {
                        Log.i(TAG, "服务器有更新的版本 ");
                        Message msg = new Message();
                        msg.what = UPDATE_CLIENT;
                        msg.obj = info;
                        handler.sendMessage(msg);
                    }
                }catch (Exception e) {
                    Message msg = new Message();
                    msg.what = GET_UNDATEINFO_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private int getVersionCode()  {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getVersionName()  {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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
    private void downloadApkForUpdate(final UpdateVersionInfo info) {
        final ProgressDialog progressDialog =  new  ProgressDialog(AboutActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIcon(R.drawable.appicon);
        progressDialog.setTitle("更新应用");
        progressDialog.setMessage("正在下载更新");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(false);

        progressDialog.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = DownLoadManager.getFileFromServer(info.getApkUrl(), progressDialog);
                    sleep(3000);
                    installApk(file);
                    progressDialog.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }}.start();
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
