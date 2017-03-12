package ca.cvst.gta;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

import java.util.concurrent.CountDownLatch;

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
