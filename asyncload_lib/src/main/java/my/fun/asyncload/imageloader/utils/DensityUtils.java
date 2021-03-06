package my.fun.asyncload.imageloader.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by admin on 2016/10/7.
 */

public class DensityUtils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density/160;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density/160;
        return (int) (pxValue / scale + 0.5f);
    }


    public  static float getScale(Context context){
        return context.getResources().getDisplayMetrics().density;
    }

    public  static float getScale(Resources res){
        return res.getDisplayMetrics().density;
    }
}


