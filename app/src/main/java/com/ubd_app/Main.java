package com.ubd_app;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ubd_app.AppDatabase.*;
import com.ubd_app.Map.Map;
import com.ubd_app.Map.Menu.FinishMenu;
import com.ubd_app.Ride.DriveLocationCheck;
import com.ubd_app.Map.Menu.StartMenu;
import com.ubd_app.Ride.DriveMap;
import com.ubd_app.Ride.Navi;
import com.ubd_app.Safety.GpsInfomation;
import com.ubd_app.Safety.SafetyRide;
import com.ubd_app.Search.SearchMap;
import com.ubd_app.bottomMenu.driveRecord.driveRecord;
import com.ubd_app.bottomMenu.favorite.favorites;
import com.ubd_app.bottomMenu.setting.ectSetting;

import java.util.List;

public class Main extends AppCompatActivity implements View.OnClickListener {

    public String DocumentID = "";
    public double weight = 70;

    View view;

    // 권한 요청 부분
    private static final int MULTIPLE_PERMISSION = 10235;

    private String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private long backKeyPressedTime = 0;
    private Toast toast;

    //즐겨찾기 관련
    public int placeNumber;

    //디비 관련
    public SearchWordViewModel wordViewModel;
    public DriveRecordeViewModel driveRecordeViewModel;
    public FavoriteDBViewModel favoriteDBViewModel;
    public userDataViewModel UserDataViewModel;

    //프래그먼트 매니저
    public FragmentManager fragmentManager = getSupportFragmentManager();
    public FragmentTransaction Transaction;

    //바텀 네비게이션
    public BottomNavigationView botNaviView;
    private TextView GoToSearch;

    //프래그먼트 객체 생성/정의
    private driveRecord driveRecord;
    private ectSetting setting;
    private favorites favorites;
    public SearchMap search = new SearchMap();
    private StartMenu startMenu;
    public Map map = new Map();
    public DriveMap drive_map;
    private SafetyRide cameraView;
    private FinishMenu finishMenu;
    public GpsInfomation gpsInfomation;
    private Navi navi = new Navi();

    //인터페이스 조정을 위한 변수
    public InputMethodManager imm;

    //장소검색을 위한 메소드
    public Geocoder geocoder;

    //GPS관련
    public LocationManager locationManager;

    public boolean SysC = false;

    public Main() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        Log.d("##MainStart", getIntent().getStringExtra("userID"));
        DocumentID = getIntent().getStringExtra("userID");

        permissionRequeest();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        geocoder = new Geocoder(this);

        //디비 접근 모델 초기화
        wordViewModel = ViewModelProviders.of(this).get(SearchWordViewModel.class);
        favoriteDBViewModel = ViewModelProviders.of(this).get(FavoriteDBViewModel.class);
        driveRecordeViewModel = ViewModelProviders.of(this).get(DriveRecordeViewModel.class);
        UserDataViewModel = ViewModelProviders.of(this).get(userDataViewModel.class);

        //로그인 정보 삽입
        UserDataViewModel.deleteAll();
        UserDataViewModel.save(new userData(1, "sss@sss", "123456", 70, 1));


