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

public class RecyclerCommentAdapter extends RecyclerView.Adapter<RecyclerCommentAdapter.MyViewHolder>{
    private Context mContext;
    private List<Comments> mData;

    public RecyclerCommentAdapter(Context mContext, List<Comments> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public RecyclerCommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view= mInflater.inflate(R.layout.card_comment,parent,false);
        return new RecyclerCommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerCommentAdapter.MyViewHolder holder, int position) {

        holder.tvContent.setText(mData.get(position).getContent());
        holder.tvaccount.setText(mData.get(position).getAccount());
        holder.imgItemImage.setImageResource(mData.get(position).getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        TextView tvaccount;
        ImageView imgItemImage;

        public MyViewHolder(View itemView) {

            super(itemView);

            tvContent = (TextView) itemView.findViewById(R.id.txtComment);
            tvaccount = (TextView) itemView.findViewById(R.id.txtCommenterName);
            imgItemImage = (ImageView) itemView.findViewById(R.id.imgCommenterImage);

            imgItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mContext.startActivity(new Intent(mContext,NormalProfile.class));

                }
            });
        }
    }
}