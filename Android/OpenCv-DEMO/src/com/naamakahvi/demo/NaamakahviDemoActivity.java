package com.naamakahvi.demo;

import org.opencv.core.Mat;
import org.opencv.android.Utils;

import com.naamakahvi.demo.R;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NaamakahviDemoActivity extends Activity {
	
	private static final String Tag = "Naamakahvi:Activity";
	ImageView image;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void takeTestPhoto(View v){
        
        image = (ImageView)findViewById(R.id.imageView1);
        
        Log.i(Tag, "Demo started.");
        Camera c = new Camera();
        Mat rslt = c.getFrame();
        Bitmap bmp = Bitmap.createBitmap(rslt.cols(), rslt.rows(), Bitmap.Config.ARGB_8888);

        

        
    	DetectFace df = new DetectFace(rslt, getApplicationContext());
    	rslt = df.getImage();
        try{
        Utils.matToBitmap(rslt, bmp);
        image.setImageBitmap(bmp);
        }catch(Exception e){
        	Log.e(Tag, e.toString());
        }
    	
        c.Release();
        Log.i(Tag, rslt.toString());
        
    }
}