package com.weather.weather.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weather.weather.R;
import com.weather.weather.Util;
import com.weather.weather.adapters.DailyWeatherAdapter;
import com.weather.weather.models.DailyWeatherItem;

import org.lucasr.twowayview.TwoWayView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays a horizontal list of weather today (small weather forecast each hour or so).
 * TODO connect with real api
 */
public class DailyTimesBoardFragment extends Fragment {
    //------------------VARIABLES--------------------
    protected View view;
    protected Gson gson = new Gson();
    protected TwoWayView dailyWeatherList;
    protected Activity activity;

    //-------------------STATUS----------------------

    public DailyTimesBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_daily_times_board, container, false);
        init();
        return view;
    }

    //-----------------MAIN-METHODS-----------------

    protected void init() {
        dailyWeatherList = (TwoWayView) view.findViewById(R.id.daily_weather_list);
        activity = getActivity();

        String jsonString = Util.loadJSONFromAsset("daily_weather.json", activity);
        Type listType = new TypeToken<List<DailyWeatherItem>>() {
        }.getType();
        ArrayList<DailyWeatherItem> dailyWeatherItems = gson.fromJson(jsonString, listType);
        DailyWeatherAdapter dailyWeatherAdapter = new DailyWeatherAdapter(activity, dailyWeatherItems);
        dailyWeatherList.setAdapter(dailyWeatherAdapter);
    }

}
