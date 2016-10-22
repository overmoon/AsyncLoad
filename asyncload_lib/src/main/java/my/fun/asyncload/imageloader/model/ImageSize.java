package my.fun.asyncload.imageloader.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import java.io.IOException;

import my.fun.asyncload.imageloader.utils.BitmapUtils;
import my.fun.asyncload.imageloader.utils.DensityUtils;

/**
 * Created by admin on 2016/10/10.
 */
public class ImageSize {

    private int mWidth;
    private int mHeight;
    private final static String IMAGESIZE_SEPERATOR = "x";

    public ImageSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public ImageSize(int resId, Resources resources){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtils.decodeBitmapFromDrawable(resources,resId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DisplayMetrics metrics= resources.getDisplayMetrics();
        mWidth = (int) (bitmap.getScaledWidth(metrics)/ DensityUtils.getScale(resources));
        mHeight = (int) (bitmap.getScaledHeight(metrics)/DensityUtils.getScale(resources));
    }

    public ImageSize() {

    }

    public String toString(){
        if (mWidth!=0 || mHeight!=0)
            return mWidth+IMAGESIZE_SEPERATOR+mHeight;

        return "original";
    }

    public int getSize() {
        return mWidth * mHeight;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public boolean equals(ImageSize imageSize){
        if (mWidth == imageSize.getmWidth() && mHeight == imageSize.getmHeight())
            return true;

        return false;
    }


}
