package ca.cvst.gta;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SubscriptionListAdapter extends RecyclerView.Adapter<SubscriptionListAdapter.ViewHolder> {

    private List<Subscription> mDataSet;

    public SubscriptionListAdapter(List<Subscription> subscriptions) {
        this.mDataSet = subscriptions;
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

        public ViewHolder(CardView v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.text_subscription_title);
            mContent = (TextView) v.findViewById(R.id.text_subscription_content);
        }
    }
}
