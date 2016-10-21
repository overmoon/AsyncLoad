package my.fun.asyncload.imageloader.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.LruCache;

import java.io.IOException;

import my.fun.asyncload.imageloader.disklrucache.DiskLruCache;
import my.fun.asyncload.imageloader.utils.DiskCacheUtils;
import my.fun.asyncload.imageloader.utils.MemCacheUtils;

/**
 * Created by admin on 2016/10/15.
 */
public class ImageLoaderConfiguration {

    // mem cache size - (%)
    private double memCacheInPercent;
    // mem cache size - (in MB)
    private int memCacheSize;
    // disk cache size - (in bytes)
    private long diskCacheSize;
    private String diskCacheFolder = "";

    private LruCache<String, Bitmap> memLruCache;
    private DiskLruCache diskLruCache;

    public ImageLoaderConfiguration(Builder builder) {
        if (builder.context == null && diskLruCache == null){
            throw new IllegalArgumentException("Context and diskLruCache are all none in ImageLoaderConfiguration.builder. Can't init diskLruCache..");
        }

        this.memCacheSize = builder.memCacheSize;
        this.memCacheInPercent = builder.memCacheInPercent;
        this.diskCacheSize = builder.diskCacheSize;
        this.diskCacheFolder = builder.diskCacheFolder;

        memLruCache = MemCacheUtils.getMemCache(memCacheSize);
        try {
            diskLruCache = DiskCacheUtils.getDiskLurCache(builder.context, diskCacheSize, diskCacheFolder);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LruCache<String, Bitmap> getMemLruCache() {
        return memLruCache;
    }

    public DiskLruCache getDiskLruCache() {
        return diskLruCache;
    }

    /**
     * ImageLoaderConfiguration Builder
     */
    public static class Builder {
        // mem cache size - (%)
        private double memCacheInPercent;
        // mem cache size - (in MB)
        private int memCacheSize;
        // disk cache size - (in bytes)
        private long diskCacheSize;
        // disk cache folder
        private String diskCacheFolder = "";
        @NonNull
        private Context context;

        private LruCache<String, Bitmap> memLruCache;
        private DiskLruCache diskLruCache;

        public Builder setMemLruCache(LruCache<String, Bitmap> memLruCache) {
            this.memLruCache = memLruCache;
            return this;
        }

        public Builder setMemCacheSize(int memCacheSize) {
            this.memCacheSize = memCacheSize;
            return this;
        }

        public Builder setMemCacheInPercent(float memCacheInPercent) {
            this.memCacheInPercent = memCacheInPercent;
            return this;
        }

        public Builder setDiskLruCache(DiskLruCache diskLruCache) {
            this.diskLruCache = diskLruCache;
            return this;
        }

        public Builder setDiskCacheSize(int diskCacheSize) {
            this.diskCacheSize = diskCacheSize;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setDiskCacheFolder(String diskCacheFolder) {
            this.diskCacheFolder = diskCacheFolder;
            return this;
        }

        public ImageLoaderConfiguration build() {
            if (context == null && diskLruCache == null){
                throw new IllegalArgumentException("Context and diskLruCache are all none in ImageLoaderConfiguration.builder. Can't init diskLruCache..");
            }
            if (memCacheInPercent == 0 && memCacheSize == 0) {
                memCacheInPercent = MemCacheUtils.DEFAULT_MEM_CACHE_SIZE_PERCENT;
                memCacheSize = MemCacheUtils.DEFAULT_MEM_CACHE_SIZE;
            } else if (memCacheInPercent == 0) {
                memCacheInPercent = MemCacheUtils.calMemCachePercent(memCacheSize);
            } else if (memCacheSize == 0) {
                memCacheSize = MemCacheUtils.calMemCacheSize(memCacheInPercent);
            }

            if (diskCacheSize == 0) {
                diskCacheSize = DiskCacheUtils.DEFAULT_DISK_CACHE_SIZE;
            }
            return new ImageLoaderConfiguration(this);
        }
    }
}
