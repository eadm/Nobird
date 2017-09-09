package ru.nobird.android.data.types;

import twitter4j.SavedSearch;

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

    public StringElement(final SavedSearch search) {
        this.id = search.getId();
        this.text = search.getQuery();
    }

    public String getText() {
        return text;
    }

    @Override
    public long getID() {
        return id;
    }
}
