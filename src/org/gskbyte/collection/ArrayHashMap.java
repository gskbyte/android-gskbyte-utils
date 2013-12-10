package org.gskbyte.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
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

public ArrayHashMap(Map<K,V> map)
{
    super(map.size());
    keyList = new ArrayList<K>(map.size());
    constructorPutAll(map);
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

// to provide faster support tu subclasses
protected List<K> unmodifiableKeyList()
{ return Collections.unmodifiableList(keyList); }

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
public boolean add(K key, V value)
{
    if( ! containsKey(key) ) {
        keyList.add(key);
        super.put(key, value);
        return true;
    } else {
        return false;
    }
}

// true if added OR changed, false if element found and stays in same position
public boolean add(int index, K key, V value)
{
    if( containsKey(key) ) {
        int existingIndex = keyList.indexOf(key);
        if(existingIndex != index) {
            keyList.set(existingIndex, keyList.set(index, keyList.get(existingIndex)));
            return true;
        } else {
            return false;
        }
    } else {
        keyList.add(index, key);
        super.put(key, value);
        return true;
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
public boolean equals(Object object)
{
    if(super.equals(object)) {
        if(object instanceof ArrayHashMap) {
            ArrayHashMap<?, ?> a = (ArrayHashMap<?, ?>)object;
            return keyList.equals( a.keyList );
        }
    }
    
    return false;
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

public V removeAt(int location)
{
    K key = keyList.remove(location);
    V value = super.remove(key);
    return value;
}


// commented out because it should return a VIEW of this map, not a copy!
/*
public ArrayHashMap<K, V> subArrayHashMap(int fromIndex, int toIndex)
{
    ArrayHashMap<K, V> ret = new ArrayHashMap<K, V>(toIndex-fromIndex);
    for(int i=fromIndex; i<toIndex; ++i) {
        K key = keyList.get(i);
        ret.put(key, get(key)); // some kind of putNoCheck() method would accelerate this
    }
    return ret;
}*/

@Override
public Iterator<V> iterator()
{ return valueIterator(); }

public ListIterator<V> listIterator()
{ return valueIterator(); }

public ListIterator<V> listIterator(int index)
{ return valueIterator(index); }

public ListIterator<K> keyIterator()
{ return keyList.listIterator(); }

public ListIterator<K> keyIterator(int index)
{ return keyList.listIterator(index); }

public ListIterator<V> valueIterator()
{ return new ValueIterator(); }

public ListIterator<V> valueIterator(int index)
{ return new ValueIterator(index); }

private final class ValueIterator
implements ListIterator<V>
{
    protected int index = -1;

    protected ValueIterator()
    { this.index = -1; }
    
    protected ValueIterator(int startIndex)
    { this.index = startIndex-1;}
    
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

    @Override
    public void add(V object)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean hasPrevious()
    { return index>0;}

    @Override
    public int nextIndex()
    { return index+1;}

    @Override
    public V previous()
    {
        --index;
        return getAt(index);
    }

    @Override
    public int previousIndex()
    { return index-1; }

    @Override
    public void set(V object)
    { put(keyList.get(index), object); }
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
