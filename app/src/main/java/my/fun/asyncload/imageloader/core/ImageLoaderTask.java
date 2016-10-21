package my.fun.asyncload.imageloader.core;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import my.fun.asyncload.imageloader.disklrucache.DiskLruCache;
import my.fun.asyncload.imageloader.model.AsyncDrawable;
import my.fun.asyncload.imageloader.model.Scheme;
import my.fun.asyncload.imageloader.utils.BitmapUtils;


/**
 * Created by admin on 2016/10/8.
 */
public class ImageLoaderTask extends AsyncTask<Object, Void, Bitmap> {


    private WeakReference<LruCache<String, Bitmap>> memCacheWeakReference;
    private WeakReference<DiskLruCache> diskCacheWeakReference;
    private WeakReference<DisplayOption> displayOptionWeakReference;

    public ImageLoaderTask() {

    }

    public ImageLoaderTask(DisplayOption displayOption, ImageLoaderConfiguration imageLoaderConfiguration) {
        displayOptionWeakReference = new WeakReference<>(displayOption);
        memCacheWeakReference = new WeakReference<>(imageLoaderConfiguration.getMemLruCache());
        diskCacheWeakReference = new WeakReference<>(imageLoaderConfiguration.getDiskLruCache());
    }

    @Override
    protected void onPreExecute() {
        if (cancelPotentialWork(displayOptionWeakReference)) {
            setPlaceHolderImage(displayOptionWeakReference);
        } else {
            this.cancel(true);
        }
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Object[] params) {
        if (!isCancelled()) {
            DisplayOption displayOption = displayOptionWeakReference.get();
            if (displayOption == null)
                return null;

            String uri = displayOption.getData();
            String key = BitmapUtils.generateKeyByHash(uri, displayOptionWeakReference.get().getImageSize());
            Bitmap bitmap = findUriInCache(key);
            // if find in cache, then return
            if (bitmap != null)
                return bitmap;
            // not in cache, continue get bitmap
            InputStream inputStream = null;
            try {
                inputStream = getBitmapInputStreamFromUri(uri, null);
                bitmap = BitmapUtils.decodeSampleBitmapFromStream(inputStream, displayOption);
                if (displayOption.isCacheInMem())
                    addBitmapToMemCache(key, bitmap);
                if (displayOption.isCacheOnDisk())
                    addBitmapToDiskCache(key, bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            addBitmapToMemCache(uri, bitmap);
            return bitmap;
        }
        return null;
    }


    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        final DisplayOption displayOption = displayOptionWeakReference.get();
        if (displayOption != null && bitmap != null) {
            final ImageView imageView = displayOption.getImageView();
            final ImageLoaderTask imageLoaderTask = getImageLoaderTask(imageView);
            final DisplayOption tmpOption = imageLoaderTask.getDisplayOptionWeakReference().get();
            if (this == imageLoaderTask && displayOption.equals(tmpOption)) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    // 设置占位图片
    public void setPlaceHolderImage(WeakReference<DisplayOption> displayOptionWeakReference) {
        AsyncDrawable<ImageLoaderTask> placeHolder;
        if (displayOptionWeakReference != null) {
            final DisplayOption displayOption = displayOptionWeakReference.get();
            int resId = displayOption.getResId();
            if (resId == 0) {
                placeHolder = new AsyncDrawable<>(displayOption.getResources(), ImageLoaderTask.this);
            } else {
                placeHolder = new AsyncDrawable<>(displayOption, ImageLoaderTask.this);
            }
            ImageView imageView = displayOption.getImageView();
            if (imageView != null && placeHolder != null) {
                imageView.setImageDrawable(placeHolder);
            }
        }
    }

    private Bitmap findUriInCache(String uri) {
        //find bitmap in mem cache
        Bitmap bitmap = findBitmapInMemCache(uri);

        //find bitmap in disk cache
        if (bitmap == null) {
            bitmap = findBitmapInDiskCache(uri);
        }

        return bitmap;
    }

    //find bitmap in disk cache
    private Bitmap findBitmapInDiskCache(String key) {
        DiskLruCache diskCache = diskCacheWeakReference.get();
        if (diskCache != null) {
            DiskLruCache.Snapshot snapshot;
            try {
                snapshot = diskCache.get(key);
                if (snapshot != null) {
                    InputStream in = snapshot.getInputStream(0);
                    return BitmapUtils.decodeBitmapFromStream(in);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //find bitmap in mem cache
    private Bitmap findBitmapInMemCache(String key) {
        LruCache<String, Bitmap> memCache = memCacheWeakReference.get();
        if (memCache != null)
            return memCache.get(key);

        return null;
    }


    @SuppressWarnings("unchecked")
    private ImageLoaderTask getImageLoaderTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                return ((AsyncDrawable<ImageLoaderTask>) drawable).getTask();
            }
        }
        return null;
    }

    public InputStream getBitmapInputStreamFromUri(String uri, Object extra) throws FileNotFoundException {
        switch (Scheme.ofScheme(uri)) {
            case HTTP:
            case HTTPS:
                return getBitmapFromNetwork(uri, extra);
            case FILE:
                return getBitmapInputStreamFromFile(uri, extra);
            case CONTENT:
            case ASSETS:
            case UNKNOWN:
            default:
                return null;
        }
    }

    private InputStream getBitmapInputStreamFromFile(String uri, Object extra) throws FileNotFoundException {
        String filePath = Scheme.FILE.crop(uri);
        return new BufferedInputStream(new FileInputStream(new File(filePath)));
    }

    private InputStream getBitmapFromNetwork(String uri, Object extra) {
        return null;
    }

    private void addBitmapToMemCache(String key, Bitmap bitmap) {
        LruCache<String, Bitmap> memCache = memCacheWeakReference.get();
        if (memCache != null && memCache.get(key) == null) {
            memCache.put(key, bitmap);
        }
    }

    private void addBitmapToDiskCache(String key, Bitmap bitmap) {
        DiskLruCache diskCache = diskCacheWeakReference.get();
        if (diskCache != null) {
            OutputStream os = null;
            try {
                DiskLruCache.Editor editor = diskCache.edit(key);
                if (editor != null) {
                    os = editor.newOutputStream(0);
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
                diskCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null)
                        os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public boolean cancelPotentialWork(WeakReference<DisplayOption> displayOptionWeakReference) {
        DisplayOption displayOption = displayOptionWeakReference.get();
        if (displayOption != null) {
            final ImageLoaderTask imageLoaderTask = getImageLoaderTask(displayOption.getImageView());

            if (imageLoaderTask != null) {
                WeakReference<DisplayOption> oldDislayOptionReference = imageLoaderTask.getDisplayOptionWeakReference();
                // If bitmapData is not yet set or it differs from the new data
                DisplayOption oldDisplayOption = oldDislayOptionReference.get();
                if (!displayOption.equals(oldDisplayOption)) {
                    // Cancel previous task
                    imageLoaderTask.cancel(true);
                } else {
                    // The same work is already in progress
                    return false;
                }
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public WeakReference<DisplayOption> getDisplayOptionWeakReference() {
        return displayOptionWeakReference;
    }

    public WeakReference<DiskLruCache> getDiskCacheWeakReference() {
        return diskCacheWeakReference;
    }

    public WeakReference<LruCache<String, Bitmap>> getMemCacheWeakReference() {
        return memCacheWeakReference;
    }

}
