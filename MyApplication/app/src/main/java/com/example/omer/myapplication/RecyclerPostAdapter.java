package com.example.omer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.myapplication.model.UploadObject;
import com.example.omer.myapplication.network.NetworkCall;
import com.example.omer.myapplication.network.NetworkUtil;
import com.example.omer.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by omer on 4/23/2018.
 */

public class RecyclerPostAdapter extends RecyclerView.Adapter<RecyclerPostAdapter.MyViewHolder> {

    private Context mContext;
    private List<Posts> mData;
    private List<Comments> count;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private String image;
    CarouselView carouselView;

    Bitmap[] sampleImages;


    public RecyclerPostAdapter(Context mContext, List<Posts> mData ) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        view= mInflater.inflate(R.layout.card_post,parent,false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        sampleImages = mData.get(position).getImage();
        Posts value = mData.get(position);

        if (mData.get(position).getID() == 0) {
            holder.frameLoading.setVisibility(View.VISIBLE);
            holder.frameLoading.getBackground().setAlpha(180);
            holder.frameComments.setVisibility(View.GONE);
        }
        holder.tvPostTitle.setText(value.getTitle());
        holder.tvAccountName.setText(value.getAccount());
        holder.tvPostLocation.setText(mData.get(position).getLocation());

        holder.tvPostDate.setText(mData.get(position).getDate());
        holder.tvPostLikes.setText(mData.get(position).getLikes());
//        holder.tvPostComments.setText(mData.get(position).getComments());
        holder.tvPostShares.setText(mData.get(position).getShares());
        holder.imgPostAccount.setImageBitmap(mData.get(position).getProfile());
        holder.carouselView.setPageCount(sampleImages.length);

        holder.carouselView.setImageListener(imageListener);
        holder.carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Bitmap img = sampleImages[position];
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap newImg = getResizedBitmap(img, 1000);
                newImg.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                //Create intent
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra("title", "");
                intent.putExtra("image", byteArray);

                //Start details activity
                mContext.startActivity(intent);
            }
        });

        holder.txtOptions.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.txtOptions);
                popupMenu.inflate(R.menu.postcard_options);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.crdmnuSave:
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.apply();
                                String ID = mSharedPreferences.getString(Constants.ID, "");
                                Save post = new Save();
                                post.setUser(ID);
                                post.setPost(value.getIDPost().toString());
                                postSave(post);


                                Toast.makeText(mContext, "Saved", Toast.LENGTH_LONG).show();

                                break;

                            case R.id.crdmnuDelete:
                                mData.remove(position);
                                notifyItemRemoved(position);
                                System.out.println("Error" + value.getIDPost());
                                delete(value.getIDPost().toString());
                                Toast.makeText(mContext, "Deleted", Toast.LENGTH_LONG).show();
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                Toast.makeText(mContext, "Clicked @ " + position, Toast.LENGTH_LONG).show();

            }

        });


    }
    public int getItemCount() {

        return mData.size();

    }

    public void addAll(List<Posts> list) {
        if (list.size() > 0) {
            mData.clear();
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {


                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder().url(Constants.Image).build();

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

                            JSONArray array = json.getJSONArray("FileUpload");
                            for (int n = 0; n < array.length(); n++) {
                                JSONObject posts = array.getJSONObject(n);

                                if(posts.getString("_id").toString().equals(mData.get(position).getImageId().toString())) {
                                    image = posts.getJSONObject("file").get("url").toString();


                                }


//                        Bitmap[] post = {p1};


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }

                });

            Picasso.with(mContext).load("http://192.168.43.58:3000"+image).placeholder(R.drawable.hotel).into(imageView);

//            imageView.setImageBitmap(sampleImages[position]);
        }
    };


    public  class MyViewHolder extends RecyclerView.ViewHolder {

        FrameLayout frameLoading;
        RelativeLayout frameComments;
        TextView tvPostTitle;
        CarouselView carouselView;
        ImageView imgPostAccount;
        TextView tvAccountName;
        TextView tvPostLocation;
        TextView tvPostDate;
        TextView tvPostLikes;
        TextView tvPostComments;
        TextView tvPostShares;
        RelativeLayout lIntraction;

        TextView txtOptions;


        public MyViewHolder(View itemView) {

            super(itemView);
            frameComments = (RelativeLayout)itemView.findViewById(R.id.lIntraction);
            frameLoading = (FrameLayout)itemView.findViewById(R.id.frameLoading);
            tvPostTitle = (TextView) itemView.findViewById(R.id.tvPostTitle);
            tvAccountName = (TextView) itemView.findViewById(R.id.tvAccountName);
            tvPostLocation = (TextView) itemView.findViewById(R.id.tvPostLocation);
            tvPostDate = (TextView) itemView.findViewById(R.id.tvPostDate);
            tvPostLikes = (TextView) itemView.findViewById(R.id.tvPostLikes);
            tvPostComments = (TextView) itemView.findViewById(R.id.tvPostComments);
            tvPostShares = (TextView) itemView.findViewById(R.id.tvPostShares);
            carouselView = (CarouselView) itemView.findViewById(R.id.carouselView);
            imgPostAccount = (ImageView) itemView.findViewById(R.id.imgPostAccount);
            lIntraction = (RelativeLayout) itemView.findViewById(R.id.lIntraction);

            txtOptions = (TextView) itemView.findViewById(R.id.txtOptions);




        }



    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    public void postSave(Save post) {
        mSubscriptions = new CompositeSubscription();
        mSubscriptions.add(NetworkUtil.getRetrofit().savePost(post)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }
    public void delete(String postID){
        mSubscriptions = new CompositeSubscription();
        mSubscriptions.add(NetworkUtil.getRetrofit().deletePost(postID)
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
    private void showSnackBarMessage(String message) {


    }
}
