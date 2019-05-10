package com.example.omer.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.example.omer.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.synnapps.carouselview.CarouselView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import rx.subscriptions.CompositeSubscription;


public class FragmentHome extends Fragment {

    private Context context;
    List<Posts> lstPosts;
    List<Comments> count;

    String txtString;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public String account,location,avtar,date,className,like,comment,shares,title,url_avtar, image;
    public Object post_id ;
    private FragmentHome.OnFragmentInteractionListener mListener;
    private RecyclerView rvPosts;
    private RecyclerPostAdapter postAdapter;
    private SharedPreferences mSharedPreferences;
    private String mToken;
    private List<Posts> mData;
    private String mEmail,fullname;
    private ImageView imageView;
    private CompositeSubscription mSubscriptions;
    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
    }
    public String FragmentHome() {
        // Required empty public constructor
        return image;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedPreferences();

    }
    Gson gson = new GsonBuilder().create();
// use retrofit to create an instance of BookService

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        count = new ArrayList<>();
        int coun = count.size();

        lstPosts = new ArrayList<>();
        Bitmap avatar = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.av1);
        Bitmap p1 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.hotel);
//        Bitmap p2 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.meroe_opener);
//        Bitmap p3 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.car);
//        Bitmap p4 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.camp);
//        Bitmap p5 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.city);
        Bitmap[] post = {p1};
        Bundle b = getArguments();
//        Posts value = mData.get(position);
        if(b != null){

            ArrayList<String> imageURI = getArguments().getStringArrayList("imageURI");
            String postText = getArguments().getString("text");
            int lenght = imageURI.size();
            Bitmap[] uploaded = new Bitmap[lenght];
            for(int i = 0 ; i<= lenght-1 ; i++){
                String imgUri = imageURI.get(i).toString();
                Uri uri = Uri.parse(imgUri);
                File imgFile = new File(imgUri);
                Bitmap myBitmap = null;
                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploaded[i] = myBitmap;
            }

//            lstPosts.add(new Posts(0,post_id,uploaded,avatar,account,date,location,className, coun, like, shares, title));
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.Post)
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

                        JSONObject posts = array.getJSONObject(n);
                        post_id = posts.getString("_id");
                        title = posts.getString("title");
                        date = posts.getString("date");
                        location= posts.getString("location");
                        image = posts.getJSONArray("Image").getString(0);
//                        findImage(posts.getJSONArray("Image").getString(0));
                       System.out.println("Error picasso="+image);


//                        Bitmap[] post = {p1};

                        lstPosts.add(new Posts(n,post_id,post,image,avatar,posts.getString("account"),date,location,className, coun,like, shares, title));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }


        });


        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rvPosts = (RecyclerView)  rootView.findViewById(R.id.rvPost);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);
        postAdapter = new RecyclerPostAdapter(this.getContext(), lstPosts);

//
//        mLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
//        rvPosts.setLayoutManager(mLayoutManager);
        rvPosts.setAdapter(postAdapter);
        // Inflate the layout for this fragment

        return rootView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHome.OnFragmentInteractionListener) {
            mListener = (FragmentHome.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
