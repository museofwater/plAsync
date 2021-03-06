# plAsync Android Client
========================

This project contains the Android client libraries and Test App for plAsync. 

###Requirements:
1.  Android Studio with Gradle 0.2.3 or better
2.  Android 4.2 SDK

###Modules:

ContentProviderContract - An Android library that provides constants for the plAsync Content
                          Provider.  This library is used by the DataManager and the SDK

DataManager - An Android application that contains a Content Provider implementation for accessing
              locally stored plAsync Configuration that is applicable across multiple apps,
              i.e. plAsync user name and id.

SDK - An Android library that interacts with the server to provide plAsync functionality to
      Android apps.

TestApp - An android application that can exercise all of the plAsync functionality including
          adding friends, sending game invites, and updating game state.

google-play-services_lib - The Google Play Services library, used for Google Cloud Messaging, which
                           must be included as a module in the project so that some of the resources
                           get merged in correctly with the application.
