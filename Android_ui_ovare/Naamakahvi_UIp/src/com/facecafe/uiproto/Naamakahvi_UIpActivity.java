package com.facecafe.uiproto;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Naamakahvi_UIpActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void avaaTunnistus(View v) {
    	setContentView(R.layout.tunnistus);
    }
    
    public void avaaKayttajalista(View v) {
    	setContentView(R.layout.kayttajalista);
    }
    
    public void avaaMain(View v) {
    	setContentView(R.layout.main);
    }
    
    public void avaaTuotelista(View v) {
    	setContentView(R.layout.tuotelista);
    }
    public void avaaUusiKayttaja(View v) {
    	setContentView(R.layout.uusi_kayttaja);
    }
}