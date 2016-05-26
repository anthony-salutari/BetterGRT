package com.asal.bettergrt;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FavouritesFragment extends Fragment {
    private OnListFragmentInteractionListener mListener;
    private ArrayList<FavouriteStop> mItems;
    private SharedPreferences mFavouritePreferences;
    private FavouritesAdapter mAdapter;

    public FavouritesFragment() {
    }

    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Favourites");

        // get the favourite stops from the shared preference
        mFavouritePreferences = getActivity().getSharedPreferences(Utilities.PREFERENCE_FAVOURITES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String favourites = mFavouritePreferences.getString("favourites", "");
        if (favourites.isEmpty()) {
            //Snackbar snackbar = Snackbar.make(getView(), "No favourite stops. Add some from the map", Snackbar.LENGTH_INDEFINITE);
            //snackbar.show();
        }
        else {
            Type type = new TypeToken<List<FavouriteStop>>() {
            }.getType();

            mItems = gson.fromJson(favourites, type);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites_list, container, false);

        mAdapter = new FavouritesAdapter(mItems, mListener);

        // configure recyclerview
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            recyclerView.setAdapter(mAdapter);
            recyclerView.setHasFixedSize(true);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(FavouriteStop item);
    }
}
