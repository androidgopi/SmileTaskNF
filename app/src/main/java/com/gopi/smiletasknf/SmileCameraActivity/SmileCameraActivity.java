package com.gopi.smiletasknf.SmileCameraActivity;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.gopi.smiletasknf.R;


public class SmileCameraActivity extends AppCompatActivity
        implements Camera.PictureCallback{

    private OldCameraView cameraView;
    private FrameLayout cameraPreview;
    private View cameraOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smile_camera);
        this.init();
    }
    protected void init(){
        this.initViews();
    }
    protected void initViews(){
        this.cameraPreview = (FrameLayout) findViewById(R.id.container);
        this.cameraOverlay = findViewById(R.id.cameraPreviewOverlay);
    }
    @Override
    public void onResume() {
        super.onResume();
        cameraView = new OldCameraView(this, this);
        this.cameraPreview.addView(cameraView);
    }
    @Override
    public void onPause() {
        super.onPause();
        this.cameraView.stopPreviewAndFreeCamera();
        this.cameraPreview.removeAllViews();
    }
    /**
     * Called when image data is available after a picture is taken.
     * The format of the data depends on the context of the callback
     * and {@link Camera.Parameters} settings.
     *
     * @param data   a byte array of the picture data
     * @param camera the Camera service object
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }
}
