package my.fun.asyncload.imageloader.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.LruCache;

import java.io.IOException;

import my.fun.asyncload.imageloader.core.DisplayOption;
import my.fun.asyncload.imageloader.model.ImageSize;

/**
 * Created by admin on 2016/10/10.
 */
public class MemCacheUtils {
    // default mem cache size - (1/8 max memory)
    public final static double DEFAULT_MEM_CACHE_SIZE_PERCENT = 1.0/8.0;
    public final static int DEFAULT_MEM_CACHE_SIZE = MemCacheUtils.calMemCacheSize(DEFAULT_MEM_CACHE_SIZE_PERCENT);

    // in MB
    public static long getMaxMem(){
        return Runtime.getRuntime().maxMemory()/1024;
    }

    public static double calMemCachePercent(long cacheSize){
        return cacheSize/getMaxMem();
    }

    public static int calMemCacheSize(double percent){
        return (int) (percent * getMaxMem());
    }

    // default mem cache size is 0.2 percent of MaxMem
    public static LruCache getMemCache(int cacheSize) {
        LruCache lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()/1024;
            }
        };
        return lruCache;
    }

    // set mem cache size by percent of MaxMem
    public static LruCache getMemCacheByPercent(float percent) {
        if (percent < 0 || percent > 1) {
            throw new IllegalArgumentException("percentage must be 0~1");
        }
        final int maxMem = (int) getMaxMem();
        LruCache lruCache = new LruCache<String, Bitmap>((int) (maxMem * percent)) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()/1024;
            }
        };
        return lruCache;
    }

    // set mem cache size by num of images
    public static LruCache getMemCacheByImageNum(int num, DisplayOption displayOption) throws IOException {
        int memNeed = 0;
        final int maxMem = (int) getMaxMem();
        ImageSize imageSize= displayOption.getImageSize();
        int resId = displayOption.getResId();
        Resources res = displayOption.getResources();
        // if not set imageSize, then will use the drawable size
        if (imageSize == null) {
            if (resId != 0) {
                Bitmap bitmap = BitmapUtils.decodeBitmapFromDrawable(res, resId);
                memNeed = bitmap.getByteCount() * num ;
                if (memNeed >= maxMem) {
                    throw new IllegalArgumentException("memory needed is larger than maxmem. Argument num is too large");
                }
            } else {
                memNeed = DEFAULT_MEM_CACHE_SIZE;
            }
        } else {        // else will calculate the size needed by num
            float scale = DensityUtils.getScale(res);
            int memNeeded = (int) (num * imageSize.getSize() * scale * scale);
            if (memNeeded >= maxMem) {
                throw new IllegalArgumentException("memory needed is larger than maxmem. Argument num is too large");
            }
        }

        return getMemCache(memNeed);
    }
}
