AndroidUtils
============

Some utility views, utility classes, fragments and Activities for Android projects. It is intended to be used as an Android Library Project on Eclipse.

This is a not-yet-finished project and will be improved while I develop new stuff. I hope it is somehow useful for you!

Included components
-------------------

### Bitmap stuff ###

- LRUBitmapCache: an implementation of an LRU cache especially designed for Bitmaps.
- BitmapColorizer: utility class to get colorized copies of a given Bitmap.
    - CachedBitmapColorizer: utilizes an LRU cache to improve access speed.
- BitmapManager class: manages bitmaps referenced by their path and depending on their location (resources, assets, private directory or external directory). Loads images using lazy load.
- ReferencedBitmaps: class used to reference subsets of a BitmapManager, allowing to have just a BitmapManager per application.
- IndexedBitmaps: same functionality as ReferencedBitmaps, allowing to refer to bitmaps by index.

### Collections ###

- Double hash map: hashmap that allows fast access by key and value (obviously uses more memory than a single hash map)
- Double sparse array: a map with integer keys, with fast access by key but also by value.

### Interface components ###

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

### Listener ###

- Listenable class: thread-safe abstract class that includes methods to manage listeners using weak references.

### Tasks and concurrency ###

- QueuedTaskExecutor and Task: a QueuedTaskExecutor allows concurrently executing sets of tasks one after another. This way, you can for example execute three tasks and wait for all them before executing another set of tasks.

### Utility classes ###

- FrequentIntents: to launch common Android intents such as maps, call, email, etc.
- Logger: wraps the android.util.Log class, while giving some extra methods.
- IOUtils: includes methods to copy and move files from different locations, as well as some other small things.
- XMLUtils: utility class to have a more direct and faster access to DOM XML objects.

### Views ###

- AsyncImageView: image view that loads a bitmap in background, having an AbstractBitmapManager as backend.
- AutoBackgroundButton, AutoBackgroundImageButton: buttons that change their background dynamically, avoiding to create two images for it.
- AutoHeightImageView: ImageView that computes its height depending on the given width and a minimum proportion.
- FontableButton, -EditText and -TextView: text components that easily allow using fonts located in the assets folder.
    - FontUtil: class with static methods used as a helper by the previous listed components.
- PullToRefreshListView: pullable ListView originally created by Erik Wallentinsen (https://github.com/erikwt/PullToRefresh-ListView) with some small enhancements.
- StepSeekBar: a seekbar that displays secondary thumb points in the available positions.
- TextViewUtil: some static methods to set formatted text into TextViews.
- ViewUtils: some utility methods to deal with View subclasses.

Usage
-----

The project itself is an Android Library Project, to use it within your project, just import is as a library via right click -> Properties -> Android -> Add... (in the library section)

License
-------

This project is released under the LGPLv3 license. Basically, it implies:
1. If you make changes to any component, you must make your changes public.
2. If you use use any component in an own project, please include a reference this original project.
3. You can create closed-source using this project, just pay attention to point 2 :)

Demo Project
------------

There is no demo project yet, it will be added soon!

