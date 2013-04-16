package org.gskbyte.listener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ListenableMixin<ListenerClass>
extends Listenable<ListenerClass>
{

public List< WeakReference<ListenerClass> > getListeners()
{ return listeners; }

public synchronized List<ListenerClass> getListenersSafe()
{
    cleanupListeners();
    ArrayList<ListenerClass> list = new ArrayList<ListenerClass>(listeners.size());
    for(WeakReference<ListenerClass> lref : listeners) {
        list.add(lref.get());
    }
    return list;
}

}
