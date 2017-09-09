package ru.nobird.android.fragment;

import android.databinding.BindingAdapter;
import android.text.method.MovementMethod;
import android.widget.TextView;

/**
 * Created by ruslandavletshin on 24/08/16.
 */
public class BindAdapters {

    @BindingAdapter({"movementMethod"})
    public static void setMovementMethod(final TextView view, final MovementMethod movementMethod) {
        view.setMovementMethod(movementMethod);
    }

}
