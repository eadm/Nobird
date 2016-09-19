package ru.eadm.nobird.broadcast;

import java.util.HashMap;

import ru.eadm.nobird.data.types.Element;

/**
 * Manager to broadcast data and messages between fragments
 */
public class BroadcastMgr {
    private static BroadcastMgr instance;
    public synchronized static void init() {
        if (instance == null) {
            instance = new BroadcastMgr();
        }
    }
    public synchronized static BroadcastMgr getInstance() {
        return instance;
    }

//    private final static String TAG = "BroadcastMgr";

    public enum TYPE {
        TWEET_ELEMENT,
        SAVED_SEARCH_ELEMENT
    }

    private final HashMap<TYPE, HashMap<String, BroadcastReceiver<Element>>> receivers;

    public BroadcastMgr() {
        receivers = new HashMap<>();
        for (final TYPE type : TYPE.values()) {
            receivers.put(type, new HashMap<String, BroadcastReceiver<Element>>());
        }
    }

    public void register(final TYPE type, final String name, final BroadcastReceiver<Element> receiver) {
        receivers.get(type).put(name, receiver);
    }

    public void unregister(final TYPE type, final String name) {
        receivers.get(type).remove(name);
    }

    public void remove(final long id, final TYPE type, final String name) {
        receivers.get(type).get(name).notifyItemRemoved(id);
    }

    public void remove(final long id, final TYPE type) {
        for (final BroadcastReceiver<Element> receiver : receivers.get(type).values()) {
            receiver.notifyItemRemoved(id);
        }
    }

//    public void dump() {
//        for (final HashMap<String, BroadcastReceiver<Element>> broadcastReceiverHashMap : receivers.values()) {
//            for (final String name : broadcastReceiverHashMap.keySet()) {
//                Log.d(TAG, name);
//            }
//        }
//    }
}
