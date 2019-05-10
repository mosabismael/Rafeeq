package com.example.omer.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class VideoPlayActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener,
AudioManager.OnAudioFocusChangeListener{

    private MediaPlayer mMediaPlayer;
    private Uri mVideoUri;
    private ImageButton mPlayButton;
    private SurfaceView mSurfaceView;
    private int vis = 0;

    private AudioManager mAudioManager;
    private IntentFilter mNoisyIntentFilter;
    private AudioBecomingNoisy mAudioBecomingNoisy;

    private class AudioBecomingNoisy extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            mediaPause();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_preview);

        setContentView(R.layout.activity_video_play);
        mPlayButton = (ImageButton)findViewById(R.id.btnVideoPlay);
        mSurfaceView = (SurfaceView)findViewById(R.id.svVideo);
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayButton.getVisibility() == View.VISIBLE){
                    mPlayButton.setVisibility(View.GONE);
                }else if(mPlayButton.getVisibility() == View.GONE){
                    mPlayButton.setVisibility(View.VISIBLE);
                }
            }
        });

        Intent callingIntent = this.getIntent();
        if(callingIntent != null){
            mVideoUri = callingIntent.getData();
        }

        mAudioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        mAudioBecomingNoisy = new AudioBecomingNoisy();
        mNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    public void playPauseClick(View view) {
        if(mMediaPlayer.isPlaying()){
            mediaPause();
        }else {
            mediaPlay();
        }
    }

    @Override
    protected void onStop() {
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onStop();
    }

    @Override
    protected void onPause() {

        super.onPause();
        mediaPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mMediaPlayer!= null){
            mediaPlay();
        }else{
            SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
            surfaceHolder.addCallback(this);
        }
    }

    private void mediaPlay(){
        registerReceiver(mAudioBecomingNoisy, mNoisyIntentFilter);
        int requestAudioFocusResult = mAudioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(requestAudioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mMediaPlayer.start();
            mPlayButton.setImageResource(R.drawable.pause);
        }

    }

    private void mediaPause(){
        mMediaPlayer.pause();
        mPlayButton.setImageResource(R.drawable.play);
        mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mMediaPlayer = MediaPlayer.create(this, mVideoUri, holder);
        mMediaPlayer.setOnCompletionListener(this);
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();

        //Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        //Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();

        //Set the width of the SurfaceView to the width of the screen
        lp.width = screenWidth;

        //Set the height of the SurfaceView to match the aspect ratio of the video
        //be sure to cast these as floats otherwise the calculation will likely be 0
        lp.height = (int) (((float)videoHeight / (float)videoWidth) * (float)screenWidth);

        //Commit the layout parameters
        mSurfaceView.setLayoutParams(lp);
        mediaPlay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayButton.setImageResource(R.drawable.play);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mediaPause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                mediaPlay();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                mediaPause();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
