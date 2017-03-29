package ca.cvst.gta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ca.cvst.gta.db.DbHelper;
import ca.cvst.gta.db.SubscriptionsContract.SubscriptionEntry;

public class SubscriptionListAdapter extends RecyclerView.Adapter<SubscriptionListAdapter.ViewHolder> {

    private List<Subscription> mDataSet;
    private Context context;

    public SubscriptionListAdapter(List<Subscription> subscriptions, Context context) {
        this.mDataSet = subscriptions;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subscription, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Subscription sub = mDataSet.get(position);
        String title = sub.getTitle();
        String content = sub.getContent();
        holder.mTitle.setText(title);
        holder.mContent.setText(content);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mContent;
        public Button mUnsubBtn;

        public ViewHolder(CardView v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.text_subscription_title);
            mContent = (TextView) v.findViewById(R.id.text_subscription_content);
            mUnsubBtn = (Button) v.findViewById(R.id.btn_unsubscribe);
            mUnsubBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    final Subscription subscription = mDataSet.get(position);
                    JSONObject payload = new JSONObject();
                    try {
                        payload.put("subscriptionId", subscription.getSubscriptionId());
                        if (subscription.getType() == Subscription.Type.TTC) {
                            payload.put("publisherName", "ttc");
                        } else if (subscription.getType() == Subscription.Type.AIRSENSE) {
                            payload.put("publisherName", "airsense");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    System.out.println("payload = " + payload);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://subs.portal.cvst.ca/api/unsubscribe", payload, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("response = " + response);
                            String status = "error";
                            String message = "There was an error, please try again.";
                            try {
                                status = response.getString("status");
                                message = response.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            if (status.equals("success")) {
                                DbHelper helper = new DbHelper(context);
                                SQLiteDatabase db = helper.getWritableDatabase();
                                String[] subscriptionId = {subscription.getSubscriptionId()};
                                db.delete(SubscriptionEntry.TABLE_NAME, SubscriptionEntry.SUBSCRIPTION_ID + "= ?", subscriptionId);
                                db.close();


                                mDataSet.remove(position);
                                notifyItemRemoved(position);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("error = " + error);
                            System.out.println("error.getMessage() = " + error.getMessage());
                            Toast.makeText(context, "Subscription failed.", Toast.LENGTH_LONG).show();
                        }
                    });
                    NetworkManager.getInstance(context).addToRequestQueue(request);
                }
            });
        }
    }
}
