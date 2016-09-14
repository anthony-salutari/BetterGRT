package com.asal.bettergrt;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anthony on 9/11/2016.
 */
public class SearchStopsAdapter extends RecyclerView.Adapter<SearchStopsAdapter.ViewHolder> {
    private ArrayList<BusStop> mValues;

    public SearchStopsAdapter(ArrayList<BusStop> values) {
        mValues = values;
    }

    @Override
    public SearchStopsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(SearchStopsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mStopID;
        public final TextView mStopName;
        //public final TextView mNextScheduledTime;
        //public final TextView mActualTime;
        public BusStop mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mStopID = (TextView) view.findViewById(R.id.stopID);
            mStopName = (TextView) view.findViewById(R.id.stopName);
            //mNextScheduledTime = (TextView) view.findViewById(R.id.nextScheduledTime);
            //mActualTime = (TextView) view.findViewById(R.id.actualTime);
        }
    }
}
