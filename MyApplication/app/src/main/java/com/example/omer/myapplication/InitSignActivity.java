package com.example.omer.myapplication;

import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InitSignActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button next;
    public static final String TAG = InitSignActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_sign);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorLoginStatus));
        }
    }

    public void toNormalSign(View arg0) {
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        String pass = password.getText().toString();
        Intent intent = new Intent(InitSignActivity.this,NormalSignActivity.class);
        Bundle b = new Bundle();
        b.putString("password",pass);
        intent.putExtra("user",b);
        startActivity(intent);
//        Toast.makeText(this, pass, Toast.LENGTH_SHORT).show();

    }
}
