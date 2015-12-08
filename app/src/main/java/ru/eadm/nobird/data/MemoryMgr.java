package ru.eadm.nobird.data;

        import android.content.Context;
        import android.graphics.Typeface;

        import java.util.ArrayList;

public final class MemoryMgr {
    private final Context context;
    private static MemoryMgr instance;

    private final ArrayList<Object> data;


    private MemoryMgr(final Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    public synchronized static void init(final Context context) {
        if (instance == null) {
            instance = new MemoryMgr(context);
        }
    }

    public synchronized static MemoryMgr getInstance() {
        return instance;
    }

    public synchronized int add(final Object o) {
        data.add(o);
        return data.size() - 1;
    }

    public synchronized Object get(final int i) {
        return data.get(i);
    }

    public Object pop(final int i) {
        final Object o = data.get(i);
        data.remove(i);
        return o;
    }
}
