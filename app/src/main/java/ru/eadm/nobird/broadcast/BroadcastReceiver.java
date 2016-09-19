package ru.eadm.nobird.broadcast;

import ru.eadm.nobird.data.types.Element;

/**
 * Represents broadcastable
 */
public interface BroadcastReceiver<E extends Element> {
//    E get(final long id);
    void remove(final long id);
//    void add(final long id);
}
