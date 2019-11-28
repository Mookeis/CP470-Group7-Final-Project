package com.cp470group7.launcherapp.pricemanager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.cp470group7.launcherapp.R;
import com.cp470group7.launcherapp.pricemanager.AmazonItemFragment.OnListFragmentInteractionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AmazonItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class AmazonItemRecyclerViewAdapter extends RecyclerView.Adapter<AmazonItemRecyclerViewAdapter.ViewHolder> {

    private final List<AmazonItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Activity activity;
    private int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int height = Resources.getSystem().getDisplayMetrics().heightPixels;

    public AmazonItemRecyclerViewAdapter(Activity activity, List<AmazonItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_amazonitem, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Removes item from recycler view
     * @param position item position
     */
    public void removeItem(int position){
        mValues.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        try{
            String filePath = new URL(mValues.get(position).getImages()[0]).getFile();
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            ImageRequest ir = new ImageRequest(new URL(mValues.get(position).getImages()[0]), fileName, holder);
            ir.execute();
            holder.mNameView.setText(mValues.get(position).getName());
            holder.mPriceView.setText(mValues.get(position).getPrice());
        }catch(MalformedURLException e){
            Log.e("RCA", "Bad Image URL");
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                            .setTitle("Item Description")
                            .setMessage(holder.mItem.getDescription())
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton(R.string.deleteItemLabel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mValues.remove(position);
                                    notifyItemRemoved(position);
                                }
                            });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    /**
     * Class for handling image request for item
     */
    public class ImageRequest extends AsyncTask<String, Integer, String> {

        URL imageURL;
        Bitmap itemImage;
        String fileName;
        ViewHolder holder;

        /**
         * Constructor for image handling class
         * @param imageURL absolute URL to image file to download
         * @param fileName name of image file to download
         * @see Bitmap
         */
        ImageRequest(URL imageURL, String fileName, ViewHolder holder){
            this.imageURL = imageURL;
            this.fileName = fileName;
            this.holder = holder;

        }

        @Override
        protected void onPostExecute(String a) {
            int imgWidth = 300, imgHeight = 300;
            itemImage = Bitmap.createScaledBitmap(itemImage, imgWidth, imgHeight, true);
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), itemImage);
            holder.mImageView.setImageBitmap(itemImage);
            final float roundPx = (float) imgWidth * 0.1f;
            roundedBitmapDrawable.setCornerRadius(roundPx);
            holder.mImageView.setImageDrawable(roundedBitmapDrawable);
            holder.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        }

        private Bitmap getImage(URL url) {
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    return BitmapFactory.decodeStream(connection.getInputStream());
                } else
                    return null;
            } catch (Exception e) {
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            File file = activity.getBaseContext().getFileStreamPath(fileName);
            if(file.exists()) {
                FileInputStream fis = null;
                try {
                    fis = activity.openFileInput(fileName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.i("RCV", "Found the file locally");
                itemImage = BitmapFactory.decodeStream(fis);
            }else {
                itemImage = getImage(imageURL);
                try {
                    FileOutputStream outputStream = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
                    itemImage.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    Log.i("RCV", "Downloaded the file from the Internet");
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mNameView;
        public final TextView mPriceView;
        public AmazonItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.itemImage);
            mNameView = (TextView) view.findViewById(R.id.itemName);
            mPriceView = (TextView) view.findViewById(R.id.itemPrice);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPriceView.getText() + "'";
        }
    }
}
