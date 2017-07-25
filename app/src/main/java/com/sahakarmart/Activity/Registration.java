package com.sahakarmart.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sahakarmart.Service.ConnectionDetector;
import com.sahakarmart.R;
import com.sahakarmart.Service.Utils;

import cz.msebera.android.httpclient.Header;

import static com.sahakarmart.Service.Utils.FIREBASEREGID;
import static com.sahakarmart.Service.Utils.MY_PREFS_NAME;
import static com.sahakarmart.Service.Utils.Register_Preference;
import static com.sahakarmart.Service.Utils.UserRegister;

public class Registration extends AppCompatActivity {

    private static final String TAG = Registration.class.getSimpleName();
    SharedPreferences prefs;
    String restoretoken;
    SharedPreferences.Editor editor;
    Long unixtime;
    ConnectionDetector cd;
    private EditText inputName, inputLName, inputPassword, inputConfirmPwd, inputContact, inputEmail;
    private Button btnRegister;
    private TextView txtLogin;
    private AutoCompleteTextView CityautocompleteView;
    private SharedPreferences RegisterPrefences;
    private String address_1 = "address";
    // India Country id from open cart db
    private String countryid = "99";
    private String firstname, lastname, telephone, password, confirm, city, email, dummyemail, registrationId, regID;
    private String regId = "";
    // Maharashtra Zone id from open cart db
    private String zone_id = "1493";
    private Integer agree = 1;
    private ProgressBar progressBar;
    private RequestParams params = new RequestParams();
    private ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        RegisterPrefences = getSharedPreferences(Register_Preference, Context.MODE_PRIVATE);

        // getting token from shared preference
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

//        restoretoken = prefs.getString(TOKEN_KEY, null);

        String[] cityArray = getResources().getStringArray(R.array.city_array);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please Wait...");


        registrationId = prefs.getString(FIREBASEREGID, "");


        // Declare AutoComplete adapter and design of autocomplete textview  first then auto complete text view intailsation
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cityArray);


        // Edit Text
        inputName = (EditText) findViewById(R.id.input_name);
        inputLName = (EditText) findViewById(R.id.input_lname);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputConfirmPwd = (EditText) findViewById(R.id.input_confirm_password);
        inputContact = (EditText) findViewById(R.id.input_contact);
        inputEmail = (EditText) findViewById(R.id.input_email);
        // inputCity=(TextInputEditText)findViewById(R.id.input_city);

        // button
        btnRegister = (Button) findViewById(R.id.btn_register);

        //TextView
