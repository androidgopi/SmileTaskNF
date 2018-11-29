package com.gopi.smiletasknf.mainActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.FaceDetector;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gopi.smiletasknf.R;
import com.gopi.smiletasknf.faceTrackerActivity.FaceTrackerActivity;
import com.gopi.smiletasknf.mainActivity.Interfaces.FetchAlbumAndImagesInterface;
import com.gopi.smiletasknf.models.FileOperations;
import com.gopi.smiletasknf.models.Item;
import com.gopi.smiletasknf.models.MediaStoreOperations;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener
        , FetchAlbumAndImagesInterface{
//    private SimpleDraweeView smileImage;
    private FaceDetector faceDetector;
    private Canvas tempCanvas;
    private final int CAMERA_PERMISSION_REQUEST = 1;
    private final int STORAGE_READ_PERMISSION_REQUEST = 2;
    private final int STORAGE_WRITE_PERMISSION_REQUEST = 3;
    private final int STORAGE_REQUESTS = 2;
    private View backgroundView;
    private ImageView smileImage;
    private SmilesRecyclerView smilesRecyclerView;
    private TwoWayView mosaicView;
    private MosaicLayoutAdapter mosaicLayoutAdapter;
    private LinearLayout emptyMosaicView;
    private TextView emptyMosaicText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int PERMISSION_ALL = 10;
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        init();
    }

    private boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                } else {
                }
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSmileAlbumImages();

    }

    private void init(){
        initViews();
    }

    private void initViews(){
        this.smileImage = (ImageView)findViewById(R.id.smileImage);
        this.backgroundView = findViewById(R.id.viewBackground);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);


        this.mosaicView = (TwoWayView) findViewById(R.id.mosaicView);
        this.mosaicLayoutAdapter = new MosaicLayoutAdapter(this
                , this.mosaicView
                , new ArrayList<Item>()
                , getBlockSize());
        this.mosaicView.setAdapter(this.mosaicLayoutAdapter);
        this.emptyMosaicView = (LinearLayout) findViewById(R.id.emptyMosaicView);
        this.emptyMosaicText = (TextView) findViewById(R.id.emptyMosaicText);
        setupTwoWayView();
    }

    public int getBlockSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int block = width/3;
        return block;
    }

    public void setupTwoWayView(){
        mosaicView.setHasFixedSize(true);
        mosaicView.setHapticFeedbackEnabled(true);
        mosaicView.setLongClickable(true);
        mosaicView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public void onClick(View view) {
        launchFaceTracker();
    }

    public void launchFaceTracker(){
        Intent intent = new Intent(this
                , FaceTrackerActivity.class);
        startActivity(intent);
    }

    @Override
    public void getSmileAlbumImages() {
        FileOperations.getOrCreateFolder(this);
        Cursor cur = MediaStoreOperations
                    .getGalleryAlbumsCursor(this);
        ArrayList<Item> smileImages = MediaStoreOperations
                .getSmileAlbumFromCursor(cur);
        this.mosaicLayoutAdapter.repopulate(smileImages);
        if(smileImages.size() == 0){
            onEmptyMosaicView();
        }else{
            this.emptyMosaicView.setVisibility(View.INVISIBLE);
        }
    }

    public void onEmptyMosaicView(){
        this.emptyMosaicView.setVisibility(View.VISIBLE);
    }

}
