package ca.cvst.gta;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import ca.cvst.gta.db.AirsenseNotificationsContract.AirsenseNotificationEntry;
import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.SubscriptionsContract.SubscriptionEntry;
import ca.cvst.gta.db.TtcNotificationContract.TtcNotificationEntry;


/**
 * This mimics the demo portal. For the proper way of doing this according to Daiqing
 *
 * @see ca.cvst.gta.SubscriptionService
 */
public class UpdatesListenerIntentService extends IntentService {

    private static int airsenseNotifId = 1;

    public UpdatesListenerIntentService() {
        super("UpdatesListenerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.key_username), null);
        System.out.println("username = " + username);
        AsyncHttpClient.getDefaultInstance().websocket("ws://subs.portal.cvst.ca/liveupdate/" + username, null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(final Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        System.out.println("s = " + s);
                        try {
                            JSONObject root = new JSONObject(s);
                            JSONObject data = root.getJSONObject("data");
                            JSONArray subscriptionIds = root.getJSONArray("subscriptionIds");
                            String[] subscriptionIdsArray = new String[subscriptionIds.length()];
                            for (int i = 0; i < subscriptionIds.length(); i++) {
                                subscriptionIdsArray[i] = subscriptionIds.getString(i);
                            }


                            DbHelper dbHelper = new DbHelper(getApplicationContext());
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            String query = "SELECT " +
                                    SubscriptionEntry.TYPE + ", " +
                                    SubscriptionEntry.FILTERS + ", " +
                                    SubscriptionEntry.NAME + ", " +
                                    SubscriptionEntry.MONDAY + ", " +
                                    SubscriptionEntry.TUESDAY + ", " +
                                    SubscriptionEntry.WEDNESDAY + ", " +
                                    SubscriptionEntry.THURSDAY + ", " +
                                    SubscriptionEntry.FRIDAY + ", " +
                                    SubscriptionEntry.SATURDAY + ", " +
                                    SubscriptionEntry.SUNDAY + ", " +
                                    SubscriptionEntry.START_TIME + ", " +
                                    SubscriptionEntry.END_TIME + ", " +
                                    SubscriptionEntry.NOTIFICATION_ENABLED +
                                    " FROM " + SubscriptionEntry.TABLE_NAME + " WHERE " +
                                    SubscriptionEntry.SUBSCRIPTION_ID + " IN (" + makePlaceholders(subscriptionIdsArray.length) + ")";
                            Cursor cursor = db.rawQuery(query, subscriptionIdsArray);

                            while (cursor.moveToNext()) {
                                String type = cursor.getString(cursor.getColumnIndexOrThrow(SubscriptionEntry.TYPE));
                                SubscriptionType subscriptionType = SubscriptionType.valueOf(type.toUpperCase());
                                String filters = cursor.getString(cursor.getColumnIndexOrThrow(SubscriptionEntry.FILTERS));

                                String name = cursor.getString(cursor.getColumnIndex(SubscriptionEntry.NAME));
                                int mon = cursor.getInt(cursor.getColumnIndex(SubscriptionEntry.MONDAY));
                                int tues = cursor.getInt(cursor.getColumnIndex(SubscriptionEntry.TUESDAY));
                                int wed = cursor.getInt(cursor.getColumnIndex(SubscriptionEntry.WEDNESDAY));
                                int thur = cursor.getInt(cursor.getColumnIndex(SubscriptionEntry.THURSDAY));
                                int fri = cursor.getInt(cursor.getColumnIndex(SubscriptionEntry.FRIDAY));
                                int sat = cursor.getInt(cursor.getColumnIndex(SubscriptionEntry.SATURDAY));
                                int sun = cursor.getInt(cursor.getColumnIndex(SubscriptionEntry.SUNDAY));
                                int startTime = cursor.getInt(cursor.getColumnIndex(SubscriptionEntry.START_TIME));
                                int endTime = cursor.getInt(cursor.getColumnIndex(SubscriptionEntry.END_TIME));
                                int notif_enabled = cursor.getInt(cursor.getColumnIndex(SubscriptionEntry.NOTIFICATION_ENABLED));
                                persistNotification(subscriptionType, root);

                                if (notif_enabled > 0 && shouldNotifyNow(startTime, endTime, mon, tues, wed, thur, fri, sat, sun)) {
                                    notifyUser(subscriptionType, data, filters, name);
                                }

                            }
                            cursor.close();
                            db.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                webSocket.setDataCallback(new DataCallback() {
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                        System.out.println("I got some bytes!");
                        // note that this data has been read
                        byteBufferList.recycle();
                    }
                });
            }
        });
    }

    private void persistNotification(SubscriptionType subscriptionType, JSONObject root) {
        switch (subscriptionType) {
            case TTC:
                persistTtc(root);
                break;
            case AIRSENSE:
                persistAirsense(root);
                break;
        }
    }


