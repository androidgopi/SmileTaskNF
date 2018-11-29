package com.gopi.smiletasknf.faceTrackerActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.vision.CameraSource;
import com.gopi.smiletasknf.R;
import com.gopi.smiletasknf.models.FileOperations;
import com.gopi.smiletasknf.models.ImageUtils;
import com.gopi.smiletasknf.models.MTAnimations;
import com.gopi.smiletasknf.ui.SmileFaceDetector;

public class FaceTrackerActivity extends AppCompatActivity
        implements SmileFaceDetector.SmileTrackerCallback
        , CameraSource.PictureCallback{
    private static final int CAMERA_PERMISSION_REQUEST = 2;
    private static final String TAG = "FaceTrackerActivity";
    private SmileFaceDetector smileFaceDetector;
    private View backgroundView;
    private ImageView smileImage;
    private SimpleDraweeView imagePreview;
    private RelativeLayout imagePreviewCard;
    private ProgressBar smileMeter;
    private TextView funnyText;
    private final String IMAGE_PREVIEW_TAG = "ImagePreview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_tracker);
        init();
        this.checkForCameraPermission();
    }

    private void hideSystemUI(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    protected void init(){
        this.initViews();
    }

    protected void initViews(){
        this.backgroundView = findViewById(R.id.viewBackground);
        this.smileImage = (ImageView)findViewById(R.id.smileImage);
        this.imagePreview = (SimpleDraweeView) findViewById(R.id.imagePreview);
        this.imagePreviewCard = (RelativeLayout) findViewById(R.id.imagePreviewCard);
        this.smileMeter = (ProgressBar) findViewById(R.id.smileMeter);
        this.smileMeter.getProgressDrawable().setColorFilter(
                Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
        this.smileMeter.getIndeterminateDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        this.smileMeter.setProgress(0);
        this.funnyText = (TextView) findViewById(R.id.funnyText);

    }

    protected void initFaceDetector(){
        this.smileFaceDetector = new SmileFaceDetector();
    }

    private void checkForCameraPermission(){
        int requestPermission = ActivityCompat.checkSelfPermission(this
                , Manifest.permission.CAMERA);
        if (requestPermission ==
                PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions
                    , this.CAMERA_PERMISSION_REQUEST);
            return;
        }

    }
    private void createCameraSource() {
        this.initFaceDetector();
        this.smileFaceDetector.init(this
                , findViewById(android.R.id.content)
                , CameraSource.CAMERA_FACING_FRONT
                , this);
    }

    @Override
    public void onPersonSmiling() {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                MTAnimations.captureAnimation(backgroundView
                        , smileImage);
            }
        };
        this.runOnUiThread(myRunnable);
        this.smileFaceDetector.takeAPicture(this);
    }

    @Override
    public void onPictureTaken(byte[] bytes) {
        boolean hasWritePermission = true;
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
            hasWritePermission = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this
                    , Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(hasWritePermission) {
            this.smileFaceDetector.stopPreviewAndFreeCamera();
            FileOperations.saveImageToFileSystem(getApplicationContext()
                    , bytes, new FileOperations.AfterImageTaken() {
                        @Override
                        public void afterImageTaken(String imagePath) {


                            imagePreviewCard.setVisibility(View.VISIBLE);
                            ImageUtils.requestImageResize(imagePreview.getWidth()
                                    , imagePreview.getHeight()
                                    , Uri.parse(ImageUtils.FRESCO_FILE
                                            + imagePath)
                                    , imagePreview);
                        }
                    });
        }
    }

    public void acceptPicture(View view){
        imagePreviewCard.setVisibility(View.INVISIBLE);
        this.smileFaceDetector.startCameraPreview();

    }

    @Override
    public void onUpdateFaces(final int numberOfFaces, final float happiness) {
        Runnable updateView = new Runnable() {
            @Override
            public void run() {
                int smilePercentage = (int)(happiness * 100);
                smileMeter.setProgress(smilePercentage);
                funnyText.setText(getTextForSmilePercentage(smilePercentage));
            }
        };
        this.runOnUiThread(updateView);

    }

    public String getTextForSmilePercentage(float smilePercentage){
        String funnyText = "";
        if (smilePercentage <= 30){
            funnyText = this.getString(R.string.encourage_comment_1);
        } else if (30 < smilePercentage && smilePercentage <= 60){
            funnyText = this.getString(R.string.tease_comment_1);
        } else if (60 < smilePercentage) {
            funnyText = this.getString(R.string.praise_comment_1);
        }
        return funnyText;
    }

    public void switchCamera(View view){
        this.smileFaceDetector.switchCameras();
    }
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        if(this.smileFaceDetector != null)
            if (!this.smileFaceDetector.isOperational(this)) {
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Smile App")
                        .setMessage(R.string.isOperational_snackbar_title)
                        .setPositiveButton(R.string.isOperational_snackbar_action, listener)
                        .show();
            } else {
                this.smileFaceDetector.startCameraPreview();
            }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(this.smileFaceDetector != null)
            this.smileFaceDetector.stopPreviewAndFreeCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != CAMERA_PERMISSION_REQUEST) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Smile App")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.dialog_ok, listener)
                .show();
    }
}
