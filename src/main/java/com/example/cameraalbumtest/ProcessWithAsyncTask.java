package com.example.cameraalbumtest;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class ProcessWithAsyncTask extends AsyncTask<Bundle, Void, String> {
    private static final String TAG = "AsyncTask";
    private boolean isValid;
    @Override
    protected String doInBackground(Bundle... params) {
        processFrame(params[0]);
        /*if (isValid)
            return "true";
        else
            return "false";*/
        return "test";
    }
    private void processFrame(Bundle bundle) {
        int width = bundle.getInt("width");
        int height = bundle.getInt("height");
        int scanFrq = bundle.getInt("scanFrq");
        int LEDfrq = bundle.getInt("LEDfrq");
        int code = bundle.getInt("code");
        String key = " ";
        switch (code){
            case 0: key = "0011"; break;
            case 1: key = "0010"; break;
            case 2: key = "0001"; break;
            case 3: key = "0000"; break;
        }
        byte[] data = bundle.getByteArray("img");
        ImgProcess imgPrc = new ImgProcess(bundle.getByteArray("img"), height, width, scanFrq, LEDfrq);
        //isValid = imgPrc.judge(key);
    }
}
