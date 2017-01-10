package layout;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import ca.cvst.gta.R;

public class UpdatesListenerIntentService extends IntentService {

    private static int notificationId = 1;

    public UpdatesListenerIntentService() {
        super("UpdatesListenerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AsyncHttpClient.getDefaultInstance().websocket("ws://subs.portal.cvst.ca/liveupdate/yulisunny", null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(final Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        System.out.println("s = " + s);
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle("New Update on Your Subscription")
                                .setContentText(s);
//                        final JSONObject ttcVehicle = new JSONObject(s);
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(notificationId++, mBuilder.build());
                        System.out.println("notificationId = " + notificationId);
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
}
