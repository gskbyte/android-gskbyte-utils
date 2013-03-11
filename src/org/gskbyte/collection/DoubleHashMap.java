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
package org.gskbyte.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Double hash map.
 * Allow to access values given a key or a value.
 * 
 * Both maps are implemented using HashMaps.
 * */
public class DoubleHashMap<K, E>
implements Map<K, E>, Cloneable
{
private final Map<K, E> map;
private final Map<E, K> reverseMap;

/**
 * Constructor
 * */
public DoubleHashMap()
{
    map = new HashMap<K, E>();
    reverseMap = new HashMap<E, K>();
}

/**
 * Parametrized constructor.
 * @param initialCapacity Defines the initial capacity of the map
 * */
public DoubleHashMap(int initialCapacity)
{
    map = new HashMap<K, E>(initialCapacity);
    reverseMap = new HashMap<E, K>(initialCapacity);
}

/**
 * Removes all mappings from this map.
 * */
@Override
public void clear()
{
    map.clear();
    reverseMap.clear();
}

/**
 * Returns true if this map contains a mapping for the specified key.
 * @param the key to search.
 * @return true If the map contained the given key.
 * */
@Override
public boolean containsKey(Object key)
{
    return map.containsKey(key);
}

/**
 * Returns true if this map maps one or more keys to the specified value.
 * @param value The value to search
 * @return true If the map contained the given value.
 * */
@Override
public boolean containsValue(Object value)
{
    return reverseMap.containsKey(value);
}

/**
 * Returns a shallow copy of this DoubleHashMap instance: the keys and values themselves are not cloned. 
 * @return A shallow copy of this map.
 * */
@Override
public Object clone()
{
    DoubleHashMap<K, E> ret = new DoubleHashMap<K, E>();
    
    for(K key : map.keySet()) {
        E value = map.get(key);
        ret.put(key, value);
    }
    
    return ret;
}

/**
 * Returns the entries contained in this map.
 * @return The set of entries in this map.
 * */
@Override
public Set<Map.Entry<K, E>> entrySet()
{
    return map.entrySet();
}

/**
 * Returns the value for the given key.
 * @param key The key to search.
 * @return The existing mapping for key, if any.
 * */
@Override
public E get(Object key)
{
    return map.get(key);
}

/**
 * Returns the key for the given value (it's a reverse search)
 * @param value The value to search for.
 * @return The key for the given value, if any.
 * */
public K getKeyForValue(E value)
{
    return reverseMap.get(value);
}

/**
 * Checks if the map is empty.
 * @return true if the map is empty.
 * */
@Override
public boolean isEmpty()
{
    return map.size() > 0;
}

/**
 * Returns a set view of the keys contained in this map. The set is backed by 
 * the map, so changes to the map are reflected in the set, and vice-versa. 
 * The set supports element removal, which removes the corresponding mapping 
 * from this map, via the Iterator.remove, Set.remove, removeAll, retainAll, 
 * and clear operations. It does not support the add or addAll operations.
 * @return a set view of the keys contained in this map.
 * */
@Override
public Set<K> keySet()
{
    return map.keySet();
}

/**
 * Associates the specified value with the specified key in this map. If the 
 * map previously contained a mapping for this key, the old value is replaced. 
 * @param key key with which the specified value is to be associated. Can not be null
 * @param value value to be associated with the specified key.
 * @return previous value associated with specified key, or null if there was no mapping for key. A null return can also indicate that the HashMap previously associated null with the specified key.
 * */
@Override
public E put(K key, E value)
{
    E oldValue = map.get(key);
    
    map.put(key, value);
    reverseMap.put(value, key);
    
    return oldValue;
}

/**
 * Copies all of the mappings from the specified map to this map These mappings
 * will replace any mappings that this map had for any of the keys currently in
 * the specified map. 
 * @param m mappings to be stored in this map. 
 * @throws NullPointerException if the specified map is null.
 * */
@Override
public void putAll(Map<? extends K, ? extends E> m)
{
    if(m == null)
        throw new NullPointerException();
    
    for(K key : m.keySet()) {
        put(key, m.get(key));
    }
}

/**
 * Removes the mapping for this key from this map if present, and also the opposite.
 * @param key key whose mapping is to be removed from the map.
 * @return previous value associated with specified key, or null if there was no mapping for key. A null return can also indicate that the map previously associated null with the specified key.
 * */
@Override
public E remove(Object key)
{
    E oldValue = map.get(key);
    map.remove(key);
    reverseMap.remove(oldValue);
    return oldValue;
}

/**
 * Removes a mapping given a value.
 * @param value The value to remove. The key that references it will be removed as well.
 * @return The previous key associated with the value, null if none.
 * */
public K removeValue(E value)
{
    K key = reverseMap.remove(value);
    if(key != null) {
        map.remove(key);
        return key;
    } else {
        return null;
    }
}

/**
 * Returns the number of key-value mappings in this map.
 * @return the number of key-value mappings in this map.
 * */
@Override
public int size()
{
    return map.size();
}

/**
 * Returns a collection view of the values contained in this map. The collection
 * is backed by the map, so changes to the map are reflected in the collection,
 * and vice-versa. The collection supports element removal, which removes the
 * corresponding mapping from this map, via the Iterator.remove, Collection.remove,
 * removeAll, retainAll, and clear operations. It does not support the add or
 * addAll operations.
 * @return a collection view of the values contained in this map.
 * */
@Override
public Collection<E> values()
{
    return reverseMap.keySet();
}
}
