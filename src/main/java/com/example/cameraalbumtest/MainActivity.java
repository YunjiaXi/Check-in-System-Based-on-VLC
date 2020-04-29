package com.example.cameraalbumtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.util.Log;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.FrameLayout;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    String TAG;
    Handler handlerThread;
    public int code = 0;
    CameraPreview mPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] places = getResources().getStringArray(R.array.places);
                Toast.makeText(MainActivity.this, "你选择的是:"+places[pos], Toast.LENGTH_SHORT).show();
                code = pos;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        Button buttonStartPreview = (Button) findViewById(R.id.button_start_preview);
        buttonStartPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPreview();
            }
        });
        Button buttonStopPreview = (Button) findViewById(R.id.button_stop_preview);
        buttonStopPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPreview();
            }
        });
    }

    public void startPreview() {
        mPreview = new CameraPreview(this, code);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);


    }

    public void stopPreview() {
        /*String key="";
        Bundle bundle = new Bundle();
        bundle.putInt("width", mPreview.width);
        bundle.putInt("height", mPreview.height);
        bundle.putInt("scanFrq", mPreview.scanFrq);
        bundle.putInt("LEDfrq", 500);
        bundle.putString("key", key);
        bundle.putByteArray("img", mPreview.img);
        new ImageProcessThread().start();
        Message msg = handlerThread.obtainMessage();
        msg.setData(bundle);
        handlerThread.sendMessage(msg);*/
        mPreview.mCamera.setOneShotPreviewCallback(mPreview);
//        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
//        preview.removeAllViews();
    }

    public void onPause() {
        finish();
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                if (status == LoaderCallbackInterface.SUCCESS) {
                    Log.d(TAG, "onManagerConnected: success");
                } else {
                    super.onManagerConnected(status);
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handlerMain = new Handler() {
        public void handleMessage(Message msg) {
            if ((boolean)msg.obj)
                Toast.makeText(MainActivity.this, "Sign-in Success!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Sign-in Failure!", Toast.LENGTH_SHORT).show();
        }
    };

    class ImageProcessThread extends Thread {

        Boolean isValid = false;

        @SuppressLint("HandlerLeak")
        public void run() {
            Looper.prepare();
            Handler handlerThread = new Handler() {
                public void handleMessage(Message msg) {
                    Bundle bundle = msg.getData();
                    int width = bundle.getInt("width");
                    int height = bundle.getInt("height");
                    int scanFrq = bundle.getInt("scanFrq");
                    int LEDfrq = bundle.getInt("LEDfrq");
                    String key = " ";
                    switch (code){
                        case 0: key = "0011"; break;
                        case 1: key = "0010"; break;
                        case 2: key = "0001"; break;
                        case 3: key = "0000"; break;
                    }
                    ImgProcess imgPrc = new ImgProcess(bundle.getByteArray("img"), height, width, scanFrq, LEDfrq);
                    isValid = imgPrc.judge(key);
                }
            };
            handlerMain.obtainMessage(1, isValid).sendToTarget();
            Looper.loop();
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    //Log.i(TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

}


