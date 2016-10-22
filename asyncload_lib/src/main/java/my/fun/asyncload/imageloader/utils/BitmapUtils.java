package my.fun.asyncload.imageloader.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import my.fun.asyncload.imageloader.core.DisplayOption;
import my.fun.asyncload.imageloader.disklrucache.DiskLruCache;
import my.fun.asyncload.imageloader.model.ImageSize;

/**
 * Created by admin on 2016/10/7.
 */
public class BitmapUtils {
    public final static String URI_AND_SIZE_SEPARATOR = "_";

    public static Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //只获取宽，高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        //图片的宽、高
        int width = options.outWidth;
        int height = options.outHeight;
        //根据要求的宽、高计算缩放比 inSampleSize
        options.inSampleSize = calculateInSampleSize(reqWidth, reqHeight, options);
        //设置inJustDecodeBounds来decode整张图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampleBitmapFromResource(String filePath, DisplayOption displayOption) {
        ImageSize imageSize = displayOption.getImageSize();
        float scale = DensityUtils.getScale(displayOption.getResources());
        int reqWidth = (int) (imageSize.getmWidth() * scale);
        int reqHeight = (int) (imageSize.getmHeight() * scale);

        BitmapFactory.Options options = new BitmapFactory.Options();
        //只获取宽，高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        //根据要求的宽、高计算缩放比 inSampleSize
        options.inSampleSize = calculateInSampleSize(reqWidth, reqHeight, options);
        //设置inJustDecodeBounds来decode整张图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap decodeSampleBitmapFromResource(String filePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //只获取宽，高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        //根据要求的宽、高计算缩放比 inSampleSize
        options.inSampleSize = calculateInSampleSize(reqWidth, reqHeight, options);
        //设置inJustDecodeBounds来decode整张图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap decodeSampleBitmapFromStream(InputStream inputStream, DisplayOption displayOption) throws IOException {
        if (inputStream == null) {
            return null;
        }
        byte[] bytes = getBytesFromStream(inputStream);
        return decodeSampleBitmapFromByteArray(bytes, displayOption);
    }

    private static byte[] getBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(bytes)) != -1) {
            bos.write(bytes, 0, len);
        }
        byte[] data = bos.toByteArray();
        inputStream.close();
        bos.close();
        return data;
    }

    public static Bitmap decodeSampleBitmapFromByteArray(byte[] data, DisplayOption displayOption) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        ImageSize imageSize = displayOption.getImageSize();
        // if imageSize not null, will scale the image
        if (imageSize != null && imageSize.getSize() != 0) {
            float scale = DensityUtils.getScale(displayOption.getResources());
            int reqWidth = (int) (imageSize.getmWidth() * scale);
            int reqHeight = (int) (imageSize.getmHeight() * scale);
            //只获取宽，高
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            //根据要求的宽、高计算缩放比 inSampleSize
            options.inSampleSize = calculateInSampleSize(reqWidth, reqHeight, options);
            //设置inJustDecodeBounds来decode整张图片
            options.inJustDecodeBounds = false;
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    private static int calculateInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSzie = 1;

        //当宽或高大于要求的值时，缩小一倍
        while (width / inSampleSzie > reqWidth || height / inSampleSzie > reqHeight) {
            inSampleSzie *= 2;
        }

        return inSampleSzie;
    }

    public static Bitmap decodeBitmapFromDrawable(Resources res, int drawable) throws IOException {
        return BitmapFactory.decodeResource(res, drawable);
    }

    public static Bitmap decodeBitmapFromStream(InputStream inputStream) throws IOException {
        return BitmapFactory.decodeStream(inputStream);
    }

    public static InputStream getVideoThumbInputStream(String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        Bitmap bitmap = retriever.getFrameAtTime();
        return getInArrayStreamFromBitmap(bitmap);
    }

    public static InputStream getInArrayStreamFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byo = (ByteArrayOutputStream) getOutArrayStreamFromBitmap(bitmap);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byo.toByteArray());
        return byteArrayInputStream;
    }

    public static ByteArrayOutputStream getOutArrayStreamFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(DisplayOption.DEFAULT_COMPRESS_FORMAT, DisplayOption.DEFAULT_COMPRESS_QUALITY, byteArrayOutputStream);
        return byteArrayOutputStream;
    }

    // get the thumb file
    public static File getVideoThumbFile(String filePath, WeakReference<DiskLruCache> diskRef) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        Bitmap bitmap = retriever.getFrameAtTime();
        return writeBitmapToDiskCache(bitmap, diskRef, filePath);
    }

    // store the bitmap to disk
    public static File writeBitmapToDiskCache(Bitmap bitmap, WeakReference<DiskLruCache> diskCacheUtilsWeakReference, String filePath) throws IOException {
        File f = null;
        if (diskCacheUtilsWeakReference != null) {
            DiskLruCache diskLruCache = diskCacheUtilsWeakReference.get();

            if (diskLruCache != null) {
                String key = Utils.generateKeyByHash(filePath);
                File dir = diskLruCache.getDirectory();
                String fileStr = new StringBuffer().append(dir.getAbsolutePath()).append(File.separator)
                        .append(DiskCacheUtils.thumbnailPath).append(File.separator).append(key).toString();
                f = new File(fileStr);
                ByteArrayOutputStream o = getOutArrayStreamFromBitmap(bitmap);
                f = BitmapUtils.writeBitmapToDisk(f, o);
            }
        }

        return f;
    }

    public static File writeBitmapToDisk(File file, ByteArrayOutputStream outputStream) throws IOException {
        // if file exists, then check if it is not null
        if (file.exists() && file.length() != 0) {
            return file;
        }
        // if file not exists
        if (outputStream != null) {
            OutputStream fos = null;
            // create directorys
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            //create file
            file.createNewFile();
            fos = new FileOutputStream(file);
            outputStream.writeTo(fos);

            fos.close();
        }
        return file;
    }
}
