package ru.nobird.android.data;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import ru.nobird.android.R;

public final class ImageMgr {
    private final static String CACHE_DIR = "Android/data/ru.eadm.nobird/cache";

    private final Context context;
    private static ImageMgr instance;

    public final ImageLoader imageLoader;
    public final PauseOnScrollListener listener;
    public final DisplayImageOptions options, options_round, options_dark;

    private ImageMgr(final Context context) {
        this.context = context;

        imageLoader = ImageLoader.getInstance();
        final File cacheDir = StorageUtils.getOwnCacheDirectory(context, CACHE_DIR);
        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 2 Mb
                .discCache(new UnlimitedDiskCache(cacheDir))
                .imageDownloader(new BaseImageDownloader(context))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();
        imageLoader.init(config);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showStubImage(R.drawable.image_placeholder_light)
                .build();

        options_dark = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showStubImage(R.drawable.image_placeholder_dark)
                .build();

        options_round = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showStubImage(R.drawable.image_placeholder_light)
                .displayer(new RoundedBitmapDisplayer(999999)) // ~ infinite to get round
                .build();

        listener = new PauseOnScrollListener(imageLoader, true, true);

    }

    public synchronized static void init(final Context context) {
        if (instance == null) {
            instance = new ImageMgr(context);
        }
    }

    public synchronized static ImageMgr getInstance() {
        return instance;
    }

    @BindingAdapter({"imageUrl", "roundImage"})
    public static void displayImage(final ImageView view, final String src, final boolean rounded) {
        if (rounded) {
            ImageMgr.getInstance().displayRoundImage(src, view);
        } else {
            ImageMgr.getInstance().displayImage(src, view);
        }
    }

    public void displayImage(final String url, final ImageView i){
        imageLoader.displayImage(url, i, options);
    }

    public void displayRoundImage(final String url, final ImageView i){
        imageLoader.displayImage(url, i, options_round);
    }

    public void displayDarkImage(final String url, final ImageView i){
        imageLoader.displayImage(url, i, options_dark);
    }

    public void loadImage(final String url, final ImageLoadingListener listener) {
        imageLoader.loadImage(url, listener);
    }

    public void loadBackgroundImage(final String url, final View target) {
        final WeakReference<View> targetReference = new WeakReference<>(target);
        imageLoader.loadImage(url, new ImageSize(target.getWidth(), target.getHeight()), options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(final String imageUri, final View view, final Bitmap loadedImage) {
                if (targetReference.get() != null) {
                    targetReference.get().setBackground(new BitmapDrawable(context.getResources(), loadedImage));
                }
            }
        });
    }
}
