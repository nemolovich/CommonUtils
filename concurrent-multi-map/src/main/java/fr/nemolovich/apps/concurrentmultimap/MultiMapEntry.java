package fr.nemolovich.apps.concurrentmultimap;

import java.io.Serializable;
import java.util.Map.Entry;

/**
 *
 * @author Nemolovich
 */
public class MultiMapEntry<K, V> implements Entry<K, V>,
    Serializable {

    private static final long serialVersionUID
        = 1503845102686034755L;

    private final K key;
    private V value;

    public MultiMapEntry(K key) {
        this.key = key;
    }

    public MultiMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public MultiMapEntry(Entry<? extends K, ? extends V> entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V value) {
        V v = this.value;
        this.value = value;
        return v;
    }

}
