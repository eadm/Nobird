package ru.eadm.nobird.fragment.listener;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.eadm.nobird.fragment.ImageGallery;

/**
 * Image click listener for several images
 */
public class ImageOnClickListener implements View.OnClickListener{

    private final List<String> images;
    private final int pos;

    public ImageOnClickListener(final List<String> images, final int pos) {
        this.images = images;
        this.pos = pos;
    }

    @Override
    public void onClick(final View v) {
        ImageGallery.show(new ArrayList<>(images), pos);
    }
}
