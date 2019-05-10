package com.example.omer.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.example.omer.myapplication.model.UploadObject;
import com.example.omer.myapplication.network.NetworkUtil;
import com.example.omer.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link tabInfo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link tabInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tabInfo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String bio = "bio";
    public static final String phone = "phone";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2,mEmail;
    private TextView profile_email,profile_phone,profile_location,profile_bio;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;

    private OnFragmentInteractionListener mListener;

    public tabInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tabInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static tabInfo newInstance(String param1, String param2) {
        tabInfo fragment = new tabInfo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mSubscriptions = new CompositeSubscription();
        loadProfile();



    }
    private void loadProfile() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
         mEmail = mSharedPreferences.getString(Constants.EMAIL,"");

        mSubscriptions.add(NetworkUtil.getRetrofit().getProfile(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }
    private void handleResponse(Normal user) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(tabInfo.bio,user.getBio());
        editor.putString(tabInfo.phone,user.getPhone());

        editor.apply();

    }

    private void handleError(Throwable error) {


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                UploadObject response = gson.fromJson(errorBody,UploadObject.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String Bio = mSharedPreferences.getString(tabInfo.bio,"");
        String Phone = mSharedPreferences.getString(tabInfo.phone,"");


        View view = inflater.inflate(R.layout.fragment_tab_info, container, false);
         profile_email = (TextView)view.findViewById(R.id.profEmail);
        profile_phone = (TextView)view.findViewById(R.id.profPhone);
        profile_bio = (TextView)view.findViewById(R.id.profBio);
        profile_location = (TextView)view.findViewById(R.id.profLocation);
        profile_email.setText(mEmail);
        profile_phone.setText(Phone.toString());
        profile_bio.setText(Bio.toString());
        return  view;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
