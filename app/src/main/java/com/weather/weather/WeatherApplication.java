package com.weather.weather;

import android.app.Application;

/**
 * Current applications most root class.ß
 */
public class WeatherApplication extends Application {
    private static WeatherApplication application;
    private static SqlLiteHelper sqliteHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        if (application == null) application = this;
        if (sqliteHelper == null) sqliteHelper = new SqlLiteHelper(this);
    }

    /**
     * Retrieves an instance of this application.
     *
     * @return Current instance of this application.
     */
    public static synchronized WeatherApplication getInstance() {
        return application;
    }

    public static SqlLiteHelper getSqliteHelper() {
        return sqliteHelper;
    }
}
