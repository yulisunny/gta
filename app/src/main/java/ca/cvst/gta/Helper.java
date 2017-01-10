package ca.cvst.gta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by harryyu on 2017-01-09.
 */

public class Helper {

    static final ArrayList<String> ttcDirections =
            new ArrayList<String>(Arrays.asList("N","NNE","NE","ENE","E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"));

    public static String calculateDirection(int degree) {
        int val = (int) floor(degree / 22.5 + 0.5);
        return ttcDirections.get(val%16);
    }

    public static String convertTimestampToString(long timestamp) {
        // recognized that the timestamp is supposed to be 13 digit long in 2017, but it's only ten.
        Date date = new Date(timestamp * 1000);
        return date.toString();
    }

    public static boolean isNearby(double currentLat, double currentLong, double latitude, double longitude) {
        double cLat = abs(currentLat);
        double cLong = abs(currentLong);

        double targetLat = abs(latitude);
        double targetLong = abs(longitude);

        if (targetLat > cLat - 0.015 && targetLat < cLat + 0.015
                && targetLong > cLong - 0.015 && targetLong < cLong + 0.015) {
            return true;
        }

        return false;
    }
}
