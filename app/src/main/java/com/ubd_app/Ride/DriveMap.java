package com.ubd_app.Ride;

import android.app.ProgressDialog;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.*;
import com.naver.maps.map.overlay.*;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.GeometryUtils;
import com.ubd_app.Main;
import com.ubd_app.Map.Map;
import com.ubd_app.Map.Route.Route;
import com.ubd_app.R;
import com.ubd_app.Ride.Guide.*;
import com.ubd_app.Safety.GpsInfomation;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;


import java.util.List;

import static com.ubd_app.UBDLibrary.TextConverter.*;

public class DriveMap extends Fragment implements OnMapReadyCallback {

    private ProgressDialog progressDialog;
    private Main main;
    private RideRecordObject rideRecordObject;

    //뷰에 있는 위젯
    private View view;
    private MapFragment mapFragment;
    private TextView RemainingDistance, rideSpeed, ArrowDistance;
    private ImageView ArrowImage, checkGPS;
    private Button finishButton;

    //NaverMap 관련
    private LocationOverlay locationOverlay;
    private PathOverlay pathOverlay = new PathOverlay();
    private Marker marker = new Marker();
    private NaverMap naverMap;
    private boolean NaviCheck = true;

    //이전좌표, 도착지 좌표
    private Location PreviousLocation = null;

    //gps정보 받아오는 메소드 선언/스레드 선언 , 위치정보 관련 메소트
    private Route route;
    private FusedLocationSource fusedLocationSource;
    private GpsInfomation gpsInfomation;

    private SpeedDpThread GuiControl;
    private DriveLocationCheck driveLocationCheck;

    private DriveSounds driveSounds;
    private InfoThread infoThread;

    private int check = 0;

    public DriveMap() {

    }

    public DriveMap(GpsInfomation gpsInfomation, Map map) {
        this.route = map.getRoute();
        this.gpsInfomation = gpsInfomation;
        this.fusedLocationSource = new FusedLocationSource(this, 100);

    }

