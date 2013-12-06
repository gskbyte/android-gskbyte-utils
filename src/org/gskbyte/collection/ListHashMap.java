package org.gskbyte.collection;

import java.util.Collection;
import java.util.List;

/**
 * Allows some extra methods, just by implementing the abstract method keyForValue()
 * */
public abstract class ListHashMap<K, V>
extends ArrayHashMap<K, V>
{
private static final long serialVersionUID = -8108026109763603119L;

protected abstract K keyForValue(V value);

public boolean append(V value)
{
    K key = keyForValue(value);
    return append(key, value);
}

//true if list changes, false if not
public boolean appendAll(List<V> values)
{
    boolean appendsSomething = false;
    for(V v : values) {
        if( append(v) ) {
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
