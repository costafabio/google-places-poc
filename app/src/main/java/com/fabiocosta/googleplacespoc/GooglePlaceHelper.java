package com.fabiocosta.googleplacespoc;

import android.util.Log;

public class GooglePlaceHelper {
    private static final String TAG = "GooglePlaceHelper";
    private static String googleApiKey = null;

    public GooglePlaceHelper(String key) {
        this.googleApiKey = key;
    }

    public String getNearbySearchURL(double lat, double lon, int radiusInMiles) {
        Log.i(TAG, "Reading Google Places for lat=" + lat + ", lon=" + lon + ", radius=" + radiusInMiles + " miles...");
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + lat + "," + lon);
        googlePlacesUrl.append("&radius=" + radiusInMiles*1609.344);
        googlePlacesUrl.append("&types=establishment");
        googlePlacesUrl.append("&key=" + googleApiKey);

        return googlePlacesUrl.toString();
    }


    public String getImageURL(String photoRef, int width) {
        // assemble photo URL
        StringBuilder imageUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        imageUrl.append("maxwidth=" + width);
        imageUrl.append("&photoreference=" + photoRef);
        imageUrl.append("&key=" + googleApiKey);

        return imageUrl.toString();
    }
}
