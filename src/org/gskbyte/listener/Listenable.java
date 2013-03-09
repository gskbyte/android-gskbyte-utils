package org.gskbyte.listener;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class Listenable<ListenerClass>
{

protected final CopyOnWriteArrayList< WeakReference<ListenerClass> > listeners = new CopyOnWriteArrayList< WeakReference<ListenerClass> >();

public synchronized void addListener(ListenerClass listener)
{
    cleanupListeners();
    for(WeakReference<ListenerClass> l : listeners) {
        if(l.get() == listener)
            return;
    }
    listeners.add( new WeakReference<ListenerClass>(listener) );
}

protected synchronized void cleanupListeners()
{
    Iterator< WeakReference<ListenerClass> > it = listeners.iterator();
    while(it.hasNext()) {
        ListenerClass l = it.next().get();
        if(l == null)
            it.remove();
    }
}

public synchronized void removeListener(ListenerClass listener)
{
    Iterator< WeakReference<ListenerClass> > it = listeners.iterator();
    while(it.hasNext()) {
        ListenerClass l = it.next().get();
        if(l == listener || l == null)
            it.remove();
    }
}

public synchronized void removeAllListeners()
{ listeners.clear(); }
}
