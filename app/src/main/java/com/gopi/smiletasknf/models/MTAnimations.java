package com.gopi.smiletasknf.models;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

public class MTAnimations {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    public static void captureAnimation(final View viewBackground
            , final ImageView imageView) {
        final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
        final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
        viewBackground.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        int backGroundMilliseconds = 400;
        int smileMilliseconds = 500;
        viewBackground.setScaleY(0.1f);
        viewBackground.setScaleX(0.1f);
        viewBackground.setAlpha(1f);
        imageView.setScaleY(0.1f);
        imageView.setScaleX(0.1f);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(viewBackground, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(backGroundMilliseconds);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(viewBackground, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(backGroundMilliseconds);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(viewBackground, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(backGroundMilliseconds);
        bgAlphaAnim.setStartDelay(backGroundMilliseconds);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(imageView, "scaleY", 0.1f, 2f);
        imgScaleUpYAnim.setDuration(smileMilliseconds);
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(imageView, "scaleX", 0.1f, 2f);
        imgScaleUpXAnim.setDuration(smileMilliseconds);
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(imageView, "scaleY", 2f, 0f);
        imgScaleDownYAnim.setDuration(smileMilliseconds);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(imageView, "scaleX", 2f, 0f);
        imgScaleDownXAnim.setDuration(smileMilliseconds);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetCaptureAnimationStates(viewBackground, imageView);
            }
        });
        animatorSet.start();

    }

    public static void resetCaptureAnimationStates(View viewBackground
            , ImageView imageView) {
        viewBackground.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
    }
}
