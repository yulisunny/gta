package layout;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import ca.cvst.gta.R;

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
                        try {
                            JSONObject root = new JSONObject(s);
                            JSONObject data = root.getJSONObject("data");
                            if (data.has("category")) {
                                handleTtc(root);
                            } else {
                                handleAirsense(root);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        System.out.println("s = " + s);
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

    private void handleTtc(JSONObject root) {
//        DbHelper dbHelper = new DbHelper(this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        try {
//            JSONObject data = root.getJSONObject("data");
//            values.put(TtcNotificationEntry.BUS_ID, data.getInt("id"));
//            values.put(TtcNotificationEntry.TIMESTAMP, data.getInt("timestamp"));
//            values.put(TtcNotificationEntry.DIR_TAG, data.getString("dirTag"));
//            values.put(TtcNotificationEntry.NAME, data.getString("name"));
//            values.put(TtcNotificationEntry.GPS_TIME, data.getInt("GPStime"));
//            values.put(TtcNotificationEntry.LAST_TIME, data.getString("lastTime"));
//            values.put(TtcNotificationEntry.LATITUDE, data.getJSONArray("coordinates").getDouble(1));
//            values.put(TtcNotificationEntry.LONGITUDE, data.getJSONArray("coordinates").getDouble(0));
//            values.put(TtcNotificationEntry.DATE_TIME, data.getString("dateTime"));
//            values.put(TtcNotificationEntry.HEADING, data.getString("heading"));
//            values.put(TtcNotificationEntry.PREDICTABLE, data.getBoolean("predictable"));
//            values.put(TtcNotificationEntry.ROUTE_NUMBER, data.getString("routeNumber"));
//            JSONArray subscriptionIds = root.getJSONArray("subscriptionIds");
//            values.put(TtcNotificationEntry.SUBSCRIPTION_IDS, subscriptionIds.join(","));
//            db.insert(TtcNotificationEntry.TABLE_NAME, null, values);
//
//            String[] columns = {
//                    TtcSubscriptionEntry.NAME,
//                    TtcSubscriptionEntry.MONDAY,
//                    TtcSubscriptionEntry.TUESDAY,
//                    TtcSubscriptionEntry.WEDNESDAY,
//                    TtcSubscriptionEntry.THURSDAY,
//                    TtcSubscriptionEntry.FRIDAY,
//                    TtcSubscriptionEntry.SATURDAY,
//                    TtcSubscriptionEntry.SUNDAY,
//                    TtcSubscriptionEntry.START_TIME,
//                    TtcSubscriptionEntry.END_TIME,
//                    TtcSubscriptionEntry.NOTIFICATION_ENABLED
//            };
//
//            List<String> validSubs = new ArrayList<>();
//            for (int i = 0; i < subscriptionIds.length(); i++) {
//                String[] subscriptionId = {subscriptionIds.getString(i)};
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
//                Cursor cursor = db.query(TtcSubscriptionEntry.TABLE_NAME, columns, TtcSubscriptionEntry.SUBSCRIPTION_ID + "= ?", subscriptionId, null, null, null);
//                while (cursor.moveToNext()) {
//                    String name = cursor.getString(cursor.getColumnIndex(TtcSubscriptionEntry.NAME));
//                    int mon = cursor.getInt(cursor.getColumnIndex(TtcSubscriptionEntry.MONDAY));
//                    int tues = cursor.getInt(cursor.getColumnIndex(TtcSubscriptionEntry.TUESDAY));
//                    int wed = cursor.getInt(cursor.getColumnIndex(TtcSubscriptionEntry.WEDNESDAY));
//                    int thur = cursor.getInt(cursor.getColumnIndex(TtcSubscriptionEntry.THURSDAY));
//                    int fri = cursor.getInt(cursor.getColumnIndex(TtcSubscriptionEntry.FRIDAY));
//                    int sat = cursor.getInt(cursor.getColumnIndex(TtcSubscriptionEntry.SATURDAY));
//                    int sun = cursor.getInt(cursor.getColumnIndex(TtcSubscriptionEntry.SUNDAY));
//                    int startTime = cursor.getInt(cursor.getColumnIndex(TtcSubscriptionEntry.START_TIME));
//                    int endTime = cursor.getInt(cursor.getColumnIndex(TtcSubscriptionEntry.END_TIME));
//                    int notif_enabled = cursor.getInt(cursor.getColumnIndex(TtcSubscriptionEntry.NOTIFICATION_ENABLED));
//                    if (notif_enabled == 0) {
//                        break;
//                    }
//
//                    Calendar calendar = Calendar.getInstance();
//                    int day = calendar.get(Calendar.DAY_OF_WEEK);
//                    int seconds = (calendar.get(Calendar.HOUR_OF_DAY) * 3600) + (calendar.get(Calendar.MINUTE) * 60) + (calendar.get(Calendar.SECOND));
//                    if ((seconds > startTime) && (seconds < endTime)) {
//                        if ((day == Calendar.MONDAY) && (mon > 0)) {
//                            validSubs.add(name);
//                            break;
//                        }
//
//                        if ((day == Calendar.TUESDAY) && (tues > 0)) {
//                            validSubs.add(name);
//                            break;
//                        }
//
//                        if ((day == Calendar.WEDNESDAY) && (wed > 0)) {
//                            validSubs.add(name);
//                            break;
//                        }
//
//                        if ((day == Calendar.THURSDAY) && (thur > 0)) {
//                            validSubs.add(name);
//                            break;
//                        }
//
//                        if ((day == Calendar.FRIDAY) && (fri > 0)) {
//                            validSubs.add(name);
//                            break;
//                        }
//
//                        if ((day == Calendar.SATURDAY) && (sat > 0)) {
//                            validSubs.add(name);
//                            break;
//                        }
//                        if ((day == Calendar.SUNDAY) && (sun > 0)) {
//                            validSubs.add(name);
//                            break;
//                        }
//
//                    }
//                }
//                cursor.close();
//            }
//
//            if (validSubs.size() > 0) {
//                Intent intent = new Intent(this, MainActivity.class);
//                PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
//                        .setSmallIcon(R.drawable.ic_notification)
//                        .setContentIntent(pending)
//                        .setContentTitle("TTC Update: " + TextUtils.join(",", validSubs))
//                        .setContentText("Route " + data.getString("routeNumber"));
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(data.getInt("id"), mBuilder.build());
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        db.close();

    }

    private void handleAirsense(JSONObject root) {
//        DbHelper dbHelper = new DbHelper(this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        try {
//            JSONObject data = root.getJSONObject("data");
//            values.put(AirsenseNotificationEntry.TIMESTAMP, data.getInt("timestamp"));
//            values.put(AirsenseNotificationEntry.MONITOR_NAME, data.getString("monitor_name"));
//            values.put(AirsenseNotificationEntry.DATE, data.getString("date"));
//            values.put(AirsenseNotificationEntry.LATITUDE, data.getJSONArray("coordinates").getDouble(1));
//            values.put(AirsenseNotificationEntry.LONGITUDE, data.getJSONArray("coordinates").getDouble(0));
//            values.put(AirsenseNotificationEntry.NOX, data.getDouble("nox"));
//            values.put(AirsenseNotificationEntry.O3, data.getDouble("o3"));
//            values.put(AirsenseNotificationEntry.CO2, data.getDouble("co2"));
//            values.put(AirsenseNotificationEntry.AQHI, data.getDouble("aqhi"));
//            values.put(AirsenseNotificationEntry.PM, data.getDouble("pm"));
//            values.put(AirsenseNotificationEntry.AH, data.getDouble("ah"));
//            values.put(AirsenseNotificationEntry.CO, data.getDouble("co"));
//            values.put(AirsenseNotificationEntry.COO, data.getDouble("coo"));
//            values.put(AirsenseNotificationEntry.ADDRESS, data.getString("address"));
//            JSONArray subscriptionIds = root.getJSONArray("subscriptionIds");
//            values.put(AirsenseNotificationEntry.SUBSCRIPTION_IDS, subscriptionIds.join(","));
//            db.insert(AirsenseNotificationEntry.TABLE_NAME, null, values);
//
//            String[] columns = {
//                    AirsenseSubscriptionEntry.NAME,
//                    AirsenseSubscriptionEntry.AIR_TYPE,
//                    AirsenseSubscriptionEntry.MONDAY,
//                    AirsenseSubscriptionEntry.TUESDAY,
//                    AirsenseSubscriptionEntry.WEDNESDAY,
//                    AirsenseSubscriptionEntry.THURSDAY,
//                    AirsenseSubscriptionEntry.FRIDAY,
//                    AirsenseSubscriptionEntry.SATURDAY,
//                    AirsenseSubscriptionEntry.SUNDAY,
//                    AirsenseSubscriptionEntry.START_TIME,
//                    AirsenseSubscriptionEntry.END_TIME,
//                    AirsenseSubscriptionEntry.NOTIFICATION_ENABLED
//            };
//
//            List<String> validSubs = new ArrayList<>();
//            Set<String> fieldsOfInterest = new HashSet<>();
//            for (int i = 0; i < subscriptionIds.length(); i++) {
//                String[] subscriptionId = {subscriptionIds.getString(i)};
//                Cursor cursor = db.query(AirsenseSubscriptionEntry.TABLE_NAME, columns, AirsenseSubscriptionEntry.SUBSCRIPTION_ID + "= ?", subscriptionId, null, null, null);
//                while (cursor.moveToNext()) {
//                    String name = cursor.getString(cursor.getColumnIndex(AirsenseSubscriptionEntry.NAME));
//                    String airType = cursor.getString(cursor.getColumnIndex(AirsenseSubscriptionEntry.AIR_TYPE));
//                    int mon = cursor.getInt(cursor.getColumnIndex(AirsenseSubscriptionEntry.MONDAY));
//                    int tues = cursor.getInt(cursor.getColumnIndex(AirsenseSubscriptionEntry.TUESDAY));
//                    int wed = cursor.getInt(cursor.getColumnIndex(AirsenseSubscriptionEntry.WEDNESDAY));
//                    int thur = cursor.getInt(cursor.getColumnIndex(AirsenseSubscriptionEntry.THURSDAY));
//                    int fri = cursor.getInt(cursor.getColumnIndex(AirsenseSubscriptionEntry.FRIDAY));
//                    int sat = cursor.getInt(cursor.getColumnIndex(AirsenseSubscriptionEntry.SATURDAY));
//                    int sun = cursor.getInt(cursor.getColumnIndex(AirsenseSubscriptionEntry.SUNDAY));
//                    int startTime = cursor.getInt(cursor.getColumnIndex(AirsenseSubscriptionEntry.START_TIME));
//                    int endTime = cursor.getInt(cursor.getColumnIndex(AirsenseSubscriptionEntry.END_TIME));
//                    int notif_enabled = cursor.getInt(cursor.getColumnIndex(AirsenseSubscriptionEntry.NOTIFICATION_ENABLED));
//                    if (notif_enabled == 0) {
//                        break;
//                    }
//
//                    Calendar calendar = Calendar.getInstance();
//                    int day = calendar.get(Calendar.DAY_OF_WEEK);
//                    int seconds = (calendar.get(Calendar.HOUR_OF_DAY) * 3600) + (calendar.get(Calendar.MINUTE) * 60) + (calendar.get(Calendar.SECOND));
//                    if ((seconds > startTime) && (seconds < endTime)) {
//                        if ((day == Calendar.MONDAY) && (mon > 0)) {
//                            validSubs.add(name);
//                            if (!airType.equals("-1")) {
//                                fieldsOfInterest.add(airType);
//                            }
//                            break;
//                        }
//
//                        if ((day == Calendar.TUESDAY) && (tues > 0)) {
//                            validSubs.add(name);
//                            fieldsOfInterest.add(airType);
//                            break;
//                        }
//
//                        if ((day == Calendar.WEDNESDAY) && (wed > 0)) {
//                            validSubs.add(name);
//                            if (!airType.equals("-1")) {
//                                fieldsOfInterest.add(airType);
//                            }
//                            break;
//                        }
//
//                        if ((day == Calendar.THURSDAY) && (thur > 0)) {
//                            validSubs.add(name);
//                            if (!airType.equals("-1")) {
//                                fieldsOfInterest.add(airType);
//                            }
//                            break;
//                        }
//
//                        if ((day == Calendar.FRIDAY) && (fri > 0)) {
//                            validSubs.add(name);
//                            if (!airType.equals("-1")) {
//                                fieldsOfInterest.add(airType);
//                            }
//                            break;
//                        }
//
//                        if ((day == Calendar.SATURDAY) && (sat > 0)) {
//                            validSubs.add(name);
//                            if (!airType.equals("-1")) {
//                                fieldsOfInterest.add(airType);
//                            }
//                            break;
//                        }
//                        if ((day == Calendar.SUNDAY) && (sun > 0)) {
//                            validSubs.add(name);
//                            if (!airType.equals("-1")) {
//                                fieldsOfInterest.add(airType);
//                            }
//                            break;
//                        }
//
//                    }
//                }
//                cursor.close();
//            }
//
//            if (validSubs.size() > 0) {
//                Intent intent = new Intent(this, MainActivity.class);
//                PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
//                        .setSmallIcon(R.drawable.ic_notification)
//                        .setContentIntent(pending)
//                        .setContentTitle("Airsense Update: " + TextUtils.join(",", validSubs));
//                String content = "";
//                for (String field : fieldsOfInterest) {
//                    content += (field + ": " + data.getString(field.toLowerCase()) + ". ");
//                }
//                mBuilder.setContentText(content);
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(data.getInt("id"), mBuilder.build());
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        db.close();
    }


}
