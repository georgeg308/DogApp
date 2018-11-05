package com.example.georgeg308.dogapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.georgeg308.dogapp.CloudFragment.OnListFragmentInteractionListener;
import com.example.georgeg308.dogapp.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyCloudRecyclerViewAdapter extends RecyclerView.Adapter<MyCloudRecyclerViewAdapter.ViewHolder> {

    private  List<DogObject> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;

    public MyCloudRecyclerViewAdapter(Context context,List<DogObject> items, CloudFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;

    }
    public void newData(List<DogObject> newDogs)
    {
        mValues = newDogs;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cloud, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);
        holder.mBreedView.setText(mValues.get(position).breed);
        holder.mContentView.setText(mValues.get(position).url);
        Linkify.addLinks(holder.mContentView,Linkify.WEB_URLS);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCloudListFragmentInteraction(holder.mItem);
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
        public final TextView mNameView;
        public final TextView mContentView;
        public final TextView mBreedView;
        public DogObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mBreedView = (TextView) view.findViewById(R.id.breed);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


}


