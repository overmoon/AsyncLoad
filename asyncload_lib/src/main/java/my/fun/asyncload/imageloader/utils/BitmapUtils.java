package my.fun.asyncload.imageloader.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import my.fun.asyncload.imageloader.core.DisplayOption;
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
        if (imageSize != null && imageSize.getSize()!=0) {
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

        //当宽、高大于要求的值时，缩小一倍
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

    // eg. file://xxxxxx.xxx_36x36
    public static String generateKeyString(String uri, ImageSize imageSize) {
        return new StringBuffer().append(uri).append(URI_AND_SIZE_SEPARATOR).append(imageSize.toString()).toString();
    }

    public static String generateKeyByHash(String uri, ImageSize imageSize) {
        return String.valueOf(generateKeyString(uri, imageSize).hashCode());
    }

    public static String generateKeyByHash(String keyString) {
        return String.valueOf(keyString.hashCode());
    }

    public static InputStream getVideoThumbInputStream(String filePath){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        Bitmap bitmap = retriever.getFrameAtTime();
        return getStreamFromBitmap(bitmap);
    }

    public static  InputStream getStreamFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(DisplayOption.DEFAULT_COMPRESS_FORMAT, DisplayOption.DEFAULT_COMPRESS_QUALITY, byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayInputStream;
    }
}
