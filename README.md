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

## Issues & workarounds

Currently 