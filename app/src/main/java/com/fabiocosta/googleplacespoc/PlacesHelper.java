package com.fabiocosta.googleplacespoc;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlacesHelper {

    private static final String TAG = "PlacesHelper";

    public List<HashMap<String, String>> parse(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        String status = null;
        String errorMsg = null;
        try {
            // Find out read status
            status = jsonObject.getString("status");
            if(status.equals("REQUEST_DENIED")) {
                errorMsg = jsonObject.getString("error_message");
                Log.e(TAG, "ERROR: Got error message: " + errorMsg);
            }
            // Get results array
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> placeMap = null;

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
        String placeName = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        JSONArray photos = null;
        String photoReference = "";
        String rating = "0.0";

        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("photos")) {
                photos = googlePlaceJson.getJSONArray("photos");
                if(photos != null) {
                    int photosCount = photos.length();
                    if(photosCount > 0) {
                        // get first element in the array only
                        JSONObject photoObj = (JSONObject) photos.get(0);
                        if (!photoObj.isNull("photo_reference")) {
                            photoReference = photoObj.getString("photo_reference");
                            googlePlaceMap.put("photo_reference", photoReference);
                        }
                    }
                }
            }
            if (!googlePlaceJson.isNull("rating")) {
                rating = googlePlaceJson.getString("rating");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");
            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lon", longitude);
            googlePlaceMap.put("reference", reference);
            googlePlaceMap.put("rating", rating);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }
}