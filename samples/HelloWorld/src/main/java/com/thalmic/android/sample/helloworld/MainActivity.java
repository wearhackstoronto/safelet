package com.thalmic.android.sample.helloworld;



import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.util.Log;



public class MainActivity extends Activity  {
    TextView appTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        int pad_titleTop = dpToPix(55) ;
        int pad_titleBtm = dpToPix(5) ;
        int pad_buttonTB = dpToPix(10) ;

        appTitle = (TextView)findViewById(R.id.appTitle);
        appTitle.setPadding(0,pad_titleTop,0,pad_titleBtm);	//int left, int top, int right, int bottom
       // Typeface face=Typeface.createFromAsset(getAssets(), "fonts/LoraRegular.ttf");
       // appTitle.setTypeface(face, Typeface.BOLD);
        appTitle.setTextColor(getResources().getColor(R.color.white));


        //bindService(new Intent(this, OpenSpatialService.class), mOpenSpatialServiceConnection, BIND_AUTO_CREATE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    public int dpToPix(int dp){

        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (dp * scale + 0.5f);

        return padding_in_px;
    }

    public void startScreen(View view){
        Intent getFull = new Intent(getApplicationContext(), HelloWorldActivity.class);
        startActivity(getFull);
    }






}

