package com.tan.dnatreatment.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tan.dnatreatment.R;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mNameView;
    private EditText mPhoneView;
    private EditText mPasswdView;
    private View mProgressView;
    private View mLoginFormView;

    private TextView mEmployeeName;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mNameView = (AutoCompleteTextView) findViewById(R.id.name);
        mPhoneView = (EditText) findViewById(R.id.phone);
        mPasswdView = (EditText)findViewById(R.id.password);

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mPhoneView.setError(null);
        mPasswdView.setError(null);

        // Store values at the time of the login attempt.
        String employee_name = mNameView.getText().toString();
        String employee_phone = mPhoneView.getText().toString();
        String employee_password = mPasswdView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!employee_password.equals(getString(R.string.employee_password))) {
            mPasswdView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswdView;
            cancel = true;
        }
        else {
            if (TextUtils.isEmpty(employee_name) ) {
                mNameView.setError(getString(R.string.error_field_required));
                focusView = mNameView;
                cancel = true;
            } else if (!isNameValid(employee_name)) {
                mNameView.setError(getString(R.string.error_invalid_name));
                focusView = mNameView;
                cancel = true;
            }
            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(employee_phone) && !isPhoneValid(employee_phone)) {
                mPhoneView.setError(getString(R.string.error_invalid_phone));
                focusView = mPhoneView;
                cancel = true;
            }
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(employee_name, employee_phone);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private boolean isNameValid(String name) {
        //TODO: Replace this with your own logic
        return name.length() >= 2;
    }

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
         // return Integer.
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(phone);
        return (m.matches());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mPhone;

        UserLoginTask(String name, String phone) {
            mName = name;
            mPhone = phone;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            HttpURLConnection connection = null;

            try {
                URL url = new URL(APIConfig.REGISTER_EMPLOYEE_URL);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                String writeParams ="name="+URLEncoder.encode(mName,"UTF-8")+"&phone="+mPhone;
                out.writeBytes(writeParams);
                //out.writeUTF(writeParams);
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
                String employeeId = jsonObject.getString("eid");
                Log.d("Employee Register","status is " + requestStatus);
                Log.d("Employee Register", "id is " + employeeId);
                if (requestStatus.equals("OK")) {
                    EmployeeInfo employeeInfo = new EmployeeInfo(employeeId,mName,mPhone);
                    MyApplication myApplication = (MyApplication)getApplication();
                    myApplication.setEmployeeInfo(employeeInfo);
                }

                // Simulate network access.
                Thread.sleep(100);
            } catch (Exception e) {
                return false;
            }finally {
                if (connection !=null) {
                    connection.disconnect();
                }
            }




            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                setResult(RESULT_OK, null);
                finish();
            } else {
                mPhoneView.setError(getString(R.string.error_incorrect_password));
                mPhoneView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
