package com.weather.weather.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.weather.weather.R;

/**
 * Fragment controlling display of detailed city weather information.
 */
public class CityWeatherDetailsFragment extends Fragment {
    //------------------VARIABLES--------------------
    protected View view;
    protected Activity activity;
    protected ImageButton closeDetailsBtn;

    //------------------LISTENERS--------------------

    View.OnClickListener closeDetailsClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO use same animation as normally activity exits, to do so export this to activity and access through interface
            activity.finish();
            activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    };

    //-------------------STATUS----------------------

    public CityWeatherDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_city_weather_details, container, false);
        init();
        return view;
    }

    //-----------------MAIN-METHODS-----------------

    protected void init() {
        activity = getActivity();
        closeDetailsBtn = (ImageButton) view.findViewById(R.id.close_details_btn);
        closeDetailsBtn.setOnClickListener(closeDetailsClicked);
    }
}
