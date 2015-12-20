package ru.eadm.nobird.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;

/**
 * Created by ruslandavletshin on 15/12/15.
 */
public class ImagePreview extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View page = inflater.inflate(R.layout.fragment_image_preview, container, false);

        ImageMgr.getInstance().displayDarkImage(
                getArguments().getString("url"),
                ((ImageView) page.findViewById(R.id.fragment_image_preview))
        );

        return page;
    }

    public static void openImagePreview(final String url) {
        final Fragment fragment = new ImagePreview();
        final Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }
}
