package ca.cvst.gta;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

public final class NetworkManager {

    private static NetworkManager mInstance;
    private RequestQueue mRequestQueue;
    private  static Context mContext;

    private NetworkManager(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (mInstance == null) {
            CookieStore cookieStore = new CvstCookieStore();
            CookieManager manager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);
            mInstance = new NetworkManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
