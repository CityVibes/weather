package com.weather.weather.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weather.weather.R;
import com.weather.weather.models.DailyWeatherItem;

import java.util.ArrayList;

public class DailyWeatherAdapter extends ArrayAdapter<DailyWeatherItem> {
    //--------------------VARIABLES-------------------------
    protected Activity activity;

    //-------------------MAIN-METHODS-----------------------

    /**
     * Static inner class to keep reference to list item views.
     */
    static class ViewHolder {
        public ImageView icon;
        public TextView temperature;
        public TextView time;
    }

    public DailyWeatherAdapter(Activity activity, ArrayList<DailyWeatherItem> items) {
        super(activity, R.layout.weather_list_item, items);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = setRowView(convertView, parent);
        ViewHolder holder = (ViewHolder) rowView.getTag();
        DailyWeatherItem item = getItem(position);

        //TODO this should be validated for null and etc
        setThumbnail(holder, item.getIcon());
        holder.time.setText(item.getTime());
        holder.temperature.setText(item.getTemperature() + " \u2103");
        return rowView;
    }

    private void setThumbnail(ViewHolder holder, String icon) {
        //TODO check if resource actually exist
        int resID = activity.getResources().getIdentifier(icon, "mipmap", activity.getPackageName());
        Bitmap iconBitmap = BitmapFactory.decodeResource(activity.getResources(), resID);
        holder.icon.setImageBitmap(iconBitmap);
    }

    /**
     * Sets how an item within list will look, keep reference of item views in
     * static inner class variables.
     */
    protected View setRowView(View rowView, ViewGroup parent) {
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.weather_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) rowView.findViewById(R.id.weather_icon);
            viewHolder.temperature = (TextView) rowView.findViewById(R.id.weather_temperature);
            viewHolder.time = (TextView) rowView.findViewById(R.id.weather_time);
            rowView.setTag(viewHolder);
        }
        return rowView;
    }
}
