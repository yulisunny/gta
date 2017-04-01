package ca.cvst.gta;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

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
    private SubscriptionType type;

    public PastNotification(String title, String content, float latitude, float longitude, int timestamp, SubscriptionType type) {
        this.title = title;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.type = type;
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
                TtcNotificationEntry.NAME,
                TtcNotificationEntry.BUS_ID,
                TtcNotificationEntry.HEADING,
        };

        Cursor cursor = db.query(TtcNotificationEntry.TABLE_NAME, projection, null, null, null, null, null, String.valueOf(n));
        List<PastNotification> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            float lat = cursor.getFloat(cursor.getColumnIndexOrThrow(TtcNotificationEntry.LATITUDE));
            float lon = cursor.getFloat(cursor.getColumnIndexOrThrow(TtcNotificationEntry.LONGITUDE));
            int timestamp = cursor.getInt(cursor.getColumnIndexOrThrow(TtcNotificationEntry.TIMESTAMP));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(TtcNotificationEntry.NAME));
            int busId = cursor.getInt(cursor.getColumnIndexOrThrow(TtcNotificationEntry.BUS_ID));
            String heading = cursor.getString(cursor.getColumnIndexOrThrow(TtcNotificationEntry.HEADING));
            String content = "<b>" + "Time: " + "</b>" + new Date(((long) timestamp) * 1000L).toString();
            content += "<br /><b>" + "Bus ID: " + "</b>" + busId;
            content += "<br /><b>" + "Direction: " + "</b>" + Helper.calculateDirection(Integer.valueOf(heading));
            PastNotification pn = new PastNotification(name, content, lat, lon, timestamp, SubscriptionType.TTC);
            ret.add(pn);

        }
        cursor.close();
        db.close();
        return ret;
    }

    private static List<PastNotification> loadNAirsense(Context context, int n) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(AirsenseNotificationEntry.TABLE_NAME, null, null, null, null, null, null, String.valueOf(n));
        List<PastNotification> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            float lat = cursor.getFloat(cursor.getColumnIndexOrThrow(AirsenseNotificationEntry.LATITUDE));
            float lon = cursor.getFloat(cursor.getColumnIndexOrThrow(AirsenseNotificationEntry.LONGITUDE));
            int timestamp = cursor.getInt(cursor.getColumnIndexOrThrow(AirsenseNotificationEntry.TIMESTAMP));
            String monitorName = cursor.getString(cursor.getColumnIndexOrThrow(AirsenseNotificationEntry.MONITOR_NAME));
            String filters = cursor.getString(cursor.getColumnIndexOrThrow(AirsenseNotificationEntry.FILTERS));

            String content = "<b>" + "Time: " + "</b>" + new Date(((long) timestamp) * 1000L).toString();
            for (String filterString : TextUtils.split(filters, ",")) {
                Filter filter = Filter.fromString(filterString);
                int columnIndex = cursor.getColumnIndexOrThrow(filter.getFieldName());
                int type = cursor.getType(columnIndex);
                switch (type) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        content += "<br /><b>" + filter.getReadableFieldName() + ": </b>" + cursor.getInt(columnIndex);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        content += "<br /><b>" + filter.getReadableFieldName() + ": </b>" + cursor.getFloat(columnIndex);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        content += "<br /><b>" + filter.getReadableFieldName() + ": </b>" + cursor.getString(columnIndex);
                        break;
                }
            }


            PastNotification pn = new PastNotification(monitorName, content, lat, lon, timestamp, SubscriptionType.AIRSENSE);
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

    public SubscriptionType getType() {
        return type;
    }
}
