package org.gskbyte.listener;


public interface IListenable<ListenerClass>
{

/**
 * Adds a listener, if it's not already added. Before doing it, this method
 * removes the existing null listener weak references.
 * @param listener Listener to add
 * @return true If the listener has been added, false if it was already there.
 * */
public boolean addListener(ListenerClass listener);

/**
 * Removes the given listener from the list.
 * @param listener The listener to remove.
 * @return true if listener was in the list, false otherwise
 * */
public boolean removeListener(ListenerClass listener);
/** Removes all listeners */
public void removeAllListeners();

}
