package com.ubd_app.Safety;

import android.graphics.*;
import android.location.Location;
import android.media.AudioManager;
import android.media.ImageReader;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Size;
import android.util.TypedValue;
import android.view.*;


import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.ubd_app.Main;
import com.ubd_app.R;
import com.ubd_app.Safety.tensorflow.customview.OverlayView;
import com.ubd_app.Safety.tensorflow.env.BorderedText;
import com.ubd_app.Safety.tensorflow.env.ImageUtils;
import com.ubd_app.Safety.tensorflow.tflite.Classifier;
import com.ubd_app.Safety.tensorflow.tflite.TFLiteObjectDetectionAPIModel;
import com.ubd_app.Safety.tensorflow.tracking.MultiBoxTracker;

import java.io.IOException;
import java.util.*;


public class SafetyRide extends Tensorflow implements ImageReader.OnImageAvailableListener, View.OnClickListener {

    protected Main main;

    private View view;

    private CameraView cameraView;

    private ImageView checkGps;

    // 텐서플로우
    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 640);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    public OverlayView trackingOverlay;
    private Integer sensorOrientation;

    private Classifier detector;

    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    private BorderedText borderedText;

    private FrameLayout warring;
    private int objectNum = 0;
    private TextView NUMofOBJECT;
    private Thread ChangeNumThread, ChangeBackground, ChangeInfomation;
    private boolean ThreadCheck = true;

    private SoundPool soundPool;


    //gps기반 속도 계산
    private TextView distanced, DriveTime, SPEED_TEXT, AvgSpeed, highSpeed, startTime;
    private GpsInfomation gpsInfomation;


    private double DistanceTraveled = 0;
    private Location PreviousLocation = null;
    private String StartTime = null;
    private int DrivingTime = 0;
    private double HighSpeed = 0;

    //생성자
    public SafetyRide(GpsInfomation gpsInfomation) {
        this.gpsInfomation = gpsInfomation;
    }

    //스레드 선언
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        ChangeNumThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (ThreadCheck) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {

                    }
                    ControlThread.sendEmptyMessage(0);
                }
            }
        });


        ChangeBackground = new Thread(new Runnable() {
            @Override
            public void run() {

                int soundNumber = soundPool.load(getContext(), R.raw.el2, 1);

                while (ThreadCheck) {
                    if (objectNum > 1) {
                        try {
                            ControlThread.sendEmptyMessage(1);
                            soundPool.play(soundNumber, 1, 1, 0, 0, 1);
                            Thread.sleep(1500);
                        } catch (Exception e) {

                        }
                    }
                    ControlThread.sendEmptyMessage(2);
                    soundPool.stop(soundNumber);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                }
                soundPool.stop(soundNumber);
            }
        });

        ChangeInfomation = new Thread(new Runnable() {
            @Override
            public void run() {

                while (ThreadCheck) {
                    try {
                        ControlThread.sendEmptyMessage(3);
                        Thread.sleep(500);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    @Override
    public synchronized void onResume() {
        ThreadCheck = true;
        ChangeNumThread.start();
        ChangeBackground.start();
        ChangeInfomation.start();
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.safety_ride, container, false);

        setCameraView();

        DriveTime = (TextView) view.findViewById(R.id.DrivingTime);
        NUMofOBJECT = (TextView) view.findViewById(R.id.NumOfobject);
        warring = (FrameLayout) view.findViewById(R.id.warring);
        SPEED_TEXT = (TextView) view.findViewById(R.id.speed_text);
        checkGps = (ImageView) view.findViewById(R.id.checkGPS);
        distanced = (TextView) view.findViewById(R.id.distanced);
        AvgSpeed = (TextView) view.findViewById(R.id.AvgSpeed);
        highSpeed = (TextView) view.findViewById(R.id.HighSpeed);
        startTime = (TextView) view.findViewById(R.id.startTime);

        ImageButton B = (ImageButton) view.findViewById(R.id.GotoMap);
        B.setOnClickListener(this);


        checkGps.setImageResource(R.drawable.gps_able);

        //바텀 시트뷰
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.safetyRideINFO));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        return view;
    }

    //스레드를 위한 핸들러 정의
    private Handler ControlThread = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case (0):
                    NUMofOBJECT.setText(String.valueOf(objectNum));
                    break;
                case (1):
                    try {
                        warring.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.frame_warring));

                    } catch (Exception e) {

                    }
                    break;
                case (2):
                    try {
                        warring.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.frame_reset));
                    } catch (Exception e) {

                    }
                case (3):

                    if (StartTime == null) {
                        StartTime = gpsInfomation.StartTime;
                        startTime.setText(StartTime);
                    }

                    try {
                        int DrivingTime = gpsInfomation.DrivingTime;
                        DriveTime.setText(String.valueOf((DrivingTime / 3600) + ":" + String.valueOf((DrivingTime / 60) % 60) + ":" + String.valueOf(DrivingTime % 60)));


                        double DistanceTraveled = gpsInfomation.DistanceTraveled;
                        distanced.setText(String.format("%.2f km", DistanceTraveled / 1000));

                        double speed = gpsInfomation.speed;
                        SPEED_TEXT.setText(String.format("%.1f", speed));


                        double HighSpeed = gpsInfomation.HighSpeed;
                        highSpeed.setText(String.format("%.1f km/h", HighSpeed));


                        double avgSpeed = gpsInfomation.HighSpeed;
                        AvgSpeed.setText(String.format("%.1f km/h", avgSpeed));
                    } catch (Exception e) {

                    }
                    break;
            }
        }
    };

    @Override
    public synchronized void onPause() {
        ThreadCheck = false;
        ChangeBackground.interrupt();
        ChangeNumThread.interrupt();
        ChangeInfomation.interrupt();
        removeCameraView();
        super.onPause();
    }


    private void setCameraView() {
        cameraView = new CameraView(this, getLayoutId(), getDesiredPreviewFrameSize());
        getChildFragmentManager().beginTransaction().replace(R.id.safetyFrame, cameraView).commitAllowingStateLoss();
    }

    private void removeCameraView() {
        getChildFragmentManager().beginTransaction().replace(R.id.safetyFrame, cameraView).commitAllowingStateLoss();
        getChildFragmentManager().beginTransaction().remove(cameraView);
    }


    //텐서플로우 관련 메소드 재정의
    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        final Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;

                        final List<Classifier.Recognition> mappedRecognitions =
                                new LinkedList<Classifier.Recognition>();

                        for (final Classifier.Recognition result : results) {
                            final RectF location = result.getLocation();
                            if (location != null && result.getConfidence() >= minimumConfidence) {
                                canvas.drawRect(location, paint);

                                cropToFrameTransform.mapRect(location);

                                result.setLocation(location);
                                mappedRecognitions.add(result);
                            }
                        }

                        tracker.trackResults(mappedRecognitions, currTimestamp);
                        trackingOverlay.postInvalidate();

                        objectNum = mappedRecognitions.size();

                        computingDetection = false;

                    }
                });
    }


    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(getContext());

        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            getActivity().getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            getActivity().finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();

        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = cameraView.trackingOverlay;
        trackingOverlay.addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        //tracker.draw(canvas);
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.safety_ride;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.GotoMap): {
                ((Main) getActivity()).MapAndRideChange(3);
                break;
            }
        }
    }
}

