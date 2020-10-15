package com.ubd_app.Ride;

import android.location.Location;
import android.util.Log;
import com.ubd_app.Main;
import com.ubd_app.Map.Route.Route;
import com.ubd_app.Ride.Guide.DriveSounds;
import com.ubd_app.Ride.Guide.InfoThread;

import static com.ubd_app.UBDLibrary.LocationCheck.*;

public class DriveLocationCheck {

    private Main main;

    //주행한 정보
    private RideRecordObject rideRecordObject;

    //경로 정보
    private Route route;

    //남은거리
    private double RemainingDistance = 0;

    //디스플레이 정보
    private int DisplayInfo = 201;
    private int DisplayDis = 0;


    //포인트 경로 (좌회전/우회전 안내를 위해)
    private int GeoPointer;
    //포인트 사이의 세부경로
    private int LineStringPointer;

    //스택 길안내 몇번 이상틀리는지 확인
    private int Stack = 0;

    private double speed = 0;
    private double MaxSpeed = 0;

    private InfoThread infoThread;

    public DriveLocationCheck() {

    }

    public DriveLocationCheck(Route route, Main main, RideRecordObject rideRecordObject, InfoThread infoThread) {
        this.main = main;

        //좌표정보를 가지고있음
        this.route = route;
        this.rideRecordObject = rideRecordObject;

        this.infoThread = infoThread;

        setValuse();

    }

    public void setValuse() {
        this.RemainingDistance = route.getTotalDistance();
        this.LineStringPointer = 0;
        this.GeoPointer = 0;
        this.Stack = 0;
    }

    //좌표 이동했을때 확인
    public int CalculateDistance(Location preLocation, Location location) {

        speed = location.getSpeed() * 3.6;
        if (speed > MaxSpeed) {
            MaxSpeed = speed;
            rideRecordObject.setMaxSpeed(MaxSpeed);
        }

        //이동한 좌표 입력
        rideRecordObject.AddGeoArray(location.getLatitude(), location.getLongitude());

        if (preLocation != null) {
            //이동한 거리 계산
            int distance = (int) preLocation.distanceTo(location);
            //이동한거리 누적 계산
            rideRecordObject.AddTotalRideDis(distance);

            return CheckLocationPoint(preLocation, location, distance);
        }
        return 1;
    }

    private int CheckLocationPoint(Location preLocation, Location location, int distance) {

        Location Point = null;
        Location LineStringPrevious = null;
        Location LineStringNext = null;

        int LineStringLength = 0;


        int dis1 = 0;
        int dis2 = 0;
        int dis3 = 0;

        try {
            //올바른길을 가는지 확인하는 부분
            //골인지점인지 확인

            LineStringLength = route.getLineStringList().get(GeoPointer).length;
            LineStringPrevious = CreateLocation(route.getLineStringList().get(GeoPointer)[LineStringPointer]);
            LineStringNext = CreateLocation(route.getLineStringList().get(GeoPointer)[LineStringPointer + 1]);

            dis1 = (int) LineStringPrevious.distanceTo(location);
            dis2 = (int) location.distanceTo(LineStringNext);
            dis3 = (int) LineStringPrevious.distanceTo(LineStringNext);

            if (GoalInCheck(location, route.getGoalLocation()) && (GeoPointer + 1 >= route.getGoalPointNumber())) {
                return 2;
            } else {
                //경로벗어나지 않았는지 확인
//                Log.d("lll geo", String.valueOf(GeoPointer));
//                Log.d("lll geo", String.valueOf(route.getGoalPointNumber()));
//                Log.d("lll line", String.valueOf(LineStringPointer));
//                Log.d("lll lineLength", String.valueOf(LineStringLength));
//                Log.d("lll", String.valueOf(dis1) + "," + String.valueOf(dis2) + "," + String.valueOf(dis3) + "," + "         " + String.valueOf(dis1 + dis2) + "<=" + String.valueOf(dis3 + 20));

                if ((dis1 + dis2) <= (dis3 + 20)) {
                    //날릴부분
                    try {

                        if (infoThread.getState() == Thread.State.NEW) {
                            infoThread.setValue(location, GeoPointer);
                            infoThread.run();
                        } else if (infoThread.getState() == Thread.State.TERMINATED) {
                            infoThread.setValue(location, GeoPointer);
                            infoThread.run();
                        }



                    } catch (Exception e) {
                        Log.d("lll 예외", "2");
                        return StackCheck();
                    }


                    if (dis2 <= 10) {
                        if (LineStringPointer + 1 == LineStringLength - 1) {
                            LineStringPointer = 0;
                            GeoPointer += 1;
                        } else {
                            LineStringPointer += 1;
                        }
                    }

                    RemainingDistance -= distance;
                    Stack = 0;
                    return 1;


                } else {

                    Log.d("lll 예외", "3");
                    return StackCheck();
                }
            }

        } catch (
                Exception e) {
            Log.d("lll", e.getMessage());
            Log.d("lll 예외", "4");
            return StackCheck();
        }

    }

    //벙향번호 확인
    public int arrowNumber(Location location, int i) {
        if (i == 201) {
            if (location.distanceTo(route.getGoalLocation()) < 30) {
                return 201;
            } else {
                return 11;
            }
        } else if (i == 200) {
            return 11;
        }
        return i;
    }

    private int StackCheck() {
        Stack += 1;
        if (Stack == 3) {
            return 3;
        } else {
            return 1;
        }
    }

    public double getRemainingDistance() {
        return RemainingDistance;
    }

    public RideRecordObject getRideRecordObject() {
        return rideRecordObject;
    }

    public double getSpeed() {
        return speed;
    }

}

