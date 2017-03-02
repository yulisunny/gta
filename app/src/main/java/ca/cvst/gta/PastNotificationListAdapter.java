package ca.cvst.gta;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PastNotificationListAdapter extends RecyclerView.Adapter<PastNotificationListAdapter.ViewHolder> {

    private List<PastNotification> mDataSet;

    public PastNotificationListAdapter(List<PastNotification> pastNotifications) {
        mDataSet = pastNotifications;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_past_notification, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PastNotification pastNotification = mDataSet.get(position);
        String title = pastNotification.getTitle();
        String content = pastNotification.getContent();
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
            mTitle = (TextView) v.findViewById(R.id.text_past_notification_title);
            mContent = (TextView) v.findViewById(R.id.text_past_notification_content);

        }
    }
}
