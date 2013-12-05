/*******************************************************************************
 * Copyright (c) 2013 Jose Alcalá Correa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Contributors:
 *     Jose Alcalá Correa - initial API and implementation
 ******************************************************************************/
package org.gskbyte.listener;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Abstract base class for classes who can have listeners (implementation of the
 * delegate design pattern). The methods included in this class are thread safe.
 * 
 * Classes extending this abstract class can have multiple listeners. What this
 * class does is to store weak references to the listeners, avoiding dependency
 * cycles.
 * 
 * When iterating over the listeners, subclasses should check that the references
 * are still valid, doing like this:
 * 
 * for(WeakReference<ListenerClass> lref : listeners) {
 *     ListenerClass l = lref.get();
 *     if(l != null) l.method();
 * }
 * 
 * */
public abstract class ListenableNG<ListenerClass>
implements IListenable<ListenerClass>
{

/**
 * Array of listeners. We use a {@link CopyOnWriteArrayList} to allow add and
 * remove listeners while iterating.
 * 
 * A {@link CopyOnWriteArraySet} would be faster for adding and removing
 * elements, but not for iterating and it's a much more executed action.
 * */
private final ArrayList< WeakReference<ListenerClass> > mutableListeners = new ArrayList< WeakReference<ListenerClass> >();
private ImmutableList< WeakReference<ListenerClass> > immutableListeners = null;

public List< WeakReference<ListenerClass> > getListeners()
{
    if(immutableListeners == null) {
        immutableListeners = ImmutableList.copyOf(mutableListeners);
    }
    return immutableListeners;
}

private void invalidateImmutableListeners()
{
    for(WeakReference<ListenerClass> lref : getListeners()) {
        ListenerClass l = lref.get();
        if(l == null) {
            mutableListeners.remove(lref);
        }
    }
    
    immutableListeners = null;
}

public boolean containsListener(ListenerClass listener)
{
    for(WeakReference<ListenerClass> l : mutableListeners) {
        if(l.get() == listener)
            return true;
    }
    return false;
}

/**
 * Adds a listener, if it's not already added. Before doing it, this method
 * removes the existing null listener weak references.
 * @param listener Listener to add
 * @return true If the listener has been added, false if it was already there.
 * */
public synchronized boolean addListener(ListenerClass listener)
{
    if( ! containsListener(listener) ) {
        mutableListeners.add( new WeakReference<ListenerClass>(listener) );
        invalidateImmutableListeners();
        return true;
    } else {
        return false;
    }
}

/**
 * Removes the given listener from the list.
 * @param listener The listener to remove.
 * @return true if listener was in the list, false otherwise
 * */
public synchronized boolean removeListener(ListenerClass listener)
{
    int indexToRemove = -1;
    for(int i=0; i<mutableListeners.size() && indexToRemove<0; ++i) {
        if(mutableListeners.get(i).get() == listener) {
            indexToRemove = i;
        }
    }
    
    if(indexToRemove >= 0) {
        mutableListeners.remove(indexToRemove);
        invalidateImmutableListeners();
        return true;
    } else {
        return false;
    }
}

/** Removes all listeners */
public synchronized void removeAllListeners()
{
    mutableListeners.clear();
    invalidateImmutableListeners();
}
/*
public synchronized void invokeMethodOnListeners(Method m, Object ... args)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
{
    cleanupListeners();
    for(WeakReference<ListenerClass> lref : listeners) {
        ListenerClass l = lref.get();
        m.invoke(l, args);
    }
}

public synchronized void invokeMethodOnListeners(String methodName, Object arg)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
{
    cleanupListeners();
    if(listeners.size()>0) {
        Method m = listeners.get(0).get().getClass().getMethod(methodName, new Class[]{arg.getClass()});
        Object [] args = {arg};
        for(WeakReference<ListenerClass> lref : listeners) {
            ListenerClass l = lref.get();
            m.invoke(l, args);
        }
    }
}

public synchronized void invokeMethodOnListeners(String methodName, Object arg0, Object arg1)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
{
    if(listeners.size()>0) {
        Method m = listeners.get(0).get().getClass().getMethod(methodName, new Class[]{arg0.getClass(), arg1.getClass()});
        Object [] args = {arg0, arg1};
        for(WeakReference<ListenerClass> lref : listeners) {
            ListenerClass l = lref.get();
            m.invoke(l, args);
        }
    }
}
*/


}
