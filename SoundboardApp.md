# References:

## NavDrawer 
- https://guides.codepath.com/android/Fragment-Navigation-Drawer
    
        - implementation 'com.google.android.material:material:1.0.0'    (may not need)
        - Create menu with a Toolbar
        - Disable default Toolbar to use as ActionBar in styles.xml
        - Create drawer layout file with androidx.drawerlayout.widget.DrawerLayout as root
        - Add code to enable nav drawer functionality

    Optional:
        NavDrawer Header:
    
        Create nav header layout for NavigationView to reference (in activity_main)
        
    Hamburger icon:
    
        Add code to change icon to animated hamburger menu
        
    Translucent Status Bar: 

        name="android:windowTranslucentStatus">true in styles.xml
     

## Fragments 
- https://www.tutorialspoint.com/android/android_fragments.htm#:~:text=A%20Fragment%20is%20a%20piece,its%20own%20life%20cycle%20callbacks.
- https://www.tutorialspoint.com/android/android_single_fragments.htm
- https://developer.android.com/training/basics/fragments/communicating.html
- https://developer.android.com/topic/libraries/architecture/viewmodel
- https://heartbeat.fritz.ai/passing-data-between-fragments-on-android-using-viewmodel-d47fa6773f9c
- https://www.youtube.com/watch?v=ACK67xU1Y3s

        - implementation "android.arch.lifecycle:extensions:2.0.0"
        - implementation "androidx.lifecycle:lifecycle-common-java8:2.0.0"  (Java 8 req)
        - Create empty fragment + layout
        - Include fragments in host layout (eg.activity_main)
            *note: Using FrameLayout to swap fragments instead

    Passing data between fragments using ViewModel and LiveData

        - Create a ViewModel to interface between fragments
        - Instantiate ViewModel in the onCreate of the activity hosting the fragments
        - Set an observer to listen for changes in ViewModel


## AssetManager for reading assets included in APK
- http://developer.android.com/reference/android/content/res/AssetManager.html

        - getAssets is an object of AssetManager
        - requires context of activity when used inside a fragment (getActivity().getAssets())



## MediaPlayer
- https://developer.android.com/training/data-storage/shared/media
- https://stackoverflow.com/questions/30100083/how-to-play-audio-file-in-android-from-internal-memory-of-phone
- https://developer.android.com/training/data-storage/shared/documents-files

        - In manifest, get permission: READ_EXTERNAL_STORAGE
        - In code, ask user for permission (for api23+ devices)
        - MediaPlayer should be a global variable, or else garbage collection will dispose before audio is finished
        - .release() MediaPlayer instances when finished to release resources


## Accessing external storage

## MediaRecorder
- https://developer.android.com/guide/topics/media/mediarecorder
- https://xjaphx.wordpress.com/2011/10/02/store-and-use-files-in-assets/

        - In manifest get permission: RECORD_AUDIO

# Color locations:
R/layout
- styles.xml

R/values
- nav_header.xml

fragments
- background
- image buttons
