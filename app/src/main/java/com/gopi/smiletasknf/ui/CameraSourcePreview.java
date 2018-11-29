package com.gopi.smiletasknf.ui;
/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraSourcePreview extends ViewGroup {
    private static final String TAG = "CameraSourcePreview";

    private Context mContext;
    private SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSource mCameraSource;
    private int rotation = 0;
    private Camera.CameraInfo cameraInfo;
    private static SparseArray ORIENTATIONS = new SparseArray();
    private GraphicOverlay graphicOverlay;
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private static int getDeviceRotation(Camera.CameraInfo cameraInfo
            , int deviceOrientation){
        int cameraOrientation = cameraInfo.orientation;
        deviceOrientation = (int)ORIENTATIONS.get(deviceOrientation);
        return ( cameraOrientation + deviceOrientation + 360 ) % 360;
    }
    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);
    }

    public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException {
        graphicOverlay = overlay;
        Log.d(TAG, "Start was Called");
        if (cameraSource == null) {
            stop();
        }
        mCameraSource = cameraSource;
        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady();
        }
    }

    private void startIfReady() throws IOException {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource.start(mSurfaceView.getHolder());
            if (graphicOverlay != null) {
                int cameraRotation = getDeviceRotation(this.cameraInfo, this.rotation);
                cameraSetup(this.mCameraSource, cameraRotation);
                Size size = this.mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
//                cameraFocus(mCameraSource, Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                if (isPortraitMode()) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
//                    cameraSetup(this.mCameraSource, 90);
                    graphicOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
                } else {
                    graphicOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
                }
                graphicOverlay.clear();
            }
            mStartRequested = false;
        }
    }

    public void stop() {
        if (mCameraSource != null) {
            mCameraSource.stop();
//            mCameraSource.release();
//            mCameraSource = null;
        }
    }

    //TODO: set a default preview size
    public static Camera.Size getBestPreviewSize(Camera.Parameters parameters) {
        Camera.Size bestSize = null;
        if(parameters != null){
            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();//parameters.getSupportedPreviewSizes();
            bestSize = sizeList.get(0);
            for (int i = 1; i < sizeList.size(); i++) {
                if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                    bestSize = sizeList.get(i);
                }
            }
        }
        return bestSize;
    }

    /*
    Borrowed from: https://gist.github.com/Gericop/7de0b9fdd7a444e53b5a
 */
    public static boolean cameraSetup(@NonNull CameraSource cameraSource, int rotation) {
        Log.d(TAG, "Setting Camera focus");
        Field[] declaredFields = CameraSource.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();
                        Log.d(TAG, "Has Camera");
                        params.setJpegQuality(100);
                        params.setJpegThumbnailQuality(100);
                        //TODO for 1+1
//                        List<Camera.Size> picSizes = params.getSupportedPictureSizes();
                        //Best Quality
                        params.setRotation(rotation);
                        camera.setParameters(params);
//                        camera.setDisplayOrientation(rotation);
                        Log.d(TAG, "Focus mode was set");
                        return true;
                    }
                    Log.d(TAG, "Camera is null");
                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to set camera parameters", e);
                }
                break;
            }
        }
        return false;
    }

    public static List<Camera.Size> getCameraPreviewSizes(@NonNull CameraSource cameraSource) {
        Log.d(TAG, "Setting Camera focus");
        List<Camera.Size> parameters = null;
        Field[] declaredFields = CameraSource.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();
                        parameters = params.getSupportedPictureSizes();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to set camera parameters", e);
                }
                break;
            }
        }
        return parameters;
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            mSurfaceAvailable = true;
            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 1080;
        int height = 1920;
        if (mCameraSource != null) {
            Size size = mCameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;

        // Computes height and width for potentially doing fit width.
        int childWidth = layoutWidth;
        int childHeight = (int)(((float) layoutWidth / (float) width) * height);

        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight ){//&& cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            childHeight = layoutHeight;
            childWidth = (int)(((float) layoutHeight / (float) height) * width);
        }
//        } else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
//            childWidth = layoutWidth;
//            childHeight = layoutHeight;
//        }
        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(0, 0, childWidth, childHeight);
        }


        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void setCameraInfo(Camera.CameraInfo cameraInfo) {
        this.cameraInfo = cameraInfo;
    }

    private static class CompareSizeByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long)lhs.getWidth() * (long) lhs.getHeight() /
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    public static Size getOptimalSize(List<Camera.Size> sizes, int width, int height){
        ArrayList<Size> optimalSizeOptions = new ArrayList<>();
        Size sizeToReturn = new Size(sizes.get(0).width
                , sizes.get(0).height);
        for(Camera.Size size : sizes){
            if(size.height == size.width * height / width &&
                    size.width >= width && size.height >= height){
                optimalSizeOptions.add(new Size(size.width, size.height));
            }
        }
        if(optimalSizeOptions.size() > 0 ){
            sizeToReturn =  Collections.min(optimalSizeOptions, new CompareSizeByArea());
        }
        return sizeToReturn;
    }

    //Camera.Parameters myParameters = this.camera.getParameters();
//    final Camera.Size bestPreviewSize = getBestPreviewSize(width, height, myParameters);
}
