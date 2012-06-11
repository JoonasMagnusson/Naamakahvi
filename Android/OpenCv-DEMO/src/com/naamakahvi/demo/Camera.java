package com.naamakahvi.demo;


import java.util.List;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.highgui.Highgui;

public class Camera {
	
	private static final String Tag = "Naamakahvi:Camera";
		
		VideoCapture androidCamera = null;
	
	
	public Camera(){
		
		try{
			//The Device? Front camera
			androidCamera = new VideoCapture(Highgui.CV_CAP_ANDROID+1);
			androidCamera.set(Highgui.CV_CAP_PROP_ANDROID_WHITE_BALANCE,Highgui.CV_CAP_ANDROID_WHITE_BALANCE_AUTO);
			
            List<Size> sizes = androidCamera.getSupportedPreviewSizes();
            androidCamera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, sizes.get(sizes.size()-1).width);
            androidCamera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, sizes.get(sizes.size()-1).height);
			Log.i(Tag, "Got Camera:");
			Log.i(Tag, sizes.toString());
			
		}catch(Exception e){
			Log.i(Tag,e.toString());
		}

	}
	
	public Mat getFrame(){

		Mat mFrame = new Mat();
		
		//Throw away bad frames
		for (int i=0;i<10;i++){
			
			//needs to be synchronized, otherwise will fail
			synchronized(this){
			if (androidCamera.isOpened()){
				if(androidCamera.grab()){
					androidCamera.retrieve(mFrame, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
					Log.i(Tag,"Got frame");
				}else {
					Log.e(Tag, "No Frame");
				}
			}else {
				Log.e(Tag, "No Device");
				}
			}
	}
		Core.flip(mFrame, mFrame, 1);
		return mFrame;
	}
	
	public void Release(){
        androidCamera.release();
        androidCamera = null;
	}

}