//        txtLogin = (TextView) findViewById(R.id.txtPleaseLogin);

        // AutoCompleteTextView
        CityautocompleteView = (AutoCompleteTextView) findViewById(R.id.autoComplete_City);


        progressBar = (ProgressBar) findViewById(R.id.registerProgress);


        CityautocompleteView.setAdapter(adapter);

        CityautocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Log.i("Main", "Clicked on Item ");
                city = (String) parent.getItemAtPosition(position);

            }
        });
        boolean isUserRegister = RegisterPrefences.getBoolean(UserRegister, false);

        if (isUserRegister == true) {
            Intent next = new Intent(this, HomeActivity.class);
            startActivity(next);
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                firstname = inputName.getText().toString();
                lastname = inputLName.getText().toString();
                password = inputPassword.getText().toString();
                confirm = inputConfirmPwd.getText().toString();
                email = inputEmail.getText().toString();
                telephone = inputContact.getText().toString();

                unixtime = System.currentTimeMillis() / 1000L;

                dummyemail = unixtime.toString();

              // if connected to internet

                if (cd.isConnectingToInternet(getApplicationContext())) {


                    if ((TextUtils.isEmpty(CityautocompleteView.getText().toString()))) {
                        city = "city";
                    }

                    // create dummy email if  user not entered email id using unix time
                    if ((TextUtils.isEmpty(inputEmail.getText().toString()))) {
                        email = dummyemail + "@sahakarmart.com";
                        email.trim();

                    }

                    if ((TextUtils.isEmpty(inputName.getText().toString())) || (TextUtils.isEmpty(inputLName.getText().toString())) || (TextUtils.isEmpty(inputContact.getText().toString())) || (TextUtils.isEmpty(inputPassword.getText().toString())) || (TextUtils.isEmpty(inputConfirmPwd.getText().toString()))) {
                        Snackbar.make(btnRegister, "Please enter all registration details", Snackbar.LENGTH_LONG).show();
                    }

                    if ((!TextUtils.isEmpty(inputConfirmPwd.getText().toString())) && (!TextUtils.isEmpty(inputPassword.getText().toString()))) {
                        if (!confirm.equals(password)) {
                            Snackbar.make(btnRegister, "Password Doesn't Match", Snackbar.LENGTH_LONG).show();
                        }
                    }


                    if ((!TextUtils.isEmpty(inputName.getText().toString())) && (!TextUtils.isEmpty(inputLName.getText().toString())) && (!TextUtils.isEmpty(inputContact.getText().toString())) && (!TextUtils.isEmpty(inputPassword.getText().toString())) && (!TextUtils.isEmpty(inputConfirmPwd.getText().toString()))) {

                        if ((!TextUtils.isEmpty(inputConfirmPwd.getText().toString())) && (!TextUtils.isEmpty(inputPassword.getText().toString()))) {


                            if (!confirm.equals(password)) {
                                Snackbar.make(btnRegister, "Password Doesn't Match", Snackbar.LENGTH_LONG).show();
                            }

                            if ((!(password.length() >= 4)) || (!(confirm.length() >= 4))) {
                                Snackbar.make(btnRegister, "Password must be between 4 and 20 characters!", Snackbar.LENGTH_LONG).show();

                            }

                            if (!(telephone.length() >= 10)) {
                                Snackbar.make(btnRegister, "Phone Number must be between 10 and 12 digits", Snackbar.LENGTH_LONG).show();

                            }

                            if (!(lastname.length() >= 3)) {
                                Snackbar.make(btnRegister, "Lastname must be more than 3 characters!", Snackbar.LENGTH_LONG).show();
                            }

                            if (!(firstname.length() >= 3)) {
                                Snackbar.make(btnRegister, "Firstname must be more than 3 characters!", Snackbar.LENGTH_LONG).show();
                            }

                            if ((confirm.equals(password)) && (telephone.length() >= 10) && (password.length() >= 4) && (confirm.length() >= 4) && (firstname.length() >= 3) && (lastname.length() >= 3)) {

                                inputName.setEnabled(false);
                                inputLName.setEnabled(false);
                                inputContact.setEnabled(false);
                                inputPassword.setEnabled(false);
                                inputConfirmPwd.setEnabled(false);
                                inputEmail.setEnabled(false);
                                progressBar.setVisibility(View.VISIBLE);
                                // for Register on.net side for notifications

                                prgDialog.show();
                                prgDialog.setCanceledOnTouchOutside(false);
                                prgDialog.setCancelable(false);


                                regID = FirebaseInstanceId.getInstance().getToken();
                                final String devid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                final String mobileno = inputContact.getText().toString();
                                final String fname = inputName.getText().toString();
                                final String lname = inputLName.getText().toString();

                                params.put("flag", "R");
                                params.put("regId", regID);
                                params.put("devId", devid);
                                params.put("mobile", mobileno);
                                params.put("fname", fname);
                                params.put("lname", lname);

                                final int DEFAULT_TIMEOUT = 70 * 1000;

                                // Make RESTful webservice call using AsyncHttpClient object
                                AsyncHttpClient client = new AsyncHttpClient();
                                client.setTimeout(DEFAULT_TIMEOUT);

                                client.post(Utils.APP_SERVER_URL, params,
                                        new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                                                SharedPreferences.Editor editor = RegisterPrefences.edit();
                                                editor.putBoolean(UserRegister, true);
                                                editor.commit();


                                                // prgDialog.hide();
                                                if (prgDialog != null) {
                                                    prgDialog.dismiss();
                                                }


                                                Toast.makeText(getApplicationContext(), "Register Successful..!!", Toast.LENGTH_SHORT).show();
                                                Intent next = new Intent(Registration.this, HomeActivity.class);
                                                startActivity(next);
                                                finish();


                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable error) {


                                                inputName.setEnabled(true);
                                                inputLName.setEnabled(true);
                                                inputContact.setEnabled(true);
                                                inputPassword.setEnabled(true);
                                                inputConfirmPwd.setEnabled(true);
                                                inputEmail.setEnabled(true);
                                                progressBar.setVisibility(View.GONE);


                                                // prgDialog.hide();
                                                if (prgDialog != null) {
                                                    prgDialog.dismiss();
                                                }
                                                // When Http response code is '404'
                                                if (statusCode == 404) {
                                                    Toast.makeText(getApplicationContext(), "Internet Required . Please Try Later.", Toast.LENGTH_LONG).show();
                                                }
                                                // When Http response code is '500'
                                                else if (statusCode == 500) {
                                                    Toast.makeText(getApplicationContext(), "Internet Required . Please Try Later.", Toast.LENGTH_LONG).show();
                                                }
                                                // When Http response code other than 404, 500
                                                else {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Internet Required . Please Try Later.",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                            }

                        }

                    }

                } else {

                    Snackbar.make(btnRegister, "Please turn on your mobile data or wifi ", Snackbar.LENGTH_LONG).show();
                }


            }
        });


    }


}
