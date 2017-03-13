package ca.cvst.gta;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.TtcNotificationContract.TtcNotificationEntry;

public class PastNotification {

    private String title;
    private String line1name;
    private String line1value;
    private String line2name;
    private String line2value;
    private float latitude;
    private float longitude;

    public PastNotification(String title, String line1name, String line1value, float latitude, float longitude) {
        this.title = title;
        this.line1name = line1name;
        this.line1value = line1value;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static List<PastNotification> loadNFromDb(Context context, int n) {
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
            PastNotification pn = new PastNotification("TTC", "Time", new Date(Long.valueOf(timestamp) * 1000).toString(), lat, lon);
            pn.setLine2("Route Number", routeNumber);
            ret.add(pn);

        }
        cursor.close();
        db.close();
        return ret;

    }

    public String getLine2name() {
        return line2name;
    }

    public String getLine2value() {
        return line2value;
    }

    public String getLine1value() {
        return line1value;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLine2(String line2name, String line2value) {
        this.line2name = line2name;
        this.line2value = line2value;
    }

    public String getTitle() {
        return title;
    }

    public String getLine1name() {
        return line1name;
    }

}
