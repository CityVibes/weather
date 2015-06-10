package com.weather.weather.listeners;

import android.view.ViewGroup;
import android.view.animation.Animation;

public class NullificationAnimationListener implements Animation.AnimationListener {
    protected ViewGroup viewGroup;

    public NullificationAnimationListener(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        viewGroup.setAnimation(null);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
