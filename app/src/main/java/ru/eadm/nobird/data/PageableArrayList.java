package ru.eadm.nobird.data;

import java.util.ArrayList;

import twitter4j.CursorSupport;

public class PageableArrayList<E> extends ArrayList<E> implements CursorSupport {
    private boolean hasNext, hasPrevious;
    private long previousCursor, nextCursor;

    public PageableArrayList() {
        super();
        setCursors(false, false, -1, -1);
    }

    public PageableArrayList(final int size) {
        super(size);
        setCursors(false, false, -1, -1);
    }

    public void setCursors(final boolean hasNext, final boolean hasPrevious, final long nextCursor, final long previousCursor) {
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;

        this.nextCursor = nextCursor;
        this.previousCursor = previousCursor;
    }

    public void setCursors(final CursorSupport cursorSupport) {
        hasNext = cursorSupport.hasNext();
        hasPrevious = cursorSupport.hasPrevious();

        nextCursor = cursorSupport.getNextCursor();
        previousCursor = cursorSupport.getPreviousCursor();
    }

    @Override
    public boolean hasPrevious() {
        return hasPrevious;
    }

    @Override
    public long getPreviousCursor() {
        return previousCursor;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public long getNextCursor() {
        return nextCursor;
    }
}
