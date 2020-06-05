package com.fabiocosta.googleplacespoc.ui.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;

import com.fabiocosta.googleplacespoc.R;
import com.fabiocosta.googleplacespoc.ui.home.Place;
import com.fabiocosta.googleplacespoc.ui.home.PlaceAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class HistoryFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "HistoryFragment";
    private HistoryViewModel historyViewModel;
    private ArrayList<History> mHistoryArray;
    private JSONArray mSavedSearchesJsonArray;
    private PlaceAdapter mPlaceAdapter;
    private static boolean mInDetailedViewMode = false;
    private TextView historyTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        // retrieve saved search results
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String savedSearchResults = sharedPref.getString(getString(R.string.saved_current_search_results), null);
        if(savedSearchResults != null) {
            Log.i(TAG, "*** LOADED SAVED SEARCH RESULTS: " + savedSearchResults);
            try {
                mSavedSearchesJsonArray = new JSONArray(savedSearchResults);
                Log.i(TAG, "*** NUMBER OF SEARCHES SAVED: " + mSavedSearchesJsonArray.length());
                mHistoryArray = extractHistoryDataFromPreviousSearches(mSavedSearchesJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "*** No saved search results loaded...");
        }

        historyTextView = (TextView)root.findViewById(R.id.historyText);
        historyTextView.setText("Saved Searches");

        return root;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create the adapter to convert the array to views
        if(mHistoryArray != null) {
            HistoryAdapter historyAdapter = new HistoryAdapter(getContext(), mHistoryArray);
            setListAdapter(historyAdapter);
            getListView().setOnItemClickListener(this);
        }
        // indicate we are in the historical view
        mInDetailedViewMode = false;
    }


    private ArrayList<History> extractHistoryDataFromPreviousSearches(JSONArray array) {
        ArrayList<History> historyList = new ArrayList<History>();
        try {
            for(int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String date = obj.getString("search_date");
                History history = new History(date);
                historyList.add(history);
                //Log.i(TAG, "... search #" + i + " (" + date + ")");
                JSONArray results = obj.getJSONArray("search_results");
                for(int j = 0; j < results.length(); j++) {
                    String name = results.getJSONObject(j).getString("place_name");
                    //Log.i(TAG, "...... result #" + j + ": " + name);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return historyList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // only process click action if we are showing the historical view
        if(mInDetailedViewMode == false) {
            String date = mHistoryArray.get(position).date;
            Log.i(TAG, "[onItemClick] Clicked item: " + date);

            historyTextView.setText("- Search Results From " + date);

            // create the adapter to convert the array to views
            ArrayList<Place> arrayOfPlaces = new ArrayList<Place>();
            mPlaceAdapter = new PlaceAdapter(getContext(), arrayOfPlaces);
            setListAdapter(mPlaceAdapter);

            // get list of places for the selected history and update list
            mInDetailedViewMode = true;
            getPlacesFromSavedHistory(position);
        }
    }

    private void getPlacesFromSavedHistory(int position) {
        try {
            JSONObject obj = mSavedSearchesJsonArray.getJSONObject(position);
            JSONArray resultsArray = obj.getJSONArray("search_results");
            for(int j = 0; j < resultsArray.length(); j++) {
                String name = resultsArray.getJSONObject(j).getString("place_name");
                String imgUrl = resultsArray.getJSONObject(j).getString("place_imgUrl");
                double rating = resultsArray.getJSONObject(j).getDouble("place_rating");
                //Log.i(TAG, "...... place #" + j + ": " + name);

                // finally spawn task to add places to the list
                AddPlaceToListTask addToListTask = new AddPlaceToListTask();
                Object[] toPass = new Object[3];
                toPass[0] = name;
                toPass[1] = imgUrl;
                toPass[2] = (float)rating;
                addToListTask.execute(toPass);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddPlaceToListTask extends AsyncTask<Object, Integer, HashMap<String, Object>> {
        private static final String TAG = "AddToListTask";

        @Override
        protected HashMap<String, Object> doInBackground(Object... inputObj) {
            HashMap<String, Object> placeMap = new HashMap<String, Object>();
            String name = (String)inputObj[0];
            String imgUrl = (String)inputObj[1];
            float rating = (float)inputObj[2];
            Bitmap bmp = null;
            if(imgUrl != null) {
                try {
                    URL url = new URL(imgUrl);
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            placeMap.put("name", name);
            placeMap.put("bitmap", bmp);
            placeMap.put("rating", rating);
            return placeMap;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> map) {
            String name = (String)map.get("name");
            Bitmap bitmap = (Bitmap)map.get("bitmap");
            float rating = (float)map.get("rating");
            Place newPlace = new Place(name, bitmap, rating);

            Log.i(TAG, "Adding new place: " + name);
            mPlaceAdapter.add(newPlace);
        }
    }
}