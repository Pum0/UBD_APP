package com.ubd_app.bottomMenu.driveRecord;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.*;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.MarkerIcons;
import com.ubd_app.AppDatabase.DriveRecode;
import com.ubd_app.Main;
import com.ubd_app.OnBackPressedListener;
import com.ubd_app.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class recordFragment extends Fragment implements View.OnClickListener, OnBackPressedListener, OnMapReadyCallback {

    private Main main;

    //View 구성요소
    private TextView Date, Start, Finish, Mile, Time, Avg, High, Kcal;
    private ImageButton imageButton;
    private DriveRecode driveRecodes;

    recordFragment(DriveRecode driveRecode){
        this.driveRecodes = driveRecode;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        main = (Main)getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.record_fragment);

        if (mapFragment == null) {
            NaverMapOptions options = new NaverMapOptions()
                    .zoomControlEnabled(false)
                    .scaleBarEnabled(false)
                    .compassEnabled(false)
                    .locationButtonEnabled(false);
            mapFragment = MapFragment.newInstance(options);
            getChildFragmentManager().beginTransaction().add(R.id.record_fragment, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ride_recode, container, false);

        imageButton = (ImageButton)view.findViewById(R.id.backToMain);
        imageButton.setOnClickListener(this);

        Date = (TextView) view.findViewById(R.id.recodeDate);
        Start = (TextView) view.findViewById(R.id.recodeStart);
        Finish = (TextView) view.findViewById(R.id.recodeFinish);
        Mile = (TextView) view.findViewById(R.id.recodeMileage);
        Time = (TextView) view.findViewById(R.id.recodeTime);
        Avg = (TextView) view.findViewById(R.id.recodeAvg);

        High = (TextView) view.findViewById(R.id.recodeHigh);
        Kcal = (TextView) view.findViewById(R.id.recodeKcal);


        Date.setText(driveRecodes.getDate());

        Start.setText(driveRecodes.getStartingPoint());

        Finish.setText(driveRecodes.getDestination());

        Mile.setText(String.format("%.2f Km",driveRecodes.getTotalMileage()/1000));

        Time.setText(requiredTime(driveRecodes.getTotalDrivingTime()));

        Avg.setText(String.format("%.2f Km/h",driveRecodes.getAverageSpeed()));

        High.setText(String.format("%.2f Km/h",driveRecodes.getTopSpeed()));

        Kcal.setText(String.format("%.1f Kcal",driveRecodes.getKcal()));


        return view;
    }

    @Override
    public void onClick(View v) {
        main.getSupportFragmentManager().beginTransaction().remove(recordFragment.this).commitAllowingStateLoss();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setLocationButtonEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setZoomControlEnabled(false);

        //지도내의 경로 수정 및 위치 확인

        List<LatLng> list = CreateLatLngList(driveRecodes.getLatitude(),driveRecodes.getLongitude());
        int size = list.size()-1;

        PathOverlay pathOverlay = new PathOverlay();

        pathOverlay.setCoords(list);
        pathOverlay.setWidth(20);
        pathOverlay.setColor(getResources().getColor(R.color.RecodePathColor));

        CameraUpdate cameraUpdate = CameraUpdate.fitBounds(new LatLngBounds(list.get(0), list.get(size)), 150);
        naverMap.moveCamera(cameraUpdate);

        Marker startMarker = new Marker();
        startMarker.setCaptionText("출발지");
        startMarker.setCaptionTextSize(16);
        startMarker.setIcon(MarkerIcons.BLACK);
        startMarker.setIconTintColor(Color.RED);
        startMarker.setWidth(60);
        startMarker.setHeight(80);
        startMarker.setPosition(list.get(0));

        Marker goalMarker = new Marker();
        goalMarker.setCaptionText("도착지");
        goalMarker.setCaptionTextSize(16);
        goalMarker.setIcon(MarkerIcons.BLACK);
        goalMarker.setIconTintColor(Color.BLUE);
        goalMarker.setWidth(60);
        goalMarker.setHeight(80);
        goalMarker.setPosition(list.get(size));

        startMarker.setMap(naverMap);
        goalMarker.setMap(naverMap);
        pathOverlay.setMap(naverMap);
    }

    private List<LatLng> CreateLatLngList(String Lat, String Lng){
        List<LatLng> list = new ArrayList<>();

        String[] lat = Lat.split(",");
        String[] lng = Lng.split(",");

        for (int i = 0 ;i <lat.length;i++ ){
            list.add(new LatLng(Double.valueOf(lat[i].replace("[","").replace("]","")),
                                Double.valueOf(lng[i].replace("[","").replace("]",""))));
        }

        return list;
    }

    @Override
    public void onBackPressed() {
        imageButton.performClick();
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
}
