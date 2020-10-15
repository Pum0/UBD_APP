package com.ubd_app.Ride.Guide;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.ubd_app.Ride.DriveLocationCheck;

public class SpeedDpHandler extends Handler {

    private TextView rideSpeed;
    private DriveLocationCheck driveLocationCheck;

    public SpeedDpHandler(TextView rideSpeed, DriveLocationCheck driveLocationCheck){
        this.rideSpeed = rideSpeed;
        this.driveLocationCheck = driveLocationCheck;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case (0):
                double speed = driveLocationCheck.getSpeed();
                rideSpeed.setText(String.format("%.1f km", speed));
                break;
        }
    }

}
