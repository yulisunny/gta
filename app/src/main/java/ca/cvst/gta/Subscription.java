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

import ca.cvst.gta.db.AirsenseSubscriptionsContract.AirsenseSubscriptionEntry;
import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.TtcSubscriptionsContract.TtcSubscriptionEntry;

public class Subscription {

    private Type type;
    private String title;
    private String content;
    private int timestamp;
    private String subscriptionId;

    public Subscription(Type type, String title, String content, int timestamp, String subscriptionId) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.subscriptionId = subscriptionId;
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
                    return 1;
                } else if (o1.timestamp > o2.timestamp) {
                    return -1;
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
                TtcSubscriptionEntry.SUBSCRIPTION_ID
        };

        Cursor cursor = db.query(TtcSubscriptionEntry.TABLE_NAME, projection, null, null, null, null, null);
        List<Subscription> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            int timestamp = cursor.getInt(cursor.getColumnIndexOrThrow(TtcSubscriptionEntry.TIMESTAMP));
            String subscriptionId = cursor.getString(cursor.getColumnIndexOrThrow(TtcSubscriptionEntry.SUBSCRIPTION_ID));
            Date date = new Date(timestamp * 1000L);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm, MMM dd", Locale.CANADA);
            String createAt = "Created at: " + format.format(date);
            Subscription sub = new Subscription(Type.TTC, "TTC", createAt, timestamp, subscriptionId);
            ret.add(sub);
        }
        cursor.close();
        db.close();
        return ret;
    }

    private static List<Subscription> loadAllAirsense(Context context) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                AirsenseSubscriptionEntry.NAME,
                AirsenseSubscriptionEntry.TIMESTAMP,
                AirsenseSubscriptionEntry.SUBSCRIPTION_ID
        };

        Cursor cursor = db.query(AirsenseSubscriptionEntry.TABLE_NAME, projection, null, null, null, null, null);
        List<Subscription> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            int timestamp = cursor.getInt(cursor.getColumnIndexOrThrow(AirsenseSubscriptionEntry.TIMESTAMP));
            String subscriptionId = cursor.getString(cursor.getColumnIndexOrThrow(AirsenseSubscriptionEntry.SUBSCRIPTION_ID));
            Date date = new Date(timestamp * 1000L);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm, MMM dd", Locale.CANADA);
            String createAt = "Created at: " + format.format(date);
            Subscription sub = new Subscription(Type.AIRSENSE, "Airsense", createAt, timestamp, subscriptionId);
            ret.add(sub);
        }
        cursor.close();
        db.close();
        return ret;
    }

    public Type getType() {
        return type;
    }

    public String getSubscriptionId() {
        return subscriptionId;
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
