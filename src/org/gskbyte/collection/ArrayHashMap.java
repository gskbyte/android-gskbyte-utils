package org.gskbyte.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public class ArrayHashMap<K,V>
extends HashMap<K, V>
implements Iterable<V>
{

private static final long serialVersionUID = -3578702226046722524L;

private final List<K> keyList;

public ArrayHashMap()
{
    keyList = new ArrayList<K>();
}

public ArrayHashMap(int capacity)
{
    super(capacity);
    keyList = new ArrayList<K>(capacity);
}

public ArrayHashMap(int capacity, float loadFactor)
{
    super(capacity, loadFactor);
    keyList = new ArrayList<K>(capacity);
}

// TODO add other constructors


public V getAt(int location)
{ return get( keyList.get(location) ); }

// adds at the end of the array
@Override
public V put(K key, V value)
{
    V oldValue = super.put(key, value);
    if(oldValue == null) {
        keyList.add(key);
    }
    return oldValue;
}

@Override
public void putAll(Map<? extends K, ? extends V> map)
{
    Collection<? extends K> keys = null;
    if( (map instanceof ArrayHashMap<?, ?>) ) {
        keys = ((ArrayHashMap<? extends K, ? extends V>) map).keyList();
    } else {
        keys = map.keySet();
    }
    
    // add non-existant entries at the end
    for(K key : keys) {
        if( ! containsKey(key) ) {
            put(key, map.get(key));
        }
    }
}


@Override
public Set< Map.Entry<K, V> > entrySet()
{
    LinkedHashSet< Map.Entry<K,V> > ret = new LinkedHashSet< Map.Entry<K,V>>( size() );
    for(K k : keyList) {
        ret.add( new Entry(k) );
    }
    return ret;
}

@Override
public Set<K> keySet()
{ return new LinkedHashSet<K>(keyList); }

public List<K> keyList()
{ return ImmutableList.copyOf(keyList); }

@Override
public Collection<V> values()
{ return valuesList(); }

public List<V> valuesList()
{
    ArrayList<V> values = new ArrayList<V>( size() );
    for(K k : keyList) {
        values.add( get(k) );
    }
    return values;
}

// false if already existing, does nothing
public boolean append(K key, V value)
{
    if( ! containsKey(key) ) {
        keyList.add(key);
        super.put(key, value);
        return true;
    } else {
        return false;
    }
}

@Override
public void clear()
{
    super.clear();
    keyList.clear();
}

private void constructorPutAll(Map<K,V> origin)
{
    super.putAll(origin);
    if(origin instanceof ArrayHashMap) {
        ArrayHashMap<K,V> a = (ArrayHashMap<K, V>)origin;
        this.keyList.addAll(a.keyList);
    } else {
        this.keyList.addAll(origin.keySet()); // keeps order if linkedhashmap
    }
}

@SuppressWarnings("unchecked")
@Override
public Object clone()
{
    ArrayHashMap<K, V> copy;
    try {
        copy = this.getClass().newInstance();
        copy.constructorPutAll(this);
        return copy;
    } catch (InstantiationException e) {
        // won't happen
    } catch (IllegalAccessException e) {
        // won't happen
    }
    return null;
}

@Override
public V remove(Object key)
{
    V removed = super.remove(key);
    if( removed != null) {
        keyList.remove(key);
    }
    return removed;
}

// works only if keyForValue() implemented, otherwise throws exception

public V removeAt(int location)
{
    K key = keyList.remove(location);
    V value = super.remove(key);
    return value;
}

@Override
public Iterator<V> iterator()
{ return valueIterator(); }

public Iterator<K> keyIterator()
{ return keyList.iterator(); }

public Iterator<V> valueIterator()
{ return new ValueIterator(); }

private final class ValueIterator
implements Iterator<V>
{
    int index = -1;

    @Override
    public boolean hasNext()
    { return (index < size()-1); }

    @Override
    public V next()
    {
        ++index;
        return getAt(index);
    }

    @Override
    public void remove()
    { removeAt(index); }
}


private final class Entry
implements Map.Entry<K, V>
{
public final K key;

public Entry(K key)
{ this.key = key; }

public K getKey()
{ return key; }

public V getValue()
{ return get(key); }

public V setValue(V v)
{ return put(key, v); }
}


}