    //시작할때 사용할 스레드 생성
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.main = (Main) getActivity();
        this.driveSounds = new DriveSounds(main);
        this.rideRecordObject = new RideRecordObject(route.getSPointName(), route.getGPointName(), main.weight, main.DocumentID, main);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);




        //위치 받아오는 객체 생성
        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.ride_fragment);

        if (mapFragment == null) {
            NaverMapOptions options = new NaverMapOptions()
                    .zoomControlEnabled(false)
                    .scaleBarEnabled(false)
                    .compassEnabled(false)
                    .locationButtonEnabled(false);
            mapFragment = MapFragment.newInstance(options);
            getChildFragmentManager().beginTransaction().add(R.id.ride_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        NaviCheck = true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ride_view, container, false);

        RemainingDistance = (TextView) view.findViewById(R.id.RemainingDistance);
        rideSpeed = (TextView) view.findViewById(R.id.rideSpeed);
        ArrowImage = (ImageView) view.findViewById(R.id.Arrow);
        ArrowDistance = (TextView) view.findViewById(R.id.GoToArrow);
        checkGPS = (ImageView) view.findViewById(R.id.checkGPS);

        finishButton = (Button) view.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(View -> {
            main.setDriveMap(1);
            try {
                rideRecordObject.upload();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        ImageButton B = (ImageButton) view.findViewById(R.id.GoToRide);
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.MapAndRideChange(2);
            }
        });


        this.infoThread = new InfoThread(main,new InfoHandler(main,ArrowImage,ArrowDistance),route.getTurnType(),route.getPointToPointDistance(),driveSounds);
        this.driveLocationCheck = new DriveLocationCheck(route, main, rideRecordObject,infoThread);
        this.GuiControl = new SpeedDpThread(new SpeedDpHandler(rideSpeed, driveLocationCheck));

        GuiControl.start();
        //infoThread.start();

        RemainingDistance.setText(ConverterDistance(driveLocationCheck.getRemainingDistance()));
        checkGPS.setImageResource(R.drawable.gps_able);


        return view;
    }

    @Override
    public void onPause() {
        GuiControl.setThreadCheck(false);
        super.onPause();
    }


    //맵의 구성을 정의
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setZoomControlEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setLogoMargin(20, 0, 0, 350);

        marker.setIcon(OverlayImage.fromResource(R.drawable.finish));
        marker.setPosition(route.getGoalLatLng());
        marker.setMap(naverMap);

        setPath(naverMap, route.getLatLngList());

        naverMap.setExtent(new LatLngBounds(new LatLng(31.43, 122.37), new LatLng(44.35, 132)));
        naverMap.setMinZoom(5.0);

        locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setIcon(null);
        main.naviOnOff(true);

        naverMap.setLocationSource(fusedLocationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);

        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int i, boolean b) {
                naverMap.setDefaultCameraAnimationDuration(1000);
                if (i == CameraUpdate.REASON_GESTURE) {
                    NaviCheck = false;
                    locationOverlay.setIcon(OverlayImage.fromResource(R.drawable.navigation_on));
                    main.naviOnOff(NaviCheck);

                }
            }
        });

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull @NotNull Location location) {

                //1초마다 호출됨
                PreviousLocation = setCameraAndUI(location, naverMap);
                rideRecordObject.AddTotalRideTime();
                RemainingDistance.setText(ConverterDistance(driveLocationCheck.getRemainingDistance()));
                //setInfomation(driveLocationCheck.getDisplayDis(), driveLocationCheck.getDisplayInfo());
            }
        });
        this.naverMap = naverMap;
    }

    private void setPath(NaverMap naverMap, List<LatLng> latLngs) {
        pathOverlay.setMap(null);
        pathOverlay.setCoords(latLngs);
        pathOverlay.setWidth(35);
        pathOverlay.setOutlineWidth(2);
        pathOverlay.setColor(getResources().getColor(R.color.PathColor));
        pathOverlay.setOutlineColor(getResources().getColor(R.color.PathColor));
        pathOverlay.setPassedColor(getResources().getColor(R.color.NullColor));
        pathOverlay.setPassedOutlineColor(getResources().getColor(R.color.NullColor));
        pathOverlay.setMap(naverMap);
    }

    //카메라 이동
    private Location setCameraAndUI(Location location, NaverMap naverMap) {
        LatLng latLng = new LatLng(location);
        if (PreviousLocation == null) {
            CameraPosition cameraPosition = new CameraPosition(latLng, 18);
            naverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition).animate(CameraAnimation.Fly)
                    .pivot(new PointF(0.5f, 0.5f))
                    .finishCallback(new CameraUpdate.FinishCallback() {
                        @Override
                        public void onCameraUpdateFinish() {
                            locationOverlay.setIcon(null);
                            main.naviOnOff(false);
                            driveSounds.playETCAudio(0);
                        }
                    })
            );
        } else {
            //카메라 및 위치 이동
            CameraPosition cameraPosition = new CameraPosition(latLng, 18, 0, location.getBearing());
            naverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition).animate(CameraAnimation.Linear)
                    .pivot(new PointF(0.5f, 0.5f))
                    .finishCallback(new CameraUpdate.FinishCallback() {
                        @Override
                        public void onCameraUpdateFinish() {
                            locationOverlay.setIcon(null);
                            main.naviOnOff(true);
                        }
                    })
            );

            pathOverlay.setProgress(GeometryUtils.getProgress(pathOverlay.getCoords(), new LatLng(location)));
        }

        //남은거리 계산 및 표시
        switch (driveLocationCheck.CalculateDistance(PreviousLocation, location)) {
            case (1): {
                //제대로 된 길을 갈때
                double dis = driveLocationCheck.getRemainingDistance();
                if (dis > 20) {
                    RemainingDistance.setText(ConverterDistance(dis));
                } else {
                    RemainingDistance.setText("목적지 근처");
                }
                break;
            }
            case (2): {
                //도착했을때
                driveSounds.playETCAudio(3);
                Toast.makeText(main, "도착.", Toast.LENGTH_SHORT).show();
                main.rideFinishMenu(true, driveLocationCheck);
                fusedLocationSource.deactivate();
                driveSounds.AllRelease();
                break;
            }
            case (3): {
                //이전 좌표를 못받아올때
                if (check > 0) {
                    driveSounds.playETCAudio(2);
                    check = 0;
                }
                Toast.makeText(main, "경로 재탐색.", Toast.LENGTH_SHORT).show();
                findRoute(route.getGoalLatLng(), location);
                infoThread.Reset(route.getTurnType(),route.getPointToPointDistance());
                driveLocationCheck.setValuse();
                check++;
                break;
            }
        }
        return location;
    }

    //경로 찾는 메소드
    private void findRoute(LatLng destination, Location location) {
        if (location != null) {
            progressDialog.show();
            route.requestRoute(location, destination);
            setPath(naverMap, route.getLatLngList());
            progressDialog.dismiss();

        } else {
            Toast.makeText(getActivity(), "검색 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public RideRecordObject getFinishUpload() {
        return rideRecordObject;
    }

    public Route getRoute() {
        return route;
    }

    public FusedLocationSource getFusedLocationSource() {
        return fusedLocationSource;
    }
}