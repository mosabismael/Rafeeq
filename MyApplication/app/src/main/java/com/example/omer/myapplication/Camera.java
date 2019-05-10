package com.example.omer.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Camera extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final int REQUEST_STORAGE_PERMISSION_RESULT = 1;
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private int mCaptureState = STATE_PREVIEW;
    private TextureView mtextureView;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupCamera(width, height);
            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };


    private CameraDevice mCameraDEvice;
    private  CameraDevice.StateCallback mCameradeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDEvice = camera;
            mMediaRecorder = new MediaRecorder();
            if(isReecording) {
                try {
                    createVideoFileNAme();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecording();

                mMediaRecorder.start();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                        mChronometer.setVisibility(View.VISIBLE);
                        mChronometer.start();
                    }
                });
            } else {
                startPreview();
            }
            // Toast.makeText(getApplicationContext(),
            //         "Camera connection made!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDEvice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDEvice = null;
        }
    };
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private int mCameraLensFacingDirection = 1;
    private String mCameraID ;
    private Size mPreviewSize;
    private Size mVideoSize;
    private Size mImageSize;
    private ImageReader mImageReader;
    private Image img;
    private final  ImageReader.OnImageAvailableListener mOnImageAvailableListner = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            img = reader.acquireLatestImage();
            mBackgroundHandler.post(new ImageSaver(img));

            if(mImageFileName != null){
                Intent intent=new Intent(Camera.this, CameraPreview.class);
                Bundle bundle=new Bundle();
                bundle.putString("path",mImageFileName);
                intent.putExtras(bundle);
                startActivity(intent);
            }





        }
    };
    private CameraCaptureSession mPreviewCaptureSession;
    private CameraCaptureSession.CaptureCallback mPreviewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult captureResult) {
            switch (mCaptureState) {
                case STATE_PREVIEW:
                    // Do nothing
                    break;
                case STATE_WAIT_LOCK:
                    mCaptureState = STATE_PREVIEW;

                    if(mCameraLensFacingDirection == CameraCharacteristics.LENS_FACING_FRONT){


                        startStillCaptureRequest();

                    }else{
                        Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                        if(afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                                afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {

                            startStillCaptureRequest();
                        }

                    }
                    break;

            }
        }



        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            process(result);



        }
    };

    private CameraCaptureSession mRecordCaptureSession;
    private CameraCaptureSession.CaptureCallback mRecordCaptureCallback = new
            CameraCaptureSession.CaptureCallback() {

                private void process(CaptureResult captureResult) {
                    switch (mCaptureState) {
                        case STATE_PREVIEW:
                            // Do nothing
                            break;
                        case STATE_WAIT_LOCK:
                            mCaptureState = STATE_PREVIEW;

                            if(mCameraLensFacingDirection == CameraCharacteristics.LENS_FACING_FRONT){


                                startStillCaptureRequest();


                            }else{
                                Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                                if(afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                                        afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {

                                    startStillCaptureRequest();
                                }

                            }
                            break;


                    }
                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);

                    process(result);
                }
            };


    private MediaRecorder mMediaRecorder;
    private int mTotalRotation;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    private ImageButton mRecaordImageButton;
    private ImageButton mStillImageCapture;
    private ImageButton mSwitchCamera;
    private CardView crdCapture;
    private CardView crdRecord;
    private boolean isReecording = false;

    private File mVideoFolder;
    private String mVideoFileName;
    private File mImageFolder;
    private String mImageFileName;


    Intent cameraIntent;

    private class ImageSaver implements Runnable {

        private final Image mImage;

        public ImageSaver(Image image) {
            mImage = image;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(mImageFileName);
                fileOutputStream.write(bytes);


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();

                Intent mediaStoreUpdateIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaStoreUpdateIntent.setData(Uri.fromFile(new File(mImageFileName)));
                sendBroadcast(mediaStoreUpdateIntent);
                if(fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }



    private Chronometer mChronometer;

    private static SparseIntArray ORIENTATION = new SparseIntArray();
    static {
        ORIENTATION.append(Surface.ROTATION_0,90);
        ORIENTATION.append(Surface.ROTATION_90,0);
        ORIENTATION.append(Surface.ROTATION_180,270);
        ORIENTATION.append(Surface.ROTATION_270,180);
    }

    private static  class CompareSizeByArea implements Comparator<Size> {

        @Override
        public int compare(Size o1, Size o2) {
            return Long.signum((long) o1.getHeight() * o2.getHeight() / (long) o2.getWidth() * o1.getHeight());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        createVideoFolder();
        createImageFolder();

        mMediaRecorder= new MediaRecorder();

        mChronometer = (Chronometer) findViewById(R.id.chronometer) ;
        mtextureView = (TextureView) findViewById(R.id.textureView);

        mSwitchCamera = (ImageButton) findViewById( R.id.btnSwitch);
        mSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReecording = false;
                switchCamera();
            }
        });

        crdCapture = (CardView) findViewById(R.id.crdCapture);
        crdCapture.getBackground().setAlpha(51);

        crdRecord = (CardView) findViewById(R.id.crdRecord);
        crdRecord.getBackground().setAlpha(51);

        mRecaordImageButton = (ImageButton) findViewById(R.id.btnRecord);
        mRecaordImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReecording){
                    mChronometer.stop();
                    mChronometer.setVisibility(View.INVISIBLE);
                    isReecording= false;
                    mRecaordImageButton.setImageResource(R.drawable.start_record);
                    mRecaordImageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    mRecaordImageButton.setBackgroundColor(Color.TRANSPARENT);
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();

                    Intent mediaStoreUpdateIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaStoreUpdateIntent.setData(Uri.fromFile(new File(mVideoFileName)));
                    sendBroadcast(mediaStoreUpdateIntent);
                    startPreview();

                    mSwitchCamera.setVisibility(View.VISIBLE);
                }else{
                    checkWriteStoragePermission();
                    mSwitchCamera.setVisibility(View.GONE);

                }
            }
        });

        mStillImageCapture = (ImageButton) findViewById(R.id.btnCapture);
        mStillImageCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation scale = new ScaleAnimation(1,2,1,2, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                scale.setDuration(200);

                mStillImageCapture.startAnimation(scale);


                cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                lockFocus();


            }

        });






    }

    @Override
    protected void onResume() {
        super.onResume();

        startBackgroundThread();;
        if(mtextureView.isAvailable()){
            setupCamera(mtextureView.getWidth(), mtextureView.getHeight());
            connectCamera();
        }else {
            mtextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    protected void onPause() {
        closeCamera();

        stopBackgroundThread();
        super.onPause();
    }




    @Override
    public void onWindowFocusChanged(boolean hasFoucus) {

        super.onWindowFocusChanged(hasFoucus);
        View decorView = getWindow().getDecorView();
        if (hasFoucus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }

    }



    private void switchCamera() {
        if (mCameraLensFacingDirection == CameraCharacteristics.LENS_FACING_BACK) {
            mCameraLensFacingDirection = CameraCharacteristics.LENS_FACING_FRONT;
            closeCamera();
            reopenCamera();

        } else if (mCameraLensFacingDirection == CameraCharacteristics.LENS_FACING_FRONT) {
            mCameraLensFacingDirection = CameraCharacteristics.LENS_FACING_BACK;
            closeCamera();
            reopenCamera();
        }
    }

    private void reopenCamera() {
        if (mtextureView.isAvailable()) {
            setupCamera(mtextureView.getWidth(), mtextureView.getHeight());
            connectCamera();
        } else {
            mtextureView.setSurfaceTextureListener(mSurfaceTextureListener);
            connectCamera();
        }
    }

    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics
                        = cameraManager.getCameraCharacteristics(cameraId);

                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing != mCameraLensFacingDirection) {
                    continue;
                }
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
                int rotatedWidth = width;
                int rotatedHeight = height;
                if(swapRotation) {
                    rotatedWidth = height;
                    rotatedHeight = width;
                }
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);
                mImageSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(), ImageFormat.JPEG, 1);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListner, mBackgroundHandler);
                mCameraID = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private  void connectCamera(){
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    cameraManager.openCamera(mCameraID, mCameradeviceStateCallback, mBackgroundHandler);
                }else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                        Toast.makeText(this, "This Action requires Camera Permission", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[] {Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_PERMISSION_RESULT);
                }
            }else {
                cameraManager.openCamera(mCameraID, mCameradeviceStateCallback, mBackgroundHandler);
            }


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startRecording(){
        try {
            setUpMediaRecorder();
            SurfaceTexture surfaceTexture = mtextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(),mPreviewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            Surface recordSurface = mMediaRecorder.getSurface();
            mCaptureRequestBuilder = mCameraDEvice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCaptureRequestBuilder.addTarget(recordSurface);

            mCameraDEvice.createCaptureSession(Arrays.asList(previewSurface, recordSurface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mRecordCaptureSession = session;
                            try {
                                mRecordCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),null,null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(getApplicationContext(), "configuration failed", Toast.LENGTH_SHORT).show();
                        }
                    },null);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void startPreview(){
        SurfaceTexture surfaceTexture = mtextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(),mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);

        try {
            mCaptureRequestBuilder = mCameraDEvice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);

            mCameraDEvice.createCaptureSession(Arrays.asList(previewSurface,mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewCaptureSession= session;
                            try {
                                mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),
                                        null, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(getApplicationContext(),"Unalbe To Set Camera Preview",Toast.LENGTH_SHORT).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startStillCaptureRequest() {
        try {
            if(isReecording) {
                mCaptureRequestBuilder = mCameraDEvice.createCaptureRequest(CameraDevice.TEMPLATE_VIDEO_SNAPSHOT);
            } else {
                mCaptureRequestBuilder = mCameraDEvice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            }






            mCaptureRequestBuilder.addTarget(mImageReader.getSurface());

            int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics cameraCharacteristics
                    = cameraManager.getCameraCharacteristics(mCameraID);
            mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);


            mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mTotalRotation);

            CameraCaptureSession.CaptureCallback stillCaptureCallback = new
                    CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                            super.onCaptureStarted(session, request, timestamp, frameNumber);

                            try {
                                createImageFileName();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

            if(isReecording) {
                mRecordCaptureSession.capture(mCaptureRequestBuilder.build(), stillCaptureCallback, null);
            } else {
                mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(), stillCaptureCallback, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CAMERA_PERMISSION_RESULT){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Application Will Run Without Camera Services", Toast.LENGTH_SHORT).show();
            }
            if(grantResults[1] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Application Will Run Without Audio Services", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == REQUEST_STORAGE_PERMISSION_RESULT){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isReecording = true;
                mRecaordImageButton.setImageResource(R.drawable.stop_record);
                mRecaordImageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mRecaordImageButton.setBackgroundColor(Color.TRANSPARENT);
                try {
                    createVideoFileNAme();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getApplicationContext(), "This Action Needs Storage Permission To Save Media", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void closeCamera(){
        if(mCameraDEvice != null){
            mCameraDEvice.close();
            mCameraDEvice = null;
        }
    }

    private void startBackgroundThread(){
        mBackgroundHandlerThread = new HandlerThread("CameraTest");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());

    }

    private void stopBackgroundThread(){
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int sensorToDeviceRotation(CameraCharacteristics c, int deviceOrientation){
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN) return 0;
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;

        // Reverse device orientation for front-facing cameras
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) deviceOrientation = -deviceOrientation;

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;

        return jpegOrientation;
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int hieght){
        List<Size> bigEnough = new ArrayList<Size>();
        for(Size option : choices){
            if(option.getHeight() == option.getWidth() * hieght / width && option.getWidth() >= width && option.getHeight() >= hieght){
                bigEnough.add(option);
            }
        }
        if(bigEnough.size()> 0){
            return Collections.min(bigEnough, new CompareSizeByArea());
        }else{
            return choices[0];
        }

    }

    private File createVideoFileNAme() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String prepend = "VIDEO_"+timeStamp+"_";
        File videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
        mVideoFileName = videoFile.getAbsolutePath();

        return videoFile;
    }

    private void checkWriteStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                isReecording = true;
                mRecaordImageButton.setImageResource(R.drawable.stop_record);
                mRecaordImageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mRecaordImageButton.setBackgroundColor(Color.TRANSPARENT);
                try {
                    createVideoFileNAme();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecording();
                mMediaRecorder.start();
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.setVisibility(View.VISIBLE);
                mChronometer.start();
            }else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "This Action requires Strorage Permission", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION_RESULT);
            }
        }else {
            isReecording = true;
            mRecaordImageButton.setImageResource(R.drawable.stop_record);
            mRecaordImageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mRecaordImageButton.setBackgroundColor(Color.TRANSPARENT);
            try {
                createVideoFileNAme();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startRecording();
            mMediaRecorder.start();

        }
    }

    private void createVideoFolder(){
        File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        mVideoFolder = new File(movieFile,"Rafeeq");
        if(!mVideoFolder.exists()){
            mVideoFolder.mkdirs();
        }
    }

    private void createImageFolder() {
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mImageFolder = new File(imageFile, "Rafeeq");
        if(!mImageFolder.exists()) {
            mImageFolder.mkdirs();
        }
    }

    private File createImageFileName() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "IMAGE_" + timestamp + "_";
        File imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
        mImageFileName = imageFile.getAbsolutePath();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        return imageFile;
    }

    private void setUpMediaRecorder() throws  IOException{


        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mVideoFileName);
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOrientationHint(mTotalRotation);

        if(mCameraLensFacingDirection == CameraCharacteristics.LENS_FACING_FRONT){
            mMediaRecorder.setVideoSize(1920, 1080);
            mMediaRecorder.setVideoFrameRate(15);
        }else{
            mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
            mMediaRecorder.setVideoFrameRate(30);
        }
        mMediaRecorder.prepare();
    }

    private void lockFocus() {
        mCaptureState = STATE_WAIT_LOCK;
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
        try {
            if(isReecording) {
                mRecordCaptureSession.capture(mCaptureRequestBuilder.build(), mRecordCaptureCallback, mBackgroundHandler);

            } else {
                mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(), mPreviewCaptureCallback, mBackgroundHandler);

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
