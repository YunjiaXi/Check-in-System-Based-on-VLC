package com.example.cameraalbumtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    public Camera mCamera;
    private Context superContext;
    private int code;
    //process frame type
    private static final int PROCESS_WITH_HANDLER_THREAD = 1;

    private int processType = PROCESS_WITH_HANDLER_THREAD;
    public byte[] img;
    public int width = 0, height = 0, scanFrq = 0;
    //HandlerThread
//    private MainActivity.ImageProcessThread processFrameHandlerThread;
//    private Handler processFrameHandler;




    public CameraPreview(Context context, int tmp) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        superContext = context;
        code = tmp;
//        MainActivity mainActivity = new MainActivity();
//        MainActivity.ImageProcessThread processFrameHandlerThread = mainActivity.new ImageProcessThread();
//        processFrameHandler = new Handler(processFrameHandlerThread.getLooper(), processFrameHandlerThread);
//


    }

    private void openCameraOriginal() {
        try {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
        } catch (Exception e) {
            Log.d(TAG, "camera is not available");
        }
    }

    public Camera getCameraInstance() {
        if (mCamera == null) {
            CameraHandlerThread mThread = new CameraHandlerThread("camera thread");
            synchronized (mThread) {
                mThread.openCamera();
            }
        }
        return mCamera;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        getCameraInstance();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {

//        mCamera.setOneShotPreviewCallback(this);
//        System.out.println("destory!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        mHolder.removeCallback(this);
        mCamera.setOneShotPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    Handler handlerThread;
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        System.out.println("data: " + Arrays.toString(data));
        img = new byte[width * height * 3 / 2];
        img = data;
//        Log.d(TAG, "data: " +data);
        System.out.println("img: " + Arrays.toString(img));
        Camera.Size csize = mCamera.getParameters().getPreviewSize();
        width = csize.width;
        //Log.d(TAG, "width: " +width);
        height = csize.height;
        //Log.d(TAG, "height: " +height);
        scanFrq = mCamera.getParameters().getPreviewFrameRate();
        Log.d(TAG, "scanFrq: " +scanFrq);
        String key="";

        Bundle bundle = new Bundle();
        bundle.putInt("width", width);
        bundle.putInt("height", height);
        bundle.putInt("scanFrq", scanFrq);
        bundle.putInt("LEDfrq", 500);
        bundle.putByteArray("img", img);

        new ProcessWithAsyncTask().execute(bundle);

    }//处理帧数据

    private class CameraHandlerThread extends HandlerThread {
        Handler mHandler;

        public CameraHandlerThread(String name) {
            super(name);
            start();
            mHandler = new Handler(getLooper());
        }

        synchronized void notifyCameraOpened() {
            notify();
        }

        void openCamera() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    openCameraOriginal();
                    notifyCameraOpened();
                }
            });
            try {
                wait();
            } catch (InterruptedException e) {
                Log.w(TAG, "wait was interrupted");
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handlerMain = new Handler() {
        public void handleMessage(Message msg) {
            if ((boolean)msg.obj)
                Toast.makeText(superContext, "Sign-in Success!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(superContext, "Sign-in Failure!", Toast.LENGTH_SHORT).show();
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
                    //ImgProcess imgPrc = new ImgProcess(bundle.getByteArray("img"), height, width, scanFrq, LEDfrq);
                    //isValid = imgPrc.judge(key);
                }
            };
            handlerMain.obtainMessage(1, isValid).sendToTarget();
            Looper.loop();
        }
    }
}

