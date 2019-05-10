package com.example.omer.myapplication;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MediaStoreAdapter extends RecyclerView.Adapter<MediaStoreAdapter.ViewHolder> {
    private Cursor mediaStoreCursor;
    private final Activity mActivity;
    private OnClickThumbListener mOnClickThumbListener;

    public interface OnClickThumbListener{
        void OnClickImage(Uri imageUri);
        void OnClickVideo(Uri videoUri);

    }

    public MediaStoreAdapter(Activity activity){
        this.mActivity = activity;
        this.mOnClickThumbListener = (OnClickThumbListener)activity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_image_view,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(mActivity)
                .load(getUriFromMediastore(position))
                .centerCrop()
                .override(96,96)
                .into(holder.getmImageView());

    }

    @Override
    public int getItemCount() {
        return (mediaStoreCursor == null ? 0 : mediaStoreCursor.getCount());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView mImageView;
        public ViewHolder(View itemView){
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.imageThumbnail);
            mImageView.setOnClickListener(this);
        }

        public ImageView getmImageView(){
            return mImageView;
        }

        @Override
        public void onClick(View v) {
            getOnClickUri(getAdapterPosition());
        }
    }

    private Cursor swapCursor(Cursor cursor){
        if(mediaStoreCursor == cursor){
            return null;
        }

        Cursor oldCursor = mediaStoreCursor;
        this.mediaStoreCursor=cursor;
        if(cursor != null){
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public void changeCursor(Cursor cursor){
        Cursor oldCusor = swapCursor(cursor);
        if(oldCusor != null){
            oldCusor.close();
        }
    }

    private Bitmap getBitmapFromMEdiaStore(int position){
        int idIndex = mediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        int mediaTypeIndex = mediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);

        mediaStoreCursor.moveToPosition(position);
        switch (mediaStoreCursor.getInt(mediaTypeIndex)){
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                return MediaStore.Images.Thumbnails.getThumbnail(
                        mActivity.getContentResolver(),
                        mediaStoreCursor.getLong(idIndex),
                        MediaStore.Images.Thumbnails.MICRO_KIND,null
                );
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                return MediaStore.Video.Thumbnails.getThumbnail(
                        mActivity.getContentResolver(),
                        mediaStoreCursor.getLong(idIndex),
                        MediaStore.Video.Thumbnails.MICRO_KIND,null
                );
                default:
                    return null;

        }
    }

    private Uri getUriFromMediastore(int position){
        int dataIndex = mediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        mediaStoreCursor.moveToPosition(position);
        String dataString = mediaStoreCursor.getString(dataIndex);
        Uri mediaUri = Uri.parse("file://"+dataString);
        return  mediaUri;
    }

    private void getOnClickUri(int position){
        int mediaTypeIndex = mediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
        int dataIndex = mediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        mediaStoreCursor.moveToPosition(position);

        String dataString = mediaStoreCursor.getString(dataIndex);
        Uri mediaUri = Uri.parse("file://"+dataString);

        switch (mediaStoreCursor.getInt(mediaTypeIndex)){
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                mOnClickThumbListener.OnClickImage(mediaUri);
                break;
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                mOnClickThumbListener.OnClickVideo(mediaUri);
                break;
            default:
        }
    }
}
