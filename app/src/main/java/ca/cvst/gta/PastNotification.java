package ca.cvst.gta;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ca.cvst.gta.db.AirsenseNotificationsContract.AirsenseNotificationEntry;
import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.TtcNotificationContract.TtcNotificationEntry;

public class PastNotification {

    private String title;
    private String content;
    private float latitude;
    private float longitude;
    private int timestamp;

    public PastNotification(String title, String content, float latitude, float longitude, int timestamp) {
        this.title = title;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public static List<PastNotification> loadNFromDb(Context context, int n) {
        List<PastNotification> ttc = loadNTtc(context, n);
        List<PastNotification> air = loadNAirsense(context, n);
        List<PastNotification> all = new ArrayList<>(ttc);
        all.addAll(air);
        Collections.sort(all, new Comparator<PastNotification>() {
            @Override
            public int compare(PastNotification o1, PastNotification o2) {
                if (o1.timestamp < o2.timestamp) {
                    return 1;
                } else if (o1.timestamp > o2.timestamp) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        if (all.size() > n) {
            return all.subList(0, n);
        } else {
            return all;
        }

    }

    private static List<PastNotification> loadNTtc(Context context, int n) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                TtcNotificationEntry.LATITUDE,
                TtcNotificationEntry.LONGITUDE,
                TtcNotificationEntry.TIMESTAMP,
                TtcNotificationEntry.ROUTE_NUMBER
        };

        Cursor cursor = db.query(TtcNotificationEntry.TABLE_NAME, projection, null, null, null, null, null, String.valueOf(n));
        List<PastNotification> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            float lat = cursor.getFloat(cursor.getColumnIndexOrThrow(TtcNotificationEntry.LATITUDE));
            float lon = cursor.getFloat(cursor.getColumnIndexOrThrow(TtcNotificationEntry.LONGITUDE));
            int timestamp = cursor.getInt(cursor.getColumnIndexOrThrow(TtcNotificationEntry.TIMESTAMP));
            String routeNumber = cursor.getString(cursor.getColumnIndexOrThrow(TtcNotificationEntry.ROUTE_NUMBER));
            String content = "<b>" + "Time: " + "</b>" + new Date(((long) timestamp) * 1000L).toString();
            content += "<br /><b>" + "Route Number: " + "</b>" + routeNumber;
            PastNotification pn = new PastNotification("TTC", content, lat, lon, timestamp);
            ret.add(pn);

        }
        cursor.close();
        db.close();
        return ret;
    }

    private static List<PastNotification> loadNAirsense(Context context, int n) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                AirsenseNotificationEntry.LATITUDE,
                AirsenseNotificationEntry.LONGITUDE,
                AirsenseNotificationEntry.TIMESTAMP
        };

        Cursor cursor = db.query(AirsenseNotificationEntry.TABLE_NAME, projection, null, null, null, null, null, String.valueOf(n));
        List<PastNotification> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            float lat = cursor.getFloat(cursor.getColumnIndexOrThrow(AirsenseNotificationEntry.LATITUDE));
            float lon = cursor.getFloat(cursor.getColumnIndexOrThrow(AirsenseNotificationEntry.LONGITUDE));
            int timestamp = cursor.getInt(cursor.getColumnIndexOrThrow(AirsenseNotificationEntry.TIMESTAMP));
            String content = "<b>" + "Time: " + "</b>" + new Date(((long) timestamp) * 1000L).toString();
            PastNotification pn = new PastNotification("Airsense", content, lat, lon, timestamp);
            ret.add(pn);

        }
        cursor.close();
        db.close();
        return ret;
    }


    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
