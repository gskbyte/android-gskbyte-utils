AndroidUtils
============

Some utility views, utility classes, fragments and Activities for Android projects. It is intended to be used as an Android Library Project on Eclipse.

This is a not-yet-finished project and will be improved while I develop new stuff. I hope it is somehow useful.

Included components
-------------------

### Collections ###

- Double sparse array: a map with integer keys, with fast access per key but also per value.

### Views / Interface components ###

- BitmapColorizer: useful class to colorize a Bitmap. Simplifies getting lots of colorized copies of an android.graphics.Bitmap object. Supports different color configurations and transparency.
- CachedBitmapColorizer: provides the same functionality as the previous class, but keeping an LRU cache with a maximum defined size. Useful if certain color combinations are more frequent. The bitmap size computation is compatible with older Android versions.

    #### IconifiedMainMenuList ###
    
    This is a custom view to be used in the ActionBar, when the different Fragments of the Main Activity are accesed via a dropdown menu.


    #### ColorDialog ####
    
    - ColorDialog: a dialog to choose a color given ARGB values. These values are selected using SeekBars. Provides a simple preview of the choosen color, as well as over a desired image.
    
### Preferences ###

- DialogSeekBarPreference: an integer preference using a SeekBar. Supports minimum, maximum, suffix and some description text.
- InlineSeekBarPreference: an integer preference using a SeekBar, embedded in the preference, without opening a dialog.
- ColorPreference: based on the ColorDialog, not yet finished!!!

### Other utilities ###

- Logger: wraps the android.util.Log class, while giving some extra methods.

Usage
-----

The project itself is an Android Library Project, to use it within your project, just import is as a library via right click -> Properties -> Android -> Add... (in the library section)

