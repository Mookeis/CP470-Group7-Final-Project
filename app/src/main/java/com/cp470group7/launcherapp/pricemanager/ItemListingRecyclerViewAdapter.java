package com.cp470group7.launcherapp.pricemanager;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cp470group7.launcherapp.R;
import com.cp470group7.launcherapp.pricemanager.ItemListingFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ItemListing} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 *
 */
public class ItemListingRecyclerViewAdapter extends RecyclerView.Adapter<ItemListingRecyclerViewAdapter.ViewHolder> {

    private final List<ItemListing> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ItemListingRecyclerViewAdapter(List<ItemListing> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_itemlisting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mSellerView.setText(mValues.get(position).sellerName);
        holder.mPriceView.setText(mValues.get(position).itemPrice);
        holder.mConditionView.setText(mValues.get(position).itemCondition);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        public final TextView mSellerView;
        public final TextView mPriceView;
        public final TextView mConditionView;
        public ItemListing mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mSellerView = (TextView) view.findViewById(R.id.itemSeller);
            mPriceView = (TextView) view.findViewById(R.id.itemPrice);
            mConditionView = (TextView) view.findViewById(R.id.itemCondition);

            float maxWidth = Resources.getSystem().getDisplayMetrics().widthPixels/4;
            mSellerView.setMaxWidth((int) maxWidth);
            mSellerView.setMinWidth((int) maxWidth);
            mPriceView.setMaxWidth((int) maxWidth);
            mPriceView.setMinWidth((int) maxWidth);
            mConditionView.setMaxWidth((int) maxWidth);
            mConditionView.setMinWidth((int) maxWidth);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSellerView.getText() + "': " + mPriceView;
        }
    }
}
