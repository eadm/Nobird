package ru.nobird.android.design.span;

import android.text.style.ClickableSpan;

/**
 * Created by ruslandavletshin on 26/12/15.
 */
public abstract class AbsSpan extends ClickableSpan {
    public abstract String getData();
}
