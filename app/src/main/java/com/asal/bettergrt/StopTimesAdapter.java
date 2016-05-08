package com.asal.bettergrt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anthony on 5/8/2016.
 */
public class StopTimesAdapter extends RecyclerView.Adapter<StopTimesAdapter.ViewHolder> {
    private final List<StopTime> mValues;
    private final NearMe.OnListFragmentInteractionListener mListener;

    public StopTimesAdapter(List<StopTime> items, NearMe.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public StopTimesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_favourites, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StopTimesAdapter.ViewHolder holder, int position) {
        holder.mTripHeadsign.setText(mValues.get(position).tripHeadsign);
        holder.mDepartureTime.setText(mValues.get(position).departureTime);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
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
        public StopTime mItem;
        public final TextView mTripHeadsign;
        public final TextView mDepartureTime;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mTripHeadsign = (TextView) view.findViewById(R.id.tripHeadsign);
            mDepartureTime = (TextView) view.findViewById(R.id.departureTime);
        }
    }
}
