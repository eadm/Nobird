package ru.nobird.android.broadcast;

import ru.nobird.android.data.types.Element;

/**
 * Represents broadcastable
 */
public interface BroadcastReceiver<E extends Element> {
//    E get(final long id);
    void notifyItemRemoved(final long id);
    boolean exists(final long id);
}
