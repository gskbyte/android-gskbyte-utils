package org.gskbyte.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import android.util.SparseArray;

public class DoubleSparseArray<E> implements Map<Integer, E>
{
    private final SparseArray<E> map;
    private final TreeMap<E, Integer> reverseMap;
    
    public DoubleSparseArray()
    {
        map = new SparseArray<E>();
        reverseMap = new TreeMap<E, Integer>();
    }
    
    public DoubleSparseArray(int initialCapacity)
    {
        map = new SparseArray<E>(initialCapacity);
        reverseMap = new TreeMap<E, Integer>();
    }
    
    @Override
    public void clear()
    {
        map.clear();
        reverseMap.clear();
    }

    @Override
    public boolean containsKey(Object key)
    {
        if(key instanceof Integer) {
            int intKey = (Integer)key;
            return map.get(intKey) != null;
        } else {
            throw new IllegalArgumentException("Key must be an integer");
        }
    }

    @Override
    public boolean containsValue(Object value)
    {
        return reverseMap.containsKey(value);
    }
    
    private final static class IntEntry<E> implements Map.Entry<Integer, E>
    {
        private final int key;
        private E value;
        
        public IntEntry(int key, E value)
        {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public Integer getKey()
        { return key; }

        @Override
        public E getValue()
        { return value; }

        @Override
        public E setValue(E object)
        { return value; }
    }

    @Override
    public Set<Map.Entry<Integer, E>> entrySet()
    {
        final Set<Map.Entry<Integer, E>> set = new TreeSet<Map.Entry<Integer, E>>();
        for(int i=0; i<map.size(); ++i) {
            set.add( new IntEntry<E>(map.keyAt(i), map.valueAt(i)) );
        }
        
        return set;
    }

    @Override
    public E get(Object key)
    {
        if(key instanceof Integer) {
            int intKey = (Integer)key;
            return map.get(intKey);
        } else {
            throw new IllegalArgumentException("Key must be an integer");
        }
    }
    
    public int getKeyForValue(E value)
    {
        return reverseMap.get(value);
    }

    @Override
    public boolean isEmpty()
    {
        return map.size() > 0;
    }

    @Override
    public Set<Integer> keySet()
    {
        return new TreeSet<Integer>(reverseMap.values());
    }

    @Override
    public E put(Integer key, E value)
    {
        E oldValue = map.get(key);
        
        map.put(key, value);
        reverseMap.put(value, key);
        
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends E> p_map)
    {
        for(int i : p_map.keySet()) {
            put(i, p_map.get(i));
        }
    }

    @Override
    public E remove(Object key)
    {
        if(key instanceof Integer) {
            int intKey = (Integer)key;
            E oldValue = map.get(intKey);
            map.remove(intKey);
            reverseMap.remove(oldValue);
            return oldValue;
        } else {
            throw new IllegalArgumentException("Key must be an integer");
        }
    }
    
    public Integer removeValue(E value)
    {
        Integer key = reverseMap.remove(value);
        if(key != null) {
            map.remove(key);
            return key;
        } else {
            return null;
        }
    }
    

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public Collection<E> values()
    {
        return reverseMap.keySet();
    }
}
