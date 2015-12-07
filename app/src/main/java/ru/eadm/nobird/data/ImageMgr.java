package ru.eadm.nobird.data;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import ru.eadm.nobird.R;

public final class ImageMgr {
    private final static String CACHE_DIR = "Android/data/ru.eadm.nobird/cache";

    private final Context context;
    private static ImageMgr instance;

    public final ImageLoader imageLoader;
    public final PauseOnScrollListener listener;
    public final DisplayImageOptions options;


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
                .showStubImage(R.drawable.bg_white_round)
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

    public void displayImage(final String url, final ImageView i){
        imageLoader.displayImage(url, i, options);
    }
}
