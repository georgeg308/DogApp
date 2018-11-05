package com.example.georgeg308.dogapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.georgeg308.dogapp.dummy.DummyContent;
import com.example.georgeg308.dogapp.dummy.DummyContent.DummyItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CloudFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private  MyCloudRecyclerViewAdapter mAdapter;
    private List<DogObject> dogs = new ArrayList<DogObject>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CloudFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CloudFragment newInstance(int columnCount) {
        CloudFragment fragment = new CloudFragment();
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


        View view = inflater.inflate(R.layout.fragment_cloud_list, container, false);


        // Set the adapter

            Context context = view.getContext();

            RecyclerView recyclerView = view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }


            List<DogObject> dogList = getDogs();

           mAdapter = new MyCloudRecyclerViewAdapter(context,dogList, mListener);
            recyclerView.setAdapter(mAdapter);


        FloatingActionButton fabShare = (FloatingActionButton) view.findViewById(R.id.fab);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refresh();
                Snackbar.make(view, "Page Refreshed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });

        return view;
    }

    public void refresh()
    {

        //rv.getAdapter().notifyDataSetChanged();
        mAdapter.newData(getDogs());
        //rv.setAdapter(new MyItemRecyclerViewAdapter(viewContext,dogs, mListener));
    }

    public List<DogObject> getDogs(){
        dogs.clear();
        List<DogObject> dogList = new ArrayList<DogObject>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(MainActivity.userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("get", document.getId() + " => " + document.getData());
                                Log.d("CCCCCCCC", document.getString("url"));
                                Log.d("CCCCCCCC", document.getString("breed"));
                                Log.d("CCCCCCCC", document.getString("name"));

                                dogs.add(new DogObject(document.getString("url"),
                                        document.getString("breed"),
                                        document.getString("name")));

                            }
                        } else {
                            Log.w("Error Get", "Error getting documents.", task.getException());
                            Snackbar.make(getView(), "Failed to retrieve data", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
        dogList = dogs;
        return dogList;
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
        void onCloudListFragmentInteraction(DogObject item);
    }
}
