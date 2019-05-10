package com.example.omer.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.myapplication.utils.Constants;
import com.mikhaellopez.circularimageview.CircularImageView;

public class NormalProfile extends AppCompatActivity implements tabInfo.OnFragmentInteractionListener,FragmentHome.OnFragmentInteractionListener {

    private CircularImageView imgProfile;
    private  TextView Prof_email,Prof_phone,Prof_bio;
    private SharedPreferences mSharedPreferences;
    private String mEmail;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_profile);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));

        imgProfile = (CircularImageView) findViewById(R.id.imgNormalProfile);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PageAdapterNP pagerAdapter = new PageAdapterNP(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        Prof_email = (TextView)v.findViewById(R.id.profEmail);
//        Prof_phone = (TextView)v.findViewById(R.id.profPhone);
//       Prof_bio = (TextView)v.findViewById(R.id.profBio);

                mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
//     Prof_email.setText("");
//                Prof_phone.setText("00000");
//                Toast.makeText(NormalProfile.this, "Yes", Toast.LENGTH_SHORT).show();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorLoginStatus));
        }



    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
