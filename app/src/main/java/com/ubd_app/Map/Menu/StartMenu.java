package com.ubd_app.Map.Menu;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ubd_app.Main;
import com.ubd_app.Map.Map;
import com.ubd_app.Map.Route.Route;
import com.ubd_app.OnBackPressedListener;
import com.ubd_app.R;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StartMenu extends Fragment implements OnBackPressedListener {

    private Main main;

    private Route route;

    private View view;

    private Button StartButton, CancleButton;
    private TextView distanceT, averageT, estimatedDemandT;

    private double Distance;

    public StartMenu(){

    }
    public StartMenu(Map map){
        this.route = map.getRoute();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.main = (Main)getActivity();
        this.Distance = route.getTotalDistance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_ride_start, container, false);

        StartButton = view.findViewById(R.id.rideStart);
        CancleButton = view.findViewById(R.id.rideCancle);
        distanceT = view.findViewById(R.id.distance_start);
        averageT = view.findViewById(R.id.averageTime_start);
        estimatedDemandT = view.findViewById(R.id.estimatedDemandTime_start);

        distanceT.setText(String.format("%.2f Km", Distance / 1000));
        averageT.setText(getaverageTime(Distance));

        estimatedDemandT.setText(getEstimatedDemandTime(Distance));

        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setMapFragment(1);
                main.setDriveMap(0);
                main.rideStartMenu(1);
            }
        });

        CancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.map.delMarker();
                main.rideStartMenu(1);
            }
        });

        return view;
    }

    //예상 소모 시간
    public String getaverageTime(double distance) {
        int estimatedTime = (int)(((distance / 1000) / 15) * 3600);

        return String.format("%d시 %d분", estimatedTime / 3600, (estimatedTime / 60) % 60);
    }

    //도착시간 계산
    public String getEstimatedDemandTime(double distance) {
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        String[] time = getaverageTime(distance).split(" ");

        for (int i =0;i< time.length;i++){
            time[i] = time[i].substring(0,time[i].length()-1);
        }

        calendar.setTime(date);

        calendar.add(Calendar.MINUTE, Integer.valueOf(time[1]));
        calendar.add(Calendar.HOUR, Integer.valueOf(time[0]));

        SimpleDateFormat simpleDate = new SimpleDateFormat("hh시 mm분");

        if (Calendar.HOUR > 23)
            return "새벽 " + simpleDate.format(calendar.getTime());
        else
            return simpleDate.format(calendar.getTime());
    }

    @Override
    public void onBackPressed() {
        CancleButton.performClick();
    }
}
