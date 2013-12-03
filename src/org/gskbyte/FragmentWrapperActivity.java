package org.gskbyte;

import org.gskbyte.util.Logger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * A very simple activity whose content is just a Fragment. Useful to develop just fragments to implement stuff and avoid the
 * use of mixins and some other overcomplicated stuff.
 * */

public class FragmentWrapperActivity
extends FragmentActivity
{

private static final String FRAGMENT_CLASS_NAME = "fragmentClass";
private static final String FRAGMENT_ARGUMENTS = "fragmentArguments";

/**
 * Call this static method to get an intent for the given Fragment class and arguments. Use this method whenever possible.
 * @param context Context from which to launch the Activity.
 * @param fragmentClass The fragment class that will fulfill the Activity.
 * @param fragmentArguments The arguments to be passed to the fragment contained in the activity.
 * @return An intent to be passed to startActivity() 
 * */
public static Intent GetIntentForFragment(Context context, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments)
{
    Intent i = new Intent(context, FragmentWrapperActivity.class);
    i.putExtra(FRAGMENT_CLASS_NAME, fragmentClass);
    if(fragmentArguments != null)
        i.putExtra(FRAGMENT_ARGUMENTS, fragmentArguments);
    return i;
}

/**
 * This method does the same as the above, but is particularly useful for situations on which we can't access to
 * the arguments to pass to the Fragment (for example, we *must* call a static method to instantiate it). It's easier to use
 * but involves allocating more memory, but it should be safe on most situations. If not, bad luck.
 * @param context Context from which to launch the Activity.
 * @param fragmentClass The initialized fragment that will fulfill the Activity.
 * @return An intent to be passed to startActivity() 
 * */
public static Intent GetIntentForFragment(Context context, Fragment fragment)
{
    return GetIntentForFragment(context, fragment.getClass(), fragment.getArguments());
}


/**
 * Override to implement custom WrapperActivities
 * */
@SuppressWarnings("unchecked")
public Class<? extends Fragment> getFragmentClass(Bundle extras)
{
    Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) extras.getSerializable(FRAGMENT_CLASS_NAME);
    return fragmentClass;
}

/**
 * Override to customize 
 * */
public Bundle getFragmentArguments(Bundle extras)
{
    return extras.getBundle(FRAGMENT_ARGUMENTS);
}

@Override
protected void onCreate(Bundle savedState)
{
    super.onCreate(savedState);
    setContentView(R.layout.activity_full_fragment);
    
    Bundle extras = getIntent().getExtras();
    Class<? extends Fragment> fragmentClass = getFragmentClass(extras);
    Bundle fragmentArguments = getFragmentArguments(extras);
    
    if(fragmentClass == null) {
        throw new IllegalStateException("fragment class is null, a fragment can't be instantiated");
    }
    
    try {
        Fragment fragment = fragmentClass.newInstance();
        if(fragmentArguments != null)
            fragment.setArguments(fragmentArguments);
        
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    } catch (InstantiationException e) {
        Logger.except(getClass(), e);
    } catch (IllegalAccessException e) {
        Logger.except(getClass(), e);
    }
}

}