        GoToSearch = findViewById(R.id.GoToSearch);
        GoToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSearchBar(v);
                search.Type = true;
            }
        });

        ImageButton GoToRide = (ImageButton) findViewById(R.id.GoToRide);
        GoToRide.setOnClickListener(this);

        setBotNaviView();

        setMapFragment(0);

        view = findViewById(R.layout.main_view);
    }

    public InputMethodManager getIMM() {
        return imm;
    }

    public Geocoder getGeocoder() {
        return geocoder;
    }

    //권한 요청
    public void permissionRequeest() {
        if (!hasPermissions(this, PERMISSIONS))
            ActivityCompat.requestPermissions(this, PERMISSIONS, MULTIPLE_PERMISSION);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    //권한 요청에 대한 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // 하나라도 거부한다면.
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("앱 권한");
                    alertDialog.setMessage("해당 앱의 원할한 기능을 이용하시려면 애플리케이션 정보>권한> 에서 모든 권한을 허용해 주십시오");
                    // 권한설정 클릭시 이벤트 발생
                    alertDialog.setPositiveButton("권한설정",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                    startActivity(intent);
                                    dialog.cancel();
                                }
                            });
                    //취소
                    alertDialog.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }
                return;
        }
    }

    public void changeSearchBar(View v) {
        if (v.getId() == R.id.GoToSearch) {
            Transaction = fragmentManager.beginTransaction();
            Transaction.setCustomAnimations(R.animator.enter_search, R.animator.exit_search);
            Transaction.replace(R.id.frame_layout, search).commitAllowingStateLoss();

        } else {
            Transaction = fragmentManager.beginTransaction();
            Transaction.setCustomAnimations(R.animator.exit_search, R.animator.exit_search);
            Transaction.remove(search).commitAllowingStateLoss();
        }
    }


    public void setMapFragment(int num) {
        if (num == 0) {
            Transaction = fragmentManager.beginTransaction();
            Transaction.replace(R.id.map_frame, map).commitAllowingStateLoss();
        } else {
            Transaction = fragmentManager.beginTransaction();
            Transaction.remove(map).commitAllowingStateLoss();
        }
    }

    public void setDriveMap(int num) {
        gpsInfomation = new GpsInfomation();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gpsInfomation);
        if (num == 0) {
            gpsInfomation = new GpsInfomation();
            drive_map = new DriveMap(gpsInfomation, map);
            Transaction = fragmentManager.beginTransaction();
            Transaction.replace(R.id.ride_frame, drive_map).commitAllowingStateLoss();
        } else {
            Transaction = fragmentManager.beginTransaction();
            Transaction.remove(drive_map).commitAllowingStateLoss();
            setMapFragment(0);
        }
    }

    public void rideStartMenu(int num) {
        if (num == 0) {
            startMenu = new StartMenu(map);
            Transaction = fragmentManager.beginTransaction();
            Transaction.setCustomAnimations(R.animator.y_enter_anim, R.animator.y_enter_anim);
            Transaction.replace(R.id.ride_start_menu, startMenu).commitAllowingStateLoss();
        } else {
            Transaction = fragmentManager.beginTransaction();
            Transaction.setCustomAnimations(R.animator.y_exit_anim, R.animator.y_exit_anim);
            Transaction.remove(startMenu).commitAllowingStateLoss();
        }
    }

    private SafetyRide setCameraview(GpsInfomation gpsInfomation) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gpsInfomation);
        SafetyRide cameraView = new SafetyRide(gpsInfomation);

        return cameraView;
    }

    public void MapAndRideChange(int i) {
        switch (i) {
            default:
                //메인에서 이동
            case 1: {
                gpsInfomation = new GpsInfomation();
                cameraView = setCameraview(gpsInfomation);
                Transaction = fragmentManager.beginTransaction();
                Transaction.replace(R.id.safety_view, cameraView).commitAllowingStateLoss();
                break;
            }
            //라이더 뷰 에서 이동
            case 2: {
                cameraView = setCameraview(gpsInfomation);
                Transaction = fragmentManager.beginTransaction();
                Transaction.replace(R.id.safety_view, cameraView).commitAllowingStateLoss();
                break;
            }
            //삭제
            case 3: {
                Transaction = fragmentManager.beginTransaction();
                Transaction.setCustomAnimations(R.animator.x_exit_anim, R.animator.x_exit_anim);
                Transaction.remove(cameraView).commitAllowingStateLoss();
                break;
            }
        }
    }

    public void setBotNaviView() {
        botNaviView = findViewById(R.id.bot_navi_view);
        botNaviView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Transaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.navigation_menu1: {
                        favorites = new favorites();
                        favorites.show(getSupportFragmentManager(), favorites.getTag());
                        break;
                    }
                    case R.id.navigation_menu2: {
                        driveRecord = new driveRecord();
                        driveRecord.show(getSupportFragmentManager(), driveRecord.getTag());
                        break;
                    }
                    case R.id.navigation_menu3: {
                        setting = new ectSetting();
                        setting.show(getSupportFragmentManager(), setting.getTag());
                        break;
                    }
                }
                return true;
            }
        });
    }

    public void rideFinishMenu(boolean res, DriveLocationCheck driveLocationCheck) {
        if (res) {
            finishMenu = new FinishMenu(driveLocationCheck);
            Transaction = fragmentManager.beginTransaction();
            Transaction.setCustomAnimations(R.animator.y_enter_anim, R.animator.y_enter_anim);
            Transaction.replace(R.id.ride_finish_menu, finishMenu).commitAllowingStateLoss();
        } else {
            Transaction = fragmentManager.beginTransaction();
            Transaction.setCustomAnimations(R.animator.y_exit_anim, R.animator.y_exit_anim);
            Transaction.remove(finishMenu).commitAllowingStateLoss();
        }
    }

    public void naviOnOff(boolean res) {
        if (res) {
            Transaction = fragmentManager.beginTransaction();
            Transaction.replace(R.id.navi_frame, navi).commitAllowingStateLoss();
        } else {
            Transaction = fragmentManager.beginTransaction();
            Transaction.remove(navi).commitAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.GoToRide): {
                MapAndRideChange(1);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList.size() > 1) {
            //TODO: Perform your logic to pass back press here
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof OnBackPressedListener) {
                    ((OnBackPressedListener) fragment).onBackPressed();
                }
            }
        }
        else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
                backKeyPressedTime = System.currentTimeMillis();
                toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
                toast.show();
                return;
            }

            if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
                finish();
                toast.cancel();
                toast = Toast.makeText(this, "이용해 주셔서 감사합니다.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
