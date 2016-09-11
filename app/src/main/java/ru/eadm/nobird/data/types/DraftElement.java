package ru.eadm.nobird.data.types;

/**
 * Created by ruslandavletshin on 09/09/16.
 */
public class DraftElement implements Element {
    private final long id;
    private final String text;

    public DraftElement(final long id, final String text) {
        this.id = id;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public long getID() {
        return id;
    }
}
