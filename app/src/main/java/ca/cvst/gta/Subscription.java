package ca.cvst.gta;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.TtcSubscriptionsContract.TtcSubscriptionEntry;

public class Subscription {

    private String title;
    private String content;
    private int timestamp;

    public Subscription(String title, String content, int timestamp) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public static List<Subscription> loadAll(Context context) {
        List<Subscription> ttbSubs = loadAllTtc(context);
        List<Subscription> airSenseSubs = loadAllAirsense(context);
        List<Subscription> all = new ArrayList<>(ttbSubs);
        all.addAll(airSenseSubs);

        Collections.sort(all, new Comparator<Subscription>() {
            @Override
            public int compare(Subscription o1, Subscription o2) {
                if (o1.timestamp < o2.timestamp) {
                    return -1;
                } else if (o1.timestamp > o2.timestamp) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        System.out.println(all);
        return all;
    }

    private static List<Subscription> loadAllTtc(Context context) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                TtcSubscriptionEntry.NAME,
                TtcSubscriptionEntry.TIMESTAMP,
        };

        Cursor cursor = db.query(TtcSubscriptionEntry.TABLE_NAME, projection, null, null, null, null, null);
        List<Subscription> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            int timestamp = cursor.getInt(cursor.getColumnIndexOrThrow(TtcSubscriptionEntry.TIMESTAMP));
            Date date = new Date(timestamp * 1000L);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm, MMM dd", Locale.CANADA);
            String createAt = "Created at: " + format.format(date);
            Subscription sub = new Subscription("TTC", createAt, timestamp);
            ret.add(sub);
        }
        cursor.close();
        db.close();
        return ret;
    }

    private static List<Subscription> loadAllAirsense(Context context) {
        return new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    enum Type {
        TTC,
        AIRSENSE
    }
}
