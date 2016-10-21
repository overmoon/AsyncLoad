package my.fun.asyncload.imageloader.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import my.fun.asyncload.imageloader.disklrucache.DiskLruCache;

/**
 * Created by admin on 2016/10/13.
 */
public class DiskCacheUtils {
    // default disk cache size - (50M in bytes)
    public final static long DEFAULT_DISK_CACHE_SIZE = 50 * 1024 * 1024;
    // default max size of disk
//    public final static long DEFAULT_MAX_DISK_CACHE_SIZE ;

    public static File getDiskCacheDir(Context context, String dirName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        if (dirName != null)
            cachePath = cachePath + File.separator + dirName;
        return new File(cachePath);
    }

    public static int getAppVersion(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionCode;
    }

    public static DiskLruCache getDiskLurCache(Context context, long diskCacheSize, String diskCacheFolder) throws PackageManager.NameNotFoundException, IOException {
        return DiskLruCache.open(getDiskCacheDir(context, diskCacheFolder), getAppVersion(context), 1, diskCacheSize);
    }
}
