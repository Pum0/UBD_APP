package com.ubd_app.Map;

import android.app.ProgressDialog;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.*;
import com.naver.maps.map.overlay.*;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.CompassView;
import com.naver.maps.map.widget.LocationButtonView;
import com.naver.maps.map.widget.ScaleBarView;
import com.ubd_app.Main;
import com.ubd_app.Map.Route.Route;
import com.ubd_app.R;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class Map extends Fragment implements OnMapReadyCallback {

    private ProgressDialog progressDialog;

    private Main main;

    //화면구성 관련
    private View view;
    private MapFragment mapFragment;
    private LocationButtonView locationButtonView;
    private ScaleBarView scaleBarView;
    private CompassView compassView;

    // 지도 관련
    private FusedLocationSource fusedLocationSource;
    private NaverMap naverMap;
    private Marker marker = new Marker();
    private PathOverlay pathOverlay = new PathOverlay();

    // 정보창
    private InfoWindow infoWindow = new InfoWindow();
    private Geocoder geocoder;

    // 경로관련
    private Route route;

    public Map(){

    }

    public ProgressDialog setProgressDialog() {

        ProgressDialog progressDialog = new ProgressDialog(main);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);

        return progressDialog;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.main = (Main) getActivity();
        this.progressDialog = setProgressDialog();
        this.fusedLocationSource = new FusedLocationSource(this, 100);
        this.geocoder = main.getGeocoder();
        this.route = new Route();

        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            NaverMapOptions options = new NaverMapOptions()
                    .zoomControlEnabled(false)
                    .scaleBarEnabled(false)
                    .compassEnabled(false)
                    .locationButtonEnabled(false);
            mapFragment = MapFragment.newInstance(options);
            getChildFragmentManager().beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

    }


    //맵 프레그먼트 구성요소 정의
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        naverMap.setExtent(new LatLngBounds(new LatLng(31.43, 122.37), new LatLng(44.35, 132)));
        naverMap.setMinZoom(5.0);

        naverMap.setLocationSource(fusedLocationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setLocationButtonEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setLogoMargin(30, 0, 0, 210);

        //지도 내의 요소 위치 지정
        compassView.setMap(naverMap);
        locationButtonView.setMap(naverMap);
        scaleBarView.setMap(naverMap);

        //경로선 설정
        pathOverlay.setWidth(28);
        pathOverlay.setOutlineWidth(2);

        pathOverlay.setPatternImage(OverlayImage.fromResource(R.drawable.path_pattern));
        pathOverlay.setPatternInterval(30);
        pathOverlay.setColor(getResources().getColor(R.color.PathColor));
        pathOverlay.setOutlineColor(getResources().getColor(R.color.PathColor));
        pathOverlay.setPassedColor(getResources().getColor(R.color.NullColor));
        pathOverlay.setPassedOutlineColor(getResources().getColor(R.color.NullColor));

        //맵 내부 클릭 리스너
        naverMap.setOnMapLongClickListener(new com.naver.maps.map.NaverMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull @NotNull PointF pointF, @NonNull @NotNull LatLng latLng) {
                marker.setMap(null);

                LatLng latLng1 = new LatLng(latLng.latitude, latLng.longitude);
                findRoute(latLng);
                marker.setPosition(latLng);
                marker.setMap(naverMap);
                try {
                    Address add = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                    setwindowInfo(add.getAddressLine(0)).open(marker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        marker.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull @NotNull Overlay overlay) {
                main.rideStartMenu(0);
                return false;
            }
        });

        this.naverMap = naverMap;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_view, container, false);

        compassView = view.findViewById(R.id.compass);
        locationButtonView = view.findViewById(R.id.location_button);
        scaleBarView = view.findViewById(R.id.scale_bar);

        return view;
    }

    // 카메라 이동, 마커, 정보창
    public void setCamera(LatLng latLng, String str) {
        naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(latLng, 15).animate(CameraAnimation.Fly, 1000));

        marker.setPosition(latLng);
        marker.setMap(naverMap);
        setwindowInfo(str).open(marker);
    }

    //경로 찾는 메소드
    public boolean findRoute(LatLng destination) {
        if (fusedLocationSource.getLastLocation() != null) {
            progressDialog.show();

            route.requestRoute(fusedLocationSource.getLastLocation(), destination);

            pathOverlay.setMap(null);
            pathOverlay.setCoords(route.getLatLngList());
            pathOverlay.setMap(naverMap);

            progressDialog.dismiss();

            return true;
        } else {
            Toast.makeText(getActivity(), "검색 실패", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    // 마커 위 정보창 표시
    public InfoWindow setwindowInfo(String str) {
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getActivity()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return str;
            }
        });
        return infoWindow;
    }

    public void delMarker() {
        marker.setMap(null);
        pathOverlay.setMap(null);
    }

    public Route getRoute() {
        return route;
    }

    public FusedLocationSource getFusedLocationSource(){
        return fusedLocationSource;
    }
}
