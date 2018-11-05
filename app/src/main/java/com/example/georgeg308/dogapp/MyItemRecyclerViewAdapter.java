package com.example.georgeg308.dogapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.georgeg308.dogapp.SavedFragment.OnListFragmentInteractionListener;
import com.example.georgeg308.dogapp.dummy.DummyContent.DummyItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> implements Filterable {

    private  List<DogObject> mValues;
    private  List<DogObject> filteredDogs;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    View mView;


    public MyItemRecyclerViewAdapter(Context context,List<DogObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
        filteredDogs = items;
    }


    public void newData(List<DogObject> newDogs)
    {
        mValues = newDogs;
        filteredDogs = newDogs;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        mView = view;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = filteredDogs.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        holder.mBreedView.setText(filteredDogs.get(position).breed);
        holder.mContentView.setText(filteredDogs.get(position).url);
        Linkify.addLinks(holder.mContentView,Linkify.WEB_URLS);
        holder.mImage.setImageResource(0);


        // load image

            // get input stream
            byte[] outImage= holder.mItem.image;
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            holder.mImage.setImageBitmap(theImage);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    Toast.makeText(mContext,"Item: "+position+" Clicked",Toast.LENGTH_SHORT).show();

                    showDialog(filteredDogs.get(position).breed,filteredDogs.get(position).url);



                }
            }
        });

        holder.mBtnShare.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.d("SHARE CLICKED", "onClick: ");
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = holder.mContentView.getText().toString();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sending a nice doggo");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));

            Toast toast = Toast.makeText(mContext, "Share button clicked", Toast.LENGTH_SHORT);
            toast.show();
            }
        }
        );


        holder.mBtnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("DELETE CLICKED", "onClick: ");

                DogDbHelper db = new DogDbHelper(mContext);
                db.deleteDog(holder.mContentView.getText().toString());

                filteredDogs.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,filteredDogs.size());

                Toast toast = Toast.makeText(mContext, "Image Removed", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void showDialog(final String breed, final String url){
        //show dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.text_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Name Dog");
        dialogBuilder.setMessage("Name the dog and save it to cloud");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                MainActivity.namedDogQueue.add(new DogObject(url,breed,edt.getText().toString()));
                Toast.makeText(mContext, edt.getText().toString() + " is queued to save to cloud", Toast.LENGTH_LONG)
                        .show();


/*
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Create a new user with a first and last name
                Map<String, Object> dog = new HashMap<>();
                dog.put("name", edt.getText().toString());
                dog.put("breed", breed);
                dog.put("url", url);

// Add a new document with a generated ID
                db.collection(MainActivity.userID)
                        .add(dog)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("added", "DocumentSnapshot added with ID: " + documentReference.getId());
                                Snackbar.make(mView, "Dog has been named and saved to cloud", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("error", "Error adding document", e);
                                Snackbar.make(mView, "Fail to add to cloud", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
*/
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    @Override
    public int getItemCount() {
        return filteredDogs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView, mBreedView;
        public final ImageView mImage;
        public  DogObject mItem;
        public final ImageButton mBtnDelete, mBtnShare;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mBreedView = (TextView) view.findViewById(R.id.breed);
            mImage = (ImageView) view.findViewById(R.id.item_image);

            mBtnDelete =  view.findViewById(R.id.imageButtonDelete);
            mBtnShare =  view.findViewById(R.id.imageButtonShare);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                Log.d("FILTERING", "performFiltering: 11111111111111111");
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredDogs = mValues;
                } else {
                    List<DogObject> filteredList = new ArrayList<DogObject>();
                    for (DogObject row : mValues) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.breed.toLowerCase().contains(charString.toLowerCase()) || row.url.contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    filteredDogs = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredDogs;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Log.d("2222222222222", "publishResults: " + charSequence);

                filteredDogs = (ArrayList<DogObject>) filterResults.values;
                Log.d("2222222222222", "publishResults: " + getItemCount());
                for (DogObject a :filteredDogs
                     ) {
                    Log.d("List", "publishResults: " + a.breed);

                }
                notifyDataSetChanged();

            }
        };
    }


}
