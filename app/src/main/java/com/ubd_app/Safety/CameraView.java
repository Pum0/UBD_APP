package com.ubd_app.Safety;

import android.graphics.*;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.*;


import androidx.fragment.app.Fragment;
import com.ubd_app.Safety.tensorflow.customview.AutoFitTextureView;
import com.ubd_app.R;
import com.ubd_app.Safety.tensorflow.customview.OverlayView;
import com.ubd_app.Safety.tensorflow.env.ImageUtils;

import java.io.IOException;
import java.util.*;


public class CameraView extends Fragment {

    private HandlerThread backgroundThread;

    private static final int MINIMUM_PREVIEW_SIZE = 320;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private android.hardware.Camera camera;
    private android.hardware.Camera.PreviewCallback imageListener;
    private Size desiredSize;

    private int layout;

    private AutoFitTextureView textureView;
    public OverlayView trackingOverlay;
    private SurfaceTexture availableSurfaceTexture = null;

    private final TextureView.SurfaceTextureListener surfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(
                        final SurfaceTexture texture, final int width, final int height) {
                    availableSurfaceTexture = texture;
                    startCamera();
                }

                @Override
                public void onSurfaceTextureSizeChanged(
                        final SurfaceTexture texture, final int width, final int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(final SurfaceTexture texture) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(final SurfaceTexture texture) {
                }
            };

    private void startCamera() {
        int index = getCameraId();
        camera = android.hardware.Camera.open(index);

        try {
            android.hardware.Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes != null
                    && focusModes.contains(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            List<android.hardware.Camera.Size> cameraSizes = parameters.getSupportedPreviewSizes();
            Size[] sizes = new Size[cameraSizes.size()];
            int i = 0;
            for (android.hardware.Camera.Size size : cameraSizes) {
                sizes[i++] = new Size(size.width, size.height);
            }
            Size previewSize =
                    chooseOptimalSize(
                            sizes, desiredSize.getWidth(), desiredSize.getHeight());
            parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
            camera.setDisplayOrientation(90);
            camera.setParameters(parameters);
            camera.setPreviewTexture(availableSurfaceTexture);
        } catch (IOException exception) {
            camera.release();
        }

        camera.setPreviewCallbackWithBuffer(imageListener);
        android.hardware.Camera.Size s = camera.getParameters().getPreviewSize();
        camera.addCallbackBuffer(new byte[ImageUtils.getYUVByteSize(s.height, s.width)]);

        textureView.setAspectRatio(s.height, s.width);

        camera.startPreview();
    }

    private int getCameraId() {
        android.hardware.Camera.CameraInfo ci = new android.hardware.Camera.CameraInfo();
        for (int i = 0; i < android.hardware.Camera.getNumberOfCameras(); i++) {
            android.hardware.Camera.getCameraInfo(i, ci);
            if (ci.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) return i;
        }
        return -1; // No camera found
    }


    public CameraView(
            final android.hardware.Camera.PreviewCallback imageListener, final int layout, final Size desiredSize) {
        this.imageListener = imageListener;
        this.layout = layout;
        this.desiredSize = desiredSize;
    }

    protected static Size chooseOptimalSize(final Size[] choices, final int width, final int height) {
        final int minSize = Math.max(Math.min(width, height), MINIMUM_PREVIEW_SIZE);
        final Size desiredSize = new Size(width, height);

        // Collect the supported resolutions that are at least as big as the preview Surface
        boolean exactSizeFound = false;
        final List<Size> bigEnough = new ArrayList<Size>();
        final List<Size> tooSmall = new ArrayList<Size>();
        for (final Size option : choices) {
            if (option.equals(desiredSize)) {
                // Set the size but don't return yet so that remaining sizes will still be logged.
                exactSizeFound = true;
            }

            if (option.getHeight() >= minSize && option.getWidth() >= minSize) {
                bigEnough.add(option);
            } else {
                tooSmall.add(option);
            }
        }

        if (exactSizeFound) {
            return desiredSize;
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            final Size chosenSize = Collections.min(bigEnough, new CompareSizesByArea());
            return chosenSize;
        } else {
            return choices[0];
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.safety_ride_camera, container, false);

        textureView = (AutoFitTextureView) v.findViewById(R.id.cameraTextureView);
        trackingOverlay = (OverlayView) v.findViewById(R.id.tracking_overlay);

        return v;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        if (textureView.isAvailable()) {
            startCamera();
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        stopCamera();
        stopBackgroundThread();
        super.onPause();
    }

    protected void stopCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("ImageListener");
        backgroundThread.start();
    }

    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
        } catch (final InterruptedException e) {

        }
    }


    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(final Size lhs, final Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum(
                    (long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

}

