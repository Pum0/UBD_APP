package com.ubd_app.Ride.Guide;

import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.ubd_app.Main;

import java.util.List;

public class InfoThread extends Thread {

    private Main main;
    private Handler handler;
    private DriveSounds driveSounds;

    private boolean Check = true;
    private boolean mcheck = true;

    private int DirectionPoint = 1;
    private int DistancePoint = 0;

    private boolean FirstCheck = true;
    private boolean SecondCheck = false;
    private boolean ThirdCheck = true;

    private int Distance = 0;
    private int Direction = 0;

    private List<Integer> DirectionList;
    private List<Integer> DistanceList;

    private Location location = null;
    private Location PreLocation = null;
    private boolean Next = true;

    public InfoThread() {
    }

    public InfoThread(Main main, Handler handler, List<Integer> Direction, List<Integer> Distance, DriveSounds driveSounds) {
        this.handler = handler;
        this.DirectionList = Direction;
        this.DistanceList = Distance;
        this.driveSounds = new DriveSounds(main);
    }

    @Override
    public void run() {
        super.run();
            try {
                //화면표시 변수 설정
                DistaceCal();
                
                //음성 보내주고
                //VoiceOutput(Direction);

                //화면표시 변경
                Message message = handler.obtainMessage();
                message.what = 1;
                message.arg1 = Direction;
                message.arg2 = Distance;
                handler.sendMessage(message);

                Log.d("lll", "초기값 세팅후" + String.valueOf(Direction) + "  " + String.valueOf(Distance));
                Log.d("lll", String.valueOf(message.toString()));

                setPreLocation(getLocation());

                //mcheck = false;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("lll", String.valueOf(e.getMessage()));
            }

    }

//    private void VoiceOutput(int direction, int distance) {
//        if (FirstCheck) {
//            VoiceSet(direction);
//            Log.d("lll", "체크1");
//            FirstCheck = false;
//
//        } else if (SecondCheck) {
//            VoiceSet(direction);
//            Log.d("lll", "체크2");
//            SecondCheck = false;
//
//        } else if (ThirdCheck) {
//            VoiceSet(direction);
//            Log.d("lll", "체크3");
//            ThirdCheck = false;
//
//        }
//    }

    private Integer[] VoiceSet(int direction, int distance) {
        Integer[] arr = new Integer[2];
        arr[0] = direction;
        if (150 > distance && distance > 50) {
            arr[1] = 150;
        } else if (50 > distance && distance > 10) {
            arr[1] = 50;
        } else if (10 > distance) {
            arr[1] = 10;
        }

        return arr;
    }

    private void CheckVoiceToDistance(int distance) {
        if (distance > 150) {
            FirstCheck = true;
        } else if (distance > 50) {
            SecondCheck = true;
        } else if (distance > 10) {
            ThirdCheck = true;
        }
    }

    private void DistaceCal() {
        if (PreLocation == null) {
            //이전 좌표가 없을때 초기 값 세팅
            Direction = DirectionList.get(DirectionPoint);
            Distance = DistanceList.get(DistancePoint);
            Log.d("lll", "초기값 세팅후" + String.valueOf(Direction) + "  " + String.valueOf(Distance));
            //150m / 50m / 근접 / 단위로 설명할 구분자 확인
            CheckVoiceToDistance(Distance);
        } else {
            Distance -= location.distanceTo(PreLocation);
            if (0 > Distance) {
                Distance = 0;
            }
        }
    }

    //외부에서 사용할 호출해서 사용할 함수
    public void setValue(Location location, int Point) {
        if (Point != DistancePoint) {
            this.DistancePoint = Point;
            this.DirectionPoint = Point + 1;
            PreLocation = null;
        }
        this.location = location;

        mcheck = true;
    }

    public void Reset(List<Integer> Direction, List<Integer> Distance) {
        this.DirectionList = Direction;
        this.DistanceList = Distance;
        this.FirstCheck = false;
        this.SecondCheck = false;
        this.ThirdCheck = false;
    }

    public void close() {
        this.Check = false;
    }

    private void setPreLocation(Location location) {
        this.PreLocation = location;
    }

    private Location getLocation() {
        return this.location;
    }
}
