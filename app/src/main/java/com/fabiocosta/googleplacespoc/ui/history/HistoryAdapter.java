package com.fabiocosta.googleplacespoc.ui.history;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fabiocosta.googleplacespoc.R;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<History> {
    private final String TAG = "GooglePlacesPOC";

    public HistoryAdapter(Context context, ArrayList<History> places) {
        super(context, 0, places);
        Log.i(TAG, "HistoryAdapter constructor called...");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        History history = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_row, parent, false);
        }
        // Lookup view for data population
        TextView tvDate = (TextView) convertView.findViewById(R.id.date);
        // Populate the data into the template view using the data object
        tvDate.setText(history.date);
        // Return the completed view to render on screen
        return convertView;
    }
}
