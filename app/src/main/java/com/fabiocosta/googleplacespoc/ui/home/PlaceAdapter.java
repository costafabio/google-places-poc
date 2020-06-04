package com.fabiocosta.googleplacespoc.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fabiocosta.googleplacespoc.R;

import java.util.ArrayList;

public class PlaceAdapter extends ArrayAdapter<Place> {
    private final String TAG = "GooglePlacesPOC";
    public PlaceAdapter(Context context, ArrayList<Place> places) {
        super(context, 0, places);
        Log.i(TAG, "PlaceAdapter constructor called...");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Place place = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.placeName);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.placeImageView);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        // Populate the data into the template view using the data object
        tvName.setText(place.name);
        if (place.image != null) {
            imageView.setImageBitmap(place.image);
        }
        ratingBar.setRating(place.rating);
        ratingBar.setNumStars(5);
        // Return the completed view to render on screen
        return convertView;
    }
}
