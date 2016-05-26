package com.asal.bettergrt;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

//import com.asal.bettergrt.ItemFragment.OnListFragmentInteractionListener;
import com.asal.bettergrt.dummy.DummyContent.DummyItem;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {
    private final List<FavouriteStop> mValues;
    private final FavouritesFragment.OnListFragmentInteractionListener mListener;
    private int selectedPos = 0;

    public FavouritesAdapter(List<FavouriteStop> items, FavouritesFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_favourites, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mstopID.setText(mValues.get(position).stopID);
        holder.mStopName.setText(mValues.get(position).stopName);
        holder.mNextScheduledTime.setText(mValues.get(position).nextScheduledTime);
        holder.mActualTime.setText(mValues.get(position).actualTime);
        holder.itemView.setSelected(selectedPos == position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selectedPos);

                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mstopID;
        public final TextView mStopName;
        public final TextView mNextScheduledTime;
        public final TextView mActualTime;
        public final ImageButton mDeleteButton;
        public FavouriteStop mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mstopID = (TextView) view.findViewById(R.id.stopID);
            mStopName = (TextView) view.findViewById(R.id.stopName);
            mNextScheduledTime = (TextView) view.findViewById(R.id.nextScheduledTime);
            mActualTime = (TextView) view.findViewById(R.id.actualTime);
            mDeleteButton = (ImageButton) view.findViewById(R.id.deleteButton);

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar snackbar = Snackbar.make(v, "Delete coming soon", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    snackbar.show();
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStopName.getText() + "'";
        }
    }
}
