package my.fun.asyncload.imageloader.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

import my.fun.asyncload.imageloader.core.DisplayOption;

/**
 * Created by admin on 2016/10/7.
 */
public class AsyncDrawable<T>  extends BitmapDrawable {
    private final WeakReference<T> weakReference;

    public AsyncDrawable(DisplayOption displayOption, T task){
        this(displayOption.getResources(), displayOption.getResId(), task);
    }

    public AsyncDrawable(Resources res, T task){
        super(res);
        weakReference = new WeakReference<T>(task);
    }

    public AsyncDrawable(Resources res, Bitmap placeHolder, T task){
        super(res,placeHolder);
        weakReference = new WeakReference<T>(task);
    }

    public AsyncDrawable(Resources res,int resid, T task ){
        this(res, ((BitmapDrawable) res.getDrawable(resid)).getBitmap(), task);
    }

    public T getTask(){
        return  weakReference.get();
    }


}
