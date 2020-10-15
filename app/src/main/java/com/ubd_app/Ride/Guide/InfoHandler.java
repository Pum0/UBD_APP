package com.ubd_app.Ride.Guide;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.ubd_app.Main;
import com.ubd_app.Ride.DriveLocationCheck;


import java.util.concurrent.ThreadPoolExecutor;

import static com.ubd_app.UBDLibrary.TextConverter.*;


public class InfoHandler extends Handler {

    private Main main;
    private InfoDrawable arrows;

    private int Distance = 0;
    private int Direction = 0;

    private TextView textView;
    private ImageView imageView;

    private int distance = 0;

    public InfoHandler() {

    }

    public InfoHandler(Main main, ImageView imageView, TextView textView) {
        this.main = main;
        this.textView = textView;
        this.imageView = imageView;
        this.arrows = new InfoDrawable(main);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case (1):
                setDistance(msg.arg1,msg.arg2);
                break;

        }
    }

    private void setDistance(int direction, int distance){
        Drawable drawable = arrows.getDrawable(direction);
        Drawable drawable1 = arrows.getDrawable(11);
        Log.d("lll 핸들러", String.valueOf(distance));
        if (direction == 201){
            if (30>distance) {
                imageView.setImageDrawable(drawable);
                textView.setText(ConverterDistance(distance));
            }else {
                imageView.setImageDrawable(drawable1);
                textView.setText("목적지 근처");
            }
            textView.setText(ConverterDistance(distance));
        }else {
            imageView.setImageDrawable(drawable);
            textView.setText(ConverterDistance(distance));
        }
    }
}
