package ru.eadm.nobird.fragment.adapter.listener;

import android.view.View;
import ru.eadm.nobird.fragment.ImagePreview;

public class ImageClickListener implements View.OnClickListener {
    private final String url;
    public ImageClickListener(final String url) {
        this.url = url;
    }

    @Override
    public void onClick(View v) {
        ImagePreview.openImagePreview(url);
    }
}
