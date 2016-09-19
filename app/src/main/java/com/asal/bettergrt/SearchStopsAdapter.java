package com.asal.bettergrt;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        BusStop stop = mValues.get(position);
        holder.stopID.setText(stop.stopID);
        holder.stopName.setText(stop.stopName);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public BusStop mItem;

        @BindView(R.id.stopID) TextView stopID;
        @BindView(R.id.stopName) TextView stopName;

        public ViewHolder(View view) {
            super(view);

            mView = view;

            ButterKnife.bind(this, view);
        }
    }
}
