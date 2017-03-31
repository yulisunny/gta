package ca.cvst.gta;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.SubscriptionsContract.SubscriptionEntry;

public class Subscription {

    private SubscriptionType type;
    private String title;
    private String content;
    private int timestamp;
    private String subscriptionId;

    public Subscription(SubscriptionType type, String title, String content, int timestamp, String subscriptionId) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.subscriptionId = subscriptionId;
    }

    public static List<Subscription> loadAll(Context context) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                SubscriptionEntry.NAME,
                SubscriptionEntry.TIMESTAMP,
                SubscriptionEntry.TYPE,
                SubscriptionEntry.FILTERS,
                SubscriptionEntry.SUBSCRIPTION_ID
        };

        Cursor cursor = db.query(SubscriptionEntry.TABLE_NAME, projection, null, null, null, null, null);
        List<Subscription> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            int timestamp = cursor.getInt(cursor.getColumnIndexOrThrow(SubscriptionEntry.TIMESTAMP));
            String subscriptionId = cursor.getString(cursor.getColumnIndexOrThrow(SubscriptionEntry.SUBSCRIPTION_ID));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(SubscriptionEntry.TYPE));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(SubscriptionEntry.NAME));
            Date date = new Date(((long) timestamp) * 1000L);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm, MMM dd", Locale.CANADA);
            String content = "Created at: " + format.format(date);
            String filtersString = cursor.getString(cursor.getColumnIndexOrThrow(SubscriptionEntry.FILTERS));
            if (filtersString != null) {
                String[] filtersStringArray = filtersString.split(",");
                for (String filterString : filtersStringArray) {
                    Filter filter = Filter.fromString(filterString);
                    content += "\n" + filter.getFieldName() + " " + filter.getOperation().getSymbol() + " " + filter.getFieldValue();

                }
            }

            Subscription sub = new Subscription(SubscriptionType.valueOf(type.toUpperCase()), name, content, timestamp, subscriptionId);
            ret.add(sub);
        }
        cursor.close();
        db.close();
        return ret;
    }

    public SubscriptionType getType() {
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

}
