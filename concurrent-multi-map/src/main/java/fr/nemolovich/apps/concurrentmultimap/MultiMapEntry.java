package fr.nemolovich.apps.concurrentmultimap;

import java.io.Serializable;
import java.util.Map.Entry;

/**
 * A basic {@link Entry}.
 *
 * @author Nemolovich
 * @param <K> Key Class
 * @param <V> Value Class
 */
public class MultiMapEntry<K, V> implements Entry<K, V>,
    Serializable {

    private static final long serialVersionUID
        = 1503845102686034755L;

    private final K key;
    private V value;

    /**
     * Constructor without value. Need to use
     * {@link #setValue(java.lang.Object)} to set it.
     *
     * @param key {@link Object ? extends K} - The key to use.
     */
    public MultiMapEntry(K key) {
        this.key = key;
    }

    /**
     * Default constructor.
     *
     * @param key {@link Object ? extends K} - The key to use.
     * @param value {@link Object ? extends V} - The value to associate with
     * key.
     */
    public MultiMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Copy constructor.
     *
     * @param entry {@link Entry}&lt;{@link Object ? extends K},
     * {@link Object ? extends V}&gt;: The entry to copy.
     */
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
