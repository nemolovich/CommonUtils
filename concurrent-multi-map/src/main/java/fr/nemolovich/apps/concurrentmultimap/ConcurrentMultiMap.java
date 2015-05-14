package fr.nemolovich.apps.concurrentmultimap;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * ConcurrentMultiMap is an implementation of ConcurrentMap for MultiMap (Map
 * which allows multiple keys).
 *
 * @author Nemolovich
 * @param <K> {@link Object ? extends K}: The type of keys maintained by this
 * map.
 * @param <V> {@link Object ? extends V}: The type of mapped values.
 */
public class ConcurrentMultiMap<K, V> implements
    ConcurrentMap<K, V>, Serializable {

    private static final long serialVersionUID
        = 1952840811318580407L;

    private final ConcurrentHashMap<Integer, K> keys;
    private final ConcurrentHashMap<Integer, V> values;

    /**
     * Default constructor.
     */
    public ConcurrentMultiMap() {
        this.keys = new ConcurrentHashMap<>();
        this.values = new ConcurrentHashMap<>();
    }

    /**
     * Returns the next usable index.
     *
     * @return {@link Integer} - The new index.
     */
    private Integer getNextIndex() {
        Integer result = Integer.MIN_VALUE;
        for (Entry<Integer, K> entry
            : this.keys.entrySet()) {
            if (entry.getKey().compareTo(result) >= 0) {
                result = entry.getKey() + 1;
            }
        }
        return result;
    }

    /**
     * Returns the index of given key. Returns <code>null</code> if the given
     * key does not exist in the keys list.
     *
     * @param key {@link Object}: The key to look for.
     * @return {@link Integer} - The index if key exists, <code>null</code> if
     * it does not.
     */
    private Integer getKeyIndex(Object key) {
        Integer result = null;
        for (Entry<Integer, K> entry
            : this.keys.entrySet()) {
            if (entry.getValue().equals(key)) {
                result = entry.getKey();
                break;
            }
        }
        return result;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V result;
        synchronized (this.values) {
            result = this.get(key);
            if (result == null) {
                Integer newIndex = getNextIndex();
                this.keys.put(newIndex, key);
                this.values.put(newIndex, value);
            }
        }
        return result;
    }

    @Override
    public boolean remove(Object key, Object value) {
        boolean result = false;
        synchronized (this.values) {
            if (this.containsKey((K) key)
                && this.get((K) key).equals((V) value)
                && this.remove((K) key).equals(value)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {

        boolean result = this.containsKey((K) key)
            && this.get((K) key).equals(oldValue);
        if (result) {
            result = oldValue != null && oldValue.equals(
                this.replace((K) key, newValue));
        }
        return result;
    }

    @Override
    public V replace(K key, V value) {
        V result;
        synchronized (this.values) {
            result = this.get(key);
            if (result != null) {
                Integer newIndex = getKeyIndex(key);
                this.keys.put(newIndex, key);
                this.values.put(newIndex, value);
            }
        }
        return result;
    }

    @Override
    public int size() {
        return this.keys.size();
    }

    @Override
    public boolean isEmpty() {
        return this.keys.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        boolean result = false;
        for (Entry<Integer, K> entry
            : this.keys.entrySet()) {
            if (entry.getValue().equals(key)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean result = false;
        for (Entry<Integer, V> entry
            : this.values.entrySet()) {
            if (entry.getValue().equals(value)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public V get(Object key) {
        V result = null;
        Integer idx = getKeyIndex(key);
        if (idx != null) {
            result = this.values.get(idx);
        }
        return result;
    }

    @Override
    public V put(K key, V value) {
        V result;
        synchronized (this.values) {
            result = this.get(key);
            Integer newIndex = getNextIndex();
            this.keys.put(newIndex, key);
            this.values.put(newIndex, value);
        }
        return result;
    }

    @Override
    public V remove(Object key) {

        V result = null;
        synchronized (this.values) {
            Integer idx = getKeyIndex(key);
            if (idx != null) {
                result = this.values.get(idx);
                if (!this.keys.remove(idx).equals(key)
                    || result != null && !this.values.remove(idx).equals(
                        result)) {
                    result = null;
                }
            }
        }
        return result;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry
            : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.keys.clear();
        this.values.clear();
    }

    @Override
    public Set<K> keySet() {
        return new LinkedHashSet<>(this.keys.values());
    }

    @Override
    public Collection<V> values() {
        return new ConcurrentLinkedQueue<>(
            this.values.values());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> result = new LinkedHashSet<>();

        Entry entry;
        K key;
        V value;
        Integer idx = Integer.MIN_VALUE;
        Integer nbEntry = 0;
        while (nbEntry < this.size() && idx < Integer.MAX_VALUE) {
            if (this.keys.containsKey(idx)) {
                key = this.keys.get(idx);
                value = this.values.get(idx);
                entry = new ImmutableMultiMapEntry<>(
                    key, value);
                result.add(entry);
                nbEntry++;
            }
            idx++;
        }
        return result;
    }

}
