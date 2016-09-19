package ru.eadm.nobird.data.types;

/**
 * Drafts elements
 */
public class StringElement implements Element {
    private final long id;
    private final String text;

    public StringElement(final long id, final String text) {
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
