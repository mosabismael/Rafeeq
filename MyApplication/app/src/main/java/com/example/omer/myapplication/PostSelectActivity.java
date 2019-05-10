package com.example.omer.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import retrofit2.Response;
import com.example.omer.myapplication.model.UploadObject;
import com.example.omer.myapplication.network.NetworkCall;
import com.example.omer.myapplication.network.NetworkUtil;
import com.example.omer.myapplication.utils.Constants;
import com.jess.ui.TwoWayGridView;
import okhttp3.logging.HttpLoggingInterceptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
public class PostSelectActivity extends AppCompatActivity {
    private static final String TAG = PostSelectActivity.class.getSimpleName();
    private  Uri mImageUri;
    ImageButton btnOpenCamera,btnCloseSelect;
    private Button btnAddImages,btnAddVideos,btnToFill;
    ImageView imgFiller;
    private static final int PICK_IMAGE_MULTIPLE = 200;
    String imageEncoded;
    ArrayList<String> imagesEncodedList;
    private TwoWayGridView gvGallery;
    private GalleryAdapter galleryAdapter;
    ArrayList<Uri> mArrayUri;
    ArrayList<String> uris ;
    String uri_base;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_select);
        askPermissions();
        mArrayUri = new ArrayList<Uri>();
        uris = new ArrayList<String>();
        imagesEncodedList = new ArrayList<String>();



        btnToFill = (Button) findViewById(R.id.btnToFill) ;
        btnToFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                for (Uri uri: mArrayUri) {
                    String uriString = uri.toString();
                    uris.add(i,uriString);
                    i++;
                    // do whatever you want with your string
                }
                Intent intent = new Intent(PostSelectActivity.this, PostFillActivity.class);
                Bundle b = new Bundle();
                b.putStringArrayList("picList",imagesEncodedList);
                intent.putExtras(b);
                startActivity(intent);

            }
        });

        imgFiller = (ImageView) findViewById(R.id.imgFiller);
        btnOpenCamera = (ImageButton) findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostSelectActivity.this, Camera.class));
            }
        });

        btnAddImages = (Button) findViewById(R.id.btnAddImages);
        btnAddVideos = (Button) findViewById(R.id.btnAddVideos);
        gvGallery = (TwoWayGridView) findViewById(R.id.gv);

        btnAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
        btnAddVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"), 0);
            }
        });

        btnCloseSelect = (ImageButton) findViewById(R.id.btnCloseSelect);
        btnCloseSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostSelectActivity.this, MainActivity.class));
                finish();
            }
        });

    }
    private void askPermissions() {
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {

                imgFiller.setVisibility(View.GONE);
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                     mImageUri = data.getData();
                    String uri_base = getRealPathFromURIPath(mImageUri, PostSelectActivity.this);
                    // Get the cursor
                    this.uri_base = uri_base;
                    Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    imagesEncodedList.add(0, imageEncoded);
                    cursor.close();

                    mArrayUri.add(mImageUri);

                    galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                    gvGallery.setAdapter(galleryAdapter);
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery.getLayoutParams();
                    mlp.setMargins(0, 1, 0, 0);
                    InputStream is = getContentResolver().openInputStream(data.getData());

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor

                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);

                            imagesEncodedList.add(i, imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(1);
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery.getLayoutParams();
                            mlp.setMargins(0, 1, 0, 0);

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            }
        else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
                imgFiller.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }

        return true;
    }
    }
