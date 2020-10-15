package com.ubd_app.Ride.Guide;

import android.os.Handler;

public class SpeedDpThread extends Thread{

    private boolean ThreadCheck = true;

    private Handler handler;

    public SpeedDpThread(Handler SpeedCheckHandler){
        this.handler = SpeedCheckHandler;
    }

    @Override
    public void run() {
        super.run();
        while (ThreadCheck) {
            try {
                handler.sendEmptyMessage(0);
                Thread.sleep(500);
            } catch (Exception e) {

            }
        }
    }

    public void setThreadCheck(boolean check){
        this.ThreadCheck = check;
    }
}
