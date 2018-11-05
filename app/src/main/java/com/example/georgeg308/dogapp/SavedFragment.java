package com.example.georgeg308.dogapp;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.georgeg308.dogapp.dummy.DummyContent;
import com.example.georgeg308.dogapp.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SavedFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView rv;
    private Context viewContext;
    private List<DogObject> dogs;
    private SearchView searchView;
    private  MyItemRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SavedFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SavedFragment newInstance(int columnCount) {
        SavedFragment fragment = new SavedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter

            Context context = view.getContext();
            RecyclerView recyclerView = view.findViewById(R.id.list);
            rv = recyclerView;
            viewContext = context;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //Get list of dogs to display
            DogDbHelper db = new DogDbHelper(getContext());
            dogs = db.getAllDogs();
            mAdapter = new MyItemRecyclerViewAdapter(context,dogs, mListener);
            recyclerView.setAdapter(mAdapter);


        SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
        searchView = view.findViewById(R.id.searchV);

        //searchView.setSearchableInfo(searchManager.getSearchableInfo());
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                Log.d("TEXTCHANGED", "onQueryTextChange: " + query);
                mAdapter.getFilter().filter(query);
                return false;
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            refresh();
        }
    }
    public void refresh()
    {
        DogDbHelper db = new DogDbHelper(getContext());
        dogs = db.getAllDogs();
        //rv.getAdapter().notifyDataSetChanged();
        mAdapter.newData(dogs);
        //rv.setAdapter(new MyItemRecyclerViewAdapter(viewContext,dogs, mListener));
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
        void onListFragmentInteraction(DogObject item);
    }
}
