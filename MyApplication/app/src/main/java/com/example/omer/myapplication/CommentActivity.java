package com.example.omer.myapplication;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.omer.myapplication.model.UploadObject;
import com.example.omer.myapplication.model.UploadObject;
import com.example.omer.myapplication.network.NetworkUtil;
import com.example.omer.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CommentActivity extends AppCompatActivity {

    List<Comments> lstItems;
    private StaggeredGridLayoutManager mLayoutManager;
    RecyclerView recList;
    ImageButton btnSendComment;
    EditText etComment;
    public String titlecomment,mName,mEmail,userID;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
        mName = mSharedPreferences.getString(Constants.NAME,"");
        userID = mSharedPreferences.getString(Constants.ID,"");

        lstItems = new ArrayList<>();
                OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.COMMENTS)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                final String myResponse = response.body().string();


                try {

                    JSONObject json = new JSONObject(myResponse);
//
                    JSONArray array = json.getJSONArray("Comment");




//                            txtString = ("First Name: "+json.getJSONObject("data").getString(title));
                    for (int n = 0; n < array.length(); n++){

                        JSONObject comments = array.getJSONObject(n);


//                        post_id = posts.getString("_id");
                        titlecomment = comments.getString("content");

                        //    like = posts.getString("likesId");

                        System.out.println("Out data" + ", " + titlecomment);
//                        lstPosts.add(new Posts(n,post_id,post,avatar,posts.getString("account"),date,location,className, comment, like, shares, title));
                        lstItems.add(new Comments(n,comments.getString("account"),titlecomment,R.drawable.hotel));


                    }




                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        });

//        lstItems.add(new Comments(1,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.hotel));
//        lstItems.add(new Comments(2,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.aff));
//        lstItems.add(new Comments(3,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.dsd));
//        lstItems.add(new Comments(4,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.sdfsdfsdf));
//        lstItems.add(new Comments(5,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.wewwe));
//        lstItems.add(new Comments(6,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.s));

        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        recList.setLayoutManager(llm);

        RecyclerCommentAdapter ca = new RecyclerCommentAdapter(this,lstItems);
        recList.setAdapter(ca);
        btnSendComment = (ImageButton) findViewById(R.id.btnSendComment);
        btnSendComment.setEnabled(false);
        etComment = (EditText) findViewById(R.id.etComment);
        etComment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    btnSendComment.setEnabled(false);
                    btnSendComment.setImageResource(R.drawable.send_disabled);
                } else {
                    btnSendComment.setEnabled(true);
                    btnSendComment.setImageResource(R.drawable.send);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comments coment = new Comments();
                coment.setContent(etComment.getText().toString());
                coment.setAccount(mName);
                coment.setAuthor(userID);
                etComment.setText(null);
//                recList.getAdapter().notifyItemInserted(lstItems.size() - 1);
//                recList.scrollToPosition(lstItems.size() - 1);
                commentProcess(coment);



            }
        });


        View view = findViewById(R.id.activity_comment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        }
        mSubscriptions = new CompositeSubscription();

    }


    public void exitComments(View arg0) {
        finish();
        overridePendingTransition( R.anim.activity_slide_stay, R.anim.activity_slide_out);

    }
    private void commentProcess(Comments comment) {

        mSubscriptions.add(NetworkUtil.getRetrofit().createComment(comment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(UploadObject response) {

        showSnackBarMessage(response.getMessage());



    }

    private void handleError(Throwable error) {


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
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
    private void showSnackBarMessage(String message) {


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition( R.anim.activity_slide_stay, R.anim.activity_slide_out);
    }
}
