package fr.nemolovich.apps.concurrentmultimap;

import java.io.Serializable;
import java.util.Map.Entry;

/**
 *
 * @author Nemolovich
 */
public class ImmutableMultiMapEntry<K, V> implements Entry<K, V>,
    Serializable {

    private static final long serialVersionUID = 3910883128162245898L;

    private final K key;
    private final V value;

    public ImmutableMultiMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public ImmutableMultiMapEntry(
        Entry<? extends K, ? extends V> entry) {
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

    /**
     * @throws UnsupportedOperationException always
     */
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

}
