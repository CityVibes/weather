package com.weather.weather;

import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class giving other classes easy access to most needed static methods.ß
 */
public class Util {

    /**
     * Load json from json file in assets folder.
     *
     * @param filename Filename of json to load.
     * @return Content of json file as string.
     */
    public static String loadJSONFromAsset(String filename, Activity activity) {
        String json;
        try {
            InputStream is = activity.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
