package org.gskbyte.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public abstract class ListHashMap<K, V>
extends HashMap<K, V>
{
private static final long serialVersionUID = 7659551400603461435L;


private final List<V> list;

public ListHashMap()
{
    list = new ArrayList<V>();
}

public ListHashMap(int capacity)
{
    super(capacity);
    list = new ArrayList<V>(capacity);
}

// TODO add other constructors

// implementing this method allows to call:
// append(V)
// removeAt(int)
// removeAll(array())
protected abstract K keyForValue(V value);

public V getAt(int location)
{ return list.get(location); }

// adds at the end of the array
@Override
public V put(K key, V value)
{
    V oldValue = super.put(key, value);
    if(oldValue != null) {
        int index = list.indexOf(value);
        list.set(index, value);
    } else {
        list.add(value);
    }
    return oldValue;
}

@Override
public void putAll(Map<? extends K, ? extends V> m)
{
    if( (m instanceof ListHashMap<?, ?>) || (m instanceof LinkedHashMap<?, ?>) ) {
        appendAll( ImmutableList.copyOf(m.values()) );
    } else {
        throw new IllegalArgumentException("Can't get order of elements in class " + m.getClass() + ", it must be subclass of ListHashMap or LinkedHashMap");
    }
}

@Override
public Set< Map.Entry<K, V> > entrySet()
{
    LinkedHashSet< Map.Entry<K,V> > ret = new LinkedHashSet< Map.Entry<K,V>>( size() );
    for(V v : list) {
        ret.add( new Entry<K,V>(keyForValue(v), v) );
    }
    return ret;
}

@Override
public Collection<V> values()
{ return valuesList(); }

public List<V> valuesList()
{ return ImmutableList.copyOf(list); }

// only works if keyForValue() doesn't return null
public boolean append(V value)
{
    K key = keyForValue(value);
    return append(key, value);
}

// false if already existing, does nothing
public boolean append(K key, V object)
{
    if( ! containsKey(key) ) {
        list.add(object);
        super.put(key, object);
        return true;
    } else {
        return false;
    }
}

// true if list changes, false if not
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

@Override
public void clear()
{
    super.clear();
    list.clear();
}

@SuppressWarnings("unchecked")
@Override
public Object clone()
{
    ListHashMap<K, V> copy;
    try {
        copy = this.getClass().newInstance();
        copy.appendAll( this.list );
        return copy;
    } catch (InstantiationException e) {
        // won't happen
    } catch (IllegalAccessException e) {
        // won't happen
    }
    return null;
}

public boolean containsAllValues(Collection<? extends V> values)
{
    for(V v : values) {
        if(!containsValue(v))
            return false;
    }
    
    return true;
}

public int indexOf(V object)
{ return list.indexOf(object); }

@Override
public V remove(Object key)
{
    V removed = super.remove(key);
    if( removed != null) {
        list.remove(removed);
    }
    return removed;
}

// works only if keyForValue() implemented, otherwise throws exception

public V removeAt(int location)
{
    V value = list.remove(location);
    K key = keyForValue(value);
    super.remove(key);
    return value;
}

// true if list changed
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

public boolean retainAll(Collection<?> values)
{
    final ArrayList<V> toRemove = new ArrayList<V>();
    for(int i=0; i<list.size(); ++i) {
        V value = list.get(i);
        if( ! values.contains(value) ) {
            toRemove.add(value);
        }
    }
    
    return removeAll(toRemove);
}


private static final class Entry<KK,VV>
implements Map.Entry<KK, VV>
{
public final KK key;
public VV value;

public Entry(KK key, VV value)
{ this.key = key; this.value = value; }

public KK getKey()
{ return key; }

public VV getValue()
{ return value; }

public VV setValue(VV v)
{ VV old = this.value; this.value = v; return old; }
}

}
