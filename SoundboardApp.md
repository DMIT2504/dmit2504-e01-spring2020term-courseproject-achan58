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

        - Create empty fragment + layout
        - Include fragments in host layout (eg.activity_main)
            *note: Using FrameLayout to swap fragments instead



# Color locations:
R/layout
- styles.xml

R/values
- nav_header.xml

fragments
- background
- image buttons
