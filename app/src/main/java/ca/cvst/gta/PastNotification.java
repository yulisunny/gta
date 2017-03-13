package ca.cvst.gta;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.TtcNotificationContract.TtcNotificationEntry;

public class PastNotification {

    private String title;
    private String content;
    private float latitude;
    private float longitude;

    public PastNotification(String title, String content, float latitude, float longitude) {
        this.title = title;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static List<PastNotification> loadNFromDb(Context context, int n) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                TtcNotificationEntry.LATITUDE,
                TtcNotificationEntry.LONGITUDE,
                TtcNotificationEntry.TIMESTAMP
        };

        Cursor cursor = db.query(TtcNotificationEntry.TABLE_NAME, projection, null, null, null, null, null, String.valueOf(n));
        List<PastNotification> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            float lat = cursor.getFloat(cursor.getColumnIndexOrThrow(TtcNotificationEntry.LATITUDE));
            float lon = cursor.getFloat(cursor.getColumnIndexOrThrow(TtcNotificationEntry.LONGITUDE));
            int timestamp = cursor.getInt(cursor.getColumnIndexOrThrow(TtcNotificationEntry.TIMESTAMP));
            ret.add(new PastNotification("TTC title", String.valueOf(timestamp), lat, lon));
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
