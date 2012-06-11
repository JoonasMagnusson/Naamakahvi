package com.naamakahvi.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.util.Log;

public class DetectFace {
	
    private static final String Tag = "Naamakahvi:DetectFace";
    private CascadeClassifier cascades;
    private Mat result;
    private Mat temp;
    private MatOfRect box;
    
    
	public DetectFace(Mat image,Context context){
		
		result = image.clone();
		temp = image.clone();
		try{
            InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
           
            cascades = new CascadeClassifier(cascadeFile.getAbsolutePath());
                       
            cascadeFile.delete();
            cascadeDir.delete();
            
			Log.i(Tag, "Loaded Cascades");
		}catch(Exception e){
			Log.e(Tag, e.getMessage());
		}
		
	Imgproc.cvtColor(temp, temp, Imgproc.COLOR_BGR2GRAY);
	Imgproc.equalizeHist(temp, temp);
	box = detect(temp);
	
    for (Rect r : box.toArray())
        Core.rectangle(result, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
	
	Log.i(Tag, box.toString());	
	}
	
	public Mat getImage(){
		return result;
	}
	
	public int getNumberOfFaces(){
		return box.cols();
	}
	
	private MatOfRect detect(Mat det){
		
		int height = det.rows();
        int faceSize = Math.round(height * 0.2f);
		
		MatOfRect rslt = new MatOfRect();
		cascades.detectMultiScale(det, rslt, 1.1, 2, 2, new Size(faceSize, faceSize), new Size());
		
		
	return rslt;
	}
}
