package org.gskbyte.collection;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Allows some extra methods, just by implementing the abstract method keyForValue()
 * 
 * implements most methods from the List interface
 * */
public abstract class ListHashMap<K, V>
extends ArrayHashMap<K, V>
{
private static final long serialVersionUID = -8108026109763603119L;

public ListHashMap()
{ super(); }

public ListHashMap(int capacity)
{ super(capacity); }

public ListHashMap(int capacity, float loadFactor)
{ super(capacity, loadFactor); }

public ListHashMap(Map<K,V> map)
{ super(map); }

/**
 * The star method 
 * */
protected abstract K keyForValue(V value);

public V put(V value)
{ return super.put(keyForValue(value), value); }

// List-like methods

public boolean add(V value)
{ return add(keyForValue(value), value); }

public boolean add(int index, V value)
{ return add(index, keyForValue(value), value); }

public int indexOf(V value)
{ return unmodifiableKeyList().indexOf(keyForValue(value)); }


//true if list changes, false if not
public boolean addAll(List<V> values)
{
    boolean appendsSomething = false;
    for(V v : values) {
        if( add(v) ) {
            appendsSomething = true;
        }
    }
    
    return appendsSomething;
}

//true if list changed
public boolean removeAll(Collection<V> values)
{
    boolean changed = false;
    for(V v : values) {
        K key = keyForValue(v);
        if( this.remove(key) != null)
            changed = true;
    }
    return changed;
}

//true if list changed
public boolean retainAll(Collection<V> values)
{
    boolean changed = false;
    for(V externalValue : values) {
        K keyForExternal = keyForValue(externalValue);
        if(!containsKey(keyForExternal)) {
            remove(keyForExternal);
            changed = true;
        }
    }
    return changed;
}

public boolean containsAllValues(Collection<? extends V> values)
{
    for(V v : values) {
        if(!containsValue(v))
            return false;
    }
    return true;
}
@SuppressWarnings("unchecked")
@Override
public boolean containsValue(Object value)
{
    return containsKey( keyForValue((V)value) );
}


}
