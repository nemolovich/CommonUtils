package fr.nemolovich.apps.concurrentmultimap;

import java.util.Map.Entry;

/**
 * Immutable {@link Entry}.
 *
 * @author Nemolovich
 * @param <K> Key Class
 * @param <V> Value Class
 */
public class ImmutableMultiMapEntry<K, V> extends MultiMapEntry<K, V> {

    private static final long serialVersionUID = 3910883128162245898L;

    /**
     * Default constructor.
     *
     * @param key
     * @param value
     */
    public ImmutableMultiMapEntry(K key, V value) {
        super(key, value);
    }

    /**
     * Copy constructor.
     *
     * @param entry
     */
    public ImmutableMultiMapEntry(Entry<? extends K, ? extends V> entry) {
        super(entry);
    }

    /**
     * Can not be used for immutable entry.
     *
     * @param value {@link V}: The value to set.
     * @return
     * @throws UnsupportedOperationException always
     */
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

}
