package ca.cvst.gta;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {

    private List<Filter> mDataSet;
    private Context mContext;
    // Define listener member variable
    private OnDeleteFilterBtnClickListener listener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public FilterListAdapter(Context context, List<Filter> filters) {
        mContext = context;
        mDataSet = filters;
    }

    public void setOnDeleteFilterBtnClickListener(OnDeleteFilterBtnClickListener listener) {
        this.listener = listener;
    }

    private Context getContext() {
        return mContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FilterListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_filter, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Filter filter = mDataSet.get(position);
        String fieldName = filter.getFieldName();
        String fieldValue = filter.getFieldValue();
        String operation = filter.getOperation().getSymbol();
        holder.mFilterText.setText(fieldName + operation + fieldValue);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    // Define the listener interface
    public interface OnDeleteFilterBtnClickListener {
        void onDeleteFilterBtnClick(View deleteBtn, int position);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mFilterText;
        public ImageButton mDeleteButton;

        public ViewHolder(CardView v) {
            super(v);
            mFilterText = (TextView) v.findViewById(R.id.text_filter);
            mDeleteButton = (ImageButton) v.findViewById(R.id.btn_delete_filter);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition(); // gets item position
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteFilterBtnClick(v, position);
                        }
                    }
                }
            });
        }

    }
}
