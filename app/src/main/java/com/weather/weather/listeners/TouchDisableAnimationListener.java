package com.weather.weather.listeners;

import android.animation.Animator;
import android.view.View;

public class TouchDisableAnimationListener implements Animator.AnimatorListener {
    protected View viewToDisable;
    protected View.OnTouchListener touchListener;

    public TouchDisableAnimationListener(View viewToDisable, View.OnTouchListener touchListener) {
        this.viewToDisable = viewToDisable;
        this.touchListener = touchListener;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        viewToDisable.setOnTouchListener(null);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        viewToDisable.setOnTouchListener(touchListener);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
