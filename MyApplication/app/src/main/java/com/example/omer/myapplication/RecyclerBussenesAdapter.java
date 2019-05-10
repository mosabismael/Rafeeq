package com.example.omer.myapplication;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by omer on 6/2/2018.
 */

public class RecyclerBussenesAdapter extends RecyclerView.Adapter<RecyclerBussenesAdapter.MyViewHolder> {
    private Context mContext;
    private List<Bussinses> mData;

    public RecyclerBussenesAdapter(Context mContext, List<Bussinses> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public RecyclerBussenesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view= mInflater.inflate(R.layout.card_class,parent,false);
        return new RecyclerBussenesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerBussenesAdapter.MyViewHolder holder, int position) {

        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.imgClassImage.setImageResource(mData.get(position).getThumbnail());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView imgClassImage;

        public MyViewHolder(View itemView) {

            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.txtClassName);
            imgClassImage = (ImageView) itemView.findViewById(R.id.imgClassImage);

            imgClassImage.setScaleType(ImageView.ScaleType.FIT_XY);
            imgClassImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Fragment fragment = new FragmentClassItem();
                    FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.main_frame,fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });
        }
    }
}
