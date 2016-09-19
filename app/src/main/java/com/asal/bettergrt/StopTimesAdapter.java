package com.asal.bettergrt;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Anthony on 5/8/2016.
 */
public class StopTimesAdapter extends RecyclerView.Adapter<StopTimesAdapter.ViewHolder> {
    private ArrayList<StopTime> mValues;
    private final Map.OnListFragmentInteractionListener mListener;

    public StopTimesAdapter(ArrayList<StopTime> items, Map.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public StopTimesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stop_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StopTimesAdapter.ViewHolder holder, int position) {
        Date dateObj = null;
        String convertedTime = null;

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm:s");
            dateObj = sdf.parse(mValues.get(position).departureTime);
            convertedTime = new SimpleDateFormat("h:mm a").format(dateObj);
        } catch (Exception e) {

        }

        holder.tripHeadsign.setText(mValues.get(position).tripHeadsign.replace("\"", ""));
        holder.departureTime.setText(convertedTime);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void addItems(ArrayList<StopTime> items) {
        mValues = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public StopTime mItem;

        @BindView(R.id.tripHeadsign) TextView tripHeadsign;
        @BindView(R.id.departureTime) TextView departureTime;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
