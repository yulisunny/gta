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
        String line1name = pastNotification.getLine1name();
        String line1value = pastNotification.getLine1value();
        holder.mTitle.setText(title);
        holder.line1name.setText(line1name + ": ");
        holder.line1value.setText(line1value);

        String line2name = pastNotification.getLine2name();
        String line2value = pastNotification.getLine2value();
        if (line2name != null && line1value != null) {
            holder.line2name.setText(line2name + ": ");
            holder.line2value.setText(line2value);
        }

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView line1name;
        public TextView line1value;
        public TextView line2name;
        public TextView line2value;

        public ViewHolder(CardView v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.text_past_notification_title);
            line1name = (TextView) v.findViewById(R.id.text_past_notification_line_1_name);
            line1value = (TextView) v.findViewById(R.id.text_past_notification_line_1_value);
            line2name = (TextView) v.findViewById(R.id.text_past_notification_line_2_name);
            line2value = (TextView) v.findViewById(R.id.text_past_notification_line_2_value);

        }
    }
}
