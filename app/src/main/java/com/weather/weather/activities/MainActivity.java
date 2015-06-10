package com.weather.weather.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weather.weather.R;
import com.weather.weather.fragments.CityBoardFragment;
import com.weather.weather.fragments.DailyTimesBoardFragment;
import com.weather.weather.fragments.ImportantInfoBoardFragment;
import com.weather.weather.fragments.TemperatureBoardFragment;
import com.weather.weather.interfaces.WeatherInterface;
import com.weather.weather.listeners.NullificationAnimationListener;
import com.weather.weather.listeners.TouchDisableAnimationListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewsById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Main activity connecting multiple boards/widgets together.
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends Activity implements WeatherInterface {
    //------------------VARIABLES--------------------
    protected Activity activity;
    @ViewsById({R.id.city_container, R.id.temperature_container, R.id.daily_container, R.id.important_container, R.id.map_container})
    protected List<FrameLayout> containers;
    protected CityBoardFragment cityBoardFragmentFront, cityBoardFragmentBack;
    protected TemperatureBoardFragment temperatureBoardFragmentFront, temperatureBoardFragmentBack;
    protected DailyTimesBoardFragment dailyTimesBoardFragmentFront, dailyTimesBoardFragmentBack;
    protected ImportantInfoBoardFragment importantInfoBoardFragmentFront, importantInfoBoardFragmentBack;
    @FragmentById(R.id.map_fragment)
    protected MapFragment mapFragment;
    protected boolean showingBack = false;
    protected Handler handler = new Handler();
    protected boolean moved = false;
    protected boolean disableInteraction = false;

    //different image movement modes
    protected int _yDelta;
    protected float xOriginal, yOriginal;
    protected int lastTouchedContainerIndex;
    protected int originalFrameMargin;

    //------------------LISTENERS--------------------

    View.OnClickListener cityPanelClick = new View.OnClickListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void onClick(View v) {
            if (!disableInteraction) {
                Intent intent = new Intent(activity, CityWeatherActivity_.class);
                intent.putExtra("city", (showingBack) ? cityBoardFragmentBack.getCity() : cityBoardFragmentFront.getCity());

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        new Pair<View, String>(containers.get(0), "cityPanel"));
                ActivityCompat.startActivity(activity, intent, options.toBundle());
            }
        }
    };

    View.OnClickListener temperaturePanelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnClickListener dailyPanelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnClickListener importantPanelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnClickListener mapPanelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnTouchListener dragListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View view, MotionEvent event) {
            if (disableInteraction) {
                return true;
            }

            view.bringToFront();
            final int Y = (int) event.getRawY();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    keepCurrentTouchedViewValues(view, Y);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_UP:
                    findFinalPanelPositions(view);
                    showReversePanelAnimation();
                    animateToOrigin(view, this);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(view.getTranslationY() - yOriginal) > 2) {
                        moved = true;
                    }
                    drag(Y, view);
                    view.setLayoutParams(layoutParams);
                    adjustPanelMargins(view);
                    break;
            }

            return moved;
        }
    };

    //-------------------STATUS----------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO doesn't always refresh, to avoid issues with map it should be extended and used appropriately
        if (mapFragment != null) {
            mapFragment.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //-----------------MAIN-METHODS-----------------

    /**
     * Extracts and/or calculates variety of values from currently touched values and saves those
     * values within activity variables.
     *
     * @param view   Currently touched panel.
     * @param yValue Current Y coordinate.
     */
    protected void keepCurrentTouchedViewValues(View view, int yValue) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        moved = false;
        _yDelta = Math.round(yValue - view.getTranslationY());
        xOriginal = view.getTranslationX();
        yOriginal = view.getTranslationY();
        FrameLayout currentFrameView = (FrameLayout) view;
        lastTouchedContainerIndex = containers.indexOf(currentFrameView);
        originalFrameMargin = layoutParams.topMargin;
    }

    /**
     * Adjust margins of every frame layout in containers collection.
     *
     * @param view Currently controlled(touched) view required for comparison with frame layouts.
     */
    protected void adjustPanelMargins(View view) {
        for (int i = 0; i < containers.size(); i++) {
            final FrameLayout frameLayout = containers.get(i);
            int[] locationFrame = new int[2];
            frameLayout.getLocationOnScreen(locationFrame);
            int[] locationView = new int[2];
            view.getLocationOnScreen(locationView);
            float upperFrameBound = locationFrame[1] - 30;
            float lowerFrameBound = locationFrame[1] + 30;
            Animation currentAnimation = frameLayout.getAnimation();

            if (locationView[1] > upperFrameBound && locationView[1] < lowerFrameBound
                    && i != lastTouchedContainerIndex && currentAnimation == null) {
                frameLayout.bringToFront();
                view.bringToFront();
                showItemAnimation(frameLayout);
            } else if (currentAnimation != null && currentAnimation.hasEnded()) {
                showItemReverseAnimation(frameLayout);
            }
        }
    }

    /**
     * Animate and change the position of panel if it is aligned with other panel.
     * TODO animate swapped frame to original panel
     *
     * @param view Currently controlled(touched) view required for comparison with frame layouts.ß
     */
    protected void findFinalPanelPositions(View view) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        for (int i = 0; i < containers.size(); i++) {
            FrameLayout frameLayout = containers.get(i);
            int[] locationFrame = new int[2];
            frameLayout.getLocationOnScreen(locationFrame);
            int[] locationView = new int[2];
            view.getLocationOnScreen(locationView);
            float upperFrameBound = locationFrame[1] - 30;
            float lowerFrameBound = locationFrame[1] + 30;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();

            if (locationView[1] > upperFrameBound && locationView[1] < lowerFrameBound && i != lastTouchedContainerIndex) {
                int frameLayoutMargin = params.topMargin;
                params.topMargin = originalFrameMargin;
                view.setTranslationY(0);
                yOriginal = 0;
                frameLayout.getAnimation().cancel();
                frameLayout.setAnimation(null);
                layoutParams.topMargin = frameLayoutMargin;
            }
        }
    }

    /**
     * Show a reverse animation of margin expansion.
     */
    protected void showReversePanelAnimation() {
        for (int i = 0; i < containers.size(); i++) {
            final FrameLayout frameLayout = containers.get(i);

            if (i != lastTouchedContainerIndex && frameLayout.getAnimation() != null) {
                showItemReverseAnimation(frameLayout);
            }
        }
    }

    /**
     * Animate panel back to its starting place.
     *
     * @param view          Panel to animate back to its place.
     * @param touchListener Touch listener to be temporary disabled (while animation performs).
     */
    protected void animateToOrigin(View view, View.OnTouchListener touchListener) {
        if (view.getTranslationY() != yOriginal) {
            ViewPropertyAnimator propertyAnimator = view.animate();
            propertyAnimator.translationY(yOriginal);
            propertyAnimator.setListener(new TouchDisableAnimationListener(view, touchListener));
        }
    }

    /**
     * Animate given panel/item view to go down a little.
     *
     * @param animatedView Panel/item view to animate.
     */
    protected void showItemAnimation(ViewGroup animatedView) {
        TranslateAnimation anim = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 100);
        anim.setFillAfter(true);
        anim.setDuration(500);
        animatedView.setAnimation(anim);
        anim.startNow();
    }

    /**
     * Animate panel/item to go up back to its position.
     *
     * @param animatedView Panel/item view to animate.
     */
    protected void showItemReverseAnimation(ViewGroup animatedView) {
        TranslateAnimation animReverse = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 100, Animation.ABSOLUTE, 0);
        animReverse.setDuration(500);
        animReverse.setFillAfter(true);
        animatedView.setAnimation(animReverse);
        animReverse.setAnimationListener(new NullificationAnimationListener(animatedView));
        animReverse.startNow();
    }

    /**
     * Initialise fragments, and display some of them.
     */
    @AfterViews
    protected void initialiseFragments() {
        //front facing fragments
        cityBoardFragmentFront = new CityBoardFragment();
        temperatureBoardFragmentFront = new TemperatureBoardFragment();
        dailyTimesBoardFragmentFront = new DailyTimesBoardFragment();
        importantInfoBoardFragmentFront = new ImportantInfoBoardFragment();
        //back facing fragments
        cityBoardFragmentBack = new CityBoardFragment();
        temperatureBoardFragmentBack = new TemperatureBoardFragment();
        dailyTimesBoardFragmentBack = new DailyTimesBoardFragment();
        importantInfoBoardFragmentBack = new ImportantInfoBoardFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.city_container, cityBoardFragmentFront)
                .add(R.id.temperature_container, temperatureBoardFragmentFront)
                .add(R.id.daily_container, dailyTimesBoardFragmentFront)
                .add(R.id.important_container, importantInfoBoardFragmentFront)
                .commit();

        setCity("Amsterdam");
    }

    /**
     * Initialise fragment listeners.
     */
    @AfterViews
    protected void initialiseFragmentListeners() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int topMargin = 5;
        int frameHeight = size.y / containers.size();

        for (FrameLayout container : containers) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
            layoutParams.height = frameHeight;
            layoutParams.topMargin = topMargin;
            topMargin += frameHeight + 5;
            container.setOnTouchListener(dragListener);
        }

        containers.get(0).setOnClickListener(cityPanelClick);
        containers.get(1).setOnClickListener(temperaturePanelClick);
        containers.get(2).setOnClickListener(dailyPanelClick);
        containers.get(3).setOnClickListener(importantPanelClick);
        containers.get(4).setOnClickListener(mapPanelClick);
    }

    /**
     * Changes left and top margin of a view layout parameters creating a drag effect.
     *
     * @param Y Finger Y coordinate.
     */
    protected void drag(int Y, View view) {
        int newValueY = Y - _yDelta;
        view.setTranslationY(newValueY);
    }

    @Override
    public void flipCards() {
        List<Integer> order = calculateContainersOrder();
        disableInteraction = true;

        if (showingBack) {
            replaceAfterDelay(R.id.city_container, cityBoardFragmentFront, 500 * order.indexOf(0));
            replaceAfterDelay(R.id.temperature_container, temperatureBoardFragmentFront, 500 * order.indexOf(1));
            replaceAfterDelay(R.id.daily_container, dailyTimesBoardFragmentFront, 500 * order.indexOf(2));
            replaceAfterDelay(R.id.important_container, importantInfoBoardFragmentFront, 500 * order.indexOf(3));
            showingBack = false;
        } else {
            showingBack = true;
            replaceAfterDelay(R.id.city_container, cityBoardFragmentBack, 500 * order.indexOf(0));
            replaceAfterDelay(R.id.temperature_container, temperatureBoardFragmentBack, 500 * order.indexOf(1));
            replaceAfterDelay(R.id.daily_container, dailyTimesBoardFragmentBack, 500 * order.indexOf(2));
            replaceAfterDelay(R.id.important_container, importantInfoBoardFragmentBack, 500 * order.indexOf(3));
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                disableInteraction = false;
            }
        }, 500 * order.indexOf(3) + 1000);
        //TODO change 1000 by getting it from xml integers
    }

    @Override
    public void setCity(String cityName) {
        if (!showingBack) {
            cityBoardFragmentFront.setCity(cityName);
        } else {
            cityBoardFragmentBack.setCity(cityName);
        }

        //TODO use real city location
        Random random = new Random();
        int lat = random.nextInt(80);
        int lng = random.nextInt(100);
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(lat, lng);
        markerOptions.position(latLng);
        GoogleMap map = mapFragment.getMap();
        map.clear();
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    /**
     * Replace fragment within given container with given fragment after a delay by displaying transaction animation.
     *
     * @param container Container in which replace of current fragment with given fragment happens.
     * @param fragment  Fragment to replace with.
     * @param delay     Time to delay the execution of fragment replace.
     */
    public void replaceAfterDelay(final int container, final Fragment fragment, int delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.card_flip_down_in,
                                R.anim.card_flip_down_out,
                                R.anim.card_flip_up_in, R.anim.card_flip_up_out)
                        .replace(container, fragment)
                        .commit();
            }
        }, delay);
    }

    /**
     * Calculates panel order within activity layout mapped to actual panel container.
     *
     * @return List of indexes of panels within container reflecting order of actual panels within activity layout.
     */
    public List<Integer> calculateContainersOrder() {
        List<Integer> result = new ArrayList<>();
        SparseArray<Integer> indexAndMarginArray = new SparseArray<>();
        List<Integer> marginList = new ArrayList<>();

        for (int i = 0; i < containers.size(); i++) {
            FrameLayout frameLayout = containers.get(i);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
            indexAndMarginArray.append(params.topMargin, i);
            marginList.add(params.topMargin);
        }

        Collections.sort(marginList);
        for (Integer margin : marginList) {
            result.add(indexAndMarginArray.get(margin));
        }

        return result;
    }
}
