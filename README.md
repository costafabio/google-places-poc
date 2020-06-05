# Google Places POC

This repository hosts the code used for a Google Places POC.

## Requirements

As a result from the search, the app should:
- Display 10 places from the search area.
- Default the search to a 10 miles area around the users current location.
- Allow the user to extend the search radius.
- Automatically search a wider area if there are no results found near the user.
- For each row in the list should present:
    - name
    - image
    - rating
- Persist the search results in the app you build
- Allow future visits see previous searches and their results.

## Dependencies

The correct operation of this app relies that the following dependencies are met:

- Minimum Android SDK version: 23
- Google Play services APK installed


## App install

Grab the APK from ./extras/google-places-poc.apk and install to your device using ADB:

```
adb install google-places-poc.apk
```

## App operation

After you run the app, you will see a regular tabbed activity with three tabs: Home, History, and Notifications.

1) The Home tab is the one from where the user can select the search radius from the pull down menu and tap on the "Search" button to start the search. Up to 10 results will be listed in this tab. You can adjust the search radius and tap "Search" to initiate a new search.
For test purposes you can install the [Fake GPS Location](https://play.google.com/store/apps/details?id=com.lexa.fakegps&hl=en_US) from Google Play store that will let you pick any locatoin in the map and override your phone's current location so you will get different places being returned and can play with the radius settings by selecting a deserted location and see what come back.

2) The History tab shows a list of all previous and current searches performed in the app. You can tap on a saved search and the places related to that search will be listed on screen.

3) The Notifications tab is currently not being used.

Home screen: 
![home-screen](https://github.com/costafabio/google-places-poc/blob/master/extras/home-screen.png "Home screen")

Search results example (Google CA): 
![search-results-google](https://github.com/costafabio/google-places-poc/blob/master/extras/search-results-Google.png "Search results - Google CA")

Search results example (Plantation, FL): 
![search-results-plantation](https://github.com/costafabio/google-places-poc/blob/master/extras/search-results-Plantation.jpg "Search results - Plantation")

Search history tab: 
![search-results-plantation](https://github.com/costafabio/google-places-poc/blob/master/extras/search-history-tab.png "Search history tab")

Recalled search history results: 
![search-results-plantation](https://github.com/costafabio/google-places-poc/blob/master/extras/recalled-searh-history.png "Recalled search history")



## Issues & workarounds

Currently here are the issues & workarounds for this app:

- Considering the app relies on Google Play Services to provide location support, the app might not work on older devices with older version of the Play Services app. Ideally we should understand the minimum version needed and alert the user it needs to be updated or just adjust the minimum SDK version accordingly.

- When recalling a saved search from the History tab, the search results will show on the same fragment as the list of saved searches and there is no intuitive way to return to see the list of saved searches. The workaround is to tap on the History tab again or tap on another tab and come back.

- The only way to install the app currrently is to download the provided APK (in the ./extras folder) and side loaded with ADB. I did not get around uploading it to Azure App Center or similar service where the app can be pushed to a device over the web.

- The Google Places search API is being called without a "type" parameter, which means it should be returning all types of POIs. It would be useful if we add another Spinner from where the user could choose from a list of places (restaurants, hotels, gas stations, etc) so it would only return those places.