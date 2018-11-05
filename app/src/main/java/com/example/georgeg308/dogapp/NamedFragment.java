package com.example.georgeg308.dogapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.georgeg308.dogapp.dummy.DummyContent;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NamedFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<DogObject> dogs = new ArrayList<DogObject>();
    private FirestoreRecyclerAdapter adapter;
    RecyclerView dogList;
    FirebaseFirestore db;
    ProgressBar progressBar;
    View view;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NamedFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NamedFragment newInstance(int columnCount) {
        NamedFragment fragment = new NamedFragment();
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
        view = inflater.inflate(R.layout.fragment_cloud_list, container, false);
       dogList = view.findViewById(R.id.list);
        progressBar = view.findViewById(R.id.progress_bar);

        // Set the adapter

            Context context = view.getContext();
            db = FirebaseFirestore.getInstance();


        getDogList();
        adapter.startListening();



        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            if(MainActivity.namedDogQueue != null)
            {
                if(MainActivity.namedDogQueue.size()>0)
                {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Toast.makeText(getContext(),"Processing Queued Dogs" ,Toast.LENGTH_SHORT).show();
                    WriteBatch batch = db.batch();

                    for (DogObject d:MainActivity.namedDogQueue
                         ) {

                        // Create a new user with a first and last name
                        Map<String, Object> dog = new HashMap<>();
                        dog.put("name", d.name);
                        dog.put("breed", d.breed);
                        dog.put("url", d.url);
                        String id = ""+d.name+ d.url;
                        id = id.replace("//","");
                        id = id.replace("/","");
                        Log.d("IIIIIIIIIDDDDDDDDDDD", "setUserVisibleHint: " + id);
                        //DocumentReference ref = db.collection(MainActivity.userID).document(id);
                        batch.set(db.collection(MainActivity.userID).document(id), dog);


                        /*
// Add a new document with a generated ID
                        db.collection(MainActivity.userID)
                                .add(dog)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("added", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        Snackbar.make(view,  "Dog has been named and saved to cloud", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("error", "Error adding document", e);
                                        Snackbar.make(view, "Fail to add to cloud", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });*/

                        
                    }

                    // Commit the batch
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Snackbar.make(view,  "Dogs has been named and saved to cloud", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    MainActivity.namedDogQueue.clear();
                }
            }


        }
    }


    private void getDogList(){
        Query query = db.collection(MainActivity.userID);

        final FirestoreRecyclerOptions<DogObject> response = new FirestoreRecyclerOptions.Builder<DogObject>()
                .setQuery(query, DogObject.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DogObject, ViewHolder>(response) {
            @Override
            public void onBindViewHolder(final ViewHolder holder, final int position, final DogObject mValues) {
                //holder.mItem = mValues.get(position);
                final String id = response.getSnapshots().getSnapshot(position).getId();
                holder.mNameView.setText(mValues.name);
                holder.mBreedView.setText(mValues.breed);
                holder.mContentView.setText(mValues.url);
                Linkify.addLinks(holder.mContentView,Linkify.WEB_URLS);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.

                        }
                    }
                });

                holder.mBtnShare.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Log.d("SHARE CLICKED", "onClick: ");
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "I have named this nice dog " +
                                holder.mNameView.getText().toString() +"\n"  + holder.mContentView.getText().toString();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sending a nice doggo");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));

                        Toast toast = Toast.makeText(getContext(), "Share button clicked", Toast.LENGTH_SHORT);
                        toast.show();

                    }});

                holder.mBtnDelete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {

                        db.collection(MainActivity.userID).document(id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Deleted", "DocumentSnapshot successfully deleted!");
                                        Snackbar.make(getView(), "Data has been removed", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Failed", "Error deleting document", e);
                                        Snackbar.make(getView(), "Failed to remove data", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });

                    }});


            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.fragment_cloud, group, false);

                return new ViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
                Snackbar.make(view, "Error occurred while getting data", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onDataChanged() {
                // Called each time there is a new query snapshot. You may want to use this method
                // to hide a loading spinner or check for the "no documents" state and update your UI.
                // ...

            }
        };



        adapter.notifyDataSetChanged();
        dogList.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mContentView;
        public final TextView mBreedView;
        public final ImageButton mBtnDelete, mBtnShare;


        public DogObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mBreedView = (TextView) view.findViewById(R.id.breed);
            mContentView = (TextView) view.findViewById(R.id.content);
            mBtnDelete =  view.findViewById(R.id.imageButtonDelete);
            mBtnShare =  view.findViewById(R.id.imageButtonShare);
        }

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
        adapter.stopListening();
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
        void onNamedListFragmentInteraction(DogObject item);
    }
}
