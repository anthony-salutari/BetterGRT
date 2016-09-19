package com.asal.bettergrt;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

//import com.asal.bettergrt.ItemFragment.OnListFragmentInteractionListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        holder.stopID.setText(mValues.get(position).stopID);
        holder.stopName.setText(mValues.get(position).stopName);
        holder.nextScheduledTime.setText(mValues.get(position).nextScheduledTime);
        holder.actualTime.setText(mValues.get(position).actualTime);
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
        public FavouriteStop mItem;

        @BindView(R.id.stopID) TextView stopID;
        @BindView(R.id.stopName) TextView stopName;
        @BindView(R.id.nextScheduledTime) TextView nextScheduledTime;
        @BindView(R.id.actualTime) TextView actualTime;
        @BindView(R.id.deleteButton) ImageButton deleteButton;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            ButterKnife.bind(this, view);

            deleteButton.setOnClickListener(new View.OnClickListener() {
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
            return super.toString() + " '" + stopName.getText() + "'";
        }
    }
}
