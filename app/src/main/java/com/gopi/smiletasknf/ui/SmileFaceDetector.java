package com.gopi.smiletasknf.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.gopi.smiletasknf.R;

import java.io.IOException;
import java.util.Comparator;

public final class SmileFaceDetector{
    FaceDetector faceDetector;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    private static final String TAG = "Smile Face Detector";
    private int cameraType;
    private SmileTrackerCallback smileTrackerCallback;
    private Activity activity;

    public void init(Activity activity, View view
            , int cameraType
            , SmileTrackerCallback smileTrackerCallback){
//        startDetectorAndCamera(camera, context);
        this.cameraType = cameraType;
        this.smileTrackerCallback = smileTrackerCallback;
        this.activity = activity;
        initViews(view);
    }
    protected void initViews(View view){
        this.preview = (CameraSourcePreview) view.findViewById(R.id.preview);
        this.graphicOverlay = (GraphicOverlay) view.findViewById(R.id.faceOverlay);
    }
    public void startCameraPreview(){
        Log.d(TAG, "Start camera and preview");
        try {
            this.startDetectorAndCamera(this.cameraType
                    , this.graphicOverlay.getContext());
            this.preview.setCameraInfo(this.getCameraInfo());
            this.preview.start(cameraSource, this.graphicOverlay);
            this.preview.setRotation(this
                    .activity
                    .getWindowManager()
                    .getDefaultDisplay()
                    .getRotation());
            this.preview.requestLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopPreviewAndFreeCamera() {
        if (cameraSource != null) {
            Log.d(TAG, "Stopping camera and preview");
            // Call stopPreview() to stop updating the preview surface.
            this.preview.stop();
            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            cameraSource.release();
            cameraSource = null;
            this.faceDetector.release();
            this.faceDetector = null;
        }
    }
    public void switchCameras(){
        Log.d(TAG, "Switching cameras");
        this.stopPreviewAndFreeCamera();
        if(this.cameraType == CameraSource.CAMERA_FACING_BACK){
            this.cameraType =  CameraSource.CAMERA_FACING_FRONT;
        }else{
            this.cameraType =  CameraSource.CAMERA_FACING_BACK;
        }
        this.startCameraPreview();
    }
    protected void startDetectorAndCamera(int cameraType
            , Context context){
        Log.d(TAG, "Init detector and camera");
        this.cameraType = cameraType;
        initFaceDetector(context);
        initCameraSource(context);
    }
    protected void initFaceDetector(Context context){
        this.faceDetector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        this.faceDetector.setProcessor(
                new MultiProcessor.Builder<>(
                        new GraphicFaceTrackerFactory(
                                new SmileDetector(smileTrackerCallback))
                ).build());
        Log.d(TAG, "Building face detector");
    }
    protected void initCameraSource(Context context) {
        Log.d(TAG, "Building camera source");
        Camera camera = isCameraAvailable();
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size bestPreviewSize = CameraSourcePreview.getBestPreviewSize(parameters);
        Size optimalSize = new Size(bestPreviewSize.width, bestPreviewSize.height);
        camera.release();
        this.cameraSource = new CameraSource.Builder(context, faceDetector)
//                .setRequestedPreviewSize(bestPreviewSize.width, bestPreviewSize.height)
                .setRequestedPreviewSize(optimalSize.getWidth(), optimalSize.getHeight())
                .setFacing(cameraType)
                .setRequestedFps(18.0f)
                .setAutoFocusEnabled(true)
                .build();
    }
    public void takeAPicture(CameraSource.PictureCallback pictureCallback){
        if(this.cameraSource != null)
            this.cameraSource.takePicture(null, pictureCallback);
    }
    public Camera isCameraAvailable() {
        Camera object = null;
        try {
            object = Camera.open(this.cameraType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
    public boolean isOperational(Context context){
        FaceDetector fd = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        boolean isOperational = fd.isOperational();
        fd.release();
        return isOperational;
    }
    private Camera.CameraInfo getCameraInfo(){
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(this.cameraType, info);
        return info;
    }

    private static class CompareSizeByArea implements Comparator<Size>{

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long)lhs.getWidth() * (long) lhs.getHeight() /
            (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private final class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        private SmileDetector smileDetector;

        public GraphicFaceTrackerFactory(SmileDetector smileDetector) {
            this.smileDetector = smileDetector;
        }

        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(graphicOverlay
                    , this.smileDetector);
        }
    }
    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private final class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;
        private SmileDetector smileDetector;

        GraphicFaceTracker(GraphicOverlay overlay, SmileDetector smileDetector) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
            this.smileDetector = smileDetector;
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
            this.smileDetector.addFace();
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
            this.smileDetector.updateSmilePercentage(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            this.releaseTracker();
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            this.releaseTracker();
            this.smileDetector.removeFace();

        }

        private void releaseTracker(){
            mOverlay.remove(mFaceGraphic);
//            this.smileDetector.removeFace();
        }
    }

    private final class SmileDetector{
        private int numberOfFaces = 0;
        private float totalSmilePercentage = 0;
        // 18 == 2 secs
        private final float smileThreshold = 27;
        private float facesFramesThreshold = 0;
        private final double acceptedSmilePercentage = 0.65;
        private final String TAG = "Smile Detector";
        private SmileTrackerCallback smileTrackerCallback;

        public SmileDetector(SmileTrackerCallback smileTrackerCallback) {
            this.smileTrackerCallback = smileTrackerCallback;
        }
        public void addFace() {
            Log.d(this.TAG, "Add Face");
            // increase numberOfFaces
            this.numberOfFaces ++;
            // Reset smile tracking
            this.resetSmileTracking();
        }
        public void removeFace() {
            Log.d(this.TAG, "Remove Face");
            //decrease numberOfFaces
            this.numberOfFaces --;
            // Reset smile tracking
            this.resetSmileTracking();
        }
        public void updateSmilePercentage(Face face) {
            if((numberOfFaces * smileThreshold) <= facesFramesThreshold
                    && numberOfFaces != 0){
                Log.d(this.TAG, "Checking for a smile in " + this.numberOfFaces + " Face(s)");
                //Check if face or faces are smiling
                if(isSmiling()) {
                    //Do something
                    this.smileTrackerCallback.onPersonSmiling();
                }// else do nothing
                this.resetSmileTracking();
            } else{
                // Increase facesFramesThreshold
                this.facesFramesThreshold ++;
                // Increase totalSmilePercentage
                this.totalSmilePercentage += face.getIsSmilingProbability();
                float smilePercentage = ( this.totalSmilePercentage /
                        ( this.numberOfFaces * this.smileThreshold));
                this.smileTrackerCallback.onUpdateFaces(this.numberOfFaces
                        , smilePercentage);
            }
//            Log.d(this.TAG, "Updated smile percentage");
        }
        public void resetSmileTracking() {
            Log.d(this.TAG, "Reset Smile Tracking");
            //Reset totalSmilePercentage
            this.totalSmilePercentage = 0;
            //Reset facesFramesThreshold
            this.facesFramesThreshold = 0;
        }
        private boolean isSmiling() {
            float avg = ( this.totalSmilePercentage /
                    ( this.numberOfFaces * this.smileThreshold ) );
            Log.d(TAG, "Smile for "+this.smileThreshold+" frames average is "+avg);
            return  avg > this.acceptedSmilePercentage;
        }
    }

    public interface SmileTrackerCallback{
        void onPersonSmiling();
        void onUpdateFaces(int numberOfFaces, float happiness);
    }
}
