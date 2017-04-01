package ca.cvst.gta;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

import ca.cvst.gta.db.AirsenseNotificationsContract.AirsenseNotificationEntry;
import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.TtcNotificationContract.TtcNotificationEntry;

public class SubscriptionService extends Service {
    private static final String ACTION_SUBSCRIBE = "ca.cvst.gta.action.SUBSCRIBE";
    private static final String EXTRA_PARAM1 = "ca.cvst.gta.extra.PARAM1";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private WebSocket ws;
    private CountDownLatch latch = new CountDownLatch(1);

    public static void startActionSubscribe(Context context, String payload) {
        Intent intent = new Intent(context, SubscriptionService.class);
        intent.setAction(ACTION_SUBSCRIBE);
        intent.putExtra(EXTRA_PARAM1, payload);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate called");
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand called");
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        System.out.println("service done!!!!!!!!!!!!!!");
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    private void initializeWebSocket() {
        System.out.println("initializing websocket...");
        if (ws == null) {
            AsyncHttpClient.getDefaultInstance().websocket("ws://subs.portal.cvst.ca:8888/websocket", null, new AsyncHttpClient.WebSocketConnectCallback() {
                @Override
                public void onCompleted(final Exception ex, WebSocket webSocket) {
                    if (ex != null) {
                        ex.printStackTrace();
                        return;
                    }
                    ws = webSocket;
                    latch.countDown();
                    webSocket.setStringCallback(new WebSocket.StringCallback() {
                        public void onStringAvailable(String s) {
                            System.out.println("s = " + s);
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
                        }
                    });
                    webSocket.setDataCallback(new DataCallback() {
                        public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                            System.out.println("I got some bytes!");
                            byteBufferList.recycle();
                        }
                    });
                }
            });
        }
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
            values.put(TtcNotificationEntry.LONGITUDE, data.getJSONArray("coordinates").getDouble(0));
            values.put(TtcNotificationEntry.DATE_TIME, data.getString("dateTime"));
            values.put(TtcNotificationEntry.HEADING, data.getString("heading"));
            values.put(TtcNotificationEntry.PREDICTABLE, data.getBoolean("predictable"));
            values.put(TtcNotificationEntry.ROUTE_NUMBER, data.getString("routeNumber"));
            values.put(TtcNotificationEntry.SUBSCRIPTION_ID, root.getJSONArray("subscriptionIds").join(","));
            db.insert(TtcNotificationEntry.TABLE_NAME, null, values);
            db.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleAirsense(JSONObject root) {
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
            values.put(AirsenseNotificationEntry.SUBSCRIPTION_IDS, root.getJSONArray("subscriptionIds").join(","));
            db.insert(AirsenseNotificationEntry.TABLE_NAME, null, values);
            db.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent) msg.obj;
            initializeWebSocket();
            final String action = intent.getAction();
            if (ACTION_SUBSCRIBE.equals(action)) {
                String payload = intent.getStringExtra(EXTRA_PARAM1);
                try {
                    latch.await();
                    ws.send(payload);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
