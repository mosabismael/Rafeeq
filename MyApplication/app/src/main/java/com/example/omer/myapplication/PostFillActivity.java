package com.example.omer.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.myapplication.model.UploadObject;
import com.example.omer.myapplication.network.NetworkCall;
import com.example.omer.myapplication.network.NetworkUtil;
import com.example.omer.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
public class PostFillActivity extends AppCompatActivity {
    private static final String TAG = PostFillActivity.class.getSimpleName();

    Spinner spnClass,spnArea;
    SpinnerAdapter classAdapter,areaAdapter;
    private ImageButton btnCloseFill;
    private ImageView imgFillPreview;
    private TextView txtPicsNum;
    private Button btnPost;
    MultiAutoCompleteTextView etPostText;
    private CompositeSubscription mSubscriptions;
    private ProgressBar mProgressbar;
    private  String imgUri;
    private  String ImageID;
    private List<Posts>posts;
    private String location ,classes ,fullname ,email;
    private Object id_class,id_clas;
    private SharedPreferences mSharedPreferences;
    private NetworkCall uploadService;
    ArrayList<String> pics;
    List<String> FileImage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_fill);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        email = mSharedPreferences.getString(Constants.EMAIL,"");
        fullname = mSharedPreferences.getString(Constants.NAME,"");
        OkHttpClient clients = new OkHttpClient();
        uploadService = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(clients)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NetworkCall.class);

        List<String> classArray =  new ArrayList<String>();

        List<String> areaArray =  new ArrayList<String>();
        areaArray.add("Khartoum");
        areaArray.add("Meroe");
        areaArray.add("Begrawia");
        areaArray.add("Marrah Mount.");

        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, classArray);
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, areaArray);

        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnClass = (Spinner) findViewById(R.id.spnClass);
        spnClass.setAdapter(classAdapter);
        spnClass.setPrompt("Select Class");


        spnClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                classes = spnClass.getSelectedItem().toString();
//                classes = parent.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.Class)
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
                    JSONArray array = json.getJSONArray("Posts");




                    for (int n = 0; n < array.length(); n++){

                        JSONObject classes = array.getJSONObject(n);
                        classArray.add(classes.getString("title"));
                        id_class = classes.getString("_id");


                    }




                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        });

        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnArea = (Spinner) findViewById(R.id.spnArea);
        spnArea.setAdapter(areaAdapter);
        spnArea.setPrompt("Select Area");
        spnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                location = parent.getItemAtPosition(position).toString();
                // do something upon option selection

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });
        btnCloseFill= (ImageButton) findViewById(R.id.btnCloseFill);
        btnCloseFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgFillPreview = (ImageView) findViewById(R.id.imgFillPreview);
        txtPicsNum = (TextView) findViewById(R.id.txtPicsNum);
        Intent intent = this.getIntent();
        if(intent.getExtras() !=null) {
            pics = intent.getExtras().getStringArrayList("picList");
            Log.d(TAG, "filePathoo=" + pics.get(0).toString());
            imgUri = pics.get(0).toString();

            int lenght = pics.size();
            File imgFile = new File(imgUri);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgFillPreview.setImageURI(Uri.parse(imgUri));
            txtPicsNum.setText("+"+lenght);

        }

        etPostText = (MultiAutoCompleteTextView) findViewById(R.id.etPostText) ;

        btnPost = (Button) findViewById(R.id.btnPost);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadMultiFile();



            }
        });
        mSubscriptions = new CompositeSubscription();


    }
    private void uploadMultiFile() {
        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add(pics.get(0).toString());

        Retrofit retrofit = NetworkUtil.getRetrofitClient(this);
        NetworkCall uploadAPIs = retrofit.create(NetworkCall.class);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
//
//        builder.addFormDataPart("author", ID);
//        builder.addFormDataPart("date", posts.getDate().toString());

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        MultipartBody requestBody = builder.build();
        retrofit2.Call<ResponseBody> call = uploadAPIs.uploadMultiFile(requestBody);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {

            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                Toast.makeText(PostFillActivity.this, "Success " + response.message(), Toast.LENGTH_LONG).show();
                Toast.makeText(PostFillActivity.this, "Success " + response.body().toString(), Toast.LENGTH_LONG).show();

                final String myResponse;
                try {

                    myResponse = response.body().string();
                    JSONObject json = new JSONObject(myResponse);

                     ImageID = json.getJSONObject("FileUpload").getString("_id");
                    String ID = mSharedPreferences.getString(Constants.ID,"");
                    String PostText = etPostText.getText().toString();
                    String Date = DateFormat.getTimeInstance().format(new Date());
                    Posts post = new Posts();
                    post.setAccount(fullname);
                    post.setTitle(PostText);
                    post.setDate(Date);
                    post.setLocation(location);
                    post.setClassName(classes);
                    post.setImageid( ImageID);
                    post.setAuthor(ID);
                    postProcess(post);
                    Intent intent = new Intent(PostFillActivity.this, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putStringArrayList("pic",pics);
                    b.putString("text",etPostText.getText().toString());
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Ouuu 1"+e);

                } catch (JSONException e) {
                    System.out.println("Ouuu 2"+e);

                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Error " + t.getMessage());
            }
        });
    }
    private void postProcess(Posts post) {
        System.out.println("imageID="+FileImage);

//        post.setImageid(FileImage);

        mSubscriptions.add(NetworkUtil.getRetrofit().createPost(post)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(UploadObject response) {

        showSnackBarMessage(response.getMessage());

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
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
    private void showSnackBarMessage(String message) {


    }


}




