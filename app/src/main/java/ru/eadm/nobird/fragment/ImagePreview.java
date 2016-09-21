package ru.eadm.nobird.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.fragment.implementation.FragmentMgr;

public class ImagePreview extends Fragment {
//    private final static String TAG = "ImagePreview";
    private ImageView image;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View page = inflater.inflate(R.layout.fragment_image_preview, container, false);

        image = (ImageView) page.findViewById(R.id.fragment_image_preview);

        ImageMgr.getInstance().displayDarkImage(
                getArguments().getString("url"),
                image
        );

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new ImageGestureListener());
        page.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    image.setTranslationY(0);
                    image.setAlpha(1.0f);
                }
                return true;
            }
        });

        return page;
    }

    public static Fragment createImagePreview(final String url) {
        final Fragment fragment = new ImagePreview();
        final Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void openImagePreview(final String url) {
        FragmentMgr.getInstance().replaceFragment(0, createImagePreview(url), true);
    }

    private class ImageGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            image.setTranslationY(e2.getY() - e1.getY());
            image.setAlpha(1.0f - Math.abs(e2.getY() - e1.getY()) / 200);

            if (Math.abs(e1.getY() - e2.getY()) > 200) FragmentMgr.getInstance().back();

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}
