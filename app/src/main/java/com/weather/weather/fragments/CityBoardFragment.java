package com.weather.weather.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.weather.weather.R;
import com.weather.weather.SqlLiteHelper;
import com.weather.weather.WeatherApplication;
import com.weather.weather.interfaces.WeatherInterface;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Board displaying currently selected city and option to choose/create other cities.
 */
public class CityBoardFragment extends Fragment {
    //----------------VARIABLES-----------------
    protected View view;
    protected Activity activity;
    protected String[] cities = {"Amsterdam", "Rotterdam", "Hague", "Utrecht", "Maastricht", "Asgard"};
    protected WeatherInterface weatherInterface;
    protected TextView cityNameTitle;
    protected TextView lastUpdateText;
    protected String cityName = "Amsterdam";

    //----------------LISTENERS-----------------

    View.OnClickListener addCityClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            View dialogLayout = inflater.inflate(R.layout.city_picker_dialog, new LinearLayout(activity), false);

            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(dialogLayout);
            final AlertDialog dialog = builder.show();

            final NumberPicker cityPicker = (NumberPicker) dialogLayout.findViewById(R.id.city_picker);
            dialogLayout.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialogLayout.findViewById(R.id.dialog_pick).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeatherApplication.getSqliteHelper().addCity(cities[cityPicker.getValue()]);
                    showCityList();
                    dialog.dismiss();
                }
            });

            cityPicker.setMinValue(0);
            cityPicker.setMaxValue(cities.length - 1);
            cityPicker.setDisplayedValues(cities);
            cityPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        }
    };

    AdapterView.OnItemClickListener cityListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String cityName = (String) parent.getItemAtPosition(position);
            weatherInterface.flipCards();
            weatherInterface.setCity(cityName);
        }
    };

    //----------------STATUS-----------------------

    public CityBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_city_board, container, false);
        initialise();
        return view;
    }

    //-----------------MAIN-METHODS-----------------

    protected void initialise() {
        view.findViewById(R.id.add_city_btn).setOnClickListener(addCityClick);
        cityNameTitle = (TextView) view.findViewById(R.id.city_name_txt);
        lastUpdateText = (TextView) view.findViewById(R.id.last_update_txt);
        activity = getActivity();
        weatherInterface = (WeatherInterface) activity;
        showCityList();
        setCity(cityName);
        updateData();
    }

    /**
     * Displays a list of cities from which current city might be chosen.
     */
    protected void showCityList() {
        SqlLiteHelper localDb = WeatherApplication.getSqliteHelper();
        //adding default city
        localDb.addCity(cities[0]);

        ArrayList<String> chosenCities = localDb.getAllCities();

        if (chosenCities.size() > 1) {
            ListView cityList = (ListView) view.findViewById(R.id.city_list);
            cityList.setOnItemClickListener(cityListItemClick);
            ArrayAdapter adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, chosenCities);
            cityList.setAdapter(adapter);
        }
    }

    /**
     * Updates current city data, and sets last update time.
     */
    protected void updateData() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        lastUpdateText.setText("last update " + hour + ":" + minutes);
    }

    /**
     * Set current city of this board.
     *
     * @param cityName Current city name.
     */
    public void setCity(String cityName) {
        this.cityName = cityName;

        if (cityNameTitle != null) {
            cityNameTitle.setText(cityName);
        }
    }

    /**
     * Get current city name.
     *
     * @return Current city name.
     */
    public String getCity() {
        return cityName;
    }
}
