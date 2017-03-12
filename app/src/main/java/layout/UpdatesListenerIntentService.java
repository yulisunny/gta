package layout;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.TtcNotificationContract.TtcNotificationEntry;

public class UpdatesListenerIntentService extends IntentService {

    private static int notificationId = 1;

    public UpdatesListenerIntentService() {
        super("UpdatesListenerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AsyncHttpClient.getDefaultInstance().websocket("ws://subs.portal.cvst.ca:8888", null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(final Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    System.out.println("websocket on completed");
                    ex.printStackTrace();
                    System.out.println("websocket on completed returning");
                    return;
                }
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
//                        try {
//                            JSONObject root = new JSONObject(s);
//                            JSONObject data = root.getJSONObject("data");
//                            switch (data.getString("category")) {
//                                case "ttc":
//                                    handleTtc(root);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }

                        System.out.println("s = " + s);
//                        NotificationCompat.Builder mBuilder =
//                                new NotificationCompat.Builder(getApplicationContext())
//                                        .setSmallIcon(R.drawable.ic_notification)
//                                        .setContentTitle("New Update on Your Subscription")
//                                        .setContentText(s);
////                        final JSONObject ttcVehicle = new JSONObject(s);
//                        NotificationManager notificationManager =
//                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        notificationManager.notify(notificationId++, mBuilder.build());
//                        System.out.println("notificationId = " + notificationId);
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
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            JSONObject data = root.getJSONObject("data");
            values.put(TtcNotificationEntry.TIMESTAMP, data.getInt("timestamp"));
            values.put(TtcNotificationEntry.DIR_TAG, data.getString("dirTag"));
            values.put(TtcNotificationEntry.NAME, data.getString("name"));
            values.put(TtcNotificationEntry.GPS_TIME, data.getInt("GPStime"));
            values.put(TtcNotificationEntry.LAST_TIME, data.getString("lastTime"));
            values.put(TtcNotificationEntry.LATITUDE, data.getJSONArray("coordinates").getDouble(1));
            values.put(TtcNotificationEntry.LONGITUDE, data.getJSONArray("coordinates").getDouble(1));
            values.put(TtcNotificationEntry.DATE_TIME, data.getString("dateTime"));
            values.put(TtcNotificationEntry.HEADING, data.getString("heading"));
            values.put(TtcNotificationEntry.PREDICTABLE, data.getBoolean("predictable"));
            values.put(TtcNotificationEntry.ROUTE_NUMBER, data.getString("routeNumber"));
            values.put(TtcNotificationEntry.SUBSCRIPTION_IDS, root.getJSONArray("subscriptionIds").join(","));
            db.insert(TtcNotificationEntry.TABLE_NAME, null, values);
            db.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
