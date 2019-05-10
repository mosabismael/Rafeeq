package com.example.omer.myapplication;

import android.content.Context;
import android.content.Intent;
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
 * Created by omer on 4/26/2018.
 */

public class RecyclerItemsAdapter extends RecyclerView.Adapter<RecyclerItemsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Items> mData;

    public RecyclerItemsAdapter(Context mContext, List<Items> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public RecyclerItemsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view= mInflater.inflate(R.layout.card_item,parent,false);
        return new RecyclerItemsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvDesc.setText(mData.get(position).getDesc());
        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.imgItemImage.setImageResource(mData.get(position).getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDesc;
        TextView tvTitle;
        ImageView imgItemImage;

        public MyViewHolder(View itemView) {

            super(itemView);

            tvDesc = (TextView) itemView.findViewById(R.id.txtItemDesc);
            tvTitle = (TextView) itemView.findViewById(R.id.txtItemName);
            imgItemImage = (ImageView) itemView.findViewById(R.id.imgItemImage);

            imgItemImage.setScaleType(ImageView.ScaleType.FIT_XY);
            imgItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mContext.startActivity(new Intent(mContext,BusinessProfile.class));

                }
            });
        }
    }
}
