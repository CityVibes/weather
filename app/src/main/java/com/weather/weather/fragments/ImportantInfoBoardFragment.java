package com.weather.weather.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weather.weather.R;

/**
 * Information board displaying beautifully animated, most important information to a used.
 */
public class ImportantInfoBoardFragment extends Fragment {
    //------------------VARIABLES--------------------
    protected View view;
    protected Activity activity;
    protected ImageView icon;
    protected TextView mainMsg;
    protected TextView info;

    //-------------------STATUS----------------------

    public ImportantInfoBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_important_info_board, container, false);
        init();
        return view;
    }

    //-----------------MAIN-METHODS-----------------

    protected void init() {
        activity = getActivity();

        icon = (ImageView) view.findViewById(R.id.important_icon);
        mainMsg = (TextView) view.findViewById(R.id.important_main_msg);
        info = (TextView) view.findViewById(R.id.important_information);

        //TODO connect with actual weather forecast
        setMainMessage("Starting at 11:10");
        setInfoMessage("Rain for 45 min.");
    }

    public void setMainMessage(String msg) {
        mainMsg.setText(msg);
    }

    public void setInfoMessage(String msg) {
        info.setText(msg);
    }
}
