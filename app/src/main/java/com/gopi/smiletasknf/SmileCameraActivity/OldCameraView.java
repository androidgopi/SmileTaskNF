package com.gopi.smiletasknf.SmileCameraActivity;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera.PictureCallback;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

public class OldCameraView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera;
    private static final String TAG = "CameraPreview";
    private PictureCallback pictureCallback;

    public OldCameraView(Context context, PictureCallback pictureCallback) {
        super(context);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        camera = isCameraAvailable();
        this.holder = getHolder();
        this.holder.addCallback(this);
    }

    public static Camera isCameraAvailable() {
        Camera object = null;
        try {
            if(Camera.getNumberOfCameras() >1 ) {
                object = Camera.open(0);
            }else {
                object = Camera.open(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            if(this.camera != null)
                camera.setPreviewDisplay(holder);
//            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        // Surface will be destroyed when we return, so stop the preview.
        if (camera != null) {
            // Call stopPreview() to stop updating the preview surface.
            camera.stopPreview();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (this.holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }
        if(this.camera != null) {
            Camera.Parameters myParameters = this.camera.getParameters();
            final Camera.Size bestPreviewSize = getBestPreviewSize(width, height, myParameters);
            if (bestPreviewSize != null) {
                myParameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
                myParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                myParameters.setJpegQuality(100);
                myParameters.setJpegThumbnailQuality(100);
                //TODO for 1+1
                List<Camera.Size> picSizes = myParameters.getSupportedPictureSizes();
                //Best Quality
                myParameters.setPictureSize(picSizes.get(0).width, picSizes.get(0).height);
                myParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                int o = getDeviceOrientation();
                myParameters.setRotation(o);
                this.camera.setDisplayOrientation(o);

                //TODO check for orientation
                myParameters.set("orientation", "portrait");
                this.camera.setParameters(myParameters);
//                mCamera.autoFocus(new Camera.AutoFocusCallback() {
//                    @Override
//                    public void onAutoFocus(boolean success, Camera camera) {
//                        if (success) {
////                            new MediaActionSound().play(MediaActionSound.FOCUS_COMPLETE);
////                            Log.i(TAG, "Foucus is Played");
//                        } else {
//
//                        }
//                    }
//                });
                this.camera.enableShutterSound(true);
                try {
                    camera.setPreviewDisplay(this.holder);
                    camera.startPreview();

                } catch (Exception e){
                    Log.d(TAG, "Error starting camera preview: " + e.getMessage());
                }
            }
        }

    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();

        bestSize = sizeList.get(0);

        for (int i = 1; i < sizeList.size(); i++) {
            if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                bestSize = sizeList.get(i);
            }
        }
        return bestSize;
    }

    public void stopPreviewAndFreeCamera() {

        if (camera != null) {
            // Call stopPreview() to stop updating the preview surface.
            camera.stopPreview();
            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            camera.release();
            camera = null;
        }
    }

    public int getDeviceOrientation(){
        int rotationToReturn = 90;
        Display display = ((WindowManager) this.getContext()
                .getSystemService(this.getContext().WINDOW_SERVICE))
                .getDefaultDisplay();
        int rotation = display.getRotation();

        if (rotation == Surface.ROTATION_0) {
            rotationToReturn = 90;
        } else if (rotation == Surface.ROTATION_270) {
            rotationToReturn = 180;
        }
        return rotationToReturn;
    }
}
