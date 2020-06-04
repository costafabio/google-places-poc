package com.fabiocosta.googleplacespoc.ui.home;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;

import com.fabiocosta.googleplacespoc.GooglePlaceHelper;
import com.fabiocosta.googleplacespoc.HttpHelper;
import com.fabiocosta.googleplacespoc.PlacesHelper;
import com.fabiocosta.googleplacespoc.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends ListFragment {
    private static final int MAX_NUMBER_OF_PLACES_TO_DISPLAY = 10;
    private final String TAG = "GooglePlacesPOC";
    private HomeViewModel homeViewModel;
    private LocationManager locationManager;
    private static final String GOOGLE_API_KEY = "";
    private PlaceAdapter mListAdapter;
    private static String mRadiusSelected = "";
    private static int counter = 0;
    private GooglePlaceHelper googlePlaceHelper;
    private FusedLocationProviderClient fusedLocationClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "HomeFragment onCreateView...");
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.list_fragment, container, false);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        Button searchButton = (Button) root.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateSearch();
            }
        });

        Spinner radiusSpinner = (Spinner)root.findViewById(R.id.radiusSpinner);
        radiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mRadiusSelected = parentView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
        //        R.array.Planets, android.R.layout.simple_list_item_1);
        // Construct the data source
        //TODO: LOAD CURRENT LIST FROM STORAGE
        ArrayList<Place> arrayOfPlaces = new ArrayList<Place>();
        // Create the adapter to convert the array to views
        mListAdapter = new PlaceAdapter(getContext(), arrayOfPlaces);
        setListAdapter(mListAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        googlePlaceHelper = new GooglePlaceHelper(GOOGLE_API_KEY);
    }


    private void initiateSearch() {
        int radius = getRadiusInMiles();
        Log.i(TAG, "Initiating search with a radius of " + radius + " miles...");
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        Log.i(TAG, "Getting current location...");
        if (ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Task<Location> locationResult = fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                onLocationChanged(location);
                            } else {
                                Log.e(TAG, "ERROR: Could not get fused location!");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        } else {
            Log.e(TAG, "ERROR: Does not have permission to access location...");
        }
    }

    public void onLocationChanged(Location location) {
        int radius = getRadiusInMiles();
        Log.i(TAG, "Got location fix: lat=" + location.getLatitude() + ", lon=" + location.getLongitude());
        // Read from Google Places for this location
        readGooglePlaces(location, radius);
    }

    private void readGooglePlaces(Location location, int radius) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String url = googlePlaceHelper.getNearbySearchURL(lat, lon, radius);

        GooglePlacesTask googlePlacesReadTask = new GooglePlacesTask();
        Object[] toPass = new Object[1];
        toPass[0] = url;
        googlePlacesReadTask.execute(toPass);
    }

    private int getRadiusInMiles() {
        int radius = 10;

        if(mRadiusSelected.equals("5 miles"))
            radius = 5;
        else if(mRadiusSelected.equals("10 miles"))
            radius = 10;
        else if(mRadiusSelected.equals("15 miles"))
            radius = 15;
        else if(mRadiusSelected.equals("20 miles"))
            radius = 20;
        else if(mRadiusSelected.equals("25 miles"))
            radius = 25;
        else if(mRadiusSelected.equals("50 miles"))
            radius = 50;

        return radius;
    }

    private class GooglePlacesTask extends AsyncTask<Object, Integer, String> {
        private static final String TAG = "GooglePlacesTask";
        String googlePlacesData = null;

        @Override
        protected String doInBackground(Object... inputObj) {
            Log.i(TAG, "Entering doInBackground()...");
            try {
                String googlePlacesUrl = (String) inputObj[0];
                //Log.i(TAG, "Running with Google Place URL: " + googlePlacesUrl);
                HttpHelper http = new HttpHelper();
                googlePlacesData = http.read(googlePlacesUrl);
            } catch (Exception e) {
                Log.d("Google Place Read Task", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Entering onPostExecute()...");
            PlacesListTask placesListTask = new PlacesListTask();
            Object[] toPass = new Object[1];
            toPass[0] = result;
            placesListTask.execute(toPass);
        }
    }


    private class PlacesListTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {
        private static final String TAG = "PlacesListTask";
        JSONObject googlePlacesJson;

        @Override
        protected List<HashMap<String, String>> doInBackground(Object... inputObj) {
            Log.i(TAG, "Entering doInBackground()...");
            List<HashMap<String, String>> googlePlacesList = null;
            PlacesHelper placeJsonParser = new PlacesHelper();

            try {
                googlePlacesJson = new JSONObject((String) inputObj[0]);
                googlePlacesList = placeJsonParser.parse(googlePlacesJson);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return googlePlacesList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            Log.i(TAG, "Entering onPostExecute(): " + list.size() + " results returned");
            // go through list of places returned up to the max number to show (10)
            for (int i = 0; i < list.size() && i < MAX_NUMBER_OF_PLACES_TO_DISPLAY; i++) {
                HashMap<String, String> googlePlace = list.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lon = Double.parseDouble(googlePlace.get("lon"));
                float rating = Float.parseFloat(googlePlace.get("rating"));
                String placeName = googlePlace.get("place_name");
                LatLng latLng = new LatLng(lat, lon);
                String photoRef = googlePlace.get("photo_reference");
                String imgUrl = null;
                if(photoRef != null) {
                    imgUrl = googlePlaceHelper.getImageURL(photoRef, 300);
                }
                Log.i(TAG, "-> Place #" + i +": name=" + placeName + ", lat=" + lat + ", lon=" + lon + ", rating=" + rating + ", photo_reference=" + photoRef);

                // finally spawn task to add places to the list
                AddToListTask addToListTask = new AddToListTask();
                Object[] toPass = new Object[3];
                toPass[0] = placeName;
                toPass[1] = imgUrl;
                toPass[2] = rating;
                addToListTask.execute(toPass);
            }
        }
    }

    private class AddToListTask extends AsyncTask<Object, Integer, HashMap<String, Object>> {
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
            mListAdapter.add(newPlace);
        }
    }
}