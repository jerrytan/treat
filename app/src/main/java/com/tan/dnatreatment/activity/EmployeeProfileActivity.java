package com.tan.dnatreatment.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tan.dnatreatment.R;
import com.tan.dnatreatment.dao.EmployeeInfo;
import com.tan.dnatreatment.dao.MyApplication;

import org.w3c.dom.Text;

public class EmployeeProfileActivity extends AppCompatActivity {

    private static final int REQUEST_EXIT = 1 ;

    private Button mEmployeeLogin;
    private ImageView mLoginImage;
    private MyApplication mApplication;
    private TextView mWelcomeLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);
        mWelcomeLogin = (TextView)findViewById(R.id.login_welcome);
        mEmployeeLogin = (Button) findViewById(R.id.employee_login);
        mLoginImage = (ImageView)findViewById(R.id.login_image);
        mApplication = (MyApplication)getApplication();
        setLoginStatus();
    }

    private void setLoginStatus() {

        EmployeeInfo employeeInfo = mApplication.getEmployeeInfo();


        if (employeeInfo == null) {
            mWelcomeLogin.setText(getResources().getText(R.string.welcome_login));
            mEmployeeLogin.setText(getResources().getText(R.string.login));
            mEmployeeLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EmployeeProfileActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_EXIT);
                }
            });
            mLoginImage.setImageResource(R.drawable.login);
        }
        else {
            mWelcomeLogin.setText(getResources().getText(R.string.welcome_employee));
            mEmployeeLogin.setText(getResources().getText(R.string.logout));
            mEmployeeLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mApplication.setEmployeeInfo(null);
                    finish();
                }
            });
            TextView employeeName = (TextView)findViewById(R.id.login_employee_name);
            employeeName.setText(employeeInfo.getName());
            mLoginImage.setImageResource(R.drawable.logout);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                setLoginStatus();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_profile, menu);
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
