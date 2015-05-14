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
     * @param key {@link Object ? extends K}: The key.
     * @param value {@link Object ? extends V}: The value.
     */
    public ImmutableMultiMapEntry(K key, V value) {
        super(key, value);
    }

    /**
     * Copy constructor.
     *
     * @param entry {@link Entry}&lt;{@link Object ? extends K},
     * {@link Object ? extends V}&gt;: The entry to copy.
     */
    public ImmutableMultiMapEntry(Entry<? extends K, ? extends V> entry) {
        super(entry);
    }

    /**
     * Can not be used for immutable entry.
     *
     * @param value {@link Object ? extends V}: The value to set.
     * @return {@link Object ? extends V} - The value.
     * @throws UnsupportedOperationException always
     */
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

}
