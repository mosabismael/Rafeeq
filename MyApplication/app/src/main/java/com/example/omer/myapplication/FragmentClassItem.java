package com.example.omer.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.omer.myapplication.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentClassItem.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentClassItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentClassItem extends Fragment {


    public  List<Items> lstItems;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private StaggeredGridLayoutManager mLayoutManager;

    private OnFragmentInteractionListener mListener;

    public FragmentClassItem() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentClassItem.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentClassItem newInstance(String param1, String param2) {
        FragmentClassItem fragment = new FragmentClassItem();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lstItems = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ClassItem)
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

                        JSONObject classItem = array.getJSONObject(n);
                        //        lstItems.add(new Items(1,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.hotel));

                        lstItems.add(new Items(n,classItem.getString("name"),"text",R.drawable.hotel));
//                        lstItems.add(new Items(R.drawable.hotel,classItem.getString("title"),n));
//                        lstItems.add(new Items(1,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.hotel));


                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        });

        lstItems.add(new Items(1,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.hotel));
        lstItems.add(new Items(2,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.aff));
        lstItems.add(new Items(3,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.dsd));
        lstItems.add(new Items(4,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.sdfsdfsdf));
        lstItems.add(new Items(5,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.wewwe));
        lstItems.add(new Items(6,"El-Salam Rootana","elsalam rotana is a nice hotel in khartoum that provice verius solutions",R.drawable.s));

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);


        RecyclerView rvItems = (RecyclerView)  rootView.findViewById(R.id.rvItems);
        RecyclerItemsAdapter mAdapter = new RecyclerItemsAdapter(this.getContext(), lstItems);
        mLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        rvItems.setLayoutManager(mLayoutManager);
        rvItems.setAdapter(mAdapter);
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
