package com.example.omer.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.omer.myapplication.bot.ChatHeadService;
import com.example.omer.myapplication.bot.Utils;
import com.example.omer.myapplication.model.UploadObject;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.omer.myapplication.network.NetworkUtil;
import com.example.omer.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        FragmentClasses.OnFragmentInteractionListener,FragmentHome.OnFragmentInteractionListener,
        FragmentNews.OnFragmentInteractionListener, FragmentTop.OnFragmentInteractionListener,
        FragmentCamera.OnFragmentInteractionListener, FragmentClassMain.OnFragmentInteractionListener,
        FragmentBussniss.OnFragmentInteractionListener,FragmentClassItem.OnFragmentInteractionListener,
        MediaStoreAdapter.OnClickThumbListener{

    private BottomNavigationView mBottomNav;
    public  FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FrameLayout mMainFrame;
    private Toolbar appBar;
    int likeToggle = 0;
    private ImageView imgLike;
    private TextView name,TV_email,Prof_email;
    private ProgressBar mProgressbar;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private String mToken;
    private String mEmail;
    private String mTvName;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mBottomNav.setSelectedItemId(R.id.nav_home);

//        mBottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);



        //region drawer init

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawerOpen, R.string.drowerClose);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(lst_StartService);




        appBar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

        /* the button action is in method "onOptionsItemSelected"  */

        //endregion

        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        loadProfile();

        final FragmentNews fNews = new FragmentNews();
        final FragmentTop fLeaderboard = new FragmentTop();
        final FragmentHome fHome = new FragmentHome();
        final FragmentClassMain fClasses = new FragmentClassMain();



        Intent intent = this.getIntent();
        if(intent.getExtras() !=null) {
            ArrayList<String> pic = intent.getExtras().getStringArrayList("pic");
            String text = intent.getExtras().getString("text");
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("imageURI",pic);
            bundle.putString("text",text);
            fHome.setArguments(bundle);

        }

        setFragment(fHome);


        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mBottomNav.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_home:
                        setFragment(fHome);
//                        startActivity(new Intent(MainActivity.this,FragmentHome.class));
                        fab.setVisibility(View.VISIBLE);
                        return true;

                    case R.id.nav_camera:
                        startActivity(new Intent(MainActivity.this, PostSelectActivity.class));
                        finish();
                        return true;

                    case R.id.nav_news:
                        setFragment(fNews);
                        return true;

                    case R.id.nav_leader:
                        setFragment(fLeaderboard);
                        return true;

                    case R.id.nav_classes:
                        setFragment(fClasses);
                        return true;

                    default:
                        return false;

                }
            }


        });


        View view = findViewById(R.id.activity_main);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        }


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        Intent i = getIntent();
//        Bundle b = i.getBundleExtra("login");
//        email_pass = b.getString("email");

    }


    Button.OnClickListener lst_StartService = new FloatingActionButton.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(Utils.canDrawOverlays(MainActivity.this))
//                startChatHead();
            showChatHeadMsg();
            else{
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG);
            }

        }

    };



    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }
    private void startChatHead(){
        System.out.println("Error out:");
        startService(new Intent(MainActivity.this, ChatHeadService.class));
    }

    public void nameClick(View arg0) {
        startActivity(new Intent(MainActivity.this,NormalProfile.class));

    }

    public void picClick(View arg0) {
        startActivity(new Intent(MainActivity.this,NormalProfile.class));

    }
    public void openComments(View arg0){
        Intent comments = new Intent(MainActivity.this, CommentActivity.class);
        startActivity(comments);
        overridePendingTransition( R.anim.activity_slide_in, R.anim.activity_slide_stay );
    }
    public void likeClick(View arg0){
        imgLike  =(ImageView)findViewById(R.id.imgPostLike);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.heart_pulse);
        imgLike.startAnimation(pulse);
        pulse.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(likeToggle == 0){
                    imgLike.setImageResource(R.drawable.heart_fill);
                    likeToggle = 1;
                }else{
                    imgLike.setImageResource(R.drawable.heart_empty);
                    likeToggle = 0;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.drawer_logot: {
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.set_login(false);
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                finish();
                break;
            }

            case R.id.drawer_about: {

            }
            case R.id.drawer_saved: {
                startActivity(new Intent(getBaseContext(), SavedActivity.class));
                finish();
            }
        }
        //close navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void OnClickImage(Uri imageUri) {
        String path = imageUri.getPath();

        Intent intent=new Intent(MainActivity.this, CameraPreview.class);
        Bundle bundle=new Bundle();
        bundle.putString("path",path);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void OnClickVideo(Uri videoUri) {
        Intent videoIntent = new Intent(this, VideoPlayActivity.class);
        videoIntent.setData(videoUri);
        startActivity(videoIntent);
    }
    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
        mTvName = mSharedPreferences.getString(Constants.NAME,"");

    }

    private void loadProfile() {

        mSubscriptions.add(NetworkUtil.getRetrofit().getProfile(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }
    private void handleResponse(Normal user) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.ID,user.getID());
        editor.putString(Constants.EMAIL,user.getEmail());
        editor.putString(Constants.NAME,user.getFullname());


        editor.apply();

        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
        mTvName = mSharedPreferences.getString(Constants.NAME,"");
        String ID = mSharedPreferences.getString(Constants.ID,"");


        name = (TextView) findViewById(R.id.dtName);
        TV_email = (TextView) findViewById(R.id.dtEmail);
        name.setText(mTvName);
        TV_email.setText(mEmail);
    }

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

    private void showSnackBarMessage(String message) {

        Snackbar.make(findViewById(R.id.activity_main),message,Snackbar.LENGTH_SHORT).show();

    }
    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    private void showChatHeadMsg(){
        java.util.Date now = new java.util.Date();
        String str = "Hi"+mTvName+" welcome to Rafeeq  " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);

        Intent it = new Intent(MainActivity.this, ChatHeadService.class);
        it.putExtra(Utils.EXTRA_MSG, str);
        startService(it);
    }
    private void needPermissionDialog(final int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You need to allow permission");
        builder.setPositiveButton("OK",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        requestPermission(requestCode);
                    }
                });
        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD) {
            if (!Utils.canDrawOverlays(MainActivity.this)) {
                needPermissionDialog(requestCode);
            }else{
                startChatHead();
            }

        }else if(requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG){
            if (!Utils.canDrawOverlays(MainActivity.this)) {
                needPermissionDialog(requestCode);
            }else{
                showChatHeadMsg();
            }

        }

    }

}
