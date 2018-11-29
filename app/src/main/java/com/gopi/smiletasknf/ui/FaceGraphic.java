package com.gopi.smiletasknf.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;


        import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import com.google.android.gms.vision.face.Face;
import com.gopi.smiletasknf.R;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
            Color.CYAN,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private Context context;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        this.context = overlay.getContext();

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);


        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        //Get image resource according to the person's state
        int stateDrawable = 0;
        float smileProbability = face.getIsSmilingProbability() * 100;
        if (smileProbability <= 30){
            stateDrawable = R.drawable.ic_sentiment_dissatisfied_black_48dp;
        } else if (30 < smileProbability && smileProbability <= 60){
            stateDrawable = R.drawable.ic_sentiment_satisfied_black_48dp;
        } else if (60 < smileProbability) {
            stateDrawable = R.drawable.ic_sentiment_very_satisfied_black_48dp;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawOval(left
                    , top
                    , right
                    , bottom
                    , mBoxPaint);
        } else {
            canvas.drawCircle(x
                    , y
                    , Math.max(xOffset, yOffset)
                    , mBoxPaint);
        }
        Resources res = this.context.getResources();
        Bitmap bt = BitmapFactory.decodeResource(res
                ,stateDrawable);
        canvas.drawBitmap(bt
                , left - 5.0f
                , top - 5.0f
                , new Paint());
    }
}

