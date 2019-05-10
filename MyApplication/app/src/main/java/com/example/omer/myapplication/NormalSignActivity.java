package com.example.omer.myapplication;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.myapplication.model.UploadObject;
import com.example.omer.myapplication.network.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class NormalSignActivity extends AppCompatActivity {
    public static final String TAG = NormalSignActivity.class.getSimpleName();

    private AutoCompleteTextView mEtName;
    private AutoCompleteTextView mEtEmail;
    private EditText mEtBio;
    private EditText mEtPhone;
    private Button mBtRegister;
    private TextView mTvLogin;
    public String password;

    private ProgressBar mProgressbar;
    private CompositeSubscription mSubscriptions;
    private Spinner spnCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_sign);

        spnCountries =(Spinner) findViewById(R.id.spnCounties);
        String[] items = new String[]{"1", "2", "three"};
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.countries_Array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCountries.setPrompt("Select your country!");
        spnCountries.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorLoginStatus));
        }
        mSubscriptions = new CompositeSubscription();
        Intent i = getIntent();
        Bundle b = i.getBundleExtra("user");
         password = b.getString("password");
        initViews();

    }
    private void initViews() {

        mEtName = (AutoCompleteTextView) findViewById(R.id.name);
        mEtEmail = (AutoCompleteTextView) findViewById(R.id.emai);
        mEtBio = (EditText) findViewById(R.id.bio);
        mEtPhone = (EditText) findViewById(R.id.phone);

        mBtRegister = (Button) findViewById(R.id.email_sign_in_button);
//        mTvLogin = (TextView) findViewById(R.id.tv_login);

        mBtRegister.setOnClickListener(view -> register());
//        mTvLogin.setOnClickListener(view -> goToLogin());
    }

    private void register() {


        String name = mEtName.getText().toString();
        String email = mEtEmail.getText().toString();
        String bio = mEtBio.getText().toString();
        String phone = mEtPhone.getText().toString();



        int err = 0;


        if (err == 0) {

            Normal user = new Normal();
            user.setFullName(name);
            user.setEmail(email);
            user.setBio(bio);
            user.setPhone(phone);
            user.setPassword(password);



            registerProcess(user);

        } else {

            showSnackBarMessage("Enter Valid Details !");
        }
    }

    private void showSnackBarMessage(String message) {
//        if (getView() != null) {
//
//            Snackbar.make(getView(),message,Snackbar.LENGTH_SHORT).show();
//        }
    }

    private void registerProcess(Normal user) {

        mSubscriptions.add(NetworkUtil.getRetrofit().createNormals(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(UploadObject response) {

        showSnackBarMessage(response.getMessage());
//        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NormalSignActivity.this,LoginActivity.class);
        startActivity(intent);    }

    private void handleError(Throwable error) {

        mProgressbar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                UploadObject response = gson.fromJson(errorBody,UploadObject.class);
                showSnackBarMessage(response.getMessage());


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showSnackBarMessage("Network Error !");


        }
    }


//    private void goToLogin(){
//    private void goToLogin(){
//
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        LoginFragment fragment = new LoginFragment();
//        ft.replace(R.id.fragmentFrame, fragment, LoginFragment.TAG);
//        ft.commit();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