    private void persistTtc(JSONObject root) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            JSONObject data = root.getJSONObject("data");
            values.put(TtcNotificationEntry.BUS_ID, data.getInt("id"));
            values.put(TtcNotificationEntry.TIMESTAMP, data.getInt("timestamp"));
            values.put(TtcNotificationEntry.DIR_TAG, data.getString("dirTag"));
            values.put(TtcNotificationEntry.NAME, data.getString("name"));
            values.put(TtcNotificationEntry.GPS_TIME, data.getInt("GPStime"));
            values.put(TtcNotificationEntry.LAST_TIME, data.getString("lastTime"));
            values.put(TtcNotificationEntry.LATITUDE, data.getJSONArray("coordinates").getDouble(1));
            values.put(TtcNotificationEntry.LONGITUDE, data.getJSONArray("coordinates").getDouble(0));
            values.put(TtcNotificationEntry.DATE_TIME, data.getString("dateTime"));
            values.put(TtcNotificationEntry.HEADING, data.getString("heading"));
            values.put(TtcNotificationEntry.PREDICTABLE, data.getBoolean("predictable"));
            values.put(TtcNotificationEntry.ROUTE_NUMBER, data.getString("routeNumber"));
            JSONArray subscriptionIds = root.getJSONArray("subscriptionIds");
            values.put(TtcNotificationEntry.SUBSCRIPTION_IDS, subscriptionIds.join(","));
            db.insert(TtcNotificationEntry.TABLE_NAME, null, values);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.close();

    }

    private void persistAirsense(JSONObject root) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            JSONObject data = root.getJSONObject("data");
            values.put(AirsenseNotificationEntry.TIMESTAMP, data.getInt("timestamp"));
            values.put(AirsenseNotificationEntry.MONITOR_NAME, data.getString("monitor_name"));
            values.put(AirsenseNotificationEntry.DATE, data.getString("date"));
            values.put(AirsenseNotificationEntry.LATITUDE, data.getJSONArray("coordinates").getDouble(1));
            values.put(AirsenseNotificationEntry.LONGITUDE, data.getJSONArray("coordinates").getDouble(0));
            values.put(AirsenseNotificationEntry.NOX, data.getDouble("nox"));
            values.put(AirsenseNotificationEntry.O3, data.getDouble("o3"));
            values.put(AirsenseNotificationEntry.CO2, data.getDouble("co2"));
            values.put(AirsenseNotificationEntry.AQHI, data.getDouble("aqhi"));
            values.put(AirsenseNotificationEntry.PM, data.getDouble("pm"));
            values.put(AirsenseNotificationEntry.AH, data.getDouble("ah"));
            values.put(AirsenseNotificationEntry.CO, data.getDouble("co"));
            values.put(AirsenseNotificationEntry.COO, data.getDouble("coo"));
            values.put(AirsenseNotificationEntry.ADDRESS, data.getString("address"));
            JSONArray subscriptionIds = root.getJSONArray("subscriptionIds");
            values.put(AirsenseNotificationEntry.SUBSCRIPTION_IDS, subscriptionIds.join(","));
            db.insert(AirsenseNotificationEntry.TABLE_NAME, null, values);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.close();
    }

    private void notifyUser(SubscriptionType type, JSONObject data, String filters, String name) {
        switch (type) {
            case TTC:
                notifyTtc(data, filters, name);
                break;
            case AIRSENSE:
                notifyAirsense(data, filters, name);
                break;
        }
    }

    private void notifyTtc(JSONObject data, String filters, String name) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        try {
            inboxStyle.addLine("Bus ID: " + data.getInt("id"));
            inboxStyle.addLine("Direction: " + Helper.calculateDirection(Integer.valueOf(data.getString("heading"))));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pending)
                .setContentText(name)
                .setStyle(inboxStyle);

        try {
            mBuilder.setContentTitle(data.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            notificationManager.notify(data.getInt("id"), mBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void notifyAirsense(JSONObject data, String filters, String name) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        if (filters != null) {
            String[] filtersArray = filters.split(",");
            for (String filterString : filtersArray) {
                Filter f = Filter.fromString(filterString);
                try {
                    inboxStyle.addLine(f.getFieldName() + ": " + data.getString(f.getFieldName()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Air Quality Update")
                .setContentIntent(pending)
                .setContentText(name)
                .setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());

    }

    private String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    private boolean shouldNotifyNow(int startTime, int endTime, int mon, int tues, int wed, int thur, int fri, int sat, int sun) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int seconds = (calendar.get(Calendar.HOUR_OF_DAY) * 3600) + (calendar.get(Calendar.MINUTE) * 60) + (calendar.get(Calendar.SECOND));
        if ((seconds > startTime) && (seconds < endTime)) {
            if ((day == Calendar.MONDAY) && (mon > 0)) {
                return true;
            }

            if ((day == Calendar.TUESDAY) && (tues > 0)) {
                return true;
            }

            if ((day == Calendar.WEDNESDAY) && (wed > 0)) {
                return true;
            }

            if ((day == Calendar.THURSDAY) && (thur > 0)) {
                return true;
            }

            if ((day == Calendar.FRIDAY) && (fri > 0)) {
                return true;
            }

            if ((day == Calendar.SATURDAY) && (sat > 0)) {
                return true;
            }
            if ((day == Calendar.SUNDAY) && (sun > 0)) {
                return true;
            }

        }
        return false;
    }

    // Run code below to force unsubscribe any incoming notification.
//                JSONObject payload = new JSONObject();
//                try {
//                    payload.put("subscriptionId", subscriptionIds.getString(i));
//
//                        payload.put("publisherName", "ttc");
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                System.out.println("payload = " + payload);
//                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://subs.portal.cvst.ca/api/unsubscribe", payload, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        System.out.println("response = " + response);
//                        String status = "error";
//                        String message = "There was an error, please try again.";
//                        try {
//                            status = response.getString("status");
//                            message = response.getString("message");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.println("error = " + error);
//                        System.out.println("error.getMessage() = " + error.getMessage());
//                        Toast.makeText(getApplicationContext(), "Unsub failed.", Toast.LENGTH_LONG).show();
//                    }
//                });
//                NetworkManager.getInstance(this).addToRequestQueue(request);


}
