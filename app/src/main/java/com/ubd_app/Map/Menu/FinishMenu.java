package com.ubd_app.Map.Menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ubd_app.AppDatabase.DriveRecode;
import com.ubd_app.Main;
import com.ubd_app.OnBackPressedListener;
import com.ubd_app.R;
import com.ubd_app.Ride.DriveLocationCheck;
import com.ubd_app.Ride.RideRecordObject;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FinishMenu extends Fragment implements OnBackPressedListener {

    private Main main;
    private DriveLocationCheck driveLocationCheck;
    private RideRecordObject rideRecordObject;
    private RequestRESTUpload requestRESTUpload;

    //화면 구성을 위한 변수
    private View view;

    private Button FinishButton;
    private TextView distance, finishTime, AvgSpeedView, requiredTime;

    public FinishMenu(DriveLocationCheck driveLocationCheck) {
        this.driveLocationCheck = driveLocationCheck;
        this.rideRecordObject = driveLocationCheck.getRideRecordObject();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.main = (Main) getActivity();

        //업로드를 위한 객체 생성
        requestRESTUpload = new RequestRESTUpload(rideRecordObject.toString());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_ride_finish, container, false);

        distance = view.findViewById(R.id.distance_finish);
        finishTime = view.findViewById(R.id.finishTime_finish);
        AvgSpeedView = view.findViewById(R.id.calorie_finish);
        requiredTime = view.findViewById(R.id.requiredTime_finish);

        FinishButton = view.findViewById(R.id.finish_button);

        distance.setText(ConveterDistance(rideRecordObject.getTotalRideDis()));
        requiredTime.setText(requiredTime(rideRecordObject.getTotalRideTime()));
        finishTime.setText(getTime());
        AvgSpeedView.setText(rideRecordObject.getSpeedAvg() + " Km/h");

        FinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                requestRESTUpload.execute();
                try {
                    rideRecordObject.upload();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                main.driveRecordeViewModel.save(rideRecordObject.PushRecorde());
                main.setDriveMap(1);
                main.rideFinishMenu(false, driveLocationCheck);

            }
        });

        return view;
    }

    @Override
    public void onBackPressed() {
        FinishButton.performClick();
    }


    private String ConveterDistance(int dis){
        if (dis > 1000){
            return String.format("%.2f Km", (double) dis/1000);
        }else {
            return String.valueOf(dis) + "m";
        }
    }

    private String requiredTime(int sec){
        if (sec < 60){
            return "1분 미만";
        }else if (sec < 3600){
            return ((sec%3600)/60) + "분";
        }else {
            return (sec/3600) + "시간 "+ ((sec%3600)/60) + "분";
        }
    }

    private String getTime(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        SimpleDateFormat simpleDate = new SimpleDateFormat("hh시 mm분");
        return simpleDate.format(cal.getTime());
    }
}
