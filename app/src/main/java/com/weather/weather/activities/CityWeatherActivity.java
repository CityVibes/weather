package com.weather.weather.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Display;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.weather.weather.R;
import com.weather.weather.fragments.AdFragment;
import com.weather.weather.fragments.CityBoardFragment;
import com.weather.weather.fragments.CityWeatherDetailsFragment;
import com.weather.weather.interfaces.WeatherInterface;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Activity responsible for displaying information for a single city.
 */
@EActivity(R.layout.activity_city_weather)
public class CityWeatherActivity extends Activity implements WeatherInterface {
    //------------------VARIABLES--------------------
    protected Activity activity;
    protected CityBoardFragment cityBoardFragment;
    protected CityWeatherDetailsFragment cityWeatherDetailsFragment;
    protected AdFragment adFragment;
    @ViewById(R.id.city_container)
    protected FrameLayout cityContainer;
    @ViewById(R.id.city_detail_container)
    protected FrameLayout cityDetailsContainer;
    @ViewById(R.id.city_ad_container)
    protected FrameLayout adContainer;

    //-------------------STATUS----------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition ts = new Fade();
            ts.excludeTarget(android.R.id.statusBarBackground, true);
            ts.excludeTarget(android.R.id.navigationBarBackground, true);
            ts.setDuration(1000);
            getWindow().setEnterTransition(ts);
        } else {
            overridePendingTransition(R.anim.slidein_left_view, R.anim.slideout_left_view);
        }
        activity = this;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slidein_right_view, R.anim.slideout_right_view);
    }

    //-----------------MAIN-METHODS-----------------

    @AfterViews
    protected void initialiseFragments() {
        Intent callingIntent = getIntent();
        String cityName = callingIntent.getStringExtra("city");
        //front facing fragments
        cityBoardFragment = new CityBoardFragment();
        cityBoardFragment.setCity(cityName);
        cityWeatherDetailsFragment = new CityWeatherDetailsFragment();
        adFragment = new AdFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.city_container, cityBoardFragment)
                .add(R.id.city_detail_container, cityWeatherDetailsFragment)
                .add(R.id.city_ad_container, adFragment)
                .commit();
    }

    @AfterViews
    protected void initialiseFragmentListeners() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int topMargin = 5;
        int frameHeight = size.y / 3;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cityContainer.getLayoutParams();
        layoutParams.height = frameHeight / 2;
        layoutParams.topMargin = topMargin;
        topMargin += layoutParams.height + 5;

        RelativeLayout.LayoutParams layoutParamsDetail = (RelativeLayout.LayoutParams) cityDetailsContainer.getLayoutParams();
        layoutParamsDetail.height = frameHeight * 2;
        layoutParamsDetail.topMargin = topMargin;
        topMargin += layoutParamsDetail.height + 5;

        RelativeLayout.LayoutParams layoutParamsAd = (RelativeLayout.LayoutParams) adContainer.getLayoutParams();
        layoutParamsAd.height = frameHeight / 2;
        layoutParamsAd.topMargin = topMargin;
    }

    @Override
    public void flipCards() {

    }

    @Override
    public void setCity(String cityName) {
        cityBoardFragment.setCity(cityName);
    }
}
